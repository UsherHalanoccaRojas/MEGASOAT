package  Controller;


import Modelo.DAO.PuntoVentaDAO;
import Modelo.Entidades.PuntoVenta;
import Services.PuntoVentaService;

import java.util.List;

public class PVController {
    
    private PuntoVentaDAO pvDAO;
    private PuntoVentaService pvService; 
    
    
    public PVController() {
        this.pvDAO = new PuntoVentaDAO();
        this.pvService = new PuntoVentaService();
    }
    
    public List<PuntoVenta> listarTodos() {
        return pvDAO.findAll();
    }
    
    public PuntoVenta buscarPorId(int id) {
        return pvDAO.findById(id);
    }
    
public boolean registrarPV(PuntoVenta pv) {
    // Obtener cuántos puntos de venta existen ya en la ciudad
    int total = pvDAO.findByCiudad(pv.getCiudad()).size() + 1;

    // Generar código legible: PV-CIUDAD-XXX
    String codigoGenerado = "PV-" 
        + pv.getCiudad().toUpperCase() 
        + "-" 
        + String.format("%03d", total);

    pv.setCodigo(codigoGenerado);

    return pvDAO.insert(pv);
}

    
    public boolean actualizarPV(PuntoVenta pv) {
        return pvDAO.update(pv);
    }
    
    public boolean eliminarPV(int id) {
        return pvDAO.delete(id);
    }
    
    public List<PuntoVenta> buscarPorCiudad(String ciudad) {
        return pvDAO.findByCiudad(ciudad);
    }
    public boolean cambiarEstadoPV(int id, boolean activo) {
    return pvService.cambiarEstado(id, activo);
}

}
