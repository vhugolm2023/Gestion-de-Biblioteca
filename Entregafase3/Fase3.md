# Base de datos de Páginas de Villa Serena

> **Trabajo Nº1 · Documentación del sistema** · Fase 3: diseño y documentación de la base de datos
> **Criterio RA6 d)**: se ha documentado la estructura de la información persistente.
> **Grupo:** _(nombres de los integrantes)_

**Archivos de la entrega:** este documento (`.md`), [`schema.sql`](schema.sql) y la imagen [`docs/imagenes/diagrama_er.png`](diagrama_er.png).

## Índice

1. [Resumen del caso](#1-resumen-del-caso)
2. [Análisis del caso](#2-análisis-del-caso)
3. [Reglas de negocio](#3-reglas-de-negocio)
4. [Diagrama entidad-relación](#4-diagrama-entidad-relación)
5. [Modelo lógico](#5-modelo-lógico)
6. [Script SQL (schema.sql)](#6-script-sql-schemasql)
7. [Diccionario de datos](#7-diccionario-de-datos)
8. [Decisiones de diseño](#8-decisiones-de-diseño)
9. [Datos de prueba](#9-datos-de-prueba)
10. [Consultas de prueba](#10-consultas-de-prueba)
11. [Limitaciones y mejoras futuras](#11-limitaciones-y-mejoras-futuras)

## 1. Resumen del caso

Páginas de Villa Serena es una librería con veinte años de historia que hoy cuenta con tres tiendas: Centro, Ribera (en el pueblo vecino de Aldeaverde) y Universidad. Su dueña, Elena Ruiz, vende libros del catálogo propio, cada uno con su editorial y sus autores, y tiene empleados repartidos por las tiendas y clientes, algunos de ellos socios con descuentos.

El problema de hoy es que cada tienda lleva su propia hoja de cálculo y las cifras no coinciden con la realidad: un cliente pidió un libro en Centro, la hoja decía que había tres copias y en realidad estaban en Universidad. Además, las hojas repiten datos (la editorial en cada fila), meten varios autores en una sola celda y guardan un único «stock» por libro, sin distinguir tienda. Y si el precio de un libro cambia, los pedidos antiguos dejan de cuadrar con lo que se cobró.

Elena necesita una base de datos que le diga **en todo momento cuántas copias de cada libro hay en cada tienda y cuándo se contaron**, que registre los **pedidos** con lo que realmente se cobró en cada uno, y que permita responder preguntas de negocio como el libro más vendido de cada tienda, lo facturado por tienda o los clientes más habituales.

---

## 2. Análisis del caso

Para cada elemento se indica entre paréntesis la sección del caso de la que sale (§1 a §8).

### 2.1 Entidades y atributos

| Entidad | Atributos encontrados | Fragmento del caso |
|---|---|---|
| Tienda | nombre, dirección, teléfono, ciudad | §1: «su nombre, su dirección, su teléfono y su ciudad» |
| Libro | ISBN, título, año de publicación, número de páginas, precio de catálogo | §2: «se identifica por su ISBN… título, año… páginas… precio de catálogo» |
| Editorial | nombre, país, teléfono | §2: «su nombre, el país y un teléfono de contacto» |
| Autor | nombre, nacionalidad, año de nacimiento | §2: «el nombre, la nacionalidad y el año de nacimiento» |
| Empleado | DNI, nombre, apellidos, cargo, fecha de contratación, correo | §4: «el DNI, el nombre y los apellidos, el cargo… la fecha de contratación y el correo» |
| Cliente | nombre, correo (único), teléfono (opcional), si es socio, fecha de alta | §5: «nombre completo, correo… teléfono (opcional) y fecha de alta» |
| Pedido | número, fecha, forma de pago, estado | §6: «Tiene una fecha, una forma de pago… y un estado» |

Además aparecen **datos que pertenecen a una relación** y no a una entidad, y por eso acaban en tablas intermedias:

| Dato | Pertenece a la relación | Fragmento del caso |
|---|---|---|
| Rol del autor (principal o colaborador) | Libro – Autor | §2: «distinguir si el autor es principal o colaborador» |
| Copias y fecha del último recuento | Tienda – Libro | §3: «cuántas copias hay, y cuándo se contó por última vez» |
| Cantidad y precio realmente cobrado | Pedido – Libro | §6: «de cada uno me interesa la cantidad… lo que realmente se cobró» |

### 2.2 Relaciones

| Relación | Cardinalidad | Razonamiento |
|---|---|---|
| Editorial – Libro | 1:N | Una editorial publica muchos libros; un libro lo publica una sola editorial (§2). |
| Libro – Autor | N:M | Un libro puede tener dos o tres autores y un autor tiene muchos libros (§2). Se resuelve con `libro_autor`, que guarda el rol. |
| Tienda – Libro | N:M | Una tienda tiene muchos libros y un libro puede estar en varias tiendas, o en ninguna (§3). Se resuelve con `inventario`, que guarda copias y fecha de conteo. |
| Tienda – Empleado | 1:N | En cada tienda trabajan varios empleados; cada empleado trabaja en una sola tienda (§4). |
| Tienda – Pedido | 1:N | Un pedido se hace siempre en una tienda; una tienda tiene muchos pedidos (§6). |
| Empleado – Pedido | 1:N | Un pedido lo atiende un empleado; un empleado atiende muchos pedidos (§6). |
| Cliente – Pedido | 1:N | Un pedido lo compra un cliente; un cliente puede hacer muchos pedidos (§6). |
| Pedido – Libro | N:M | Un pedido lleva varios libros distintos y un libro aparece en muchos pedidos (§6). Se resuelve con `linea_pedido`, que guarda cantidad y precio cobrado. |

---

### 2.3 Datos descartados

| Dato | Motivo |
|---|---|
| Total del ticket (50,50 €) y subtotal de cada línea (24,00 €) | Se pueden **calcular** con `cantidad × precio_unitario`. Guardarlos permitiría que un total no cuadre con sus líneas. |
| Columna «stock» por libro (hoja de cálculo, §3) | Es justo lo que falla hoy: no distingue tienda. Se sustituye por `inventario`. |
| Celda «Cortázar / Borges» (§8) | Dos valores en una celda; se descompone en dos filas de `libro_autor`. |
| Editorial repetida en cada fila de la hoja (§8) | Dato repetido; se guarda una vez en `editorial` y se referencia. |
| Nombre y cargo del empleado dentro del ticket («Marta López (cajera)») | Ya vive en `empleado`; el pedido solo guarda la referencia. |
| Dirección y teléfono de la tienda dentro del ticket | Ya viven en `tienda`. |
| Historial de cambios de tienda de un empleado | Elena dice que no le importa (§4). |
| Distinción «socio / no socio» como dos entidades | Son la misma persona con distinto estado; se modela con un único `cliente` (ver decisión 5). |

---



## 3. Reglas de negocio

1. Una editorial publica muchos libros; cada libro lo publica una única editorial.
2. Cada libro se identifica por su ISBN, que tiene 13 cifras.
3. Un libro puede tener varios autores y un autor puede tener varios libros; en cada libro cada autor figura como principal o como colaborador.
4. Cada tienda guarda, para cada libro que tiene, el número de copias y la fecha del último recuento; un libro que no está en una tienda no aparece en su inventario.
5. El número de copias de un libro en una tienda no puede ser negativo.
6. Cada empleado trabaja en una única tienda y su cargo es librero, cajero o encargado.
7. Un cliente se identifica por su correo electrónico, que no puede repetirse; el teléfono es opcional.
8. Un socio es un cliente con fecha de alta; quien no es socio no tiene fecha de alta.
9. Un pedido se hace siempre en una única tienda, lo atiende un único empleado y lo compra un único cliente.
10. Un pedido tiene fecha, una forma de pago (efectivo, tarjeta o bizum) y un estado (preparado, entregado o cancelado).
11. Un pedido puede llevar varios libros distintos, y de cada uno se guarda la cantidad, que debe ser mayor que cero.
12. Cada línea de pedido guarda el precio que realmente se cobró por copia, que no puede ser negativo y no cambia aunque cambie el precio de catálogo.
13. El total de un pedido no se guarda: se calcula sumando `cantidad × precio_unitario` de sus líneas.
14. Cuando un empleado cambia de tienda, figura en la nueva y no se conserva el historial.

---

## 4. Diagrama entidad-relación

![Diagrama entidad-relación de la librería Páginas de Villa Serena con diez tablas: editorial, libro, autor, libro_autor, tienda, inventario, empleado, cliente, pedido y linea_pedido, unidas por relaciones uno a muchos](diagrama_er.png)

El diagrama tiene **10 tablas**: 7 entidades (en azul) y 3 tablas intermedias de relaciones N:M (en naranja). En cada línea, la marca simple está en el lado «uno» y la pata de gallo en el lado «muchos». Las N:M no se dibujan directamente: se ven como dos relaciones 1:N que parten de la tabla intermedia.

### Tabla de relaciones

| Relación | Tipo | Cómo se resuelve |
|---|---|---|
| editorial – libro | 1:N | `libro.editorial_id` |
| libro – autor | N:M | Tabla intermedia `libro_autor` (con `rol`) |
| tienda – libro | N:M | Tabla intermedia `inventario` (con `copias` y `fecha_conteo`) |
| tienda – empleado | 1:N | `empleado.tienda_id` |
| tienda – pedido | 1:N | `pedido.tienda_id` |
| empleado – pedido | 1:N | `pedido.empleado_dni` |
| cliente – pedido | 1:N | `pedido.cliente_id` |
| pedido – libro | N:M | Tabla intermedia `linea_pedido` (con `cantidad` y `precio_unitario`) |

---

## 5. Modelo lógico

Las claves foráneas están en el lado «muchos» de cada relación 1:N. En las tablas intermedias, la clave primaria es la pareja de claves foráneas.

**`editorial`**

| Columna | Clave | Referencia |
|---|---|---|
| `id` | PK |  |
| `nombre` |  |  |
| `pais` |  |  |
| `telefono` |  |  |

**`autor`**

| Columna | Clave | Referencia |
|---|---|---|
| `id` | PK |  |
| `nombre` |  |  |
| `nacionalidad` |  |  |
| `anio_nacimiento` |  |  |

**`libro`**

| Columna | Clave | Referencia |
|---|---|---|
| `isbn` | PK |  |
| `titulo` |  |  |
| `anio_publicacion` |  |  |
| `num_paginas` |  |  |
| `precio_catalogo` |  |  |
| `editorial_id` | FK | `editorial.id` |

**`libro_autor`**

| Columna | Clave | Referencia |
|---|---|---|
| `isbn` | PK, FK | `libro.isbn` |
| `autor_id` | PK, FK | `autor.id` |
| `rol` |  |  |

**`tienda`**

| Columna | Clave | Referencia |
|---|---|---|
| `id` | PK |  |
| `nombre` |  |  |
| `direccion` |  |  |
| `telefono` |  |  |
| `ciudad` |  |  |

**`inventario`**

| Columna | Clave | Referencia |
|---|---|---|
| `tienda_id` | PK, FK | `tienda.id` |
| `isbn` | PK, FK | `libro.isbn` |
| `copias` |  |  |
| `fecha_conteo` |  |  |

**`empleado`**

| Columna | Clave | Referencia |
|---|---|---|
| `dni` | PK |  |
| `nombre` |  |  |
| `apellidos` |  |  |
| `cargo` |  |  |
| `fecha_contratacion` |  |  |
| `correo` |  |  |
| `tienda_id` | FK | `tienda.id` |

**`cliente`**

| Columna | Clave | Referencia |
|---|---|---|
| `id` | PK |  |
| `nombre` |  |  |
| `correo` |  |  |
| `telefono` |  |  |
| `es_socio` |  |  |
| `fecha_alta` |  |  |

**`pedido`**

| Columna | Clave | Referencia |
|---|---|---|
| `id` | PK |  |
| `fecha` |  |  |
| `forma_pago` |  |  |
| `estado` |  |  |
| `tienda_id` | FK | `tienda.id` |
| `empleado_dni` | FK | `empleado.dni` |
| `cliente_id` | FK | `cliente.id` |

**`linea_pedido`**

| Columna | Clave | Referencia |
|---|---|---|
| `pedido_id` | PK, FK | `pedido.id` |
| `isbn` | PK, FK | `libro.isbn` |
| `cantidad` |  |  |
| `precio_unitario` |  |  |

---

## 6. Script SQL (schema.sql)

El script completo (estructura y datos de prueba) está en [`schema.sql`](schema.sql). Se ha probado **desde cero en MySQL 8.0** sobre una base de datos vacía, sin errores. Requiere MySQL 8.0.16 o superior porque usa `CHECK`. A continuación, la parte de estructura; el orden de creación respeta las dependencias (primero las tablas a las que apuntan las claves foráneas).

```sql
DROP DATABASE IF EXISTS libreria;
CREATE DATABASE libreria CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE libreria;

CREATE TABLE editorial (
    id        INT          PRIMARY KEY AUTO_INCREMENT,
    nombre    VARCHAR(100) NOT NULL UNIQUE,
    pais      VARCHAR(60)  NOT NULL,
    telefono  VARCHAR(20)  NOT NULL
);

CREATE TABLE autor (
    id                INT          PRIMARY KEY AUTO_INCREMENT,
    nombre            VARCHAR(100) NOT NULL,
    nacionalidad      VARCHAR(60)  NOT NULL,
    anio_nacimiento   SMALLINT     NOT NULL,
    CHECK (anio_nacimiento BETWEEN 1000 AND 2100)
);

CREATE TABLE tienda (
    id         INT          PRIMARY KEY AUTO_INCREMENT,
    nombre     VARCHAR(50)  NOT NULL UNIQUE,
    direccion  VARCHAR(150) NOT NULL,
    telefono   VARCHAR(20)  NOT NULL,
    ciudad     VARCHAR(60)  NOT NULL
);

CREATE TABLE cliente (
    id          INT          PRIMARY KEY AUTO_INCREMENT,
    nombre      VARCHAR(100) NOT NULL,
    correo      VARCHAR(120) NOT NULL UNIQUE,
    telefono    VARCHAR(20)  NULL,
    es_socio    BOOLEAN      NOT NULL DEFAULT FALSE,
    fecha_alta  DATE         NULL,
    CHECK (es_socio = FALSE OR fecha_alta IS NOT NULL)
);

CREATE TABLE libro (
    isbn             CHAR(13)      PRIMARY KEY,
    titulo           VARCHAR(150)  NOT NULL,
    anio_publicacion SMALLINT      NOT NULL,
    num_paginas      SMALLINT      NOT NULL,
    precio_catalogo  DECIMAL(6,2)  NOT NULL,
    editorial_id     INT           NOT NULL,
    FOREIGN KEY (editorial_id) REFERENCES editorial(id),
    CHECK (isbn REGEXP '^[0-9]{13}$'),
    CHECK (num_paginas > 0),
    CHECK (precio_catalogo >= 0)
);

-- N:M libro - autor, con dato propio (rol del autor en ese libro)
CREATE TABLE libro_autor (
    isbn      CHAR(13) NOT NULL,
    autor_id  INT      NOT NULL,
    rol       ENUM('principal','colaborador') NOT NULL DEFAULT 'principal',
    PRIMARY KEY (isbn, autor_id),
    FOREIGN KEY (isbn)     REFERENCES libro(isbn),
    FOREIGN KEY (autor_id) REFERENCES autor(id)
);

-- N:M tienda - libro, con datos propios (copias y fecha del último recuento)
CREATE TABLE inventario (
    tienda_id       INT      NOT NULL,
    isbn            CHAR(13) NOT NULL,
    copias          INT      NOT NULL,
    fecha_conteo    DATE     NOT NULL,
    PRIMARY KEY (tienda_id, isbn),
    FOREIGN KEY (tienda_id) REFERENCES tienda(id),
    FOREIGN KEY (isbn)      REFERENCES libro(isbn),
    CHECK (copias >= 0)
);

CREATE TABLE empleado (
    dni                CHAR(9)      PRIMARY KEY,
    nombre             VARCHAR(50)  NOT NULL,
    apellidos          VARCHAR(100) NOT NULL,
    cargo              ENUM('librero','cajero','encargado') NOT NULL,
    fecha_contratacion DATE         NOT NULL,
    correo             VARCHAR(120) NOT NULL UNIQUE,
    tienda_id          INT          NOT NULL,
    FOREIGN KEY (tienda_id) REFERENCES tienda(id)
);

CREATE TABLE pedido (
    id            INT  PRIMARY KEY AUTO_INCREMENT,
    fecha         DATE NOT NULL,
    forma_pago    ENUM('efectivo','tarjeta','bizum')        NOT NULL,
    estado        ENUM('preparado','entregado','cancelado') NOT NULL DEFAULT 'preparado',
    tienda_id     INT      NOT NULL,
    empleado_dni  CHAR(9)  NOT NULL,
    cliente_id    INT      NOT NULL,
    FOREIGN KEY (tienda_id)    REFERENCES tienda(id),
    FOREIGN KEY (empleado_dni) REFERENCES empleado(dni),
    FOREIGN KEY (cliente_id)   REFERENCES cliente(id)
);

-- N:M pedido - libro, con datos propios (cantidad y precio realmente cobrado)
CREATE TABLE linea_pedido (
    pedido_id        INT          NOT NULL,
    isbn             CHAR(13)     NOT NULL,
    cantidad         INT          NOT NULL,
    precio_unitario  DECIMAL(6,2) NOT NULL,
    PRIMARY KEY (pedido_id, isbn),
    FOREIGN KEY (pedido_id) REFERENCES pedido(id),
    FOREIGN KEY (isbn)      REFERENCES libro(isbn),
    CHECK (cantidad > 0),
    CHECK (precio_unitario >= 0)
);
```

---

## 7. Diccionario de datos

#### `editorial`

Editorial que publica libros y a la que se hacen los pedidos de reposición (§2).

| Columna | Tipo | Obligatorio | Descripción |
|---|---|:---:|---|
| `id` | `INT` | Sí | Identificador numérico asignado por MySQL (`AUTO_INCREMENT`). |
| `nombre` | `VARCHAR(100)` | Sí | Nombre comercial de la editorial. Único: no puede haber dos editoriales con el mismo nombre. |
| `pais` | `VARCHAR(60)` | Sí | País donde tiene su sede. |
| `telefono` | `VARCHAR(20)` | Sí | Teléfono de contacto para hacer pedidos. Es texto porque admite espacios y prefijo internacional. |

#### `autor`

Persona que escribe libros (§2).

| Columna | Tipo | Obligatorio | Descripción |
|---|---|:---:|---|
| `id` | `INT` | Sí | Identificador numérico del autor. |
| `nombre` | `VARCHAR(100)` | Sí | Nombre completo con el que se busca al autor (por ejemplo, «Julio Cortázar»). |
| `nacionalidad` | `VARCHAR(60)` | Sí | Nacionalidad del autor. |
| `anio_nacimiento` | `SMALLINT` | Sí | Año de nacimiento (solo el año, como pide el caso). Debe estar entre 1000 y 2100. |

#### `libro`

Título del catálogo, identificado por su ISBN (§2).

| Columna | Tipo | Obligatorio | Descripción |
|---|---|:---:|---|
| `isbn` | `CHAR(13)` | Sí | ISBN de 13 cifras. Longitud fija, solo dígitos (lo garantiza un `CHECK`). Se usa como clave porque ya es único en el mundo real. |
| `titulo` | `VARCHAR(150)` | Sí | Título del libro. |
| `anio_publicacion` | `SMALLINT` | Sí | Año de publicación de la edición del catálogo. |
| `num_paginas` | `SMALLINT` | Sí | Número de páginas. Debe ser mayor que 0. |
| `precio_catalogo` | `DECIMAL(6,2)` | Sí | Precio **actual** de venta en euros. Puede cambiar con el tiempo; lo que se cobró de verdad en cada venta está en `linea_pedido.precio_unitario`. No puede ser negativo. |
| `editorial_id` | `INT` | Sí | Editorial que publica el libro. Un libro tiene una sola editorial. |

#### `libro_autor`

Tabla intermedia N:M entre libro y autor (§2).

| Columna | Tipo | Obligatorio | Descripción |
|---|---|:---:|---|
| `isbn` | `CHAR(13)` | Sí | Libro del que se habla. Junto con `autor_id` forma la clave primaria. |
| `autor_id` | `INT` | Sí | Autor que participa en ese libro. |
| `rol` | `ENUM` | Sí | `principal` o `colaborador` (por ejemplo, quien hace el prólogo). Por defecto `principal`. |

#### `tienda`

Cada una de las tres librerías físicas (§1).

| Columna | Tipo | Obligatorio | Descripción |
|---|---|:---:|---|
| `id` | `INT` | Sí | Identificador numérico de la tienda. |
| `nombre` | `VARCHAR(50)` | Sí | Nombre de la tienda (Centro, Ribera, Universidad). Único. |
| `direccion` | `VARCHAR(150)` | Sí | Dirección postal del local. |
| `telefono` | `VARCHAR(20)` | Sí | Teléfono de la tienda, tal y como sale en el ticket. |
| `ciudad` | `VARCHAR(60)` | Sí | Localidad donde está la tienda (Ribera está en Aldeaverde). |

#### `inventario`

Tabla intermedia N:M entre tienda y libro: copias que hay de cada libro en cada tienda (§3).

| Columna | Tipo | Obligatorio | Descripción |
|---|---|:---:|---|
| `tienda_id` | `INT` | Sí | Tienda donde se cuentan las copias. |
| `isbn` | `CHAR(13)` | Sí | Libro contado. Si un libro no está en una tienda, simplemente no hay fila. |
| `copias` | `INT` | Sí | Número de copias en la estantería en el último recuento. Puede ser 0 (agotado), nunca negativo. |
| `fecha_conteo` | `DATE` | Sí | Día en que se contaron esas copias por última vez. |

#### `empleado`

Persona que trabaja en una tienda (§4).

| Columna | Tipo | Obligatorio | Descripción |
|---|---|:---:|---|
| `dni` | `CHAR(9)` | Sí | DNI con letra (8 cifras + letra). Identifica al empleado. |
| `nombre` | `VARCHAR(50)` | Sí | Nombre de pila. |
| `apellidos` | `VARCHAR(100)` | Sí | Apellidos. |
| `cargo` | `ENUM` | Sí | `librero`, `cajero` o `encargado`. |
| `fecha_contratacion` | `DATE` | Sí | Día en que fue contratado. |
| `correo` | `VARCHAR(120)` | Sí | Correo de trabajo. Único. |
| `tienda_id` | `INT` | Sí | Tienda donde trabaja. Es una sola; si cambia de tienda se actualiza este valor y no se guarda historial (§4). |

#### `cliente`

Persona que compra, sea socia o no (§5).

| Columna | Tipo | Obligatorio | Descripción |
|---|---|:---:|---|
| `id` | `INT` | Sí | Identificador numérico del cliente. |
| `nombre` | `VARCHAR(100)` | Sí | Nombre completo. |
| `correo` | `VARCHAR(120)` | Sí | Correo electrónico. Único; se usa para enviar avisos y novedades. |
| `telefono` | `VARCHAR(20)` | No | Teléfono, opcional (§5). |
| `es_socio` | `BOOLEAN` | Sí | `TRUE` si se ha dado de alta como socio (con descuentos). Por defecto `FALSE`. |
| `fecha_alta` | `DATE` | Solo socios | Día de alta como socio. Obligatoria si `es_socio` es `TRUE` y `NULL` para quien no es socio (lo garantiza un `CHECK`). |

#### `pedido`

Cabecera de una compra: un cliente, en una tienda, atendido por un empleado (§6).

| Columna | Tipo | Obligatorio | Descripción |
|---|---|:---:|---|
| `id` | `INT` | Sí | Número de pedido (el «Pedido nº» del ticket). |
| `fecha` | `DATE` | Sí | Día de la compra. |
| `forma_pago` | `ENUM` | Sí | `efectivo`, `tarjeta` o `bizum`. |
| `estado` | `ENUM` | Sí | `preparado`, `entregado` o `cancelado`. Por defecto `preparado`. |
| `tienda_id` | `INT` | Sí | Tienda donde se hace el pedido (siempre una). |
| `empleado_dni` | `CHAR(9)` | Sí | Empleado que atiende el pedido (siempre uno). |
| `cliente_id` | `INT` | Sí | Cliente que compra (siempre uno). |

#### `linea_pedido`

Tabla intermedia N:M entre pedido y libro: cada renglón del ticket (§6).

| Columna | Tipo | Obligatorio | Descripción |
|---|---|:---:|---|
| `pedido_id` | `INT` | Sí | Pedido al que pertenece la línea. |
| `isbn` | `CHAR(13)` | Sí | Libro vendido. Un libro aparece una sola vez por pedido; si se compran varias copias se sube `cantidad`. |
| `cantidad` | `INT` | Sí | Número de copias vendidas. Debe ser mayor que 0. |
| `precio_unitario` | `DECIMAL(6,2)` | Sí | Euros que se cobraron **por copia en el momento de la venta**. No cambia aunque el precio de catálogo suba después. No puede ser negativo. |

---

## 8. Decisiones de diseño

### Decisión 1 · Guardar el precio cobrado en `linea_pedido`

- **Qué se decidió:** cada línea de pedido guarda `precio_unitario`, el precio por copia en el momento de la venta, aparte de `libro.precio_catalogo`.
- **Por qué:** Elena subió *Ficciones* de 10 € a 12 € y no quiere que los pedidos antiguos muestren el precio nuevo (§6). Con este diseño, el pedido 10401 (2025) sigue valiendo 10,00 € y el 10482 (2026) 50,50 €, como en el ticket.
- **Alternativa descartada:** leer siempre el precio de `libro.precio_catalogo` al calcular el total. Es más simple, pero cualquier subida de precio reescribiría la historia de las facturas.

### Decisión 2 · Libro y autor como relación N:M con tabla intermedia y rol

- **Qué se decidió:** tabla `libro_autor` con clave primaria `(isbn, autor_id)` y una columna `rol`.
- **Por qué:** hay libros de dos o tres autores y autores con muchos libros (§2), y Elena quiere buscar «todos los libros de Cortázar» sin leer títulos. El rol (principal o colaborador) es un dato de la relación, no del libro ni del autor.
- **Alternativa descartada:** una columna `autor` en `libro` con varios nombres separados por «/», como en la hoja de Carmen (§8). No se puede buscar con fiabilidad, repite nombres y no admite el rol.

### Decisión 3 · Inventario como tabla intermedia en lugar de una columna `stock` en `libro`

- **Qué se decidió:** tabla `inventario` con clave `(tienda_id, isbn)` y las columnas `copias` y `fecha_conteo`. Un libro que no está en una tienda no tiene fila.
- **Por qué:** el fallo de las hojas actuales es justo ese: un único stock por libro no dice en qué tienda están las copias (§1 y §3). Las copias y la fecha del último recuento dependen de la pareja tienda-libro.
- **Alternativa descartada:** una columna `stock` en `libro`, o tres columnas `stock_centro`, `stock_ribera`, `stock_universidad`. La primera pierde la tienda; la segunda obliga a cambiar la estructura si se abre una cuarta tienda.

### Decisión 4 · No guardar el total del pedido

- **Qué se decidió:** `pedido` no tiene columna de total; se calcula con `SUM(cantidad * precio_unitario)`.
- **Por qué:** es un dato derivado (§6, ticket). Si se guardara, habría que mantenerlo sincronizado con las líneas y podría quedar un total que no suma lo que dicen sus líneas.
- **Alternativa descartada:** guardar `total` en `pedido` para consultarlo más rápido. Con el volumen de una librería no compensa el riesgo de incoherencia.

### Decisión 5 · Un único `cliente` para socios y no socios

- **Qué se decidió:** una sola tabla `cliente` con `es_socio` y `fecha_alta`, que solo se rellena en los socios (`CHECK`).
- **Por qué:** el caso dice que quien compra sin ser socio también se registra con nombre y correo (§5), así que todos tienen los mismos datos básicos y todos pueden hacer pedidos. Si más adelante un cliente se hace socio, basta con actualizar la fila.
- **Alternativa descartada:** dos tablas, `socio` y `cliente_ocasional`. Obligaría a que `pedido` apunte a una u otra, y cambiar de estado sería mover datos entre tablas.

### Decisión 6 · Claves naturales para libro y empleado, y clave numérica para el resto

- **Qué se decidió:** `libro.isbn` (`CHAR(13)`) y `empleado.dni` (`CHAR(9)`) son claves primarias; el resto de tablas usa un `id INT AUTO_INCREMENT`.
- **Por qué:** el ISBN y el DNI ya identifican de forma única en el mundo real y el caso los trata como identificadores (§2 y §4). En cambio, el nombre de una editorial, una tienda o un autor puede repetirse o corregirse.
- **Alternativa descartada:** un `id` numérico en todas las tablas. Funcionaría, pero obligaría a mantener además el ISBN como columna `UNIQUE` y los `JOIN` serían menos legibles.

### Decisión 7 · Tipos adecuados para dinero, fechas y estados

- **Qué se decidió:** `DECIMAL(6,2)` para los precios, `DATE` para las fechas, `ENUM` para forma de pago, estado y cargo, y `CHAR(13)` para el ISBN.
- **Por qué:** `DECIMAL` evita los errores de redondeo de `DOUBLE` al sumar euros. `DATE` permite filtrar por año (`YEAR(fecha)`). Los `ENUM` impiden valores no previstos como «efectivoo». El ISBN es texto de longitud fija (los ceros a la izquierda no se pueden perder).
- **Alternativa descartada:** `DOUBLE` para precios y `VARCHAR` libre para estados y formas de pago.

---



## 9. Datos de prueba

Datos coherentes con el caso: la hoja de Universidad de Carmen (§8) y el ticket del pedido 10482 (§6). Los ISBN, DNI, correos de empleados y direcciones son **inventados**. El pedido 10401 (2025) se ha añadido para comprobar que el precio antiguo de *Ficciones* se conserva. Hay 4 editoriales, 4 autores (*Antología del cuento* tiene dos), 5 libros, 3 tiendas, 6 empleados, 4 clientes, 7 pedidos y 13 líneas. Se cargan al final de `schema.sql`, en el orden que respeta las claves foráneas.

```sql
INSERT INTO editorial (id, nombre, pais, telefono) VALUES
(1, 'Alfaguara',      'España', '913 000 101'),
(2, 'Alianza',        'España', '913 000 102'),
(3, 'Plaza & Janés',  'España', '934 000 103'),
(4, 'Editorial RM',   'México', '+52 55 0000 0104');

INSERT INTO autor (id, nombre, nacionalidad, anio_nacimiento) VALUES
(1, 'Julio Cortázar',     'Argentina', 1914),
(2, 'Jorge Luis Borges',  'Argentina', 1899),
(3, 'Isabel Allende',     'Chilena',   1942),
(4, 'Juan Rulfo',         'Mexicana',  1917);

INSERT INTO tienda (id, nombre, direccion, telefono, ciudad) VALUES
(1, 'Centro',       'Calle Mayor 12',          '976 000 111', 'Villa Serena'),
(2, 'Ribera',       'Avenida del Río 5',       '976 000 222', 'Aldeaverde'),
(3, 'Universidad',  'Plaza del Campus 1',      '976 000 333', 'Villa Serena');

INSERT INTO libro (isbn, titulo, anio_publicacion, num_paginas, precio_catalogo, editorial_id) VALUES
('9780000000011', 'Rayuela',               1963, 600, 16.50, 1),
('9780000000028', 'Ficciones',             1944, 224, 12.00, 2),
('9780000000035', 'Cuentos de Eva Luna',   1989, 320, 14.90, 3),
('9780000000042', 'Antología del cuento',  1975, 280, 18.00, 2),
('9780000000059', 'Pedro Páramo',          1955, 128, 10.00, 4);

INSERT INTO libro_autor (isbn, autor_id, rol) VALUES
('9780000000011', 1, 'principal'),
('9780000000028', 2, 'principal'),
('9780000000035', 3, 'principal'),
('9780000000042', 1, 'principal'),
('9780000000042', 2, 'colaborador'),
('9780000000059', 4, 'principal');

INSERT INTO inventario (tienda_id, isbn, copias, fecha_conteo) VALUES
-- Centro
(1, '9780000000011', 2, '2026-03-05'),
(1, '9780000000028', 5, '2026-03-05'),
(1, '9780000000035', 3, '2026-03-05'),
(1, '9780000000059', 4, '2026-03-05'),
-- Ribera
(2, '9780000000011', 0, '2026-03-03'),
(2, '9780000000028', 1, '2026-03-03'),
(2, '9780000000042', 2, '2026-03-03'),
(2, '9780000000059', 6, '2026-03-03'),
-- Universidad (hoja de Carmen, §8 del caso)
(3, '9780000000011', 4, '2026-03-02'),
(3, '9780000000028', 2, '2026-03-02'),
(3, '9780000000035', 0, '2026-03-02'),
(3, '9780000000042', 6, '2026-02-28');

INSERT INTO empleado (dni, nombre, apellidos, cargo, fecha_contratacion, correo, tienda_id) VALUES
('12345678Z', 'Marta',  'López Ibáñez',  'cajero',    '2022-09-01', 'marta.lopez@villaserena.example',  1),
('23456789D', 'Pablo',  'Sanz Ortiz',    'encargado', '2018-02-15', 'pablo.sanz@villaserena.example',   1),
('34567890V', 'Irene',  'Gil Navarro',   'librero',   '2023-04-10', 'irene.gil@villaserena.example',    2),
('45678901G', 'Raúl',   'Ortega Pons',   'encargado', '2016-06-01', 'raul.ortega@villaserena.example',  2),
('56789012B', 'Sofía',  'Marín Cano',    'librero',   '2024-01-08', 'sofia.marin@villaserena.example',  3),
('67890123B', 'Hugo',   'Vidal Rey',     'cajero',    '2025-02-03', 'hugo.vidal@villaserena.example',   3);

INSERT INTO cliente (id, nombre, correo, telefono, es_socio, fecha_alta) VALUES
(1, 'Andrés Pérez',    'andres.p@correo.es',    NULL,        FALSE, NULL),
(2, 'Laura Fernández', 'laura.f@correo.es',     '600 111 222', TRUE, '2024-05-20'),
(3, 'Javier Moreno',   'javier.m@correo.es',    '600 333 444', TRUE, '2025-11-02'),
(4, 'Clara Núñez',     'clara.n@correo.es',     NULL,        FALSE, NULL);

INSERT INTO pedido (id, fecha, forma_pago, estado, tienda_id, empleado_dni, cliente_id) VALUES
(10401, '2025-06-10', 'efectivo', 'entregado', 1, '12345678Z', 4),  -- antes de la subida de precio
(10482, '2026-03-12', 'tarjeta',  'entregado', 1, '12345678Z', 1),  -- el ticket de la §6
(10483, '2026-03-14', 'efectivo', 'entregado', 1, '23456789D', 2),
(10484, '2026-03-15', 'bizum',    'entregado', 2, '34567890V', 3),
(10485, '2026-03-18', 'tarjeta',  'preparado', 3, '56789012B', 2),
(10486, '2026-03-20', 'efectivo', 'cancelado', 3, '67890123B', 1),
(10487, '2026-03-25', 'bizum',    'entregado', 1, '12345678Z', 2);

INSERT INTO linea_pedido (pedido_id, isbn, cantidad, precio_unitario) VALUES
(10401, '9780000000028', 1, 10.00),   -- Ficciones cuando costaba 10 €
(10482, '9780000000011', 1, 16.50),
(10482, '9780000000028', 2, 12.00),
(10482, '9780000000059', 1, 10.00),   -- total del ticket: 50,50 €
(10483, '9780000000035', 1, 14.90),
(10483, '9780000000028', 1, 12.00),
(10484, '9780000000059', 2, 10.00),
(10484, '9780000000042', 1, 18.00),
(10485, '9780000000042', 2, 18.00),
(10485, '9780000000011', 1, 16.50),
(10486, '9780000000028', 1, 12.00),
(10487, '9780000000011', 1, 16.50),
(10487, '9780000000059', 1, 10.00);
```

---
## 10. Consultas de prueba

Se resuelven las **7 preguntas** de la §7 del caso. Los resultados son los que devuelve MySQL con los datos de prueba anteriores.

### 10.1 ¿Qué libros tiene la tienda Centro y cuántas copias quedan?

```sql
SELECT l.titulo, i.copias, i.fecha_conteo
FROM inventario i
JOIN tienda t ON t.id = i.tienda_id
JOIN libro  l ON l.isbn = i.isbn
WHERE t.nombre = 'Centro'
ORDER BY l.titulo;
```

| titulo | copias | fecha_conteo |
|---|---:|---|
| Cuentos de Eva Luna | 3 | 2026-03-05 |
| Ficciones | 5 | 2026-03-05 |
| Pedro Páramo | 4 | 2026-03-05 |
| Rayuela | 2 | 2026-03-05 |

### 10.2 ¿Cuál es el libro más vendido en cada tienda?

Se cuentan las unidades de los pedidos no cancelados y se queda con el primero de cada tienda (si hay empate, salen todos los empatados).

```sql
WITH ventas AS (
    SELECT t.nombre AS tienda, l.titulo, SUM(lp.cantidad) AS unidades,
           RANK() OVER (PARTITION BY t.id ORDER BY SUM(lp.cantidad) DESC) AS posicion
    FROM linea_pedido lp
    JOIN pedido p ON p.id = lp.pedido_id
    JOIN tienda t ON t.id = p.tienda_id
    JOIN libro  l ON l.isbn = lp.isbn
    WHERE p.estado <> 'cancelado'
    GROUP BY t.id, t.nombre, l.isbn, l.titulo
)
SELECT tienda, titulo, unidades
FROM ventas
WHERE posicion = 1
ORDER BY tienda, titulo;
```

| tienda | titulo | unidades |
|---|---|---:|
| Centro | Ficciones | 4 |
| Ribera | Pedro Páramo | 2 |
| Universidad | Antología del cuento | 2 |

### 10.3 ¿Cuánto ha facturado cada tienda este año?

El total no se guarda: se calcula desde las líneas. Se descartan los pedidos cancelados y el pedido de 2025.

```sql
SELECT t.nombre AS tienda,
       SUM(lp.cantidad * lp.precio_unitario) AS facturado_2026
FROM pedido p
JOIN tienda t        ON t.id = p.tienda_id
JOIN linea_pedido lp ON lp.pedido_id = p.id
WHERE YEAR(p.fecha) = 2026
  AND p.estado <> 'cancelado'
GROUP BY t.id, t.nombre
ORDER BY facturado_2026 DESC;
```

| tienda | facturado_2026 |
|---|---:|
| Centro | 103.90 |
| Universidad | 52.50 |
| Ribera | 38.00 |

### 10.4 ¿Qué clientes han hecho más de dos pedidos?

```sql
SELECT c.nombre, c.correo, COUNT(*) AS pedidos
FROM cliente c
JOIN pedido p ON p.cliente_id = c.id
GROUP BY c.id, c.nombre, c.correo
HAVING COUNT(*) > 2
ORDER BY pedidos DESC;
```

| nombre | correo | pedidos |
|---|---|---:|
| Laura Fernández | laura.f@correo.es | 3 |

### 10.5 ¿Qué libros están agotados en una tienda pero disponibles en otra?

«Agotado» significa que hay fila en `inventario` con `copias = 0`.

```sql
SELECT l.titulo,
       ta.nombre AS tienda_agotado,
       GROUP_CONCAT(td.nombre ORDER BY td.nombre SEPARATOR ', ') AS disponible_en
FROM inventario ia
JOIN tienda ta ON ta.id = ia.tienda_id
JOIN libro  l  ON l.isbn = ia.isbn
JOIN inventario id_ ON id_.isbn = ia.isbn AND id_.copias > 0
JOIN tienda td ON td.id = id_.tienda_id
WHERE ia.copias = 0
GROUP BY l.isbn, l.titulo, ta.id, ta.nombre
ORDER BY l.titulo, ta.nombre;
```

| titulo | tienda_agotado | disponible_en |
|---|---|---|
| Cuentos de Eva Luna | Universidad | Centro |
| Rayuela | Ribera | Centro, Universidad |

### 10.6 ¿Qué empleado ha atendido más pedidos?

```sql
SELECT CONCAT(e.nombre, ' ', e.apellidos) AS empleado,
       t.nombre AS tienda,
       COUNT(*) AS pedidos_atendidos
FROM empleado e
JOIN tienda t ON t.id = e.tienda_id
JOIN pedido p ON p.empleado_dni = e.dni
GROUP BY e.dni, e.nombre, e.apellidos, t.nombre
ORDER BY pedidos_atendidos DESC, empleado
LIMIT 1;
```

| empleado | tienda | pedidos_atendidos |
|---|---|---:|
| Marta López Ibáñez | Centro | 3 |

### 10.7 ¿Qué autores tienen libros en más de una editorial?

```sql
SELECT a.nombre AS autor, COUNT(DISTINCT l.editorial_id) AS editoriales
FROM autor a
JOIN libro_autor la ON la.autor_id = a.id
JOIN libro l        ON l.isbn = la.isbn
GROUP BY a.id, a.nombre
HAVING COUNT(DISTINCT l.editorial_id) > 1;
```

| autor | editoriales |
|---|---:|
| Julio Cortázar | 2 |

### 10.8 Comprobación extra: el ticket y el precio histórico

Esta consulta comprueba la decisión 1: el total del pedido 10482 coincide con el ticket (50,50 €) y el pedido 10401, de antes de la subida, conserva el precio antiguo de *Ficciones* (10 €).

```sql
SELECT p.id AS pedido, SUM(lp.cantidad * lp.precio_unitario) AS total
FROM pedido p JOIN linea_pedido lp ON lp.pedido_id = p.id
WHERE p.id IN (10401, 10482)
GROUP BY p.id;
```

| pedido | total |
|---|---:|
| 10401 | 10.00 |
| 10482 | 50.50 |

---

## 11. Limitaciones y mejoras futuras

| Limitación | Posible mejora |
|---|---|
| El pedido guarda tienda y empleado por separado, y la base de datos no impide que se asigne un empleado de otra tienda. | Añadir una clave foránea compuesta `(empleado_dni, tienda_id)` hacia `empleado`, o validarlo en la aplicación. |
| No se guarda el historial de cambios de tienda de un empleado (Elena dijo que no lo necesita). | Tabla `empleado_tienda` con fechas de inicio y fin. |
| `inventario` guarda el último recuento, no los movimientos: las ventas no restan copias automáticamente. | Tabla de movimientos de stock o un `TRIGGER` que descuente al entregar un pedido. |
| No se guarda el descuento de los socios: solo se sabe quién es socio, no cuánto se rebajó. | El `precio_unitario` ya refleja lo cobrado; se podría añadir un porcentaje de descuento por línea para poder analizarlo. |
| El precio de catálogo solo tiene el valor actual, sin historial. | Tabla `precio_libro` con fecha de inicio y fin de cada precio. |
| No hay control de devoluciones ni pedidos a editoriales. | Nuevas tablas `devolucion` y `pedido_editorial`. |
| Un libro solo puede aparecer una vez por pedido (clave `(pedido_id, isbn)`), aunque se vendiera a precios distintos. | Añadir un número de línea a la clave si se necesitaran precios distintos para el mismo libro en un pedido. |
| El correo de un cliente es su único identificador natural; si lo cambia hay que actualizar la fila. | Mantener el `id` numérico como referencia y permitir actualizar el correo. |