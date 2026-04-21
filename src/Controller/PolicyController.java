package Controller;

import Modelo.Entidades.PuntoVenta;
import Modelo.Entidades.Venta;
import Services.VentaService;
import Utils.PDFExtractor;
import Utils.TelegramSender;
import java.io.File;
import java.util.List;

public class PolicyController {
    private VentaService ventaService;

    public PolicyController() {
        this.ventaService = new VentaService();
    }

public Venta procesarPDFSOAT(File pdfFile, int pvId, int usuarioId) {
    PDFExtractor extractor = new PDFExtractor();
    Venta venta = extractor.extraerDatosSOAT(pdfFile);

    if (venta == null) {
        venta = new Venta();
        venta.setPdfUrl(pdfFile.getAbsolutePath());
        venta.setPlaca("DESCONOCIDO");
        venta.setAseguradora("Otra");
        venta.setTipoVehiculo("Otro");
        venta.setPrima(0.0);
    }

    if (venta.getPlaca() == null) venta.setPlaca("DESCONOCIDO");
    if (venta.getAseguradora() == null) venta.setAseguradora("Otra");
    if (venta.getTipoVehiculo() == null) venta.setTipoVehiculo("Otro");
    else venta.setTipoVehiculo(normalizarTipoVehiculo(venta.getTipoVehiculo()));

    if (venta.getPrima() == 0) venta.setPrima(0.0);
    if (venta.getCanal() == null) venta.setCanal("Particular");

    // ✅ Calcular comisiones
    double comisionPV = ventaService.calcularComision(venta);
    venta.setComisionPV(comisionPV);
    double comisionEmpresa = venta.getPrima() - comisionPV;
    venta.setComisionEmpresa(comisionEmpresa);

    // ✅ Calcular fecha de vencimiento (1 año después de emisión)
    if (venta.getFechaEmision() == null) {
        venta.setFechaEmision(new java.util.Date());
    }
    java.util.Calendar cal = java.util.Calendar.getInstance();
    cal.setTime(venta.getFechaEmision());
    cal.add(java.util.Calendar.YEAR, 1);
    venta.setFechaVencimiento(cal.getTime());

    // ✅ Asignar Punto de Venta y estado inicial
    venta.setPvId(pvId);
    venta.setEstado(Venta.ESTADO_ESPERANDO_PAGO);

    // ✅ Asignar usuario logueado
    venta.setEmitidoPor(usuarioId);

    // Guardar en BD
    ventaService.guardarVenta(venta);

    return venta;
}

    public double calcularComision(Venta venta) {
        return ventaService.calcularComision(venta);
    }

    public boolean guardarVenta(Venta venta) {
        return ventaService.guardarVenta(venta);
    }

    public List<Venta> getVentasByPV(int pvId) {
        return ventaService.getVentasByPvId(pvId);
    }

public boolean enviarSOATTelegram(Venta venta, PuntoVenta pv) {
    // Si aún no está guardada, la guardamos
    if (venta.getId() == 0) {
        venta.setPvId(pv.getId());
        venta.setEstado(Venta.ESTADO_ESPERANDO_PAGO);
        ventaService.guardarVenta(venta);
    }

    TelegramSender sender = TelegramSender.getInstance();
    String mensaje = "SOAT emitido\nPlaca: " + venta.getPlaca() +
                     "\nPrima: S/ " + venta.getPrima();

    sender.enviarMensajeAsync(pv.getTelegramChatId(), mensaje);
    return true;
}


    public boolean validarPago(Venta venta) {
        if (venta == null) return false;
        venta.setEstado(Venta.ESTADO_PAGADO);
        venta.setFechaPago(new java.util.Date());
        return ventaService.guardarVenta(venta);
    }
    private String normalizarTipoVehiculo(String tipo) {
    if (tipo == null) return "Otro";
    tipo = tipo.toLowerCase();

    if (tipo.contains("auto")) return "Auto";
    if (tipo.contains("camioneta")) return "Camioneta";
    if (tipo.contains("moto")) return "Moto";
    if (tipo.contains("camion")) return "Camión";
    if (tipo.contains("bus")) return "Bus";
    return "Otro";
}

}
