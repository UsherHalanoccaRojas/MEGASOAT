package Controller;

import Modelo.DAO.MensajeDAO;
import Modelo.Entidades.Conversacion;
import Modelo.Entidades.Mensaje;
import Modelo.Entidades.PuntoVenta;
import Modelo.Entidades.Venta;
import Services.ChatService;
import Services.PuntoVentaService;
import Services.VentaService;
import Utils.PDFExtractor;
import Utils.TelegramSender;
import View.Panels.BandejaEntradaPanel;
import java.io.File;
import java.util.List;

public class ChatController {
    private ChatService chatService;
    private VentaService ventaService;
    private PDFExtractor pdfExtractor;
    private TelegramSender telegramSender;
    
    public ChatController() {
        this.chatService = new ChatService();
        this.ventaService = new VentaService();
        this.pdfExtractor = new PDFExtractor();
         this.telegramSender = TelegramSender.getInstance();
    }
    
    public List<Conversacion> getTodasConversaciones() {
        return chatService.getTodasConversaciones();
    }
    
    public Conversacion getConversacion(int pvId) {
        return chatService.getConversacionByPvId(pvId);
    }
    
    public Venta procesarPDFSOAT(File pdfFile) {
        return pdfExtractor.extraerDatosSOAT(pdfFile);
    }
    
    public double calcularComision(Venta venta) {
        return ventaService.calcularComision(venta);
    }
    
    public boolean guardarVenta(Venta venta) {
        return ventaService.guardarVenta(venta);
    }
    
    // Enviar SOAT por Telegram
    public boolean enviarPorTelegram(Venta venta, PuntoVenta pv) {
        String mensaje = "✅ *SOAT EMITIDO*\n\n" +
                         "Placa: " + venta.getPlaca() + "\n" +
                         "Aseguradora: " + venta.getAseguradora() + "\n" +
                         "Prima: S/ " + String.format("%.2f", venta.getPrima()) + "\n\n" +
                         "Adjunto encontrarás el SOAT. Por favor, realiza el pago y envíame el voucher.";
        
        // Enviar solo texto (si quieres enviar PDF, debes usar Telegram API para documentos)
       telegramSender.enviarMensajeAsync(pv.getTelegramChatId(), mensaje);
return true;

    }
    
    public boolean actualizarEstado(Conversacion conversacion, String nuevoEstado) {
        return chatService.actualizarEstadoPV(conversacion.getPuntoVenta().getId(), nuevoEstado);
    }
    
public boolean enviarMensaje(int pvId, String mensaje) {
    boolean ok = chatService.enviarMensaje(pvId, mensaje, Mensaje.TIPO_TEXTO);

    if (ok) {
        Conversacion conv = chatService.getConversacionByPvId(pvId);
        PuntoVenta pv = conv.getPuntoVenta();

        if (pv.getTelegramChatId() != null) {
            telegramSender.enviarMensajeAsync(
                pv.getTelegramChatId(),
                mensaje // ✅ ya no anteponemos "Cliente escribió"
            );
        }
    }

    return ok;
}

    
    public int getTotalNoLeidos() {
        return chatService.getTotalMensajesNoLeidos();
    }
    
    public boolean marcarComoLeida(int pvId) {
        return chatService.marcarConversacionComoLeida(pvId);
    }
    public List<Mensaje> getMensajesByPvId(int pvId) {
        return chatService.getMensajesByPvId(pvId);
    }
    public int countUnreadByPvId(int pvId) {
    MensajeDAO dao = new MensajeDAO();
    return dao.countUnreadByPvId(pvId);
}
public int getTotalMensajesNoLeidos() {
    int total = 0;
    List<Conversacion> conversaciones = getTodasConversaciones();
    for (Conversacion conv : conversaciones) {
        total += countUnreadByPvId(conv.getPuntoVenta().getId());
    }
    return total;
}

public Conversacion crearConversacion(PuntoVenta pv) {
    Conversacion conv = new Conversacion();
    conv.setPuntoVenta(pv);
    conv.setEstadoActual("Nuevo");

    // Guardar en BD usando ChatService
    chatService.crearConversacion(conv);

    // Mensaje inicial
    Mensaje msg = new Mensaje();
    msg.setPvId(pv.getId());
    msg.setContenido("Conversación iniciada para " + pv.getNombre());
    msg.setDireccion(Mensaje.DIRECCION_ENTRANTE);
    MensajeDAO dao = new MensajeDAO();
    dao.insert(msg);

    return conv;
}
// En tu Controller
public void insertarPV(PuntoVenta pv, BandejaEntradaPanel panel) {
    PuntoVentaService service = new PuntoVentaService();
    if (service.insertar(pv)) {
        Conversacion conv = crearConversacion(pv);
        panel.agregarConversacion(conv);
    }
}

}

