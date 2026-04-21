package Modelo.DAO;

import Modelo.Entidades.Usuario;
import Utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UsuarioDAO {

    public Usuario findByUsername(String username) {
        // Consulta optimizada: usa índice (username, activo) y LIMIT 1
        String sql = "SELECT * FROM usuarios WHERE username=? AND activo=true LIMIT 1";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // Configurar timeout en la consulta (máx. 5 segundos)
            ps.setQueryTimeout(5);

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Usuario> findAll() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT * FROM usuarios ORDER BY nombre_completo";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) lista.add(map(rs));
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean insert(Usuario u) {
        String sql = "INSERT INTO usuarios (username, password, nombre_completo, " +
                     "email, telefono, rol, activo) VALUES (?,?,?,?,?,?,?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getPassword());
            ps.setString(3, u.getNombreCompleto());
            ps.setString(4, u.getEmail());
            ps.setString(5, u.getTelefono());
            ps.setString(6, u.getRol());
            ps.setBoolean(7, u.isActivo());
            if (ps.executeUpdate() > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) u.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(Usuario u) {
        String sql = "UPDATE usuarios SET username=?, nombre_completo=?, " +
                     "email=?, telefono=?, rol=?, activo=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getUsername());
            ps.setString(2, u.getNombreCompleto());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getTelefono());
            ps.setString(5, u.getRol());
            ps.setBoolean(6, u.isActivo());
            ps.setInt(7, u.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean updatePassword(int id, String newPassword) {
        String sql = "UPDATE usuarios SET password=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean delete(int id) {
        String sql = "UPDATE usuarios SET activo=false WHERE id=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private Usuario map(ResultSet rs) throws SQLException {
        Usuario u = new Usuario();
        u.setId(rs.getInt("id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password"));
        u.setNombreCompleto(rs.getString("nombre_completo"));
        try { u.setEmail(rs.getString("email")); } catch (Exception ignored) {}
        try { u.setTelefono(rs.getString("telefono")); } catch (Exception ignored) {}
        u.setRol(rs.getString("rol"));
        u.setActivo(rs.getBoolean("activo"));
        return u;
    }
}
