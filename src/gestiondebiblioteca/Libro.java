/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestiondebiblioteca;

/**
 * Representa un libro de biblioteca
 * <p>
 *  Un libro se identifica por su {@code id} y contiene su título, autor, precio y unidades disponibles en stock.
 * 
 * @author HugoLopez
 * @author DanielCarazo
 * @since 
 */



public class Libro {
    /** Identificador único del libro (asignado por el repositorio). */
    protected String id;
    /** Título del libro */
    protected String titulo;
    /** Autor del libro */
    protected String autor;
    /** Precio del libro */
    protected double precio;
    /** Número de unidades disponibles en stock */
    protected int stock;

    
    /**
     * 
     * @param id identificador del libro
     * @param titulo título del libro
     * @param autor autor del libro
     * @param precio precio del libro
     * @param stock unidades disponibles en stock
     */
    public Libro(String id, String titulo, String autor, double precio, int stock) {
        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.precio = precio;
        this.stock = stock;
    }

    /**
     * Crea un libro con todos sus datos, incluido el identificador.
     * @param titulo título del libro
     * @param autor  autor del libro
     * @param precio precio del libro
     * @param stock  unidades disponibles en stock
     */
    public Libro(String titulo, String autor, double precio, int stock) {
        this.titulo = titulo;
        this.autor = autor;
        this.precio = precio;
        this.stock = stock;
    }

    /**
     * Crea un libro vacío. Se usa, por ejemplo, al mapear filas de la base
     * de datos mediante los métodos {@code set}.
     */
    public Libro() {
    }
    
    /**
     * Devuelve el identificador del libro.
     *
     * @return el id del libro
     */
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getAutor() {
        return autor;
    }

    public void setAutor(String autor) {
        this.autor = autor;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    @Override
    public String toString() {
        return "Libro{" + "id=" + id + ", titulo=" + titulo + ", autor=" + autor + ", precio=" + precio + ", stock=" + stock + '}';
    }
    
    
}
