
package View.Panels;


import Controller.PVController;
import Modelo.Entidades.PuntoVenta;
import Utils.UITheme;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
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

/**
 *
 * @author USUARIO
 */
public class GestionPVPanel extends javax.swing.JPanel {
    private JTable tblPuntosVenta;
    private DefaultTableModel tableModel;
    private JTextField txtNombre;
    private JTextField txtCiudad;
    private JTextField txtTelefono;
    private JTextField txtDireccion;
    private JTextField txtComisionPersonalizada;
    private JCheckBox chkActivo;
    private JButton btnGuardar;
    private JButton btnNuevo;
    private JButton btnEliminar;
    private JButton btnBuscar;
    
    private PVController pvController;
    private int currentId = 0;
    
    public GestionPVPanel() {
        initControllers();
        initComponentsManual();
        setupLayout();
        setupEvents();
        cargarPuntosVenta();
    }
private void initComponentsManual() {
    setBackground(UITheme.BG_PRIMARY);
    setLayout(new BorderLayout(12, 12));
    setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

    tableModel = new DefaultTableModel(
        new String[]{"ID", "Nombre", "Ciudad", "Teléfono", "Comisión", "Activo"}, 0
    ) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    tblPuntosVenta = new JTable(tableModel);
    UITheme.styleTable(tblPuntosVenta);
    tblPuntosVenta.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

    txtNombre               = UITheme.createTextField("Nombre");
    txtCiudad               = UITheme.createTextField("Ciudad");
    txtTelefono             = UITheme.createTextField("Teléfono");
    txtDireccion            = UITheme.createTextField("Dirección");
    txtComisionPersonalizada= UITheme.createTextField("Ej: 15.0");
    chkActivo = new JCheckBox("Activo", true);
    chkActivo.setFont(UITheme.FONT_BODY);
    chkActivo.setForeground(UITheme.TEXT_PRIMARY);
    chkActivo.setBackground(UITheme.BG_CARD);

    btnGuardar  = UITheme.createPrimaryButton("💾 Guardar");
    btnNuevo    = UITheme.createSecondaryButton("➕ Nuevo");
    btnEliminar = UITheme.createDangerButton("🗑 Eliminar");
    btnBuscar   = UITheme.createSecondaryButton("🔍 Buscar ciudad");

    JPanel panelForm = UITheme.createCard("🏪 Datos del Punto de Venta");
    JPanel grid = new JPanel(new GridBagLayout());
    grid.setBackground(UITheme.BG_CARD);
    GridBagConstraints g = new GridBagConstraints();
    g.insets = new Insets(6, 6, 6, 6);
    g.fill = GridBagConstraints.HORIZONTAL;

    g.gridx=0; g.gridy=0; grid.add(label("Nombre:*"), g);
    g.gridx=1; grid.add(txtNombre, g);
    g.gridx=0; g.gridy=1; grid.add(label("Ciudad:"), g);
    g.gridx=1; grid.add(txtCiudad, g);
    g.gridx=0; g.gridy=2; grid.add(label("Teléfono:"), g);
    g.gridx=1; grid.add(txtTelefono, g);
    g.gridx=0; g.gridy=3; grid.add(label("Dirección:"), g);
    g.gridx=1; grid.add(txtDireccion, g);
    g.gridx=0; g.gridy=4; grid.add(label("Comisión %:"), g);
    g.gridx=1; grid.add(txtComisionPersonalizada, g);
    g.gridx=1; g.gridy=5; grid.add(chkActivo, g);

    JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 10));
    panelBotones.setBackground(UITheme.BG_CARD);
    panelBotones.add(btnGuardar);
    panelBotones.add(btnNuevo);
    panelBotones.add(btnEliminar);
    panelBotones.add(btnBuscar);

    panelForm.add(grid, BorderLayout.CENTER);
    panelForm.add(panelBotones, BorderLayout.SOUTH);

    JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
        UITheme.createScrollPane(tblPuntosVenta), panelForm);
    split.setDividerLocation(430);
    split.setBackground(UITheme.BG_PRIMARY);
    split.setBorder(null);

    add(split, BorderLayout.CENTER);
}

private JLabel label(String text) {
    JLabel l = new JLabel(text);
    l.setFont(UITheme.FONT_BODY);
    l.setForeground(UITheme.TEXT_SECONDARY);
    return l;
}
        private void initControllers() {
        pvController = new PVController();
    }
    
    
    private void setupLayout() {
        // Configuración adicional
    }
    
    private void setupEvents() {
        tblPuntosVenta.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                int row = tblPuntosVenta.getSelectedRow();
                if (row >= 0) {
                    cargarFormularioDesdeTabla(row);
                }
            }
        });
        
        btnGuardar.addActionListener(e -> guardarPV());
        btnNuevo.addActionListener(e -> limpiarFormulario());
        btnEliminar.addActionListener(e -> eliminarPV());
        btnBuscar.addActionListener(e -> buscarPV());
    }
    
    private void cargarPuntosVenta() {
        SwingWorker<List<PuntoVenta>, Void> worker = new SwingWorker<>() {
            @Override protected List<PuntoVenta> doInBackground() {
                return pvController.listarTodos();
            }
            @Override protected void done() {
                try {
                    List<PuntoVenta> lista = get();
                    tableModel.setRowCount(0);
                    for (PuntoVenta pv : lista) {
                        Object[] row = {
                            pv.getId(),
                            pv.getNombre(),
                            pv.getCiudad(),
                            pv.getTelefono(),
                            pv.getComisionPersonalizada() != null ? pv.getComisionPersonalizada() : "General",
                            pv.isActivo() ? "Sí" : "No"
                        };
                        tableModel.addRow(row);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(GestionPVPanel.this,
                        "Error al cargar puntos de venta: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
    private void cargarFormularioDesdeTabla(int row) {
        currentId = (int) tableModel.getValueAt(row, 0);
        txtNombre.setText((String) tableModel.getValueAt(row, 1));
        txtCiudad.setText((String) tableModel.getValueAt(row, 2));
        txtTelefono.setText((String) tableModel.getValueAt(row, 3));
        
        Object comision = tableModel.getValueAt(row, 4);
        if (comision instanceof Double) {
            txtComisionPersonalizada.setText(String.valueOf(comision));
        } else {
            txtComisionPersonalizada.setText("");
        }
        
        chkActivo.setSelected("Sí".equals(tableModel.getValueAt(row, 5)));
    }
    
 

    private void guardarPV() {
        if (txtNombre.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio",
                "Campo requerido", JOptionPane.WARNING_MESSAGE);
            txtNombre.requestFocus();
            return;
        }

        PuntoVenta pv = new PuntoVenta();
        pv.setId(currentId);
        pv.setNombre(txtNombre.getText().trim());
        pv.setCiudad(txtCiudad.getText().trim());
        pv.setTelefono(txtTelefono.getText().trim());
        pv.setDireccion(txtDireccion.getText().trim());
        pv.setActivo(chkActivo.isSelected());

        if (!txtComisionPersonalizada.getText().trim().isEmpty()) {
            try {
                double comision = Double.parseDouble(txtComisionPersonalizada.getText().trim());
                pv.setComisionPersonalizada(comision);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Comisión inválida",
                    "Error de formato", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }

        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override protected Boolean doInBackground() {
                if (currentId == 0) return pvController.registrarPV(pv);
                else {
                    boolean ok = pvController.actualizarPV(pv);
                    pvController.cambiarEstadoPV(pv.getId(), pv.isActivo());
                    return ok;
                }
            }
            @Override protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(GestionPVPanel.this,
                            "Punto de Venta guardado exitosamente",
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        limpiarFormulario();
                        cargarPuntosVenta();
                    } else {
                        JOptionPane.showMessageDialog(GestionPVPanel.this,
                            "Error al guardar el punto de venta",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(GestionPVPanel.this,
                        "Error interno: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
    
private void eliminarPV() {
    int row = tblPuntosVenta.getSelectedRow();
    if (row < 0) {
        JOptionPane.showMessageDialog(this,
            "Seleccione un punto de venta para eliminar",
            "Sin selección", JOptionPane.WARNING_MESSAGE);
        return;
    }

    int id = (int) tableModel.getValueAt(row, 0);
    String nombre = (String) tableModel.getValueAt(row, 1);

    int confirm = JOptionPane.showConfirmDialog(this,
        "¿Eliminar " + nombre + "?\nEsta acción no se puede deshacer.",
        "Confirmar eliminación",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE);

    if (confirm == JOptionPane.YES_OPTION) {
        SwingWorker<Boolean, Void> worker = new SwingWorker<>() {
            @Override protected Boolean doInBackground() {
                return pvController.eliminarPV(id);
            }
            @Override protected void done() {
                try {
                    if (get()) {
                        JOptionPane.showMessageDialog(GestionPVPanel.this,
                            "Punto de Venta eliminado",
                            "Éxito", JOptionPane.INFORMATION_MESSAGE);
                        limpiarFormulario();
                        cargarPuntosVenta();
                    } else {
                        JOptionPane.showMessageDialog(GestionPVPanel.this,
                            "Error al eliminar",
                            "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(GestionPVPanel.this,
                        "Error interno: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}
 private void buscarPV() {
    String ciudad = JOptionPane.showInputDialog(this,
        "Ingrese la ciudad a buscar:",
        "Buscar por ciudad",
        JOptionPane.QUESTION_MESSAGE);

    if (ciudad != null && !ciudad.trim().isEmpty()) {
        SwingWorker<List<PuntoVenta>, Void> worker = new SwingWorker<>() {
            @Override protected List<PuntoVenta> doInBackground() {
                return pvController.buscarPorCiudad(ciudad.trim());
            }
            @Override protected void done() {
                try {
                    List<PuntoVenta> lista = get();
                    tableModel.setRowCount(0);
                    for (PuntoVenta pv : lista) {
                        Object[] row = {
                            pv.getId(),
                            pv.getNombre(),
                            pv.getCiudad(),
                            pv.getTelefono(),
                            pv.getComisionPersonalizada() != null ? pv.getComisionPersonalizada() : "General",
                            pv.isActivo() ? "Sí" : "No"
                        };
                        tableModel.addRow(row);
                    }
                    if (lista.isEmpty()) {
                        JOptionPane.showMessageDialog(GestionPVPanel.this,
                            "No se encontraron puntos de venta en " + ciudad,
                            "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
                        cargarPuntosVenta();
                    }
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(GestionPVPanel.this,
                        "Error en búsqueda: " + e.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        };
        worker.execute();
    }
}
    
    private void limpiarFormulario() {
        currentId = 0;
        txtNombre.setText("");
        txtCiudad.setText("");
        txtTelefono.setText("");
        txtDireccion.setText("");
        txtComisionPersonalizada.setText("");
        chkActivo.setSelected(true);
        txtNombre.requestFocus();
        tblPuntosVenta.clearSelection();
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
