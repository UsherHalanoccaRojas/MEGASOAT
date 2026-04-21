
package Modelo.DAO;
import Modelo.Entidades.Voucher;
import Utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VoucherDAO {
    
public boolean insert(Voucher voucher) {
    String sql = "INSERT INTO vouchers (operation_number, banco, monto, tipo, registrado_por, observaciones, fecha_operacion) VALUES (?, ?, ?, ?, ?, ?, ?)";
    
    try (Connection conn = DatabaseConnection.getInstance().getConnection();
         PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
        
        ps.setString(1, voucher.getOperationNumber());
        ps.setString(2, voucher.getBanco());
        ps.setDouble(3, voucher.getMonto());
        ps.setString(4, voucher.getTipo());
        ps.setInt(5, voucher.getRegistradoPor());
        ps.setString(6, voucher.getObservaciones());
        ps.setDate(7, new java.sql.Date(voucher.getFechaOperacion().getTime())); // <-- nuevo campo
        
        int affectedRows = ps.executeUpdate();
        
        if (affectedRows > 0) {
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) {
                voucher.setId(rs.getInt(1));
            }
            return true;
        }
        
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}

    
    public boolean existsOperationNumber(String operationNumber) {
        String sql = "SELECT COUNT(*) FROM vouchers WHERE operation_number = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, operationNumber);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt(1) > 0;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public Voucher findByOperationNumber(String operationNumber) {
        String sql = "SELECT * FROM vouchers WHERE operation_number = ?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, operationNumber);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return mapResultSetToVoucher(rs);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Voucher> findAll() {
        List<Voucher> lista = new ArrayList<>();
        String sql = "SELECT * FROM vouchers ORDER BY fecha_registro DESC";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                lista.add(mapResultSetToVoucher(rs));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    
public boolean asignarVentaVoucher(int ventaId, int voucherId, int usuarioId) {
    String sql = "INSERT INTO venta_voucher (venta_id, voucher_id, asignado_por) VALUES (?, ?, ?)";
    
    try (Connection conn = DatabaseConnection.getInstance().getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        
        ps.setInt(1, ventaId);
        ps.setInt(2, voucherId);
        ps.setInt(3, usuarioId); // <-- nuevo campo
        
        return ps.executeUpdate() > 0;
        
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return false;
}

    
    private Voucher mapResultSetToVoucher(ResultSet rs) throws SQLException {
        Voucher voucher = new Voucher();
        voucher.setId(rs.getInt("id"));
        voucher.setOperationNumber(rs.getString("operation_number"));
        voucher.setBanco(rs.getString("banco"));
        voucher.setMonto(rs.getDouble("monto"));
        voucher.setTipo(rs.getString("tipo"));
        voucher.setFechaRegistro(rs.getTimestamp("fecha_registro"));
        voucher.setRegistradoPor(rs.getInt("registrado_por"));
        voucher.setObservaciones(rs.getString("observaciones"));
        
        return voucher;
    }
    
    // Buscar vouchers pendientes de validación (usa idx_vouchers_validacion)
public List<Voucher> findPendientesValidacion() {
    List<Voucher> lista = new ArrayList<>();
    String sql = "SELECT * FROM vouchers WHERE validado=0 ORDER BY fecha_operacion ASC";
    try (Connection conn = DatabaseConnection.getInstance().getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            lista.add(mapResultSetToVoucher(rs));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return lista;
}

// Buscar vouchers por banco y tipo (usa idx_vouchers_banco_tipo)
public List<Voucher> findByBancoTipo(String banco, String tipo) {
    List<Voucher> lista = new ArrayList<>();
    String sql = "SELECT * FROM vouchers WHERE banco=? AND tipo=? ORDER BY fecha_operacion DESC";
    try (Connection conn = DatabaseConnection.getInstance().getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, banco);
        ps.setString(2, tipo);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            lista.add(mapResultSetToVoucher(rs));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return lista;
}

// Buscar vouchers por rango de montos y fecha (usa idx_vouchers_monto_fecha)
public List<Voucher> findByMontoFecha(double minMonto, double maxMonto, Date inicio, Date fin) {
    List<Voucher> lista = new ArrayList<>();
    String sql = "SELECT * FROM vouchers WHERE monto BETWEEN ? AND ? AND fecha_operacion BETWEEN ? AND ? ORDER BY fecha_operacion DESC";
    try (Connection conn = DatabaseConnection.getInstance().getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setDouble(1, minMonto);
        ps.setDouble(2, maxMonto);
        ps.setTimestamp(3, new Timestamp(inicio.getTime()));
        ps.setTimestamp(4, new Timestamp(fin.getTime()));
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            lista.add(mapResultSetToVoucher(rs));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return lista;
}

}
