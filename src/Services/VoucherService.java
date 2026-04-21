package Services;



import Modelo.DAO.VentaDAO;
import Modelo.DAO.VoucherDAO;
import Modelo.Entidades.Voucher;
import java.util.List;

public class VoucherService {
    private VoucherDAO voucherDAO;
    private VentaDAO ventaDAO;
    private VentaService ventaService;
    
    public VoucherService() {
        this.voucherDAO = new VoucherDAO();
        this.ventaDAO = new VentaDAO();
        this.ventaService = new VentaService();
    }
    
    public boolean registrarVoucher(Voucher voucher) {
        // Validar que el número de operación no exista (anti-fraude)
        if (voucherDAO.existsOperationNumber(voucher.getOperationNumber())) {
            return false;
        }
        return voucherDAO.insert(voucher);
    }
    
    public boolean existeNumeroOperacion(String operationNumber) {
        return voucherDAO.existsOperationNumber(operationNumber);
    }
    
    public Voucher buscarVoucherPorOperacion(String operationNumber) {
        return voucherDAO.findByOperationNumber(operationNumber);
    }
    
public boolean conciliarPagoMultiple(List<Integer> ventasIds, int voucherId, int usuarioId) {
    boolean allSuccess = true;
    
    for (int ventaId : ventasIds) {
        // Asignar voucher a venta con usuario
        boolean assigned = voucherDAO.asignarVentaVoucher(ventaId, voucherId, usuarioId);
        
        // Marcar venta como pagada
        boolean marked = ventaService.marcarComoPagada(ventaId, voucherId);
        
        if (!assigned || !marked) {
            allSuccess = false;
        }
    }
    
    return allSuccess;
}

    
public boolean conciliarPagoMultipleConNuevoVoucher(List<Integer> ventasIds, Voucher voucher, int usuarioId) {
    if (!registrarVoucher(voucher)) {
        return false;
    }
    return conciliarPagoMultiple(ventasIds, voucher.getId(), usuarioId);
}

    
    public List<Voucher> listarTodosVouchers() {
        return voucherDAO.findAll();
    }
}
