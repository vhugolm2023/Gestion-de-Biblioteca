/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestiondebiblioteca;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;

/**
 * Implementación de {@link LibroRepository} que guarda los libros en una
 * base de datos MySQL, en la tabla {@code libro}.
 * <p>
 * Las conexiones se obtienen mediante {@link ConexionBD#getConnection()} y
 * todas las consultas usan {@link PreparedStatement}. Si ocurre un
 * {@link SQLException}, se muestra el error por consola.
 *
 * @author DanielCarazo
 * @author HugoLopez
 */
public class LibroRepositoryMySQL implements LibroRepository {

    
    /**
     * {@inheritDoc}
     */
    @Override
    public List<Libro> mostrarTodos() {
        List<Libro> L = new ArrayList<>();
        String sql = "SELECT idlibro,titulo,autor,precio,stock FROM libro";
        try (Connection con = ConexionBD.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                L.add(mapearFila(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return L;

    }
/**
     * {@inheritDoc}
     * <p>
     * Devuelve la primera fila que coincida con el título indicado.
     */
    @Override
    public Libro buscarPorTitulo(String titulo) {
        String sql = "SELECT idlibro,titulo,autor,precio,stock FROM libro WHERE titulo = ?";
        try (Connection con = ConexionBD.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, titulo);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return mapearFila(rs);
            }

        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }
        return null;

    }
/**
     * {@inheritDoc}
     */
    @Override
    public List<Libro> buscarPorAutor(String autor) {
        List<Libro> L = new ArrayList<>();
        String sql = "SELECT idlibro,titulo,autor,precio,stock FROM libro where autor= ?";
        try (Connection con = ConexionBD.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, autor);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                L.add(mapearFila(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return L;
    }
 /**
     * {@inheritDoc}
     */
    @Override
    public List<Libro> buscarPorRangoDePrecios(double precio1, double precio2) {
        List<Libro> L = new ArrayList<>();
        String sql = "SELECT idlibro,titulo,autor,precio,stock FROM libro where precio between ? and ?";
        try (Connection con = ConexionBD.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setDouble(1, precio1);
            ps.setDouble(2, precio2);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                L.add(mapearFila(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return L;
    }
/**
     * {@inheritDoc}
     */
    @Override
    public List<Libro> buscarPorCantidadMinimaEnStock(int cantidad) {
        List<Libro> L = new ArrayList<>();
        String sql = "SELECT idlibro,titulo,autor,precio,stock FROM libro where stock >= ?";
        try (Connection con = ConexionBD.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, cantidad);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                L.add(mapearFila(rs));
            }
        } catch (SQLException e) {
            System.out.println("Error: " + e.getMessage());
        }

        return L;
    }
/**
     * {@inheritDoc}
     * <p>
     * El id lo genera la base de datos (clave autogenerada) y se establece
     * en el objeto {@code libro1} tras la inserción.
     */
    @Override
    public boolean insertarLibro(Libro libro1) {
        String sql = "INSERT INTO libro (titulo,autor,precio,stock) VALUES (?,?,?,?)";
        try (Connection con = ConexionBD.getConnection(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, libro1.getTitulo());
            ps.setString(2, libro1.getAutor());
            ps.setDouble(3, libro1.getPrecio());
            ps.setInt(4, libro1.getStock());
            int filas = ps.executeUpdate();
            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    libro1.setId(rs.getString(1));
                }
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Error al insertar: " + e.getMessage());
        }
        return false;
    }
/**
     * {@inheritDoc}
     */
    @Override
    public boolean eliminarLibro(String id) {
        String sql = "DELETE FROM libro WHERE idlibro = ?";

        try (Connection con = ConexionBD.getConnection(); PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, id);
            int filas = ps.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            System.out.println("Error al eliminar: " + e.getMessage());
            return false;
        }
    }
/**
     * Convierte la fila actual de un {@link ResultSet} en un {@link Libro}.
     *
     * @param rs resultado de la consulta, posicionado en la fila a leer
     * @return el libro con los datos de la fila
     * @throws SQLException si ocurre un error al leer alguna columna
     */
    private Libro mapearFila(ResultSet rs) throws SQLException {
        Libro l = new Libro();
        l.setId(rs.getString("idlibro"));
        l.setTitulo(rs.getString("titulo"));
        l.setAutor(rs.getString("autor"));
        l.setPrecio(rs.getDouble("precio"));
        l.setStock(rs.getInt("stock"));
        return l;
    }
/**
     * {@inheritDoc}
     * <p>
     * Se crea una copia de cada libro antes de insertarlo en el destino,
     * que le asignará su propio id.
     */
    @Override
    public boolean copiarA(LibroRepository destino) {
        List<Libro> libros = mostrarTodos();
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
