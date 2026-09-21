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

    
    @Override
    public boolean eliminarLibro(String idlibro) {
        String sql = "DELETE FROM libro WHERE idlibro = ?";
        try (Connection con = ConexionBD.getConnection(); PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, idlibro);
            int filas = ps.executeUpdate();
            if (filas > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    return ps.executeUpdate() > 0;
                }
                return true;
            }
        } catch (SQLException e) {
            System.out.println("Error al insertar: " + e.getMessage());
        }
        return false;
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
