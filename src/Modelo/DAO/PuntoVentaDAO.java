package Modelo.DAO;

import Controller.ChatController;
import Modelo.Entidades.Conversacion;
import Modelo.Entidades.Mensaje;
import Modelo.Entidades.PuntoVenta;
import Utils.DatabaseConnection;
import Utils.TelegramSender;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PuntoVentaDAO {
    
    public boolean insert(PuntoVenta pv) {
        String sql = "INSERT INTO puntos_venta (codigo, nombre, ciudad, telefono, direccion, comision_personalizada, activo, telegram_chat_id) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, pv.getCodigo());
            ps.setString(2, pv.getNombre());
            ps.setString(3, pv.getCiudad());
            ps.setString(4, pv.getTelefono());
            ps.setString(5, pv.getDireccion());
            if (pv.getComisionPersonalizada() != null) {
                ps.setDouble(6, pv.getComisionPersonalizada());
            } else {
                ps.setNull(6, Types.DOUBLE);
            }
            ps.setBoolean(7, pv.isActivo());
            ps.setString(8, pv.getTelegramChatId());
            
            int affectedRows = ps.executeUpdate();
            
            if (affectedRows > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    pv.setId(rs.getInt(1));
                }

                // Notificar por Telegram
                if (pv.getTelegramChatId() != null) {
                    TelegramSender sender = TelegramSender.getInstance();
                    sender.enviarMensajeAsync(
                        pv.getTelegramChatId(),
                        "📩 Nuevo Punto de Venta registrado: " + pv.getNombre() + " en " + pv.getCiudad()
                    );

                }

                return true;
            }

            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean update(PuntoVenta pv) {
        String sql = "UPDATE puntos_venta SET nombre=?, telefono=?, ciudad=?, telegram_chat_id=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, pv.getNombre());
            ps.setString(2, pv.getTelefono());
            ps.setString(3, pv.getCiudad());
            ps.setString(4, pv.getTelegramChatId());
            ps.setInt(5, pv.getId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean delete(int id) {
        String sql = "DELETE FROM puntos_venta WHERE id=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public PuntoVenta findById(int id) {
        String sql = "SELECT * FROM puntos_venta WHERE id=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToPuntoVenta(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<PuntoVenta> findAll() {
        List<PuntoVenta> lista = new ArrayList<>();
        String sql = "SELECT * FROM puntos_venta ORDER BY nombre";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapResultSetToPuntoVenta(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    public List<PuntoVenta> findByCiudad(String ciudad) {
        List<PuntoVenta> lista = new ArrayList<>();
        String sql = "SELECT * FROM puntos_venta WHERE ciudad=? ORDER BY nombre";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, ciudad);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                lista.add(mapResultSetToPuntoVenta(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    private PuntoVenta mapResultSetToPuntoVenta(ResultSet rs) throws SQLException {
        PuntoVenta pv = new PuntoVenta();
        pv.setCodigo(rs.getString("codigo"));
        pv.setId(rs.getInt("id"));
        pv.setNombre(rs.getString("nombre"));
        pv.setCiudad(rs.getString("ciudad"));
        pv.setTelefono(rs.getString("telefono"));
        pv.setDireccion(rs.getString("direccion"));
        
        double comision = rs.getDouble("comision_personalizada");
        if (!rs.wasNull()) {
            pv.setComisionPersonalizada(comision);
        }
        
        pv.setActivo(rs.getBoolean("activo"));
        pv.setFechaRegistro(rs.getTimestamp("fecha_registro"));
        pv.setTelegramChatId(rs.getString("telegram_chat_id"));
        
        return pv;
    }

    public PuntoVenta findByTelefono(String telefono) {
        String sql = "SELECT * FROM puntos_venta WHERE telefono=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, telefono);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToPuntoVenta(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public PuntoVenta findByNombre(String nombre) {
        String sql = "SELECT * FROM puntos_venta WHERE nombre=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, nombre);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToPuntoVenta(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean updateChatId(int id, String chatId) {
        String sql = "UPDATE puntos_venta SET telegram_chat_id=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, chatId);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public PuntoVenta findByChatId(String chatId) {
        String sql = "SELECT * FROM puntos_venta WHERE telegram_chat_id=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, chatId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapResultSetToPuntoVenta(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // 🔥 Nuevo método para activar/inactivar en tiempo real
    public boolean actualizarEstado(int id, boolean activo) {
        String sql = "UPDATE puntos_venta SET activo=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, activo);
            ps.setInt(2, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // Buscar por nombre y ciudad (usa idx_pv_nombre_ciudad)
public List<PuntoVenta> findByNombreCiudad(String nombre, String ciudad) {
    List<PuntoVenta> lista = new ArrayList<>();
    String sql = "SELECT * FROM puntos_venta WHERE nombre=? AND ciudad=? ORDER BY nombre";
    try (Connection conn = DatabaseConnection.getInstance().getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, nombre);
        ps.setString(2, ciudad);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            lista.add(mapResultSetToPuntoVenta(rs));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return lista;
}

// Buscar por contacto (usa idx_pv_contacto)
public PuntoVenta findByContacto(String telefono, String email) {
    String sql = "SELECT * FROM puntos_venta WHERE contacto_telefono=? OR email=?";
    try (Connection conn = DatabaseConnection.getInstance().getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, telefono);
        ps.setString(2, email);
        ResultSet rs = ps.executeQuery();
        if (rs.next()) {
            return mapResultSetToPuntoVenta(rs);
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}
}
