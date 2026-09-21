/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gestiondebiblioteca;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;

/**
 *
 * @author dan38
 */
public class LibroRepositoryMySQL implements LibroRepository {

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

    @Override
    public Libro buscarPorTitulo(String titulo) {
        String sql = "SELECT id,titulo,autor,precio,stock FROM libro WHERE titulo = ?";
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

    @Override
    public List<Libro> buscarPorCantidadMinimaEnStock(int cantidad) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean insertarLibro(Libro libro1) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public boolean eliminarLibro(Libro libro1) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    private Libro mapearFila(ResultSet rs) throws SQLException {
        Libro l = new Libro();
        l.setId(rs.getString("idlibro"));
        l.setTitulo(rs.getString("titulo"));
        l.setAutor(rs.getString("autor"));
        l.setPrecio(rs.getDouble("precio"));
        l.setStock(rs.getInt("stock"));
        return l;
    }

}
