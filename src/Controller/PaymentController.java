package  Controller;


import Modelo.Entidades.Venta;
import Modelo.Entidades.Voucher;
import Services.VentaService;
import Services.VoucherService;
import java.util.List;

public class PaymentController {
    private VoucherService voucherService;
    private VentaService ventaService;
    
    public PaymentController() {
        this.voucherService = new VoucherService();
        this.ventaService = new VentaService();
    }
    
    public List<Venta> getVentasPendientes() {
       return ventaService.getVentasSinPagar();
    }
    
    public boolean existeNumeroOperacion(String operationNumber) {
        return voucherService.existeNumeroOperacion(operationNumber);
    }
    
    public Voucher registrarVoucher(String operationNumber, String banco, double monto, 
                                     String tipo, int usuarioId, String observaciones) {
        Voucher voucher = new Voucher();
        voucher.setOperationNumber(operationNumber);
        voucher.setBanco(banco);
        voucher.setMonto(monto);
        voucher.setTipo(tipo);
        voucher.setRegistradoPor(usuarioId);
        voucher.setObservaciones(observaciones);

        // 👉 Aquí va la PRIMERA opción:
        voucher.setFechaOperacion(new java.util.Date()); // asigna la fecha actual

        if (voucherService.registrarVoucher(voucher)) {
            return voucher;
        }
        return null;
    }

    
public boolean asignarVoucherAVenta(Venta venta, Voucher voucher, int usuarioId) {
    List<Integer> ventasIds = List.of(venta.getId());
    return voucherService.conciliarPagoMultiple(ventasIds, voucher.getId(), usuarioId);
}

public boolean conciliarMultiplesVentas(List<Integer> ventasIds, Voucher voucher, int usuarioId) {
    return voucherService.conciliarPagoMultiple(ventasIds, voucher.getId(), usuarioId);
}

public boolean conciliarMultiplesVentasConNuevoVoucher(List<Integer> ventasIds, 
                                                        String operationNumber, 
                                                        String banco, 
                                                        double monto, 
                                                        String tipo,
                                                        int usuarioId) {
    Voucher voucher = new Voucher();
    voucher.setOperationNumber(operationNumber);
    voucher.setBanco(banco);
    voucher.setMonto(monto);
    voucher.setTipo(tipo);
    voucher.setRegistradoPor(usuarioId);
    voucher.setFechaOperacion(new java.util.Date());

    return voucherService.conciliarPagoMultipleConNuevoVoucher(ventasIds, voucher, usuarioId);
}


    
    public Voucher buscarVoucherPorOperacion(String operationNumber) {
        return voucherService.buscarVoucherPorOperacion(operationNumber);
    }
}
