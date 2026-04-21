package Modelo.DAO;

import Utils.DatabaseConnection;
import java.sql.*;
import java.util.UUID;

public class SesionDAO {

    public boolean existeSesionActiva(int usuarioId) {
        String sql = "SELECT id FROM sesiones WHERE usuario_id=? AND activo=true LIMIT 1";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void cerrarSesiones(int usuarioId) {
        String sql = "UPDATE sesiones SET activo=false WHERE usuario_id=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String registrarSesion(int usuarioId) {
        String token = UUID.randomUUID().toString();
        String sql = "INSERT INTO sesiones (usuario_id, token, activo) VALUES (?,?,true)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, usuarioId);
            ps.setString(2, token);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return token;
    }

    public void cerrarSesionPorToken(String token) {
        String sql = "UPDATE sesiones SET activo=false WHERE token=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, token);
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
