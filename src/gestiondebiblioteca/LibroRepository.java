/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package gestiondebiblioteca;

import java.util.List;

/**
 * Contrato común para cualquier almacén de libros (archivo de texto,
 * base de datos, etc.).
 * <p>
 * Permite que el programa trabaje con distintos tipos de almacenamiento sin
 * conocer los detalles de cada uno.
 *
 * @author HugoLopez
 * @author DanielCarazo
 */
public interface LibroRepository {
    
     /**
     * Obtiene todos los libros almacenados.
     *
     * @return lista con todos los libros; vacía si no hay ninguno
     */
    List<Libro> mostrarTodos();
    /**
     * Busca un libro por su título.
     *
     * @param titulo título a buscar
     * @return el libro encontrado, o {@code null} si no existe ninguno con ese título
     */
    Libro buscarPorTitulo(String titulo);
     /**
     * Busca todos los libros de un autor.
     *
     * @param autor nombre del autor
     * @return lista de libros del autor; vacía si no hay coincidencias
     */
    List<Libro> buscarPorAutor(String autor);
    /**
     * Busca los libros cuyo precio esté dentro de un rango (ambos extremos incluidos).
     *
     * @param precio1 precio mínimo
     * @param precio2 precio máximo
     * @return lista de libros dentro del rango; vacía si no hay coincidencias
     */
    List<Libro> buscarPorRangoDePrecios(double precio1, double precio2);
     /**
     * Busca los libros con al menos una cantidad dada de unidades en stock.
     *
     * @param cantidad stock mínimo requerido
     * @return lista de libros con stock mayor o igual a {@code cantidad}
     */
    List<Libro> buscarPorCantidadMinimaEnStock(int cantidad);
    /**
     * Inserta un libro nuevo en el almacén. El repositorio asigna el id
     * y lo establece en el propio objeto recibido.
     *
     * @param libro1 libro a insertar
     * @return {@code true} si se insertó correctamente, {@code false} en caso contrario
     */
    boolean insertarLibro(Libro libro1);
    /**
     * Elimina un libro a partir de su identificador.
     *
     * @param id identificador del libro a eliminar
     * @return {@code true} si se eliminó, {@code false} si no existía o hubo un error
     */
    boolean eliminarLibro (String id);
    /**
     * Copia todos los libros de este repositorio a otro.
     *
     * @param destino repositorio donde se insertarán los libros
     * @return {@code true} si se copiaron todos correctamente; {@code false} si
     *         no había libros o alguna inserción falló
     */
    boolean copiarA(LibroRepository destino);
    
}
