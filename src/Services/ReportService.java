package Services;


import Utils.DatabaseConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportService {
    
    public Map<String, Object> getReporteDiario(Date fecha) {
        Map<String, Object> reporte = new HashMap<>();
        
        String sql = "SELECT " +
                     "  COUNT(v.id) as total_ventas, " +
                     "  SUM(v.prima) as total_prima, " +
                     "  SUM(v.comision_pv) as total_comisiones, " +
                     "  SUM(CASE WHEN v.estado = 'Pagado' THEN v.prima ELSE 0 END) as total_cobrado " +
                     "FROM venta v " +
                     "WHERE DATE(v.fecha_emision) = DATE(?)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setDate(1, new java.sql.Date(fecha.getTime()));
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                reporte.put("totalVentas", rs.getInt("total_ventas"));
                reporte.put("totalPrima", rs.getDouble("total_prima"));
                reporte.put("totalComisiones", rs.getDouble("total_comisiones"));
                reporte.put("totalCobrado", rs.getDouble("total_cobrado"));
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return reporte;
    }
    
    public List<Map<String, Object>> getReportePorPuntoVenta(Date fechaInicio, Date fechaFin) {
        List<Map<String, Object>> reporte = new ArrayList<>();
        
        String sql = "SELECT " +
                     "  pv.nombre as punto_venta, " +
                     "  pv.ciudad, " +
                     "  COUNT(v.id) as cantidad_ventas, " +
                     "  SUM(v.prima) as total_ventas, " +
                     "  SUM(v.comision_pv) as total_comisiones, " +
                     "  SUM(CASE WHEN v.estado = 'Pagado' THEN v.prima ELSE 0 END) as total_cobrado " +
                     "FROM punto_venta pv " +
                     "LEFT JOIN venta v ON pv.id = v.pv_id " +
                     "WHERE v.fecha_emision BETWEEN ? AND ? " +
                     "GROUP BY pv.id, pv.nombre, pv.ciudad " +
                     "ORDER BY total_ventas DESC";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setDate(1, new java.sql.Date(fechaInicio.getTime()));
            ps.setDate(2, new java.sql.Date(fechaFin.getTime()));
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("puntoVenta", rs.getString("punto_venta"));
                row.put("ciudad", rs.getString("ciudad"));
                row.put("cantidadVentas", rs.getInt("cantidad_ventas"));
                row.put("totalVentas", rs.getDouble("total_ventas"));
                row.put("totalComisiones", rs.getDouble("total_comisiones"));
                row.put("totalCobrado", rs.getDouble("total_cobrado"));
                reporte.add(row);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return reporte;
    }
    
    public List<Map<String, Object>> getReportePorAseguradora(Date fechaInicio, Date fechaFin) {
        List<Map<String, Object>> reporte = new ArrayList<>();
        
        String sql = "SELECT " +
                     "  aseguradora, " +
                     "  COUNT(*) as cantidad, " +
                     "  SUM(prima) as total_prima, " +
                     "  SUM(comision_pv) as total_comisiones " +
                     "FROM venta " +
                     "WHERE fecha_emision BETWEEN ? AND ? " +
                     "GROUP BY aseguradora " +
                     "ORDER BY total_prima DESC";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setDate(1, new java.sql.Date(fechaInicio.getTime()));
            ps.setDate(2, new java.sql.Date(fechaFin.getTime()));
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("aseguradora", rs.getString("aseguradora"));
                row.put("cantidad", rs.getInt("cantidad"));
                row.put("totalPrima", rs.getDouble("total_prima"));
                row.put("totalComisiones", rs.getDouble("total_comisiones"));
                reporte.add(row);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return reporte;
    }
    
    public List<Map<String, Object>> getConciliacionPagos(Date fecha) {
        List<Map<String, Object>> reporte = new ArrayList<>();
        
        String sql = "SELECT " +
                     "  v.operation_number, " +
                     "  v.banco, " +
                     "  v.tipo, " +
                     "  v.monto, " +
                     "  COUNT(vv.venta_id) as ventas_pagadas, " +
                     "  GROUP_CONCAT(DISTINCT pv.nombre) as puntos_venta " +
                     "FROM voucher v " +
                     "LEFT JOIN venta_voucher vv ON v.id = vv.voucher_id " +
                     "LEFT JOIN venta vt ON vv.venta_id = vt.id " +
                     "LEFT JOIN punto_venta pv ON vt.pv_id = pv.id " +
                     "WHERE DATE(v.fecha_registro) = DATE(?) " +
                     "GROUP BY v.id " +
                     "ORDER BY v.fecha_registro DESC";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setDate(1, new java.sql.Date(fecha.getTime()));
            ResultSet rs = ps.executeQuery();
            
            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                row.put("operationNumber", rs.getString("operation_number"));
                row.put("banco", rs.getString("banco"));
                row.put("tipo", rs.getString("tipo"));
                row.put("monto", rs.getDouble("monto"));
                row.put("ventasPagadas", rs.getInt("ventas_pagadas"));
                row.put("puntosVenta", rs.getString("puntos_venta"));
                reporte.add(row);
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return reporte;
    }
}
