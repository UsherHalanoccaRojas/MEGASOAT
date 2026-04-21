
package View.Panels;

import Controller.ChatController;
import Controller.PolicyController;
import Modelo.Entidades.Conversacion;
import Modelo.Entidades.Mensaje;
import Modelo.Entidades.PuntoVenta;
import Modelo.Entidades.Venta;
import Services.ChatService;
import Utils.ConnectionStatus;
import Utils.FileDragAndDrop;
import Utils.SesionActual;
import Utils.UITheme;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

public class BandejaEntradaPanel extends javax.swing.JPanel {
    // Lista de conversaciones
    private JList<Conversacion> listConversaciones;
    private DefaultListModel<Conversacion> listModel;

    // Chat Telegram
    private JPanel chatPanel;
    private JScrollPane scrollChat;
    private JTextField txtMensaje;
    private JButton btnEnviar;
    private JButton btnEnviarSOAT;
    private JButton btnNotificarPago;

    // Panel PDF
    private JPanel panelPDFDropZone;
    private JTextField txtPlaca;
    private JTextField txtAseguradora;
    private JTextField txtPrima;
    private JTextField txtComisionPV;
    private JButton btnCopiarDatos;

    // Info cliente
    private JLabel lblClienteNombre;
    private JLabel lblEstado;
    private JLabel lblUbicacion;
    
    // Controladores
    private ChatController chatController;
    private PolicyController policyController;
    private Conversacion conversacionActual;
    
    private JLabel lblResumen;
private Timer resumenTimer;

    public BandejaEntradaPanel() {
        initControllers();
        initComponentsManual();
        setupEvents();
        setupDragAndDrop();
        cargarConversaciones();
    }
    
private void initComponentsManual() {
    
    setLayout(new BorderLayout());

    JPanel panelResumen = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelResumen.setBackground(UITheme.BG_CARD);

    lblResumen = new JLabel("📩 No leídos: 0 | --/--/---- --:-- | Conectado");
    lblResumen.setFont(UITheme.FONT_BODY);
    lblResumen.setForeground(UITheme.TEXT_PRIMARY);

    panelResumen.add(lblResumen);
    add(panelResumen, BorderLayout.NORTH);

    resumenTimer = new Timer(1000, e -> actualizarResumen());
    resumenTimer.start();
    // Lista izquierda
    listModel = new DefaultListModel<>();
    listConversaciones = new JList<>(listModel);
    listConversaciones.setCellRenderer(new ConversacionCellRenderer());
    JScrollPane scrollLista = UITheme.createScrollPane(listConversaciones);
    scrollLista.setPreferredSize(new Dimension(240, 0));

    JLabel lblConversaciones = new JLabel("💬 Conversaciones");
    lblConversaciones.setFont(UITheme.FONT_HEADER);
    lblConversaciones.setForeground(UITheme.TEXT_SECONDARY);

    JPanel panelIzq = new JPanel(new BorderLayout());
    panelIzq.setBackground(UITheme.BG_SECONDARY);
    panelIzq.add(lblConversaciones, BorderLayout.NORTH);
    panelIzq.add(scrollLista, BorderLayout.CENTER);

    // Header cliente
    lblClienteNombre = new JLabel("Selecciona una conversación");
    lblClienteNombre.setFont(UITheme.FONT_HEADER);
    lblEstado = new JLabel("");
    lblUbicacion = new JLabel("");

    JPanel panelHeader = new JPanel(new GridLayout(3, 1));
    panelHeader.setBackground(UITheme.BG_CARD);
    panelHeader.add(lblClienteNombre);
    panelHeader.add(lblEstado);
    panelHeader.add(lblUbicacion);

    // Chat estilo Telegram
    chatPanel = new JPanel();
    chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));
    chatPanel.setBackground(new Color(240, 242, 245));

    scrollChat = new JScrollPane(chatPanel);
    scrollChat.setBorder(BorderFactory.createEmptyBorder());
    scrollChat.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

    txtMensaje = UITheme.createTextField("Escribe un mensaje...");
    btnEnviar = UITheme.createPrimaryButton("Enviar");

    JPanel panelBotonesEnvio = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelBotonesEnvio.add(btnEnviar);

    JPanel panelEnvio = new JPanel(new BorderLayout());
    panelEnvio.add(txtMensaje, BorderLayout.CENTER);
    panelEnvio.add(panelBotonesEnvio, BorderLayout.EAST);

    JPanel panelChat = new JPanel(new BorderLayout());
    panelChat.add(panelHeader, BorderLayout.NORTH);
    panelChat.add(scrollChat, BorderLayout.CENTER);
    panelChat.add(panelEnvio, BorderLayout.SOUTH);
    
    txtPlaca = UITheme.createTextField("Placa");
    txtAseguradora = UITheme.createTextField("Aseguradora");
    txtPrima = UITheme.createTextField("Prima S/.");
    txtComisionPV = UITheme.createTextField("Comisión PV S/.");
    btnCopiarDatos = UITheme.createSecondaryButton("📋 Copiar datos");

    JPanel panelPDF = crearPanelPDF();

    // Agregar todo al layout principal
    add(panelIzq, BorderLayout.WEST);
    add(panelChat, BorderLayout.CENTER);
    add(panelPDF, BorderLayout.EAST);
}


private void setupEvents() {
    listConversaciones.addListSelectionListener(e -> {
        if (!e.getValueIsAdjusting()) {
            conversacionActual = listConversaciones.getSelectedValue();
            if (conversacionActual != null) {
                cargarConversacion(conversacionActual);
            }
        }
    });

    if (btnEnviar.getActionListeners().length == 0) {
    btnEnviar.addActionListener(e -> enviarMensaje());
}
    btnCopiarDatos.addActionListener(e -> copiarDatos());
    txtMensaje.addActionListener(e -> enviarMensaje());
}

// Lista de conversaciones en el panel
private List<Conversacion> conversaciones = new ArrayList<>();

public Conversacion buscarConversacionPorPvId(int pvId) {
    for (Conversacion conv : conversaciones) {
        if (conv.getPuntoVenta().getId() == pvId) {
            return conv;
        }
    }
    return null;
}

// ✅ Método para refrescar estado en tiempo real
public void actualizarEstadoVenta(int pvId, String nuevoEstado) {
    Conversacion conv = buscarConversacionPorPvId(pvId);
    if (conv != null) {
        conv.setEstadoActual(nuevoEstado);
        listConversaciones.repaint();   // refresca la lista
        lblEstado.setText("Estado: " + nuevoEstado); // actualiza encabezado
    }
}


private JLabel label(String text) {
    JLabel l = new JLabel(text);
    l.setFont(UITheme.FONT_SMALL);
    l.setForeground(UITheme.TEXT_SECONDARY);
    return l;
}
     private void initControllers() {
        chatController = new ChatController();
        policyController = new PolicyController();
    }
    
    
    private void setupLayout() {
        // Configuración adicional de layout si es necesaria
    }
    
    private void actualizarResumen() {
        int totalNoLeidos = 0;
        try {
            totalNoLeidos = chatController.getTotalNoLeidos();
        } catch (Exception e) {
   
        }

        String estadoConexion = ConnectionStatus.getEstado();

        java.time.LocalDateTime ahora = java.time.LocalDateTime.now();
        java.time.format.DateTimeFormatter fmt =
            java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        String fechaHora = ahora.format(fmt);

        lblResumen.setText("📩 No leídos: " + totalNoLeidos +
                           "   |   " + fechaHora +
                           "   |   " + estadoConexion);
    }

private void notificarPagoTelegram() {
    if (conversacionActual == null) {
        mostrarNotificacion("Selecciona una conversación primero", "Sin selección");
        return;
    }

    PuntoVenta pv = conversacionActual.getPuntoVenta();
    List<Venta> ventas = policyController.getVentasByPV(pv.getId());
    if (ventas.isEmpty()) {
        mostrarNotificacion("No hay ventas registradas para este punto de venta", "Sin ventas");
        return;
    }

    Venta venta = ventas.get(0);

    int confirm = JOptionPane.showConfirmDialog(this,
        "Validar pago de " + pv.getNombre() + "\n"
        + "Placa: " + venta.getPlaca() + "\n"
        + "Monto: S/ " + String.format("%.2f", venta.getPrima()),
        "Confirmar validación de pago",
        JOptionPane.YES_NO_OPTION);

    if (confirm != JOptionPane.YES_OPTION) return;

    SwingWorker<Boolean, String> worker = new SwingWorker<>() {
        @Override
        protected Boolean doInBackground() {
            boolean enviado = policyController.enviarSOATTelegram(venta, pv);
            if (enviado) {
                return policyController.validarPago(venta);
            }
            return false;
        }

        @Override
        protected void done() {
            try {
                boolean actualizado = get();
                if (actualizado) {
                    conversacionActual.setEstadoActual(Venta.ESTADO_PAGADO);
                    lblEstado.setText("Estado: " + Venta.ESTADO_PAGADO);
                    lblEstado.setForeground(new Color(46, 204, 113));

                    agregarMensajeChat("📤 [Telegram] Pago validado y notificado", false);

                    mostrarNotificacion("Pago validado y notificación enviada exitosamente", "Validación de Pago");
                } else {
                    mostrarNotificacion("Error al enviar la notificación", "Error");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    worker.execute();
  }


    private void setupDragAndDrop() {
        new FileDragAndDrop(panelPDFDropZone, files -> {
            if (files.length > 0 && files[0].getName().toLowerCase().endsWith(".pdf")) {
                procesarPDF(files[0]);
            }
        });
    }
    
    private void cargarConversaciones() {
        List<Conversacion> conversaciones = chatController.getTodasConversaciones();
        listModel.clear();
        for (Conversacion conv : conversaciones) {
            listModel.addElement(conv);
        }
    }
    
// Timer para refrescar mensajes en tiempo real
private Timer refrescoTimer;

private void cargarConversacion(Conversacion conversacion) {
    this.conversacionActual = conversacion;
    lblClienteNombre.setText(conversacion.getPuntoVenta().getNombre());
    lblEstado.setText("Estado: " + conversacion.getEstadoActual());

    chatPanel.removeAll();
    chatPanel.revalidate();
    chatPanel.repaint();

    mensajesMostrados.clear(); 

    // Mostrar mensajes
    mostrarMensajes(chatController.getMensajesByPvId(conversacion.getPuntoVenta().getId()));

    // ✅ Marcar todos como leídos en BD
    chatController.marcarComoLeida(conversacion.getPuntoVenta().getId());

    // ✅ Refrescar contador de no leídos
    actualizarResumen();

    // Iniciar refresco automático
    iniciarRefrescoConversacion(conversacion.getPuntoVenta().getId());
}

private void iniciarRefrescoConversacion(int pvId) {
    if (refrescoTimer != null) {
        refrescoTimer.stop();   
    }

    refrescoTimer = new Timer(5000, e -> {
        SwingWorker<List<Mensaje>, Void> worker = new SwingWorker<>() {
            @Override
            protected List<Mensaje> doInBackground() {
                return chatController.getMensajesByPvId(pvId);
            }
            @Override
            protected void done() {
                try {
                    mostrarMensajes(get());
                } catch (Exception ex) { ex.printStackTrace(); }
            }
        };
        worker.execute();
    });
    refrescoTimer.start();
}

public boolean isConversacionAbierta(int pvId) {
    return conversacionActual != null && conversacionActual.getPuntoVenta().getId() == pvId;
}


private void enviarMensaje() {
    String mensaje = txtMensaje.getText().trim();
    if (mensaje.isEmpty()) return;

    if (conversacionActual != null) {
        boolean success = chatController.enviarMensaje(
            conversacionActual.getPuntoVenta().getId(), mensaje
        );
        if (success) {
            txtMensaje.setText("");
        }
    }
}
    
private void enviarSOAT() {
    if (conversacionActual == null) {
        mostrarNotificacion("Selecciona una conversación primero", "Sin selección");
        return;
    }

    List<Venta> ventas = policyController.getVentasByPV(conversacionActual.getPuntoVenta().getId());
    if (ventas.isEmpty()) {
        mostrarNotificacion("No hay SOAT emitido para este punto de venta", "Sin SOAT");
        return;
    }

    Venta ultimaVenta = ventas.get(0);
    boolean enviado = policyController.enviarSOATTelegram(ultimaVenta, conversacionActual.getPuntoVenta());

    if (enviado) {
        chatController.actualizarEstado(conversacionActual, Venta.ESTADO_ESPERANDO_PAGO);
        conversacionActual.setEstadoActual(Venta.ESTADO_ESPERANDO_PAGO);
        lblEstado.setText("Estado: " + Venta.ESTADO_ESPERANDO_PAGO);
        lblEstado.setForeground(new Color(241, 196, 15));

        mostrarNotificacion("SOAT enviado exitosamente por Telegram", "Envío exitoso");
    } else {
        mostrarNotificacion("Error al enviar SOAT", "Error");
    }
}
 
private JPanel crearPanelPDF() {
    JPanel panelPDF = UITheme.createCard("📄 Extracción PDF");
    panelPDF.setLayout(new BorderLayout());

    // Zona de arrastre como cuadro bonito
    panelPDFDropZone = new JPanel(new BorderLayout());
    panelPDFDropZone.setPreferredSize(new Dimension(220, 120)); // tamaño cuadrado
    panelPDFDropZone.setBackground(UITheme.BG_HOVER);
    panelPDFDropZone.setBorder(BorderFactory.createDashedBorder(UITheme.BORDER_COLOR, 2, 6));

    JLabel lblDrop = new JLabel("📂 Arrastra PDF aquí", SwingConstants.CENTER);
    lblDrop.setFont(UITheme.FONT_BODY);
    lblDrop.setForeground(UITheme.TEXT_SECONDARY);

    panelPDFDropZone.add(lblDrop, BorderLayout.CENTER);

    // Añadir al panel principal
    panelPDF.add(panelPDFDropZone, BorderLayout.NORTH);

    // Campos con labels alineados
    JPanel panelCampos = new JPanel(new GridBagLayout());
    panelCampos.setBackground(UITheme.BG_CARD);
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(6, 6, 6, 6);
    gbc.fill = GridBagConstraints.HORIZONTAL;
    gbc.weightx = 1.0;

    gbc.gridx = 0; gbc.gridy = 0;
    JLabel lblPlaca = new JLabel("Placa:");
    lblPlaca.setFont(UITheme.FONT_BODY);
    lblPlaca.setForeground(UITheme.TEXT_SECONDARY);
    panelCampos.add(lblPlaca, gbc);
    gbc.gridx = 1;
    panelCampos.add(txtPlaca, gbc);

    gbc.gridx = 0; gbc.gridy++;
    JLabel lblAseg = new JLabel("Aseguradora:");
    lblAseg.setFont(UITheme.FONT_BODY);
    lblAseg.setForeground(UITheme.TEXT_SECONDARY);
    panelCampos.add(lblAseg, gbc);
    gbc.gridx = 1;
    panelCampos.add(txtAseguradora, gbc);

    gbc.gridx = 0; gbc.gridy++;
    JLabel lblPrima = new JLabel("Prima S/:");
    lblPrima.setFont(UITheme.FONT_BODY);
    lblPrima.setForeground(UITheme.TEXT_SECONDARY);
    panelCampos.add(lblPrima, gbc);
    gbc.gridx = 1;
    panelCampos.add(txtPrima, gbc);

    gbc.gridx = 0; gbc.gridy++;
    JLabel lblComision = new JLabel("Comisión PV S/:");
    lblComision.setFont(UITheme.FONT_BODY);
    lblComision.setForeground(UITheme.TEXT_SECONDARY);
    panelCampos.add(lblComision, gbc);
    gbc.gridx = 1;
    panelCampos.add(txtComisionPV, gbc);

    panelPDF.add(panelCampos, BorderLayout.CENTER);

    // Botón copiar
    JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
    panelBotones.setBackground(UITheme.BG_CARD);
    panelBotones.add(btnCopiarDatos);
    panelPDF.add(panelBotones, BorderLayout.SOUTH);

    return panelPDF;
}

private void procesarPDF(File pdfFile) {
    if (conversacionActual == null) {
        mostrarNotificacion("Selecciona una conversación primero", "Sin selección");
        return;
    }

    int pvId = conversacionActual.getPuntoVenta().getId();

    // ✅ ahora pasamos también el pvId
    // Obtén el usuario logueado desde tu sesión
    int usuarioId = SesionActual.getUsuario().getId(); // ejemplo de clase SesionActual
    Venta venta = policyController.procesarPDFSOAT(pdfFile, pvId, usuarioId);   


    if (venta != null) {
        txtPlaca.setText(venta.getPlaca() != null ? venta.getPlaca() : "");
        txtAseguradora.setText(venta.getAseguradora() != null ? venta.getAseguradora() : "");
        txtPrima.setText(String.format("%.2f", venta.getPrima()));

        double comision = policyController.calcularComision(venta);
        venta.setComisionPV(comision);
        txtComisionPV.setText(String.format("%.2f", comision));

        int response = JOptionPane.showConfirmDialog(this,
            "¿Deseas enviar este SOAT al cliente ahora?",
            "Enviar SOAT",
            JOptionPane.YES_NO_OPTION);

        if (response == JOptionPane.YES_OPTION) {
            enviarSOAT();
        }
    } else {
        mostrarNotificacion("No se pudo extraer datos del PDF. Verifica que sea un SOAT válido.", "Error de extracción");
    }
}


public void agregarMensajeChatConFecha(String mensaje, boolean esEntrante, java.util.Date fecha) {
    String hora = new java.text.SimpleDateFormat("HH:mm").format(
        fecha != null ? fecha : new java.util.Date()
    );

    JPanel bubble = new JPanel(new BorderLayout(0, 2));
    bubble.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
    bubble.setOpaque(false);

    JLabel lbl = new JLabel("<html><body style='width:220px'>" + mensaje + "</body></html>");
    lbl.setFont(UITheme.FONT_BODY);
    lbl.setOpaque(true);
    lbl.setBorder(BorderFactory.createCompoundBorder(
        new javax.swing.border.LineBorder(
            esEntrante ? new Color(180, 230, 160) : new Color(0, 90, 170), 1, true),
        BorderFactory.createEmptyBorder(8, 12, 8, 12)
    ));

    JLabel lblHora = new JLabel(hora);
    lblHora.setFont(new Font("Segoe UI", Font.PLAIN, 10));
    lblHora.setForeground(Color.GRAY);

    if (esEntrante) {
        lbl.setBackground(new Color(220, 248, 198));
        lbl.setForeground(new Color(30, 30, 30));
        bubble.add(lbl, BorderLayout.WEST);
        lblHora.setHorizontalAlignment(SwingConstants.LEFT);
        bubble.add(lblHora, BorderLayout.SOUTH);
    } else {
        lbl.setBackground(new Color(0, 120, 212));
        lbl.setForeground(Color.WHITE);
        bubble.add(lbl, BorderLayout.EAST);
        lblHora.setHorizontalAlignment(SwingConstants.RIGHT);
        bubble.add(lblHora, BorderLayout.SOUTH);
    }

    chatPanel.add(bubble);
    chatPanel.revalidate();
    chatPanel.repaint();

    SwingUtilities.invokeLater(() ->
        scrollChat.getVerticalScrollBar().setValue(
            scrollChat.getVerticalScrollBar().getMaximum()
        )
    );
}
public void agregarMensajeChat(String mensaje, boolean esEntrante) {
    agregarMensajeChatConFecha(mensaje, esEntrante, new java.util.Date());
}

// Nueva lista para mensajes ya mostrados
private List<Mensaje> mensajesMostrados = new ArrayList<>();

private void mostrarMensajes(List<Mensaje> mensajes) {
    if (mensajes == null || mensajes.isEmpty()) return;

    for (Mensaje msg : mensajes) {
        boolean yaMostrado = mensajesMostrados.stream()
            .anyMatch(m -> m.getId() == msg.getId()); // ✅ compara por ID

        if (!yaMostrado) {
            boolean esEntrante = Mensaje.DIRECCION_ENTRANTE.equals(msg.getDireccion());
            agregarMensajeChatConFecha(msg.getContenido(), esEntrante, msg.getFechaEnvio());
            mensajesMostrados.add(msg);
        }
    }
}

public void agregarConversacion(Conversacion conv) {
    listModel.addElement(conv);
    listConversaciones.setSelectedValue(conv, true);
    cargarConversacion(conv);
}

public void refrescarMensajes(List<Mensaje> mensajes) {
    mostrarMensajes(mensajes);
}
// Método nuevo dentro de BandejaEntradaPanel
private void mostrarNotificacion(String mensaje, String titulo) {
    JDialog dialog = new JDialog((Frame) null, titulo, false); // false = no modal
    dialog.setLayout(new BorderLayout());
    dialog.add(new JLabel(mensaje, SwingConstants.CENTER), BorderLayout.CENTER);
    dialog.setSize(300, 150);
    dialog.setLocationRelativeTo(this);
    dialog.setVisible(true);
}


// Refrescar todas las conversaciones desde BD
public void refrescarConversaciones() {
    ChatService chatService = new ChatService();
    this.conversaciones = chatService.getTodasConversaciones();

    // Actualizar la lista en la UI
    listConversaciones.setListData(conversaciones.toArray(new Conversacion[0]));
    listConversaciones.repaint();

    // Si hay una conversación abierta, actualizar su estado en el encabezado
    Conversacion seleccionada = listConversaciones.getSelectedValue();
    if (seleccionada != null) {
        lblEstado.setText("Estado: " + seleccionada.getEstadoActual());
    }
}

private void copiarDatos() {
    StringBuilder datos = new StringBuilder();
    datos.append("Placa: ").append(txtPlaca.getText()).append("\n");
    datos.append("Aseguradora: ").append(txtAseguradora.getText()).append("\n");
    datos.append("Prima: S/ ").append(txtPrima.getText()).append("\n");
    datos.append("Comisión PV: S/ ").append(txtComisionPV.getText());

    java.awt.Toolkit.getDefaultToolkit()
        .getSystemClipboard()
        .setContents(new java.awt.datatransfer.StringSelection(datos.toString()), null);

    mostrarNotificacion("Datos copiados al portapapeles", "Copiado");
} 
    // Renderer personalizado para la lista de conversaciones
    private class ConversacionCellRenderer extends JPanel implements ListCellRenderer<Conversacion> {
    private JLabel lblNombre = new JLabel();
    private JLabel lblEstado = new JLabel();

    public ConversacionCellRenderer() {
        setLayout(new BorderLayout(5, 5));
        lblNombre.setFont(UITheme.FONT_BODY);
        lblEstado.setFont(UITheme.FONT_SMALL);
        add(lblNombre, BorderLayout.NORTH);
        add(lblEstado, BorderLayout.SOUTH);
        setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
    }


@Override
public Component getListCellRendererComponent(JList<? extends Conversacion> list,
                                              Conversacion value, int index,
                                              boolean isSelected, boolean cellHasFocus) {
    lblNombre.setText(value.getPuntoVenta().getNombre());

    int unread = chatController.countUnreadByPvId(value.getPuntoVenta().getId());
    lblEstado.setText(unread > 0 ? "📩 " + value.getEstadoActual() + " (" + unread + ")" 
                                 : "Estado: " + value.getEstadoActual());

    setBackground(isSelected ? UITheme.ACCENT_BLUE : UITheme.BG_SECONDARY);
    lblNombre.setForeground(isSelected ? Color.WHITE : UITheme.TEXT_PRIMARY);
    lblEstado.setForeground(isSelected ? Color.LIGHT_GRAY : UITheme.TEXT_SECONDARY);

    return this;
    
 }
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
}
