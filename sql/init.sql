SET NAMES utf8mb4;

CREATE DATABASE IF NOT EXISTS biblioteca
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_0900_ai_ci;

USE biblioteca;

DROP TABLE IF EXISTS libro;

CREATE TABLE libro (
    idlibro  INT           NOT NULL AUTO_INCREMENT PRIMARY KEY,
    titulo   VARCHAR(150)  NOT NULL,
    autor    VARCHAR(100)  NOT NULL,
    precio   DECIMAL(6,2)  NOT NULL,
    stock    INT           NOT NULL DEFAULT 0,
    CONSTRAINT chk_libro_precio CHECK (precio >= 0),
    CONSTRAINT chk_libro_stock  CHECK (stock  >= 0)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;


INSERT INTO libro (titulo, autor, precio, stock) VALUES
    ('El Quijote',            'Miguel de Cervantes',     15.50, 3),
    ('1984',                  'George Orwell',           12.00, 5),
    ('Cien años de soledad',  'Gabriel García Márquez',  18.90, 2),
    ('Fahrenheit 451',        'Ray Bradbury',            10.75, 0),
    ('Rebelión en la granja', 'George Orwell',            7.95, 12),
    ('El amor en los tiempos del cólera', 'Gabriel García Márquez', 19.90, 4),
    ('La sombra del viento',  'Carlos Ruiz Zafón',       21.50, 6),
    ('Don Juan Tenorio',      'José Zorrilla',            9.50, 8);
