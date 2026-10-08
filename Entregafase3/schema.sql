-- =====================================================================
-- Páginas de Villa Serena · Base de datos de la librería
-- Trabajo Nº1 · Fase 3 · RA6 d)
-- Probado en MySQL 8.0 (CHECK requiere 8.0.16 o superior)
-- =====================================================================

DROP DATABASE IF EXISTS libreria;
CREATE DATABASE libreria CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE libreria;

-- ---------------------------------------------------------------------
-- 1. ESTRUCTURA (DDL) · primero las tablas que no dependen de otras
-- ---------------------------------------------------------------------

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

-- ---------------------------------------------------------------------
-- 2. DATOS DE PRUEBA (DML) · mismo orden: primero las tablas de las que
--    dependen las demás. Los ISBN y DNI son ficticios.
-- ---------------------------------------------------------------------

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
