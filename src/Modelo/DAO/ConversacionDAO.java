package Modelo.DAO;

import Modelo.Entidades.Conversacion;
import Utils.DatabaseConnection;
import java.sql.*;

public class ConversacionDAO {
    public boolean insert(Conversacion conv) {
        String sql = "INSERT INTO conversaciones (pv_id, estado_actual) VALUES (?, ?)";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, conv.getPuntoVenta().getId());
            ps.setString(2, conv.getEstadoActual());

            int rows = ps.executeUpdate();
            if (rows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    conv.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
