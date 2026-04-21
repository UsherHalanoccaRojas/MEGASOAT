
package View.Panels;


import Controller.PaymentController;
import Modelo.Entidades.Venta;
import Modelo.Entidades.Voucher;
import Services.VentaService;
import Utils.UITheme;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.util.List;

import javax.swing.JTable;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

/**
 *
 * @author USUARIO
 */
public class ValidarPagosPanel extends javax.swing.JPanel {
    private JTable tblVentasPendientes;
    private DefaultTableModel tableModel;
    private JTextField txtNumOperacion;
    private JComboBox<String> cmbBanco;
    private JComboBox<String> cmbTipoPago;
    private JTextField txtMonto;
    private JTextArea txtObservaciones;
    private JButton btnRegistrarVoucher;
    private JButton btnConciliarMultiples;
    private JLabel lblTotalSeleccionadas;
    
    private PaymentController paymentController;
    private VentaService ventaService;
    private List<Venta> ventasPendientes;
    
    private int currentUserId;
    public ValidarPagosPanel(int usuarioId) {
        this.currentUserId = usuarioId;
        initControllers();
        initComponentsManual();
        setupLayout();
        setupEvents();
        cargarVentasPendientes();
    }
    private void initControllers() {
        paymentController = new PaymentController();
        ventaService = new VentaService();
    }
 private void initComponentsManual() {
    setBackground(UITheme.BG_PRIMARY);
    setLayout(new BorderLayout(12, 12));
    setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

    // Tabla
    tableModel = new DefaultTableModel(
        new String[]{"ID", "Punto de Venta", "Placa", "Aseguradora", "Prima S/.", "Comisión S/."}, 0
    ) {
        @Override public boolean isCellEditable(int r, int c) { return false; }
    };
    tblVentasPendientes = new JTable(tableModel);
    UITheme.styleTable(tblVentasPendientes);
    tblVentasPendientes.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

    JScrollPane scroll = UITheme.createScrollPane(tblVentasPendientes);

    // Label total
    lblTotalSeleccionadas = new JLabel("Seleccionadas: 0  |  Total: S/ 0.00");
    lblTotalSeleccionadas.setFont(UITheme.FONT_BODY);
    lblTotalSeleccionadas.setForeground(UITheme.ACCENT_GREEN);
    lblTotalSeleccionadas.setBorder(BorderFactory.createEmptyBorder(6, 0, 0, 0));

    JPanel panelTabla = new JPanel(new BorderLayout(0, 6));
    panelTabla.setBackground(UITheme.BG_PRIMARY);
    panelTabla.add(scroll, BorderLayout.CENTER);
    panelTabla.add(lblTotalSeleccionadas, BorderLayout.SOUTH);

    // Formulario voucher
    txtNumOperacion = UITheme.createTextField("N° Operación");
    txtMonto        = UITheme.createTextField("Monto S/.");
    txtObservaciones = new JTextArea(3, 20);
    txtObservaciones.setFont(UITheme.FONT_BODY);
    txtObservaciones.setForeground(UITheme.TEXT_PRIMARY);
    txtObservaciones.setBackground(UITheme.BG_HOVER);
    txtObservaciones.setCaretColor(UITheme.TEXT_PRIMARY);
    txtObservaciones.setBorder(BorderFactory.createCompoundBorder(
        new UITheme.RoundedBorder(10, UITheme.BORDER_COLOR),
        BorderFactory.createEmptyBorder(6, 10, 6, 10)
    ));

    cmbBanco    = UITheme.createComboBox(new String[]{"BCP","BBVA","Scotiabank","Interbank","Yape","Plin"});
    cmbTipoPago = UITheme.createComboBox(new String[]{"Yape","Transferencia","Efectivo","Plin"});

    btnRegistrarVoucher  = UITheme.createPrimaryButton("✅ Registrar Voucher");
    btnConciliarMultiples = UITheme.createSecondaryButton("🔗 Conciliar Múltiples");

    JPanel panelForm = UITheme.createCard("💳 Registrar Pago");
    JPanel grid = new JPanel(new GridBagLayout());
    grid.setBackground(UITheme.BG_CARD);
    GridBagConstraints g = new GridBagConstraints();
    g.insets = new Insets(6, 6, 6, 6);
    g.fill = GridBagConstraints.HORIZONTAL;

    g.gridx=0; g.gridy=0; grid.add(label("N° Operación:*"), g);
    g.gridx=1; grid.add(txtNumOperacion, g);
    g.gridx=0; g.gridy=1; grid.add(label("Banco:"), g);
    g.gridx=1; grid.add(cmbBanco, g);
    g.gridx=0; g.gridy=2; grid.add(label("Tipo de Pago:*"), g);
    g.gridx=1; grid.add(cmbTipoPago, g);
    g.gridx=0; g.gridy=3; grid.add(label("Monto S/:*"), g);
    g.gridx=1; grid.add(txtMonto, g);
    g.gridx=0; g.gridy=4; grid.add(label("Observaciones:"), g);
    g.gridx=1; grid.add(new JScrollPane(txtObservaciones), g);

    JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
    panelBotones.setBackground(UITheme.BG_CARD);
    panelBotones.add(btnRegistrarVoucher);
    panelBotones.add(btnConciliarMultiples);

    panelForm.add(grid, BorderLayout.CENTER);
    panelForm.add(panelBotones, BorderLayout.SOUTH);

    add(panelTabla, BorderLayout.CENTER);
    add(panelForm, BorderLayout.EAST);
}

private JLabel label(String text) {
    JLabel l = new JLabel(text);
    l.setFont(UITheme.FONT_BODY);
    l.setForeground(UITheme.TEXT_SECONDARY);
    return l;
}
    
    private void setupLayout() {
        
    }
    
    private void setupEvents() {
        tblVentasPendientes.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                actualizarTotalSeleccionado();
            }
        });
        
        btnRegistrarVoucher.addActionListener(e -> registrarVoucher());
        btnConciliarMultiples.addActionListener(e -> conciliarMultiples());
        
        cmbTipoPago.addActionListener(e -> {
            String tipo = (String) cmbTipoPago.getSelectedItem();
            if ("Efectivo".equals(tipo)) {
                cmbBanco.setEnabled(false);
            } else {
                cmbBanco.setEnabled(true);
            }
        });
    }
    
private void cargarVentasPendientes() {
    SwingWorker<List<Venta>, Void> worker = new SwingWorker<>() {
        @Override protected List<Venta> doInBackground() {
            return paymentController.getVentasPendientes();
        }
        @Override protected void done() {
            try {
                ventasPendientes = get();
                tableModel.setRowCount(0);
                for (Venta venta : ventasPendientes) {
                    Object[] row = {
                        venta.getId(),
                        venta.getPvNombre(),
                        venta.getPlaca(),
                        venta.getAseguradora(),
                        String.format("%.2f", venta.getPrima()),
                        String.format("%.2f", venta.getComisionPV())
                    };
                    tableModel.addRow(row);
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(ValidarPagosPanel.this,
                    "Error al cargar ventas: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    };
    worker.execute();
}
  
    private void actualizarTotalSeleccionado() {
        int[] selectedRows = tblVentasPendientes.getSelectedRows();
        double total = 0;
        
        for (int row : selectedRows) {
            double prima = Double.parseDouble((String) tableModel.getValueAt(row, 4));
            total += prima;
        }
        
        lblTotalSeleccionadas.setText(String.format("Seleccionadas: %d | Total: S/ %.2f", 
            selectedRows.length, total));
    }
    
private void registrarVoucher() {
    // Validar campos
    String operationNumber = txtNumOperacion.getText().trim();
    if (operationNumber.isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "Ingrese el número de operación",
            "Campo requerido",
            JOptionPane.WARNING_MESSAGE);
        txtNumOperacion.requestFocus();
        return;
    }
    if (!operationNumber.matches("\\d{6,20}")) {
        JOptionPane.showMessageDialog(this,
            "Número de operación inválido (solo dígitos, mínimo 6)",
            "Error de formato",
            JOptionPane.ERROR_MESSAGE);
        return;
    }

    if (txtMonto.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this,
            "Ingrese el monto",
            "Campo requerido",
            JOptionPane.WARNING_MESSAGE);
        txtMonto.requestFocus();
        return;
    }

    double monto;
    try {
        monto = Double.parseDouble(txtMonto.getText().trim());
        if (monto <= 0) {
            JOptionPane.showMessageDialog(this,
                "El monto debe ser mayor a 0",
                "Error de validación",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
    } catch (NumberFormatException e) {
        JOptionPane.showMessageDialog(this,
            "Monto inválido (solo números)",
            "Error de formato",
            JOptionPane.ERROR_MESSAGE);
        return;
    }

    if (paymentController.existeNumeroOperacion(operationNumber)) {
        JOptionPane.showMessageDialog(this,
            "⚠️ ERROR: Este número de operación ya existe en el sistema.",
            "Alerta de Seguridad",
            JOptionPane.ERROR_MESSAGE);
        return;
    }

    int[] selectedRows = tblVentasPendientes.getSelectedRows();
    if (selectedRows.length == 0) {
        JOptionPane.showMessageDialog(this,
            "Seleccione al menos un SOAT para pagar",
            "Sin selección",
            JOptionPane.WARNING_MESSAGE);
        return;
    }

    int confirm = JOptionPane.showConfirmDialog(this,
        "¿Registrar voucher #" + operationNumber + " para pagar " + selectedRows.length + " SOAT(s)?\n" +
        "Total a pagar: S/ " + String.format("%.2f", monto),
        "Confirmar pago",
        JOptionPane.YES_NO_OPTION);

    if (confirm != JOptionPane.YES_OPTION) return;

    String banco = (String) cmbBanco.getSelectedItem();
    String tipo = (String) cmbTipoPago.getSelectedItem();
    Voucher voucher = paymentController.registrarVoucher(
        operationNumber, banco, monto, tipo, currentUserId, txtObservaciones.getText()
    );

    if (voucher == null) {
        JOptionPane.showMessageDialog(this,
            "Error al registrar el voucher",
            "Error",
            JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Conciliar pagos y marcar como pagados
    for (int row : selectedRows) {
        int ventaId = (int) tableModel.getValueAt(row, 0);
        Venta venta = ventaService.getVentaById(ventaId);
        boolean ok = paymentController.asignarVoucherAVenta(venta, voucher, currentUserId);
        if (!ok) {
            JOptionPane.showMessageDialog(this,
                "Error al conciliar la venta ID " + ventaId,
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    JOptionPane.showMessageDialog(this,
        "✅ Pago registrado exitosamente\n" +
        "Se pagaron " + selectedRows.length + " SOAT(s) con voucher #" + operationNumber,
        "Éxito",
        JOptionPane.INFORMATION_MESSAGE);

    limpiarFormulario();
    cargarVentasPendientes(); 
}
private void conciliarMultiples() {
    JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this),
        "Conciliación Múltiple", true);
    dialog.setSize(500, 400);
    dialog.setLocationRelativeTo(this);

    int[] selectedRows = tblVentasPendientes.getSelectedRows();
    if (selectedRows.length == 0) {
        JOptionPane.showMessageDialog(this,
            "Seleccione los SOATs que desea pagar con un solo voucher",
            "Sin selección",
            JOptionPane.WARNING_MESSAGE);
        return;
    }

    double total = 0;
    for (int row : selectedRows) {
        total += Double.parseDouble((String) tableModel.getValueAt(row, 4));
    }
    final double totalFinal = total;

    JTextField txtOp = UITheme.createTextField("N° Operación");
    JComboBox<String> cmbBancoMulti = UITheme.createComboBox(new String[]{"BCP", "BBVA", "Scotiabank", "Interbank", "Yape", "Plin"});
    JComboBox<String> cmbTipoMulti = UITheme.createComboBox(new String[]{"Yape", "Transferencia", "Efectivo", "Plin"});
    JTextField txtMontoMulti = UITheme.createTextField("Monto Total");
    txtMontoMulti.setText(String.format("%.2f", total));
    txtMontoMulti.setEditable(false);

    JButton btnConfirmar = UITheme.createPrimaryButton("✅ Confirmar Pago Múltiple");
    JButton btnCancelar  = UITheme.createSecondaryButton("Cancelar");

    // Panel de campos
    JPanel panelCampos = new JPanel(new GridBagLayout());
    GridBagConstraints g = new GridBagConstraints();
    g.insets = new Insets(6, 6, 6, 6);
    g.fill = GridBagConstraints.HORIZONTAL;

    g.gridx = 0; g.gridy = 0; panelCampos.add(new JLabel("N° Operación:"), g);
    g.gridx = 1; panelCampos.add(txtOp, g);

    g.gridx = 0; g.gridy++; panelCampos.add(new JLabel("Banco:"), g);
    g.gridx = 1; panelCampos.add(cmbBancoMulti, g);

    g.gridx = 0; g.gridy++; panelCampos.add(new JLabel("Tipo de Pago:"), g);
    g.gridx = 1; panelCampos.add(cmbTipoMulti, g);

    g.gridx = 0; g.gridy++; panelCampos.add(new JLabel("Monto Total:"), g);
    g.gridx = 1; panelCampos.add(txtMontoMulti, g);

    // Panel de botones
    JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER));
    panelBotones.add(btnConfirmar);
    panelBotones.add(btnCancelar);

    // Panel principal
    JPanel panel = new JPanel(new BorderLayout());
    panel.add(panelCampos, BorderLayout.CENTER);
    panel.add(panelBotones, BorderLayout.SOUTH);

    dialog.add(panel);

    // Eventos
    btnConfirmar.addActionListener(e -> {
        String operationNumber = txtOp.getText().trim();
        if (operationNumber.isEmpty() || !operationNumber.matches("\\d{6,20}")) {
            JOptionPane.showMessageDialog(dialog,
                "Número de operación inválido",
                "Error de formato",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (paymentController.existeNumeroOperacion(operationNumber)) {
            JOptionPane.showMessageDialog(dialog,
                "ERROR: Número de operación ya existe",
                "Voucher duplicado",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<Integer> ventasIds = new java.util.ArrayList<>();
        for (int row : selectedRows) {
            ventasIds.add((int) tableModel.getValueAt(row, 0));
        }

        String banco = (String) cmbBancoMulti.getSelectedItem();
        String tipo = (String) cmbTipoMulti.getSelectedItem();

        boolean success = paymentController.conciliarMultiplesVentasConNuevoVoucher(
            ventasIds, operationNumber, banco, totalFinal, tipo, currentUserId
        );

        if (success) {
            JOptionPane.showMessageDialog(dialog,
                "✅ " + ventasIds.size() + " SOAT(s) pagados exitosamente con voucher #" + operationNumber,
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
            cargarVentasPendientes();
        } else {
            JOptionPane.showMessageDialog(dialog,
                "Error al procesar el pago",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    });

    btnCancelar.addActionListener(e -> dialog.dispose());

    dialog.setVisible(true);
}  
    private void refrescarListaPendientes() {
   List<Venta> pendientes = ventaService.getVentasSinPagar();   
    tableModel.setRowCount(0); // limpiar tabla
    for (Venta v : pendientes) {
        tableModel.addRow(new Object[]{
            v.getId(),
            v.getPlaca(),
            v.getAseguradora(),
            v.getTipoVehiculo(),
            v.getPrima(),
            v.getEstado()
        });
    }
}

    
    private void limpiarFormulario() {
        txtNumOperacion.setText("");
        txtMonto.setText("");
        txtObservaciones.setText("");
        cmbBanco.setSelectedIndex(0);
        cmbTipoPago.setSelectedIndex(0);
        txtNumOperacion.requestFocus();
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
