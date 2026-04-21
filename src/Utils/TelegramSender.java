package Utils;

import Modelo.DAO.MensajeDAO;
import Modelo.Entidades.Mensaje;
import Modelo.Entidades.PuntoVenta;
import Services.PuntoVentaService;
import View.Panels.BandejaEntradaPanel;
import java.util.ArrayList;
import java.util.List;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class TelegramSender extends TelegramLongPollingBot {

    private static final String BOT_TOKEN = "8537313937:AAHZntGSbo1nwpeDj3Fc696LEW4262SPZr8";
    private static final String BOT_USERNAME = "MSOAT_bot";

    private PuntoVentaService puntoVentaService = new PuntoVentaService();
    private MensajeDAO mensajeDAO = new MensajeDAO();
    private List<Mensaje> cacheMensajes = new ArrayList<>();
    // Singleton
    private static TelegramSender instance;
    private static boolean botRegistrado = false;
    private TelegramSender() {
    try {
        if (!botRegistrado) {
            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
            botRegistrado = true;
        }
    } catch (TelegramApiException e) {
        e.printStackTrace();
    }
}

    public static TelegramSender getInstance() {
        if (instance == null) {
            instance = new TelegramSender();
        }
        return instance;
    }

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

@Override
public void onUpdateReceived(Update update) {
    if (update.hasMessage() && update.getMessage().hasText()) {
        String chatId = update.getMessage().getChatId().toString();
        String texto = update.getMessage().getText().trim();

        PuntoVenta pv = puntoVentaService.buscarPorChatId(chatId);

        if (pv != null) {
            // ✅ Guardar mensaje en BD
            Mensaje mensaje = new Mensaje();
            mensaje.setPvId(pv.getId());
            mensaje.setDireccion(Mensaje.DIRECCION_ENTRANTE);
            mensaje.setTipo(Mensaje.TIPO_TEXTO);
            mensaje.setContenido(texto);
            mensaje.setLeido(false);

            mensajeDAO.insert(mensaje);

        } else {
          
            PuntoVenta pvTelefono = puntoVentaService.buscarPorTelefono(texto);
            if (pvTelefono != null) {
                puntoVentaService.actualizarChatId(pvTelefono.getId(), chatId);
                enviarMensajeAsync(chatId, "✅ Número validado, puedes continuar con la conversación.");
            } else {
                enviarMensajeAsync(chatId, "❌ Tu número no ha sido registrado. Por favor, ingresa un número válido.");
            }
        }
    }
}

public void enviarMensajeAsync(String chatId, String texto) {
    SwingWorker<Void, Void> worker = new SwingWorker<>() {
        @Override
        protected Void doInBackground() {
            try {
                SendMessage message = new SendMessage(chatId, texto);
                TelegramSender.this.execute(message); 
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
            return null;
        }
    };
    worker.execute(); 
}

    private BandejaEntradaPanel bandejaPanel;

public void setBandejaPanel(BandejaEntradaPanel panel) {
    this.bandejaPanel = panel;
}

    public void cargarHistorial(int pvId) {
        cacheMensajes = mensajeDAO.findByPvId(pvId);
        if (bandejaPanel != null) {
            bandejaPanel.refrescarMensajes(cacheMensajes);
        }
    }

    public void guardarHistorial() {
        for (Mensaje m : cacheMensajes) {
            if (m.getId() == 0) { 
                mensajeDAO.insert(m);
            }
        }
    }

public BandejaEntradaPanel getBandejaPanel() {
    return bandejaPanel;
}

}
