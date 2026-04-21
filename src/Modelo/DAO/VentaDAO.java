
package Modelo.DAO;


import Modelo.Entidades.Venta;
import Utils.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VentaDAO {
    
public boolean insert(Venta venta) {
    String sql = "INSERT INTO ventas (pv_id, numero_poliza, placa, aseguradora, tipo_vehiculo, canal, prima, comision_pv, comision_empresa, estado, pdf_url, fecha_emision, fecha_vencimiento, voucher_id, observaciones, emitido_por) "
               + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection conn = DatabaseConnection.getInstance().getConnection();
         PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

        ps.setInt(1, venta.getPvId());
        ps.setString(2, venta.getNumeroPoliza());
        ps.setString(3, venta.getPlaca());
        ps.setString(4, venta.getAseguradora());
        ps.setString(5, venta.getTipoVehiculo());
        ps.setString(6, venta.getCanal());
        ps.setDouble(7, venta.getPrima());
        ps.setDouble(8, venta.getComisionPV());
        ps.setDouble(9, venta.getComisionEmpresa());
        ps.setString(10, venta.getEstado());
        ps.setString(11, venta.getPdfUrl());

        // fecha_emision
        if (venta.getFechaEmision() != null) {
            ps.setTimestamp(12, new Timestamp(venta.getFechaEmision().getTime()));
        } else {
            ps.setTimestamp(12, new Timestamp(System.currentTimeMillis()));
        }

        // fecha_vencimiento (fallback automático)
        if (venta.getFechaVencimiento() != null) {
            ps.setDate(13, new java.sql.Date(venta.getFechaVencimiento().getTime()));
        } else {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(venta.getFechaEmision() != null ? venta.getFechaEmision() : new java.util.Date());
            cal.add(java.util.Calendar.YEAR, 1);
            ps.setDate(13, new java.sql.Date(cal.getTimeInMillis()));
        }

        if (venta.getVoucherId() > 0) {
            ps.setInt(14, venta.getVoucherId());
        } else {
            ps.setNull(14, Types.INTEGER);
        }

        ps.setString(15, venta.getObservaciones());
        ps.setInt(16, venta.getEmitidoPor());

        int affectedRows = ps.executeUpdate();
        if (affectedRows > 0) {
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                venta.setId(rs.getInt(1));
                System.out.println("Venta insertada con ID: " + venta.getId());
            }
            return true;
        }

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}


public boolean update(Venta venta) {
    String sql = "UPDATE ventas SET pv_id=?, numero_poliza=?, placa=?, aseguradora=?, tipo_vehiculo=?, canal=?, prima=?, comision_pv=?, comision_empresa=?, estado=?, pdf_url=?, fecha_emision=?, fecha_vencimiento=?, fecha_pago=?, voucher_id=?, observaciones=?, emitido_por=? WHERE id=?";

    try (Connection conn = DatabaseConnection.getInstance().getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, venta.getPvId());
        ps.setString(2, venta.getNumeroPoliza());
        ps.setString(3, venta.getPlaca());
        ps.setString(4, venta.getAseguradora());
        ps.setString(5, venta.getTipoVehiculo());
        ps.setString(6, venta.getCanal());
        ps.setDouble(7, venta.getPrima());
        ps.setDouble(8, venta.getComisionPV());
        ps.setDouble(9, venta.getComisionEmpresa());
        ps.setString(10, venta.getEstado());
        ps.setString(11, venta.getPdfUrl());

        if (venta.getFechaEmision() != null) {
            ps.setTimestamp(12, new Timestamp(venta.getFechaEmision().getTime()));
        } else {
            ps.setTimestamp(12, new Timestamp(System.currentTimeMillis()));
        }

        if (venta.getFechaVencimiento() != null) {
            ps.setDate(13, new java.sql.Date(venta.getFechaVencimiento().getTime()));
        } else {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(venta.getFechaEmision() != null ? venta.getFechaEmision() : new java.util.Date());
            cal.add(java.util.Calendar.YEAR, 1);
            ps.setDate(13, new java.sql.Date(cal.getTimeInMillis()));
        }

        if (venta.getFechaPago() != null) {
            ps.setTimestamp(14, new Timestamp(venta.getFechaPago().getTime()));
        } else {
            ps.setNull(14, Types.TIMESTAMP);
        }

        if (venta.getVoucherId() > 0) {
            ps.setInt(15, venta.getVoucherId());
        } else {
            ps.setNull(15, Types.INTEGER);
        }

        ps.setString(16, venta.getObservaciones());
        ps.setInt(17, venta.getEmitidoPor());
        ps.setInt(18, venta.getId());

        return ps.executeUpdate() > 0;

    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}


    public Venta findById(int id) {
        String sql = "SELECT v.*, pv.nombre as pv_nombre FROM ventas v LEFT JOIN punto_venta pv ON v.pv_id = pv.id WHERE v.id=?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToVenta(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Venta> findByEstado(String estado) {
        List<Venta> lista = new ArrayList<>();
        String sql = "SELECT v.*, pv.nombre as pv_nombre FROM ventas v LEFT JOIN punto_venta pv ON v.pv_id = pv.id WHERE v.estado=? ORDER BY v.fecha_emision DESC";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, estado);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                lista.add(mapResultSetToVenta(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    public List<Venta> findByPvId(int pvId) {
        List<Venta> lista = new ArrayList<>();
        String sql = "SELECT v.*, pv.nombre as pv_nombre FROM ventas v LEFT JOIN punto_venta pv ON v.pv_id = pv.id WHERE v.pv_id=? ORDER BY v.fecha_emision DESC";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, pvId);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                lista.add(mapResultSetToVenta(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    public List<Venta> findVentasSinPagar() {
        List<Venta> lista = new ArrayList<>();
        String sql = "SELECT v.*, pv.nombre as pv_nombre FROM ventas v LEFT JOIN punto_venta pv ON v.pv_id = pv.id WHERE v.estado IN (?, ?) ORDER BY v.fecha_emision ASC";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, Venta.ESTADO_EMITIDO);
            ps.setString(2, Venta.ESTADO_ESPERANDO_PAGO);
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                lista.add(mapResultSetToVenta(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    
public boolean marcarComoPagada(int ventaId, int voucherId) {
    String sql = "UPDATE ventas SET estado=?, fecha_pago=?, voucher_id=? WHERE id=?";
    try (Connection conn = DatabaseConnection.getInstance().getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        
        ps.setString(1, Venta.ESTADO_PAGADO);
        ps.setTimestamp(2, new Timestamp(System.currentTimeMillis()));
        ps.setInt(3, voucherId);
        ps.setInt(4, ventaId);
        
        return ps.executeUpdate() > 0;
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}

    
 private Venta mapResultSetToVenta(ResultSet rs) throws SQLException {
    Venta venta = new Venta();
    venta.setId(rs.getInt("id"));
    venta.setPvId(rs.getInt("pv_id"));
    venta.setPvNombre(rs.getString("pv_nombre"));
    venta.setNumeroPoliza(rs.getString("numero_poliza"));
    venta.setPlaca(rs.getString("placa"));
    venta.setAseguradora(rs.getString("aseguradora"));
    venta.setTipoVehiculo(rs.getString("tipo_vehiculo"));
    venta.setCanal(rs.getString("canal"));
    venta.setPrima(rs.getDouble("prima"));
    venta.setComisionPV(rs.getDouble("comision_pv"));
    venta.setComisionEmpresa(rs.getDouble("comision_empresa"));
    venta.setEstado(rs.getString("estado"));
    venta.setPdfUrl(rs.getString("pdf_url"));
    venta.setFechaEmision(rs.getTimestamp("fecha_emision"));

    Date fechaVenc = rs.getDate("fecha_vencimiento");
    if (fechaVenc != null) {
        venta.setFechaVencimiento(fechaVenc);
    }

    Timestamp fechaPago = rs.getTimestamp("fecha_pago");
    if (fechaPago != null) {
        venta.setFechaPago(fechaPago);
    }

    int voucherId = rs.getInt("voucher_id");
    if (!rs.wasNull()) {
        venta.setVoucherId(voucherId);
    }

    venta.setObservaciones(rs.getString("observaciones"));
    venta.setEmitidoPor(rs.getInt("emitido_por"));

    return venta;
}

    // Buscar ventas por período, estado y aseguradora (usa idx_ventas_fecha_estado_aseg)
public List<Venta> findByPeriodoEstadoAseguradora(Date inicio, Date fin, String estado, String aseguradora) {
    List<Venta> lista = new ArrayList<>();
    String sql = "SELECT v.*, pv.nombre as pv_nombre " +
                 "FROM ventas v LEFT JOIN punto_venta pv ON v.pv_id = pv.id " +
                 "WHERE v.fecha_emision BETWEEN ? AND ? AND v.estado=? AND v.aseguradora=? " +
                 "ORDER BY v.fecha_emision DESC";
    try (Connection conn = DatabaseConnection.getInstance().getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setTimestamp(1, new Timestamp(inicio.getTime()));
        ps.setTimestamp(2, new Timestamp(fin.getTime()));
        ps.setString(3, estado);
        ps.setString(4, aseguradora);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            lista.add(mapResultSetToVenta(rs));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return lista;
}

// Buscar ventas vencidas (usa idx_ventas_vencimiento_estado)
public List<Venta> findVentasVencidas() {
    List<Venta> lista = new ArrayList<>();
    String sql = "SELECT v.*, pv.nombre as pv_nombre " +
                 "FROM ventas v LEFT JOIN punto_venta pv ON v.pv_id = pv.id " +
                 "WHERE v.fecha_vencimiento < NOW() AND v.estado=? " +
                 "ORDER BY v.fecha_vencimiento ASC";
    try (Connection conn = DatabaseConnection.getInstance().getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, Venta.ESTADO_ESPERANDO_PAGO);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            lista.add(mapResultSetToVenta(rs));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return lista;
}


}