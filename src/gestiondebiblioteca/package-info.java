/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/package-info.java to edit this template
 */
package gestiondebiblioteca;

/**
 * Aplicación de consola para la gestión de una biblioteca de libros.
 * <p>
 * El paquete contiene:
 * <ul>
 *   <li>{@link gestiondebiblioteca.Libro}: modelo de datos de un libro
 *       (id, título, autor, precio y stock).</li>
 *   <li>{@link gestiondebiblioteca.LibroRepository}: interfaz que define las
 *       operaciones de consulta, inserción, eliminación y copia de libros.</li>
 *   <li>{@link gestiondebiblioteca.LibroRepositoryArchivo}: implementación que
 *       guarda los libros en un archivo de texto plano.</li>
 *   <li>{@link gestiondebiblioteca.LibroRepositoryMySQL}: implementación que
 *       guarda los libros en una base de datos MySQL.</li>
 *   <li>{@link gestiondebiblioteca.ConexionBD}: obtención de conexiones a la
 *       base de datos a partir de la configuración del archivo {@code .env}.</li>
 *   <li>{@link gestiondebiblioteca.GestionDeBiblioteca}: clase principal con el
 *       menú por consola.</li>
 * </ul>
 * Gracias a la interfaz {@code LibroRepository}, el programa puede trabajar
 * con cualquiera de los dos almacenamientos y copiar los libros de uno a otro.
 */