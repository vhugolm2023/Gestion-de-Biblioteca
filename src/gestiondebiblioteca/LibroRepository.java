/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package gestiondebiblioteca;

import java.util.List;

/**
 *
 * @author victo
 */
public interface LibroRepository {
    
    List<Libro> mostrarTodos();
    Libro buscarPorTitulo(String titulo);
    List<Libro> buscarPorAutor(String autor);
    List<Libro> buscarPorRangoDePrecios(double precio1, double precio2);
    List<Libro> buscarPorCantidadMinimaEnStock(int cantidad);
    boolean insertarLibro(Libro libro1);
    boolean eliminarLibro (String idlibro);
    
    
}
