package View.Panels;

import Controller.UsuarioController;
import Modelo.Entidades.Usuario;
import Utils.UITheme;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class GestionUsuariosPanel extends javax.swing.JPanel {
    private JTable tblUsuarios;
    private DefaultTableModel tableModel;
    private JTextField txtUsername;
    private JTextField txtNombre;
    private JTextField txtEmail;
    private JTextField txtTelefono;
    private JPasswordField txtPassword;
    private JComboBox<String> cmbRol;
    private JCheckBox chkActivo;
    private JButton btnGuardar;
    private JButton btnNuevo;
    private JButton btnEliminar;

    private UsuarioController usuarioController;
    private int currentId = 0;
    public GestionUsuariosPanel() {
        initControllers();
        initComponentsManual();
        cargarUsuarios();
    }
        private void initControllers() {
        usuarioController = new UsuarioController();
    }

    private void initComponentsManual() {
        setBackground(UITheme.BG_PRIMARY);
        setLayout(new BorderLayout(12, 12));
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        // Título 
        JLabel lblTitulo = new JLabel("Gestion de Usuarios");
        lblTitulo.setFont(UITheme.FONT_TITLE);
        lblTitulo.setForeground(UITheme.TEXT_PRIMARY);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        // Tabla
        tableModel = new DefaultTableModel(
            new String[]{"ID", "Usuario", "Nombre", "Email", "Rol", "Activo"}, 0
        ) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        tblUsuarios = new JTable(tableModel);
        UITheme.styleTable(tblUsuarios);
        tblUsuarios.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tblUsuarios.getColumnModel().getColumn(0).setMaxWidth(50);

        //  Formulario 
        txtUsername  = UITheme.createTextField("username");
        txtNombre    = UITheme.createTextField("Nombre completo");
        txtEmail     = UITheme.createTextField("email@ejemplo.com");
        txtTelefono  = UITheme.createTextField("Telefono");
        txtPassword  = new JPasswordField();
        estilizarCampo(txtPassword);
        cmbRol = UITheme.createComboBox(new String[]{
            "COMERCIAL", "ADMIN", "SUPERADMIN"
        });
        chkActivo = new JCheckBox("Activo", true);
        chkActivo.setFont(UITheme.FONT_BODY);
        chkActivo.setForeground(UITheme.TEXT_PRIMARY);
        chkActivo.setBackground(UITheme.BG_CARD);

        btnGuardar  = UITheme.createPrimaryButton("Guardar");
        btnNuevo    = UITheme.createSecondaryButton("Nuevo");
        btnEliminar = UITheme.createDangerButton("Desactivar");

        JPanel panelForm = UITheme.createCard("Datos del Usuario");
        JPanel grid = new JPanel(new GridBagLayout());
        grid.setBackground(UITheme.BG_CARD);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 5, 5, 5);
        g.fill = GridBagConstraints.HORIZONTAL;

        g.gridx=0; g.gridy=0; grid.add(label("Username:*"), g);
        g.gridx=1; grid.add(txtUsername, g);
        g.gridx=0; g.gridy=1; grid.add(label("Nombre:*"), g);
        g.gridx=1; grid.add(txtNombre, g);
        g.gridx=0; g.gridy=2; grid.add(label("Email:"), g);
        g.gridx=1; grid.add(txtEmail, g);
        g.gridx=0; g.gridy=3; grid.add(label("Telefono:"), g);
        g.gridx=1; grid.add(txtTelefono, g);
        g.gridx=0; g.gridy=4; grid.add(label("Password:"), g);
        g.gridx=1; grid.add(txtPassword, g);
        g.gridx=0; g.gridy=5; grid.add(label("Rol:*"), g);
        g.gridx=1; grid.add(cmbRol, g);
        g.gridx=1; g.gridy=6; grid.add(chkActivo, g);

        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 10));
        panelBotones.setBackground(UITheme.BG_CARD);
        panelBotones.add(btnGuardar);
        panelBotones.add(btnNuevo);
        panelBotones.add(btnEliminar);

        panelForm.add(grid, BorderLayout.CENTER);
        panelForm.add(panelBotones, BorderLayout.SOUTH);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
            UITheme.createScrollPane(tblUsuarios), panelForm);
        split.setDividerLocation(500);
        split.setBackground(UITheme.BG_PRIMARY);
        split.setBorder(null);

        JPanel top = new JPanel(new BorderLayout());
        top.setBackground(UITheme.BG_PRIMARY);
        top.add(lblTitulo, BorderLayout.WEST);

        add(top, BorderLayout.NORTH);
        add(split, BorderLayout.CENTER);

        // Eventos
        tblUsuarios.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblUsuarios.getSelectedRow();
                if (row >= 0) cargarFormulario(row);
            }
        });
        btnGuardar.addActionListener(e -> guardar());
        btnNuevo.addActionListener(e -> limpiar());
        btnEliminar.addActionListener(e -> eliminar());
    }

private void cargarUsuarios() {
    SwingWorker<List<Usuario>, Void> worker = new SwingWorker<>() {
        @Override protected List<Usuario> doInBackground() {
            return usuarioController.listarTodos();
        }
        @Override protected void done() {
            try {
                List<Usuario> lista = get();
                tableModel.setRowCount(0);
                for (Usuario u : lista) {
                    tableModel.addRow(new Object[]{
                        u.getId(), u.getUsername(), u.getNombreCompleto(),
                        u.getEmail(), u.getRol(), u.isActivo() ? "Si" : "No"
                    });
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(GestionUsuariosPanel.this,
                    "Error al cargar usuarios: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    };
    worker.execute();
}


    private void cargarFormulario(int row) {
        currentId = (int) tableModel.getValueAt(row, 0);
        txtUsername.setText((String) tableModel.getValueAt(row, 1));
        txtNombre.setText((String) tableModel.getValueAt(row, 2));
        txtEmail.setText((String) tableModel.getValueAt(row, 3));
        cmbRol.setSelectedItem(tableModel.getValueAt(row, 4));
        chkActivo.setSelected("Si".equals(tableModel.getValueAt(row, 5)));
        txtPassword.setText("");
        txtPassword.setToolTipText("Dejar vacio para no cambiar password");
    }

 private void guardar() {
    if (txtUsername.getText().trim().isEmpty() || 
        txtNombre.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "Username y Nombre son obligatorios",
            "Campos requeridos", JOptionPane.WARNING_MESSAGE);
        return;
    }

    Usuario u = new Usuario();
    u.setId(currentId);
    u.setUsername(txtUsername.getText().trim());
    u.setNombreCompleto(txtNombre.getText().trim());
    u.setEmail(txtEmail.getText().trim());
    u.setTelefono(txtTelefono.getText().trim());
    u.setRol((String) cmbRol.getSelectedItem());
    u.setActivo(chkActivo.isSelected());

    String pass = new String(txtPassword.getPassword());

    SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
        @Override protected Boolean doInBackground() {
            if (currentId == 0) {
                if (pass.isEmpty()) return false; // password obligatorio
                u.setPassword(pass);
                return usuarioController.crearUsuario(u);
            } else {
                boolean ok = usuarioController.actualizarUsuario(u);
                if (ok && !pass.isEmpty()) {
                    usuarioController.cambiarPassword(currentId, pass);
                }
                return ok;
            }
        }
        @Override protected void done() {
            try {
                if (get()) {
                    JOptionPane.showMessageDialog(GestionUsuariosPanel.this,
                        "Usuario guardado exitosamente",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    limpiar();
                    cargarUsuarios();
                } else {
                    JOptionPane.showMessageDialog(GestionUsuariosPanel.this,
                        "Error al guardar usuario",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(GestionUsuariosPanel.this,
                    "Error interno: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    };
    worker.execute();
}

private void eliminar() {
    int row = tblUsuarios.getSelectedRow();
    if (row < 0) {
        JOptionPane.showMessageDialog(this,
            "Selecciona un usuario", "Sin selección",
            JOptionPane.WARNING_MESSAGE);
        return;
    }
    String nombre = (String) tableModel.getValueAt(row, 2);
    int confirm = JOptionPane.showConfirmDialog(this,
        "Desactivar usuario: " + nombre + "?",
        "Confirmar", JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE);

    if (confirm == JOptionPane.YES_OPTION) {
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override protected Boolean doInBackground() {
                return usuarioController.eliminarUsuario(currentId);
            }
            @Override protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(GestionUsuariosPanel.this,
                            "Usuario desactivado",
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        limpiar();
                        cargarUsuarios();
                    } else {
                        JOptionPane.showMessageDialog(GestionUsuariosPanel.this,
                            "Error al desactivar usuario",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(GestionUsuariosPanel.this,
                        "Error interno: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}

    private void limpiar() {
        currentId = 0;
        txtUsername.setText("");
        txtNombre.setText("");
        txtEmail.setText("");
        txtTelefono.setText("");
        txtPassword.setText("");
        cmbRol.setSelectedIndex(0);
        chkActivo.setSelected(true);
        tblUsuarios.clearSelection();
        txtUsername.requestFocus();
    }

    private JLabel label(String text) {
        JLabel l = new JLabel(text);
        l.setFont(UITheme.FONT_BODY);
        l.setForeground(UITheme.TEXT_SECONDARY);
        return l;
    }

    private void estilizarCampo(JTextField f) {
        f.setFont(UITheme.FONT_BODY);
        f.setForeground(UITheme.TEXT_PRIMARY);
        f.setBackground(UITheme.BG_HOVER);
        f.setCaretColor(UITheme.TEXT_PRIMARY);
        f.setBorder(BorderFactory.createCompoundBorder(
            new UITheme.RoundedBorder(10, UITheme.BORDER_COLOR),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        f.setPreferredSize(new Dimension(200, 36));
    }

    @SuppressWarnings("unchecked")
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
