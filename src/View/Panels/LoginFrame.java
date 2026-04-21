
package View.Panels;

import Modelo.Entidades.Usuario;
import Services.AuthService;
import Utils.CaptchaGenerator;
import Utils.TelegramSender;
import Utils.UITheme;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

public class LoginFrame extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(LoginFrame.class.getName());
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JTextField txtCaptcha;
    private JLabel lblCaptchaImg;
    private JButton btnLogin;
    private JButton btnRefreshCaptcha;
    private JLabel lblError;

    private final AuthService authService       = new AuthService();
    private final CaptchaGenerator captchaGen   = new CaptchaGenerator();
    private int intentosFallidos = 0;
 
    public LoginFrame() {
        setTitle("MegaSOAT - Iniciar Sesion");
        Utils.AppIcon.aplicarIcono(this);
        setSize(440, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        initComponentsManual();
        refrescarCaptcha();
    }
        private void initComponentsManual() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(UITheme.BG_PRIMARY);

        // Header 
        JPanel header = new JPanel(new GridLayout(3, 1, 0, 4));
        header.setBackground(UITheme.BG_PRIMARY);
        header.setBorder(BorderFactory.createEmptyBorder(36, 40, 16, 40));

        JLabel lblLogo = new JLabel("MegaSOAT", SwingConstants.CENTER);
        lblLogo.setFont(new Font("Segoe UI", Font.BOLD, 34));
        lblLogo.setForeground(UITheme.ACCENT_BLUE);

        JLabel lblSub = new JLabel("Sistema de Gestion SOAT", SwingConstants.CENTER);
        lblSub.setFont(UITheme.FONT_BODY);
        lblSub.setForeground(UITheme.TEXT_SECONDARY);

        JLabel lblBienvenida = new JLabel("Inicia sesion para continuar", 
                                           SwingConstants.CENTER);
        lblBienvenida.setFont(UITheme.FONT_SMALL);
        lblBienvenida.setForeground(UITheme.TEXT_SECONDARY);

        header.add(lblLogo);
        header.add(lblSub);
        header.add(lblBienvenida);

        // Formulario
        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(UITheme.BG_PRIMARY);
        form.setBorder(BorderFactory.createEmptyBorder(0, 36, 20, 36));

        GridBagConstraints g = new GridBagConstraints();
        g.fill = GridBagConstraints.HORIZONTAL;
        g.gridx = 0; g.weightx = 1.0;

        // Usuario
        g.gridy = 0; g.insets = new Insets(6, 0, 2, 0);
        form.add(label("Usuario"), g);
        txtUsername = campoTexto();
        g.gridy = 1; g.insets = new Insets(0, 0, 10, 0);
        form.add(txtUsername, g);

        // Password
        g.gridy = 2; g.insets = new Insets(6, 0, 2, 0);
        form.add(label("Contrasena"), g);
        txtPassword = new JPasswordField();
        estilizarCampo(txtPassword);
        g.gridy = 3; g.insets = new Insets(0, 0, 10, 0);
        form.add(txtPassword, g);

        // Captcha imagen
        g.gridy = 4; g.insets = new Insets(6, 0, 2, 0);
        form.add(label("Verificacion de seguridad"), g);

        JPanel captchaRow = new JPanel(new BorderLayout(8, 0));
        captchaRow.setBackground(UITheme.BG_PRIMARY);
        lblCaptchaImg = new JLabel();
        lblCaptchaImg.setBorder(BorderFactory.createCompoundBorder(
            new UITheme.RoundedBorder(8, UITheme.BORDER_COLOR),
            BorderFactory.createEmptyBorder(4, 4, 4, 4)
        ));
        lblCaptchaImg.setBackground(new Color(30, 30, 48));
        lblCaptchaImg.setOpaque(true);

        btnRefreshCaptcha = new JButton("↺");
        btnRefreshCaptcha.setFont(new Font("Segoe UI", Font.BOLD, 18));
        btnRefreshCaptcha.setForeground(UITheme.TEXT_PRIMARY);
        btnRefreshCaptcha.setBackground(UITheme.BG_HOVER);
        btnRefreshCaptcha.setBorderPainted(false);
        btnRefreshCaptcha.setFocusPainted(false);
        btnRefreshCaptcha.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btnRefreshCaptcha.setPreferredSize(new Dimension(42, 42));
        btnRefreshCaptcha.setToolTipText("Generar nuevo captcha");

        captchaRow.add(lblCaptchaImg, BorderLayout.CENTER);
        captchaRow.add(btnRefreshCaptcha, BorderLayout.EAST);
        g.gridy = 5; g.insets = new Insets(0, 0, 6, 0);
        form.add(captchaRow, g);

        // Campo captcha
        txtCaptcha = campoTexto();
        txtCaptcha.setToolTipText("Escribe los caracteres de la imagen");
        g.gridy = 6; g.insets = new Insets(0, 0, 10, 0);
        form.add(txtCaptcha, g);

        // Error
        lblError = new JLabel(" ");
        lblError.setFont(UITheme.FONT_SMALL);
        lblError.setForeground(UITheme.ACCENT_RED);
        lblError.setHorizontalAlignment(SwingConstants.CENTER);
        g.gridy = 7; g.insets = new Insets(0, 0, 6, 0);
        form.add(lblError, g);

        // Boton login
        btnLogin = UITheme.createPrimaryButton("Iniciar Sesion");
        btnLogin.setPreferredSize(new Dimension(0, 44));
        btnLogin.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        g.gridy = 8; g.insets = new Insets(0, 0, 0, 0);
        form.add(btnLogin, g);

        main.add(header, BorderLayout.NORTH);
        main.add(form, BorderLayout.CENTER);
        setContentPane(main);

        //Eventos 
        btnLogin.addActionListener(e -> intentarLogin());
        btnRefreshCaptcha.addActionListener(e -> refrescarCaptcha());
        txtPassword.addActionListener(e -> txtCaptcha.requestFocus());
        txtCaptcha.addActionListener(e -> intentarLogin());
        txtUsername.addActionListener(e -> txtPassword.requestFocus());
    }

    private void refrescarCaptcha() {
        String codigo = captchaGen.generarCodigo();
        BufferedImage img = captchaGen.generarImagen(codigo);
        lblCaptchaImg.setIcon(new ImageIcon(img));
        txtCaptcha.setText("");
        txtCaptcha.requestFocus();
    }

private void intentarLogin() {
    String username = txtUsername.getText().trim();
    String password = new String(txtPassword.getPassword());
    String captcha  = txtCaptcha.getText().trim();

    // Validaciones rápidas
    if (username.isEmpty() || password.isEmpty()) {
        mostrarError("Ingresa usuario y contraseña");
        return;
    }
    if (captcha.isEmpty()) {
        mostrarError("Ingresa el código de verificación");
        txtCaptcha.requestFocus();
        return;
    }
    if (!captchaGen.verificar(captcha)) {
        mostrarError("Código de verificación incorrecto");
        refrescarCaptcha();
        intentosFallidos++;
        bloquearSiNecesario();
        return;
    }

    // Feedback inmediato en UI
    btnLogin.setEnabled(false);
    btnLogin.setText("Verificando...");
    lblError.setText(" ");


SwingWorker<Usuario, Void> worker = new SwingWorker<>() {
    @Override
    protected Usuario doInBackground() {
        return authService.login(username, password, false);
    }

@Override
protected void done() {
    try {
        Usuario usuario = get();
        btnLogin.setEnabled(true);
        btnLogin.setText("Iniciar Sesión");

        if (usuario != null) {
            intentosFallidos = 0;

            //Guardar usuario en la sesión
            Utils.SesionActual.setUsuario(usuario);

            dispose();
            MainFrame mainFrame = new MainFrame(usuario);
            mainFrame.setVisible(true);

            TelegramSender bot = TelegramSender.getInstance();
            bot.setBandejaPanel(mainFrame.getBandejaEntradaPanel());
        }
            else {
            int opcion = JOptionPane.showOptionDialog(LoginFrame.this,
                "⚠️ La cuenta ya está en uso.\n¿Desea continuar aquí y cerrar la otra sesión?",
                "Cuenta en uso",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE,
                null,
                new Object[]{"Continuar aquí", "Cancelar"},
                "Cancelar");

            if (opcion == JOptionPane.YES_OPTION) {
                Usuario usuarioForzado = authService.login(username, password, true);
                if (usuarioForzado != null) {
                    dispose();
                    MainFrame mainFrame = new MainFrame(usuarioForzado);
                    mainFrame.setVisible(true);
                } else {
                    mostrarError("No se pudo iniciar sesión");
                }
            } else {
                mostrarError("Inicio de sesión cancelado");
            }
        }
    } catch (Exception ex) {
        logger.severe("Error en login: " + ex.getMessage());
        btnLogin.setEnabled(true);
        btnLogin.setText("Iniciar Sesión");
        mostrarError("Error interno al verificar");
    }
}

}; 

worker.execute(); 
}


private void bloquearSiNecesario() {
    if (intentosFallidos >= 3) {
        btnLogin.setEnabled(false);
        lblError.setText("Demasiados intentos. Espera 10 segundos.");

        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                Thread.sleep(30000); // espera en segundo plano
                return null;
            }
            @Override
            protected void done() {
                btnLogin.setEnabled(true);
                intentosFallidos = 0;
                lblError.setText(" ");
                refrescarCaptcha();
            }
        };
        worker.execute(); 
    }
}


    private void mostrarError(String msg) {
        lblError.setText(msg);
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.FONT_BODY);
        l.setForeground(UITheme.TEXT_SECONDARY);
        return l;
    }

    private JTextField campoTexto() {
        JTextField f = new JTextField();
        estilizarCampo(f);
        return f;
    }

    private void estilizarCampo(JTextField f) {
        f.setFont(UITheme.FONT_BODY);
        f.setForeground(UITheme.TEXT_PRIMARY);
        f.setBackground(UITheme.BG_HOVER);
        f.setCaretColor(UITheme.TEXT_PRIMARY);
        f.setBorder(BorderFactory.createCompoundBorder(
            new UITheme.RoundedBorder(10, UITheme.BORDER_COLOR),
            BorderFactory.createEmptyBorder(10, 14, 10, 14)
        ));
        f.setPreferredSize(new Dimension(0, 42));
    }

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


    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new LoginFrame().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
