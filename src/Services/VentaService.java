package Services;

import Modelo.DAO.VentaDAO;
import Modelo.Entidades.Venta;
import java.util.List;

public class VentaService {
    private final VentaDAO ventaDAO;

    public VentaService() {
        this.ventaDAO = new VentaDAO();
    }

    // Calcular comisión (ejemplo: 15%)
    public double calcularComision(Venta venta) {
        return venta.getPrima() * 0.15;
    }

    // Guardar venta en BD
    public boolean guardarVenta(Venta venta) {
        return ventaDAO.insert(venta);
    }

    // Obtener ventas pendientes de pago (Emitido o Esperando Pago)
    public List<Venta> getVentasSinPagar() {
        return ventaDAO.findVentasSinPagar();
    }

    // Buscar venta por ID
    public Venta getVentaById(int id) {
        return ventaDAO.findById(id);
    }

    // Buscar ventas por estado
    public List<Venta> getVentasByEstado(String estado) {
        return ventaDAO.findByEstado(estado);
    }

    // Buscar ventas por Punto de Venta
    public List<Venta> getVentasByPvId(int pvId) {
        return ventaDAO.findByPvId(pvId);
    }

    // Marcar venta como pagada
    public boolean marcarComoPagada(int ventaId, int voucherId) {
        return ventaDAO.marcarComoPagada(ventaId, voucherId);
    }
}
