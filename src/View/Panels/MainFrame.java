
package View.Panels;
    
    
import Services.ChatHttpServer;
import Controller.ChatController;
import Modelo.Entidades.Usuario;
import Utils.TelegramSender;
import Services.SessionObserver;
import Utils.UITheme;
import java.awt.BorderLayout;
import java.awt.CardLayout;      
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.io.IOException;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingWorker;
import javax.swing.Timer;

/**
 *
 * @author USUARIO
 */
public class MainFrame extends JFrame implements SessionObserver{
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(MainFrame.class.getName());
    private BandejaEntradaPanel bandejaPanel;

    // Componentes del menú izquierdo
    private JPanel panelMenu;
    private JPanel panelContent;
    private JButton btnBandeja;
    private JButton btnValidarPagos;
    private JButton btnReportes;
    private JButton btnGestionPV;
    private JButton btnConfiguracion;
    private JButton btnUsuarios;
    private JButton btnSalir;
    private JLabel lblNotificaciones;
    private JLabel lblUsuario;
    
    // Controladores
    private ChatController chatController;
    private Timer timerNotificaciones;
    private Usuario usuarioActual;
    
    private ValidarPagosPanel validarPanel;
    private ReportesPanel reportesPanel;
    private GestionPVPanel pvPanel;
    private ConfiguracionPanel configPanel;
    private GestionUsuariosPanel usuariosPanel;
    
public MainFrame(Usuario usuario) {
    this.usuarioActual = usuario;
    Services.AuthService.addObserver(this); 
    Utils.AppIcon.aplicarIcono(this);
    initControllers();
    initComponentsManual();
    setupLayout();
    setupEvents();
    aplicarPermisos();
    startNotificationTimer();

    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setTitle("MegaSOAT");
    setSize(1200, 700);
    setLocationRelativeTo(null);

    // Evitar desorden al redimensionar
    panelMenu.setMinimumSize(new Dimension(220, 600));
    panelContent.setMinimumSize(new Dimension(800, 600));
    panelContent.setPreferredSize(new Dimension(1000, 700));

    // bloquear redimensionado
    setResizable(false);
    // Activar doble buffer en paneles grandes
    panelContent.setDoubleBuffered(true);
    bandejaPanel.setDoubleBuffered(true);

}
public BandejaEntradaPanel getBandejaEntradaPanel() {
    return this.bandejaPanel;
}

    private void aplicarPermisos() {
    btnValidarPagos.setVisible(usuarioActual.puedeValidarPagos());
    btnReportes.setVisible(usuarioActual.puedeExportarReportes());
    btnUsuarios.setVisible(usuarioActual.puedeGestionarUsuarios());
    btnConfiguracion.setVisible(usuarioActual.puedeGestionarUsuarios());

    String rolLabel = switch (usuarioActual.getRol()) {
        case "SUPERADMIN" -> "Super Admin";
        case "ADMIN"      -> "Administrador";
        case "COMERCIAL"  -> "Comercial";
        default           -> usuarioActual.getRol();
    };
    lblUsuario.setText("  " + usuarioActual.getNombreCompleto() 
                     + "  |  " + rolLabel);
}
    private void initControllers() {
        chatController = new ChatController();
    }
    
    /**
     * Inicializa todos los componentes visuales manualmente
     */
private void initComponentsManual() {
    panelMenu = new JPanel();
    panelMenu.setBackground(new Color(22, 22, 36));
    panelMenu.setPreferredSize(new Dimension(220, 0));
    panelMenu.setLayout(new BoxLayout(panelMenu, BoxLayout.Y_AXIS));

    // Logo
    JLabel lblTitulo = new JLabel("MegaSOAT");
    lblTitulo.setFont(new Font("Segoe UI", Font.BOLD, 20));
    lblTitulo.setForeground(Color.WHITE);
    lblTitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
    lblTitulo.setBorder(BorderFactory.createEmptyBorder(24, 20, 4, 20));

    JLabel lblSubtitulo = new JLabel("Sistema de Gestión");
    lblSubtitulo.setFont(new Font("Segoe UI", Font.PLAIN, 11));
    lblSubtitulo.setForeground(new Color(130, 130, 165));
    lblSubtitulo.setAlignmentX(Component.LEFT_ALIGNMENT);
    lblSubtitulo.setBorder(BorderFactory.createEmptyBorder(0, 20, 16, 20));

    // Separador
    JPanel sep = new JPanel();
    sep.setBackground(new Color(50, 50, 75));
    sep.setMaximumSize(new Dimension(220, 1));
    sep.setPreferredSize(new Dimension(220, 1));
    sep.setAlignmentX(Component.LEFT_ALIGNMENT);

    // Usuario
    lblUsuario = new JLabel("  Juan Perez");
    lblUsuario.setFont(new Font("Segoe UI", Font.PLAIN, 12));
    lblUsuario.setForeground(new Color(130, 130, 165));
    lblUsuario.setAlignmentX(Component.LEFT_ALIGNMENT);
    lblUsuario.setBorder(BorderFactory.createEmptyBorder(14, 20, 18, 20));

    // Separador2
    JPanel sep2 = new JPanel();
    sep2.setBackground(new Color(50, 50, 75));
    sep2.setMaximumSize(new Dimension(220, 1));
    sep2.setPreferredSize(new Dimension(220, 1));
    sep2.setAlignmentX(Component.LEFT_ALIGNMENT);

    // Etiqueta sección
    JLabel lblMenu = new JLabel("MENU PRINCIPAL");
    lblMenu.setFont(new Font("Segoe UI", Font.BOLD, 10));
    lblMenu.setForeground(new Color(90, 90, 120));
    lblMenu.setAlignmentX(Component.LEFT_ALIGNMENT);
    lblMenu.setBorder(BorderFactory.createEmptyBorder(14, 20, 8, 20));

    // Botones
    btnBandeja       = crearBotonMenu("Bandeja de Entrada", "bandeja");
    btnValidarPagos  = crearBotonMenu("Validar Pagos",      "validar");
    btnReportes      = crearBotonMenu("Reportes",           "reportes");
    btnGestionPV     = crearBotonMenu("Puntos de Venta",    "pv");
    btnConfiguracion = crearBotonMenu("Configuracion",      "config");
    btnUsuarios      = crearBotonMenu("Gestion Usuarios",  "usuarios");
    btnSalir = crearBotonMenu("Cerrar Sesión", "logout");

    // Badge
    lblNotificaciones = new JLabel("0");
    lblNotificaciones.setFont(new Font("Segoe UI", Font.BOLD, 11));
    lblNotificaciones.setForeground(Color.WHITE);
    lblNotificaciones.setBackground(new Color(239, 68, 68));
    lblNotificaciones.setOpaque(true);
    lblNotificaciones.setBorder(BorderFactory.createEmptyBorder(2, 8, 2, 8));

    // Armar menú
    panelMenu.add(lblTitulo);
    panelMenu.add(lblSubtitulo);
    panelMenu.add(sep);
    panelMenu.add(lblUsuario);
    panelMenu.add(sep2);
    panelMenu.add(lblMenu);
    panelMenu.add(btnBandeja);
    panelMenu.add(btnValidarPagos);
    panelMenu.add(btnReportes);
    panelMenu.add(btnGestionPV);
    panelMenu.add(btnConfiguracion);
    panelMenu.add(btnUsuarios);
    panelMenu.add(Box.createVerticalGlue());

    // Separador antes de salir
    JPanel sep3 = new JPanel();
    sep3.setBackground(new Color(50, 50, 75));
    sep3.setMaximumSize(new Dimension(220, 1));
    sep3.setAlignmentX(Component.LEFT_ALIGNMENT);
    panelMenu.add(sep3);
    panelMenu.add(btnSalir);
    panelMenu.add(Box.createRigidArea(new Dimension(0, 16)));

    panelContent = new JPanel(new CardLayout());
    panelContent.setBackground(new Color(15, 15, 25));

    getContentPane().setBackground(new Color(15, 15, 25));
    getContentPane().setLayout(new BorderLayout());
    getContentPane().add(panelMenu, BorderLayout.WEST);
    getContentPane().add(panelContent, BorderLayout.CENTER);
}


    
private JButton crearBotonMenu(String texto, String accion) {
    JButton boton = new JButton(texto) {
        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                                RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(4, 2, getWidth() - 8, getHeight() - 4, 10, 10);
            g2.dispose();
            super.paintComponent(g);
        }
    };
    boton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    boton.setForeground(new Color(200, 200, 230));
    boton.setBackground(new Color(22, 22, 36));
    boton.setContentAreaFilled(false);
    boton.setOpaque(false);
    boton.setBorderPainted(false);
    boton.setFocusPainted(false);
    boton.setHorizontalAlignment(SwingConstants.LEFT);
    boton.setAlignmentX(Component.LEFT_ALIGNMENT);
    boton.setMaximumSize(new Dimension(220, 42));
    boton.setMinimumSize(new Dimension(220, 42));
    boton.setPreferredSize(new Dimension(220, 42));
    boton.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
    boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    boton.setActionCommand(accion);

    boton.addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseEntered(java.awt.event.MouseEvent e) {
            if (!UITheme.ACCENT_BLUE.equals(boton.getBackground())) {
                boton.setBackground(new Color(42, 42, 65));
                boton.setForeground(Color.WHITE);
                boton.repaint();
            }
        }
        public void mouseExited(java.awt.event.MouseEvent e) {
            if (!UITheme.ACCENT_BLUE.equals(boton.getBackground())) {
                boton.setBackground(new Color(22, 22, 36));
                boton.setForeground(new Color(200, 200, 230));
                boton.repaint();
            }
        }
    });
    return boton;
}
    
private void setupLayout() {
    bandejaPanel = new BandejaEntradaPanel();
    panelContent.add(bandejaPanel, "bandeja");

    // Mostrar panel inicial
    CardLayout cl = (CardLayout) panelContent.getLayout();
    cl.show(panelContent, "bandeja");
    actualizarEstiloBoton(btnBandeja, true);
}

    
private void setupEvents() {
    btnBandeja.addActionListener(e -> cambiarPanel("bandeja"));
    btnValidarPagos.addActionListener(e -> cambiarPanel("validar"));
    btnReportes.addActionListener(e -> cambiarPanel("reportes"));
    btnGestionPV.addActionListener(e -> cambiarPanel("pv"));
    btnConfiguracion.addActionListener(e -> cambiarPanel("config"));
    btnUsuarios.addActionListener(e -> cambiarPanel("usuarios"));

    // Command aplicado
    btnSalir.addActionListener(e -> {
        Services.Command logout = new Services.LogoutCommand();
        logout.execute();

    });
}
@Override
public void onLogout() {
    dispose(); // cerrar ventana principal
    new LoginFrame().setVisible(true); // volver al login
}


    
private void cambiarPanel(String nombre) {
    CardLayout cl = (CardLayout) panelContent.getLayout();

    switch (nombre) {
        case "validar" -> {
            if (validarPanel == null) {
            int currentUserId = usuarioActual.getId(); //esta sí existe
            validarPanel = new ValidarPagosPanel(currentUserId);


                panelContent.add(validarPanel, "validar");
            }
        }
        case "reportes" -> {
            if (reportesPanel == null) {
                reportesPanel = new ReportesPanel();
                panelContent.add(reportesPanel, "reportes");
            }
        }
        case "pv" -> {
            if (pvPanel == null) {
                pvPanel = new GestionPVPanel();
                panelContent.add(pvPanel, "pv");
            }
        }
        case "config" -> {
            if (configPanel == null) {
                configPanel = new ConfiguracionPanel();
                panelContent.add(configPanel, "config");
            }
        }
        case "usuarios" -> {
            if (usuariosPanel == null) {
                usuariosPanel = new GestionUsuariosPanel();
                panelContent.add(usuariosPanel, "usuarios");
            }
        }
    }

    cl.show(panelContent, nombre);

    // Actualizar estilos de botones
    actualizarEstiloBoton(btnBandeja,       "bandeja".equals(nombre));
    actualizarEstiloBoton(btnValidarPagos,  "validar".equals(nombre));
    actualizarEstiloBoton(btnReportes,      "reportes".equals(nombre));
    actualizarEstiloBoton(btnGestionPV,     "pv".equals(nombre));
    actualizarEstiloBoton(btnConfiguracion, "config".equals(nombre));
    actualizarEstiloBoton(btnUsuarios,      "usuarios".equals(nombre));
}



private void actualizarEstiloBoton(JButton boton, boolean activo) {
    if (activo) {
        boton.setBackground(UITheme.ACCENT_BLUE);
        boton.setForeground(Color.WHITE);
        boton.setFont(new Font("Segoe UI", Font.BOLD, 13));
    } else {
        boton.setBackground(new Color(22, 22, 36));
        boton.setForeground(new Color(210, 210, 240));
        boton.setFont(new Font("Segoe UI", Font.PLAIN, 13));
    }
    boton.repaint();
}
    
    private void startNotificationTimer() {
    timerNotificaciones = new Timer(5000, e -> {
    new SwingWorker<Integer, Void>() {
        protected Integer doInBackground() {
            return chatController.getTotalNoLeidos(); 
        }
        protected void done() {
            try {
                int noLeidos = get();
                lblNotificaciones.setText(String.valueOf(noLeidos));
                lblNotificaciones.setForeground(noLeidos > 0 ? Color.RED : Color.GREEN);
                setTitle(noLeidos > 0
                    ? "MegaSOAT - " + noLeidos + " mensajes no leídos"
                    : "MegaSOAT - Sistema de Gestión SOAT");
            } catch (Exception ex) {
                logger.severe("Error notificaciones: " + ex.getMessage());
            }
        }
    }.execute();
});
}

    

    /**
     * This method is called from within the constructor to initialize the form.add
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
    UITheme.applyGlobalDefaults();
    java.awt.EventQueue.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
