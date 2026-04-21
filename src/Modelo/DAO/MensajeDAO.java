package Modelo.DAO;

import Modelo.Entidades.Mensaje;
import Utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MensajeDAO {
private Connection conn;

    public boolean insert(Mensaje mensaje) {
        String sql = "INSERT INTO mensajes (pv_id, usuario_id, tipo, contenido, direccion, leido, archivo_url) " +
                     "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, mensaje.getPvId());
            if (mensaje.getUsuarioId() != null) {
                ps.setInt(2, mensaje.getUsuarioId());
            } else {
                ps.setNull(2, Types.INTEGER);
            }
            ps.setString(3, mensaje.getTipo());
            ps.setString(4, mensaje.getContenido());
            ps.setString(5, mensaje.getDireccion());
            ps.setBoolean(6, mensaje.isLeido());
            ps.setString(7, mensaje.getArchivoUrl());

            int affectedRows = ps.executeUpdate();
            if (affectedRows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    mensaje.setId(rs.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public List<Mensaje> findByPvId(int pvId) {
        List<Mensaje> lista = new ArrayList<>();
        String sql = "SELECT m.id, m.pv_id, m.usuario_id, m.tipo, m.contenido, m.direccion, " +
             "m.leido, m.fecha_envio, m.fecha_leido, m.archivo_url, pv.nombre AS pv_nombre " +
             "FROM mensajes m LEFT JOIN puntos_venta pv ON m.pv_id = pv.id " +
             "WHERE m.pv_id=? ORDER BY m.fecha_envio ASC";


        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, pvId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                lista.add(mapResultSetToMensaje(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public List<Mensaje> findAllUnread() {
        List<Mensaje> lista = new ArrayList<>();
        String sql = "SELECT m.*, pv.nombre as pv_nombre FROM mensajes m " +
                     "LEFT JOIN puntos_venta pv ON m.pv_id = pv.id WHERE m.leido = false AND m.direccion = 'entrante' ORDER BY m.fecha_envio DESC";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                lista.add(mapResultSetToMensaje(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public int countUnreadByPvId(int pvId) {
        String sql = "SELECT COUNT(*) FROM mensajes WHERE pv_id=? AND leido=false AND direccion='entrante'";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, pvId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public boolean marcarComoLeido(int mensajeId) {
        String sql = "UPDATE mensajes SET leido=true, fecha_leido=NOW() WHERE id=?";

        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, mensajeId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

private Mensaje mapResultSetToMensaje(ResultSet rs) throws SQLException {
    Mensaje mensaje = new Mensaje();
    mensaje.setId(rs.getInt("id"));
    mensaje.setPvId(rs.getInt("pv_id"));

    // Usa try/catch para columnas opcionales
    try { mensaje.setPvNombre(rs.getString("pv_nombre")); } catch (SQLException ignored) {}

    int usuarioId = rs.getInt("usuario_id");
    if (!rs.wasNull()) {
        mensaje.setUsuarioId(usuarioId);
    }

    mensaje.setTipo(rs.getString("tipo"));
    mensaje.setContenido(rs.getString("contenido"));
    mensaje.setDireccion(rs.getString("direccion"));
    mensaje.setLeido(rs.getBoolean("leido"));

    Timestamp fechaEnvio = rs.getTimestamp("fecha_envio");
    if (fechaEnvio != null) {
        mensaje.setFechaEnvio(new java.util.Date(fechaEnvio.getTime()));
    }

    Timestamp fechaLeido = rs.getTimestamp("fecha_leido");
    if (fechaLeido != null) {
        mensaje.setFechaLeido(new java.util.Date(fechaLeido.getTime()));
    }

    mensaje.setArchivoUrl(rs.getString("archivo_url"));

    return mensaje;
}
public Mensaje insertAndReturn(Mensaje mensaje) {
    String sql = "INSERT INTO mensajes (pv_id, usuario_id, tipo, contenido, direccion, leido, archivo_url) " +
                 "VALUES (?, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = DatabaseConnection.getInstance().getConnection();
         PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

        ps.setInt(1, mensaje.getPvId());
        if (mensaje.getUsuarioId() != null) {
            ps.setInt(2, mensaje.getUsuarioId());
        } else {
            ps.setNull(2, Types.INTEGER);
        }
        ps.setString(3, mensaje.getTipo());
        ps.setString(4, mensaje.getContenido());
        ps.setString(5, mensaje.getDireccion());
        ps.setBoolean(6, mensaje.isLeido());
        ps.setString(7, mensaje.getArchivoUrl());

        int affectedRows = ps.executeUpdate();
        if (affectedRows > 0) {
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                mensaje.setId(rs.getInt(1));
            }
            // Recupera fecha_envio desde BD
            try (PreparedStatement ps2 = conn.prepareStatement("SELECT fecha_envio FROM mensajes WHERE id=?")) {
                ps2.setInt(1, mensaje.getId());
                ResultSet rs2 = ps2.executeQuery();
                if (rs2.next()) {
                    mensaje.setFechaEnvio(rs2.getTimestamp("fecha_envio"));
                }
            }
            return mensaje;
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}



    public int countTotalUnread() {
    String sql = "SELECT COUNT(*) FROM mensajes WHERE leido=false AND direccion='entrante'";
    try (Connection conn = DatabaseConnection.getInstance().getConnection();
         Statement stmt = conn.createStatement();
         ResultSet rs = stmt.executeQuery(sql)) {
        if (rs.next()) return rs.getInt(1);
    } catch (SQLException e) { e.printStackTrace(); }
    return 0;
}

public boolean marcarTodosComoLeidos(int pvId) {
    String sql = "UPDATE mensajes SET leido = true, fecha_leido = NOW() WHERE pv_id = ? AND leido = false";
    try (Connection conn = DatabaseConnection.getInstance().getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, pvId);
        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
        return false;
    }
}
}
