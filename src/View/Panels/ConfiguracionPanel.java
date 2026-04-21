
package View.Panels;


import Controller.ConfigController;
import Modelo.Entidades.ComisionRegla;
import Utils.UITheme;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;


public class ConfiguracionPanel extends javax.swing.JPanel {
    private JTable tblComisiones;
    private DefaultTableModel tableModel;
    private JComboBox<String> cmbAseguradora;
    private JComboBox<String> cmbTipoVehiculo;
    private JComboBox<String> cmbCanal;
    private JTextField txtPorcentaje;
    private JCheckBox chkActivo;
    private JButton btnGuardar;
    private JButton btnNuevo;
    private JButton btnEliminar;
    
    private ConfigController configController;
    private int currentId = 0;
    
    public ConfiguracionPanel() {
        initControllers();
        initComponentsManual();
        setupLayout();
        setupEvents();
        cargarReglas();
    }
  private void initComponentsManual() {
    tableModel = new DefaultTableModel(
        new String[]{"ID", "Aseguradora", "Tipo Vehículo", "Canal", "Comisión %", "Activo"}, 0
    ) {
        @Override
        public boolean isCellEditable(int row, int col) { return false; }
    };
    tblComisiones = new JTable(tableModel);
    tblComisiones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    cmbAseguradora = new JComboBox<>(new String[]{
        "Mapfre", "Rimac", "Pacífico", "La Positiva", "Interseguro", "HDI"
    });
    cmbTipoVehiculo = new JComboBox<>(new String[]{
        "Auto", "Camioneta", "Moto", "Camión", "Bus", "Otro"
    });
    cmbCanal = new JComboBox<>(new String[]{
        "Particular", "Flota", "Corporativo", "Taxi"
    });

    txtPorcentaje = new JTextField();
    chkActivo = new JCheckBox("Activo", true);

btnGuardar = new JButton("Guardar");
btnNuevo   = new JButton("Nuevo");
btnEliminar= new JButton("Eliminar");

// Paleta
Color azulMarino = new Color(10, 77, 132);   // principal
Color turquesa   = new Color(29, 175, 206);  // secundario
Color grisClaro  = new Color(204, 209, 211); // neutral

// Estilos
estilizarBoton(btnGuardar, azulMarino, Color.WHITE);  // botón principal
estilizarBoton(btnNuevo, turquesa, Color.WHITE);      // botón secundario
estilizarBoton(btnEliminar, grisClaro, Color.BLACK);  // botón neutral


    // Formulario
    JPanel panelForm = new JPanel(new GridBagLayout());
    panelForm.setBorder(BorderFactory.createTitledBorder("Regla de Comisión"));
    GridBagConstraints gbc = new GridBagConstraints();
    gbc.insets = new Insets(4, 4, 4, 4);
    gbc.fill = GridBagConstraints.HORIZONTAL;

    gbc.gridx = 0; gbc.gridy = 0; panelForm.add(new JLabel("Aseguradora:"), gbc);
    gbc.gridx = 1; panelForm.add(cmbAseguradora, gbc);
    gbc.gridx = 0; gbc.gridy = 1; panelForm.add(new JLabel("Tipo Vehículo:"), gbc);
    gbc.gridx = 1; panelForm.add(cmbTipoVehiculo, gbc);
    gbc.gridx = 0; gbc.gridy = 2; panelForm.add(new JLabel("Canal:"), gbc);
    gbc.gridx = 1; panelForm.add(cmbCanal, gbc);
    gbc.gridx = 0; gbc.gridy = 3; panelForm.add(new JLabel("Comisión %:*"), gbc);
    gbc.gridx = 1; panelForm.add(txtPorcentaje, gbc);
    gbc.gridx = 0; gbc.gridy = 4; panelForm.add(chkActivo, gbc);

    JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelBotones.add(btnGuardar);
    panelBotones.add(btnNuevo);
    panelBotones.add(btnEliminar);

    JPanel panelDerecho = new JPanel(new BorderLayout());
    panelDerecho.add(panelForm, BorderLayout.CENTER);
    panelDerecho.add(panelBotones, BorderLayout.SOUTH);

    JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
        new JScrollPane(tblComisiones), panelDerecho);
    split.setDividerLocation(450);

    setLayout(new BorderLayout());
    add(split, BorderLayout.CENTER);
}

        private void initControllers() {
        configController = new ConfigController();
    }
    
    
    private void setupLayout() {
        // Configuración adicional
    }
    
    private void setupEvents() {
        tblComisiones.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblComisiones.getSelectedRow();
                if (row >= 0) {
                    cargarFormularioDesdeTabla(row);
                }
            }
        });
        
        btnGuardar.addActionListener(e -> guardarRegla());
        btnNuevo.addActionListener(e -> limpiarFormulario());
        btnEliminar.addActionListener(e -> eliminarRegla());
    }
        
private void estilizarBoton(JButton boton, Color fondo, Color texto) {
    if (boton == null) return;

    boton.setBackground(fondo);
    boton.setForeground(texto);
    boton.setFocusPainted(false);
    boton.setFont(new Font("Segoe UI", Font.BOLD, 14));
    boton.setOpaque(true);

    // Bordes redondeados con padding
    boton.setBorder(BorderFactory.createCompoundBorder(
        new UITheme.RoundedBorder(10, fondo.darker()), // borde redondeado
        BorderFactory.createEmptyBorder(8, 16, 8, 16)  // padding interno
    ));
}
  
private void cargarReglas() {
    SwingWorker<List<ComisionRegla>, Void> worker = new SwingWorker<>() {
        @Override
        protected List<ComisionRegla> doInBackground() {
            return configController.listarReglas();
        }
        @Override
        protected void done() {
            try {
                List<ComisionRegla> lista = get();
                tableModel.setRowCount(0);
                for (ComisionRegla regla : lista) {
                    Object[] row = {
                        regla.getId(),
                        regla.getAseguradora(),
                        regla.getTipoVehiculo(),
                        regla.getCanal(),
                        regla.getPorcentaje(),
                        regla.isActivo() ? "Sí" : "No"
                    };
                    tableModel.addRow(row);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    worker.execute();
}
    
    private void cargarFormularioDesdeTabla(int row) {
        currentId = (int) tableModel.getValueAt(row, 0);
        cmbAseguradora.setSelectedItem(tableModel.getValueAt(row, 1));
        cmbTipoVehiculo.setSelectedItem(tableModel.getValueAt(row, 2));
        cmbCanal.setSelectedItem(tableModel.getValueAt(row, 3));
        txtPorcentaje.setText(String.valueOf(tableModel.getValueAt(row, 4)));
        chkActivo.setSelected("Sí".equals(tableModel.getValueAt(row, 5)));
    }
    
private void guardarRegla() {
    if (txtPorcentaje.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "Ingrese el porcentaje de comisión",
            "Campo requerido",
            JOptionPane.WARNING_MESSAGE);
        txtPorcentaje.requestFocus();
        return;
    }

    double porcentaje;
    try {
        porcentaje = Double.parseDouble(txtPorcentaje.getText().trim());
        if (porcentaje < 0 || porcentaje > 100) throw new NumberFormatException();
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this,
            "Porcentaje inválido (debe ser entre 0 y 100)",
            "Error de formato",
            JOptionPane.ERROR_MESSAGE);
        return;
    }

    ComisionRegla regla = new ComisionRegla();
    regla.setId(currentId);
    regla.setAseguradora((String) cmbAseguradora.getSelectedItem());
    regla.setTipoVehiculo((String) cmbTipoVehiculo.getSelectedItem());
    regla.setCanal((String) cmbCanal.getSelectedItem());
    regla.setPorcentaje(porcentaje);
    regla.setActivo(chkActivo.isSelected());

    SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
        @Override
        protected Boolean doInBackground() {
            return configController.guardarRegla(regla);
        }
        @Override
        protected void done() {
            try {
                if (get()) {
                    JOptionPane.showMessageDialog(ConfiguracionPanel.this,
                        "Regla guardada exitosamente",
                        "Éxito", JOptionPane.INFORMATION_MESSAGE);
                    limpiarFormulario();
                    cargarReglas();
                } else {
                    JOptionPane.showMessageDialog(ConfiguracionPanel.this,
                        "Error al guardar la regla",
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(ConfiguracionPanel.this,
                    "Error interno: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    };
    worker.execute();
}

    
private void eliminarRegla() {
    int row = tblComisiones.getSelectedRow();
    if (row < 0) {
        JOptionPane.showMessageDialog(this,
            "Seleccione una regla para eliminar",
            "Sin selección",
            JOptionPane.WARNING_MESSAGE);
        return;
    }

    int id = (int) tableModel.getValueAt(row, 0);
    String descripcion = tableModel.getValueAt(row, 1) + " - " +
                         tableModel.getValueAt(row, 2) + " - " +
                         tableModel.getValueAt(row, 3);

    int confirm = JOptionPane.showConfirmDialog(this,
        "¿Eliminar regla: " + descripcion + "?\nEsta acción no se puede deshacer.",
        "Confirmar eliminación",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE);

    if (confirm == JOptionPane.YES_OPTION) {
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override
            protected Boolean doInBackground() {
                return configController.eliminarRegla(id);
            }
            @Override
            protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(ConfiguracionPanel.this,
                            "Regla eliminada",
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        limpiarFormulario();
                        cargarReglas();
                    } else {
                        JOptionPane.showMessageDialog(ConfiguracionPanel.this,
                            "Error al eliminar la regla",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(ConfiguracionPanel.this,
                        "Error interno: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}

    
    private void limpiarFormulario() {
        currentId = 0;
        cmbAseguradora.setSelectedIndex(0);
        cmbTipoVehiculo.setSelectedIndex(0);
        cmbCanal.setSelectedIndex(0);
        txtPorcentaje.setText("");
        chkActivo.setSelected(true);
        txtPorcentaje.requestFocus();
        tblComisiones.clearSelection();
    }


    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
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
