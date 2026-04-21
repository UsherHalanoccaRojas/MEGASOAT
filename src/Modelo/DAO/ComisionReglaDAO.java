package Modelo.DAO;

import Modelo.DAO.ComisionReglaDAO;
import Utils.DatabaseConnection;
import Modelo.Entidades.ComisionRegla;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ComisionReglaDAO {
    
    public boolean insert(ComisionRegla regla) {
        // Si tiene ID, actualizar directamente
        if (regla.getId() > 0) {
            return update(regla);
        }
        
        String sql = "INSERT INTO comision_reglas (aseguradora, tipo_vehiculo, canal, porcentaje, activo) " +
                     "VALUES (?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE porcentaje=VALUES(porcentaje), activo=VALUES(activo)";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            ps.setString(1, regla.getAseguradora());
            ps.setString(2, regla.getTipoVehiculo());
            ps.setString(3, regla.getCanal());
            ps.setDouble(4, regla.getPorcentaje());
            ps.setBoolean(5, regla.isActivo());
            
            ps.executeUpdate();
            ResultSet rs = ps.getGeneratedKeys();
            if (rs.next()) regla.setId(rs.getInt(1));
            return true;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public ComisionRegla findBy(String aseguradora, String tipoVehiculo, String canal) {
        String sql = "SELECT * FROM comision_reglas WHERE aseguradora=? AND tipo_vehiculo=? AND canal=? AND activo=true";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, aseguradora);
            ps.setString(2, tipoVehiculo);
            ps.setString(3, canal);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return mapResultSetToComisionRegla(rs);
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<ComisionRegla> findAll() {
        List<ComisionRegla> lista = new ArrayList<>();
        String sql = "SELECT * FROM comision_reglas ORDER BY aseguradora, tipo_vehiculo";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) lista.add(mapResultSetToComisionRegla(rs));
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
    
    public boolean update(ComisionRegla regla) {
        String sql = "UPDATE comision_reglas SET aseguradora=?, tipo_vehiculo=?, canal=?, porcentaje=?, activo=? WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, regla.getAseguradora());
            ps.setString(2, regla.getTipoVehiculo());
            ps.setString(3, regla.getCanal());
            ps.setDouble(4, regla.getPorcentaje());
            ps.setBoolean(5, regla.isActivo());
            ps.setInt(6, regla.getId());
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    public boolean delete(int id) {
        String sql = "DELETE FROM comision_reglas WHERE id=?";
        
        try (Connection conn = DatabaseConnection.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    
    private ComisionRegla mapResultSetToComisionRegla(ResultSet rs) throws SQLException {
        ComisionRegla regla = new ComisionRegla();
        regla.setId(rs.getInt("id"));
        regla.setAseguradora(rs.getString("aseguradora"));
        regla.setTipoVehiculo(rs.getString("tipo_vehiculo"));
        regla.setCanal(rs.getString("canal"));
        regla.setPorcentaje(rs.getDouble("porcentaje"));
        regla.setActivo(rs.getBoolean("activo"));
        return regla;
    }
    
    // Buscar reglas activas por canal y tipo de vehículo (usa idx_comision_canal_tipo)
public List<ComisionRegla> findByCanalTipo(String canal, String tipoVehiculo) {
    List<ComisionRegla> lista = new ArrayList<>();
    String sql = "SELECT * FROM comision_reglas WHERE canal=? AND tipo_vehiculo=? AND activo=true";
    try (Connection conn = DatabaseConnection.getInstance().getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, canal);
        ps.setString(2, tipoVehiculo);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            lista.add(mapResultSetToComisionRegla(rs));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return lista;
}

// Buscar todas las reglas activas por aseguradora (aprovecha índice en aseguradora si existe)
public List<ComisionRegla> findByAseguradora(String aseguradora) {
    List<ComisionRegla> lista = new ArrayList<>();
    String sql = "SELECT * FROM comision_reglas WHERE aseguradora=? AND activo=true ORDER BY tipo_vehiculo";
    try (Connection conn = DatabaseConnection.getInstance().getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {
        ps.setString(1, aseguradora);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            lista.add(mapResultSetToComisionRegla(rs));
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return lista;
}

}