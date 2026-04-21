package Services;

import Modelo.DAO.ConversacionDAO;
import Modelo.DAO.MensajeDAO;
import Modelo.DAO.PuntoVentaDAO;
import Modelo.DAO.VentaDAO;
import Modelo.Entidades.Conversacion;
import Modelo.Entidades.Mensaje;
import Modelo.Entidades.PuntoVenta;
import Modelo.Entidades.Venta;
import Utils.TelegramSender;
import View.Panels.BandejaEntradaPanel;
import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class ChatService {
    private MensajeDAO mensajeDAO;
    private PuntoVentaDAO pvDAO;
    private VentaService ventaService;
    private BandejaEntradaPanel bandejaPanel;

    public void setBandejaPanel(BandejaEntradaPanel panel) {
        this.bandejaPanel = panel;
    }

    public BandejaEntradaPanel getBandejaPanel() {
        return bandejaPanel;
    }
    
    public ChatService() {
        this.mensajeDAO = new MensajeDAO();
        this.pvDAO = new PuntoVentaDAO();
        this.ventaService = new VentaService();
    }
    public List<Mensaje> getMensajesByPvId(int pvId) {
        return mensajeDAO.findByPvId(pvId);
    }
    public List<Conversacion> getTodasConversaciones() {
        List<PuntoVenta> puntosVenta = pvDAO.findAll();
        List<Conversacion> conversaciones = new ArrayList<>();
        
        for (PuntoVenta pv : puntosVenta) {
            Conversacion conv = new Conversacion(pv);
            conv.setMensajes(mensajeDAO.findByPvId(pv.getId()));
            conv.setNoLeidos(mensajeDAO.countUnreadByPvId(pv.getId()));
            
            List<Venta> ventas = ventaService.getVentasByPvId(pv.getId());
            if (!ventas.isEmpty()) {
                conv.setEstadoActual(ventas.get(0).getEstado());
            }
            
            conversaciones.add(conv);
        }
        
        return conversaciones;
    }
    
    public Conversacion getConversacionByPvId(int pvId) {
        PuntoVenta pv = pvDAO.findById(pvId);
        if (pv == null) return null;
        
        Conversacion conv = new Conversacion(pv);
        conv.setMensajes(mensajeDAO.findByPvId(pvId));
        conv.setNoLeidos(mensajeDAO.countUnreadByPvId(pvId));
        
        List<Venta> ventas = ventaService.getVentasByPvId(pvId);
        if (!ventas.isEmpty()) {
            conv.setEstadoActual(ventas.get(0).getEstado());
        }
        
        return conv;
    }
    
public boolean enviarMensaje(int pvId, String mensaje, String tipo) {
    Mensaje msg = new Mensaje();
    msg.setPvId(pvId);
    msg.setTipo(tipo);
    msg.setContenido(mensaje);
    msg.setDireccion(Mensaje.DIRECCION_SALIENTE);
    msg.setLeido(true);

    // ✅ Solo guarda en BD, no envía a Telegram
    return mensajeDAO.insert(msg);
}

    
    public boolean recibirMensaje(int pvId, String mensaje, String tipo) {
        Mensaje msg = new Mensaje();
        msg.setPvId(pvId);
        msg.setTipo(tipo);
        msg.setContenido(mensaje);
        msg.setDireccion(Mensaje.DIRECCION_ENTRANTE);
        msg.setLeido(false);
        
        return mensajeDAO.insert(msg);
    }
    
    public boolean notificarPago(int pvId, String placa, double monto) {
        PuntoVenta pv = pvDAO.findById(pvId);
        if (pv == null) return false;

        if (pv.getTelegramChatId() != null) {
        TelegramSender sender = TelegramSender.getInstance();

            String mensaje = "✅ Pago confirmado\nPlaca: " + placa + "\nMonto: S/ " + monto;
            sender.enviarMensajeAsync(pv.getTelegramChatId(), mensaje);
            return true;

        }
        return false;
    }

    public int getTotalMensajesNoLeidos() {
        return mensajeDAO.countTotalUnread();
    }
    
    public boolean marcarConversacionComoLeida(int pvId) {
        return mensajeDAO.marcarTodosComoLeidos(pvId);
    }
    
    public boolean actualizarEstadoPV(int pvId, String nuevoEstado) {
    List<Venta> ventas = ventaService.getVentasByPvId(pvId);
    if (!ventas.isEmpty()) {
        Venta ultimaVenta = ventas.get(0);
        ultimaVenta.setEstado(nuevoEstado);
        boolean ok = new VentaDAO().update(ultimaVenta);

        if (ok && bandejaPanel != null) {
            // ✅ Refrescar toda la bandeja en tiempo real
            bandejaPanel.refrescarConversaciones();
        }
        return ok;
    }
    return false;
}




    public boolean crearConversacion(Conversacion conv) {
    // Aquí llamas al DAO correspondiente para insertar la conversación
    ConversacionDAO dao = new ConversacionDAO();
    return dao.insert(conv);
}

}
