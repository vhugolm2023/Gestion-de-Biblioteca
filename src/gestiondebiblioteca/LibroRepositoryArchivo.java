/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestiondebiblioteca;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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

    public ArrayList<Libro> leerTodoArchivo() {
        ArrayList<Libro> lista = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(rutaarchivo))) {
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

    private void escribirTodoArchivo(List<Libro> lista) {
        try (PrintWriter pw = new PrintWriter(new FileWriter(rutaarchivo))) {
            for (Libro libro : lista) {
                pw.println(libro.getId() + "^" + libro.getTitulo() + "^"
                        + libro.getAutor() + "^" + libro.getPrecio() + "^"
                        + libro.getStock());
            }
        } catch (IOException e) {
            System.out.println("No se pudo escribir en el archivo: " + e.getMessage());
        }
    }

    private String generarNuevoId(ArrayList<Libro> lista) {
        int maximo = 0;

        for (Libro libro : lista) {
            int idActual = Integer.parseInt(libro.getId());
            if (idActual > maximo) {
                maximo = idActual;
            }
        }

        return String.valueOf(maximo + 1);
    }

    @Override
    public List<Libro> mostrarTodos() {
        return leerTodoArchivo();
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
        ArrayList<Libro> lista = leerTodoArchivo();
        ArrayList<Libro> resultado = new ArrayList<>();
        for (Libro libro : lista) {
            if (libro.getPrecio() >= precio1 && libro.getPrecio() <= precio2) {
                resultado.add(libro);
            }
        }
        return resultado;

    }

    @Override
    public List<Libro> buscarPorCantidadMinimaEnStock(int cantidad) {
        ArrayList<Libro> lista = leerTodoArchivo();
        ArrayList<Libro> resultado = new ArrayList<>();
        for (Libro libro : lista) {
            if (libro.getStock() >= cantidad) {
                resultado.add(libro);
            }
        }
        return resultado;

    }

    @Override
    public boolean insertarLibro(Libro libro1) {
        ArrayList<Libro> lista = leerTodoArchivo();
        String nuevoId = generarNuevoId(lista);
        libro1.setId(nuevoId);
        for (Libro libro : lista) {
            if (libro.getId().equals(libro1.getId())) {
                return false;
            }

        }
        lista.add(libro1);

        escribirTodoArchivo(lista);

        return true;
    }

    @Override
    public boolean eliminarLibro(String id) {
        ArrayList<Libro> lista = leerTodoArchivo();
        boolean seElimino = false;
        Libro libroAEliminar = null;
        for (Libro libro : lista) {
            if (libro.getId().equals(id)) {
                libroAEliminar = libro;
                seElimino = true;
                break;
            }
        }
        if (libroAEliminar != null) {
            lista.remove(libroAEliminar);
            escribirTodoArchivo(lista);

        }
        return seElimino;
    }

    @Override
    public boolean copiarA(LibroRepository destino) {
        List<Libro> libros = leerTodoArchivo();
        if (libros.isEmpty()) {
            return false;
        }
        boolean todoOk = true;
        for (Libro libro : libros) {
            Libro copia = new Libro(libro.getId(), libro.getTitulo(),
                    libro.getAutor(), libro.getPrecio(), libro.getStock());
            if (!destino.insertarLibro(copia)) {
                todoOk = false;
            }
        }
        return todoOk;
    }

}
