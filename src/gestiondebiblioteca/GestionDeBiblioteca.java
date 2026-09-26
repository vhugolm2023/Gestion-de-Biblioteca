/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package gestiondebiblioteca;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author victo
 */
public class GestionDeBiblioteca {

    private static Scanner sc = new Scanner(System.in);
    /**
     * @param args the command line arguments
     */
    private static final String RUTA_ARCHIVO = "libros.txt";

    public static void main(String[] args) {

        LibroRepositoryArchivo repoArchivo = new LibroRepositoryArchivo(RUTA_ARCHIVO);
        LibroRepositoryMySQL repoMySQL = new LibroRepositoryMySQL();

        LibroRepository activo;
        LibroRepository otro;
        String nombreActivo;
        String nombreOtro;

        System.out.println("¿Con qué repositorio quieres trabajar?");
        System.out.println("1 -- Archivo de texto plano");
        System.out.println("2 -- Con la base de datos");
        int eleccion = Integer.parseInt(sc.nextLine());

        if (eleccion == 1) {
            activo = repoArchivo;
            otro = repoMySQL;
            nombreActivo = "Archivo";
            nombreOtro = "MySQL";
        } else {
            activo = repoMySQL;
            otro = repoArchivo;
            nombreActivo = "MySQL";
            nombreOtro = "Archivo";
        }

        int opcion;
        do {
            mostrarMenu(nombreActivo, nombreOtro);
            System.out.println("Dime opción");
            opcion = Integer.parseInt(sc.nextLine());

            switch (opcion) {
                case 1:
                    List<Libro> todos = activo.mostrarTodos();
                    if (todos.isEmpty()) {
                        System.out.println("no hay libros");
                    } else {
                        for (Libro todo : todos) {
                            System.out.println(todo);

                        }
                    }

                    break;

                case 2:
                    System.out.println("Dime titulo");
                    Libro encontrado = activo.buscarPorTitulo(sc.nextLine());
                    if (encontrado == null) {
                        System.out.println("libro no encontrado con este titulo");
                    } else {
                        System.out.println(encontrado);
                    }

                    break;
                case 3:
                    System.out.println("Dime el autor");
                    String nombreAutor = sc.nextLine();
                    List<Libro> porAutor = activo.buscarPorAutor(nombreAutor);
                    if (porAutor.isEmpty()) {
                        System.out.println("Ningún libro con ese autor");
                    } else {
                        for (Libro librosAutor : porAutor) {
                            System.out.println(librosAutor);
                        }
                    }
                    break;

                case 4:
                    System.out.println("Dime precio 1");
                    double precio1 = Double.parseDouble(sc.nextLine());
                    System.out.println("Dime precio 2");
                    double precio2 = Double.parseDouble(sc.nextLine());
                    List<Libro> libros = activo.buscarPorRangoDePrecios(precio1, precio2);
                    for (Libro libro : libros) {
                        System.out.println(libro);
                    }

                    break;

                case 5:
                    System.out.println("Dime cantidad minima en stock");
                    int cantidadminima = Integer.parseInt(sc.nextLine());
                    List<Libro> librosminimo = activo.buscarPorCantidadMinimaEnStock(cantidadminima);
                    for (Libro libro : librosminimo) {
                        System.out.println(libro);
                    }

                    break;

                case 6:

                    System.out.println("Dime titulo");
                    String titulo = sc.nextLine();
                    System.out.println("Dime autor");
                    String autor = sc.nextLine();
                    System.out.println("Dime precio");
                    double precio = Double.parseDouble(sc.nextLine());
                    System.out.println("Dime stock");
                    int stock = Integer.parseInt(sc.nextLine());

                    Libro libroAInsertar = new Libro(titulo, autor, precio, stock);
                    activo.insertarLibro(libroAInsertar);

                    break;

                case 7:
                    int contador = 0;
                    System.out.println("Introduce titulo");
                    String titulo1 = sc.nextLine();
                    List<Libro> librosss = activo.mostrarTodos();
                    List<Libro> coincidencias = new ArrayList<>();

                    for (Libro libro : librosss) {
                        if (libro.getTitulo().equalsIgnoreCase(titulo1)) {
                            contador++;
                            coincidencias.add(libro);
                        }
                    }

                    if (contador == 0) {
                        System.out.println("No hay ningún libro con ese título");
                    } else if (contador == 1) {
                        Libro unico = coincidencias.get(0);
                        activo.eliminarLibro(unico.getId());
                        System.out.println("Libro eliminado");
                    } else {
                        System.out.println("Hay varios libros con ese título, elige por id:");
                        for (Libro libro : coincidencias) {
                            System.out.println(libro.getId() + " - " + libro.getTitulo());
                        }
                        System.out.println("Introduce el id a eliminar");
                        String idElegido = sc.nextLine();
                        activo.eliminarLibro(idElegido);
                        System.out.println("Libro eliminado");
                    }

                    break;

                case 8:
                    boolean ok = activo.copiarA(otro);
                    if (ok) {
                        System.out.println("Copia realizada con éxito");
                    } else {
                        System.out.println("Hubo algún problema al copiar, o no había libros que copiar");
                    }
                    break;
                case 0:
                    System.out.println("Saliendo del programa");
                    break;

                default:
                    System.out.println("Opción no valida");
            }
        } while (opcion != 0);
    }

    public static void mostrarMenu(String nombreActivo, String nombreOtro) {
        System.out.println("");
        System.out.println("Menu " + nombreActivo);
        System.out.println("1. Mostrar todos los libros");
        System.out.println("2. Buscar libro por titulo");
        System.out.println("3. Buscar libros por autor");
        System.out.println("4. Buscar libros por rango de precios");
        System.out.println("5. Buscar libros por cantidad minima en stock");
        System.out.println("6. Insertar nuevo libro");
        System.out.println("7. Eliminar libro por titulo");
        System.out.println("8. Hacer copia");
        System.out.println("0. Salir");
    }
}
