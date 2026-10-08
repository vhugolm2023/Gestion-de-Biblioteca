# Base de datos de Páginas de Villa Serena

> **Trabajo Nº1 · Documentación del sistema** · Fase 3: diseño y documentación de la base de datos
> **Criterio RA6 d)**: se ha documentado la estructura de la información persistente.
> **Grupo:** _(nombres de los integrantes)_

**Archivos de la entrega:** este documento (`.md`), [`schema.sql`](schema.sql) y la imagen [`docs/imagenes/diagrama_er.png`](docs/imagenes/diagrama_er.png).

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
