/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestiondebiblioteca;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author dan38
 */
public class LibroRepositoryArchivo implements LibroRepository {
    protected String rutaarchivo;

    public LibroRepositoryArchivo(String rutaarchivo) {
        this.rutaarchivo = rutaarchivo;
        File archivo = new File(rutaarchivo);
        
        if (!archivo.exists()) {
            try {
                archivo.createNewFile();
            } catch (IOException e) {
                System.out.println("No se ha podido crear el archivo: " + e.getMessage());
            }
        }
    }

    public ArrayList<Libro> leerTodoArchivo(){
        ArrayList<Libro> lista = new ArrayList();
        try (BufferedReader br = new BufferedReader(new FileReader(rutaarchivo))){
          String linea;
            while ((linea = br.readLine()) != null) {
            String[] partes = linea.split("\\^");
            String id = partes[0];
            String titulo = partes[1];
            String autor = partes[2];
            double precio = Double.parseDouble(partes[3]);
            int stock = Integer.parseInt(partes[4]);
            Libro libro = new Libro(id, titulo, autor, precio, stock);
            lista.add(libro);
            }

        } catch (Exception e) {
            System.out.println("No se pudo leeer nada" + e.getMessage());
        }
        return lista;
        
        
    }
    
    
    @Override
    public List<Libro> mostrarTodos() {
        ArrayList<Libro> lista = leerTodoArchivo();
        if (lista.isEmpty()) {
            System.out.println("Lista esta vacía");
            return lista;
            
        }
        else{
            for (Libro libro : lista) {
                System.out.println(libro);
            }
        }

    return lista;
    }

    @Override
    public Libro buscarPorTitulo(String titulo) {
        ArrayList<Libro> lista = leerTodoArchivo();
        for (Libro libros : lista) {
            if (libros.getTitulo().equalsIgnoreCase(titulo)) {
                return libros;
            }
            
        }
        return null;
    }

    @Override
    public List<Libro> buscarPorAutor(String autor) {
        ArrayList<Libro> lista = leerTodoArchivo();
        ArrayList<Libro> librosAutor = new ArrayList<>();
        
        for (Libro libro : lista) {
            if (libro.getAutor().equalsIgnoreCase(autor)) {
                librosAutor.add(libro);
            }
            
        }
        return librosAutor;
            
        }




    @Override
    public List<Libro> buscarPorRangoDePrecios(double precio1, double precio2) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public List<Libro> buscarPorCantidadMinimaEnStock(int cantidad) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean insertarLibro(Libro libro1) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean eliminarLibro(String idlibro) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    
        }
    

