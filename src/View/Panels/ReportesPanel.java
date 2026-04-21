
package View.Panels;


import Controller.ReportController;
import Utils.UITheme;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.table.DefaultTableModel;
import com.toedter.calendar.JDateChooser;

public class ReportesPanel extends javax.swing.JPanel {

    private JTabbedPane tabbedPane;
    private JTable tblDiario;
    private JTable tblPorPV;
    private JTable tblPorAseguradora;
    private JTable tblConciliacion;
    private JDateChooser dateChooserInicio;
    private JDateChooser dateChooserFin;
    private JButton btnGenerar;
    
    
    private ReportController reportController;
    public ReportesPanel() {
        
         initControllers();
        initComponentsManual();
        aplicarEstilosTablas();
        setupLayout();
        setupEvents();
        generarReportesDelDia();
    }
private void initComponentsManual() {
    // DateChoosers 
    dateChooserInicio = new JDateChooser();
    dateChooserInicio.setDateFormatString("dd/MM/yyyy");
    dateChooserInicio.setFont(UITheme.FONT_BODY);
    dateChooserInicio.setBackground(UITheme.BG_PRIMARY);

    dateChooserFin = new JDateChooser();
    dateChooserFin.setDateFormatString("dd/MM/yyyy");
    dateChooserFin.setFont(UITheme.FONT_BODY);
    dateChooserFin.setBackground(UITheme.BG_PRIMARY);

    // Botón
    btnGenerar = UITheme.createPrimaryButton("Generar Reporte");

    // Panel de filtros
    JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT));
    panelFiltros.setBorder(BorderFactory.createTitledBorder("Período"));
    panelFiltros.add(new JLabel("Desde:"));
    panelFiltros.add(dateChooserInicio);
    panelFiltros.add(new JLabel("Hasta:"));
    panelFiltros.add(dateChooserFin);
    panelFiltros.add(btnGenerar);

    // Inicialización de tablas con modelos vacíos
    tblDiario = new JTable(new DefaultTableModel(new Object[][]{}, new String[]{"Métrica", "Valor"}));
    tblPorPV = new JTable(new DefaultTableModel(new Object[][]{}, new String[]{"Punto de Venta", "Ciudad", "Cantidad", "Total Ventas (S/.)", "Comisiones (S/.)", "Cobrado (S/.)"}));
    tblPorAseguradora = new JTable(new DefaultTableModel(new Object[][]{}, new String[]{"Aseguradora", "Cantidad", "Total Prima (S/.)", "Total Comisiones (S/.)"}));
    tblConciliacion = new JTable(new DefaultTableModel(new Object[][]{}, new String[]{"N° Operación", "Banco", "Tipo", "Monto (S/.)", "Ventas Pagadas", "PVs"}));

    // Tabs con las tablas
    tabbedPane = new JTabbedPane();
    tabbedPane.addTab("Resumen Diario", new JScrollPane(tblDiario));
    tabbedPane.addTab("Por Punto de Venta", new JScrollPane(tblPorPV));
    tabbedPane.addTab("Por Aseguradora", new JScrollPane(tblPorAseguradora));
    tabbedPane.addTab("Conciliación", new JScrollPane(tblConciliacion));

    // Layout principal
    setLayout(new BorderLayout());
    add(panelFiltros, BorderLayout.NORTH);
    add(tabbedPane, BorderLayout.CENTER);
}
    
   private void initControllers() {
        reportController = new ReportController();
    }
    
    
    private void setupLayout() {
        // Configuración adicional
    }
    
    private void setupEvents() {
        btnGenerar.addActionListener(e -> generarReportes());
    }
    private void aplicarEstilosTablas() {
    UITheme.styleTable(tblDiario);
    UITheme.styleTable(tblPorPV);
    UITheme.styleTable(tblPorAseguradora);
    UITheme.styleTable(tblConciliacion);

    tblDiario.setBackground(UITheme.BG_PRIMARY);
    tblPorPV.setBackground(UITheme.BG_PRIMARY);
    tblPorAseguradora.setBackground(UITheme.BG_PRIMARY);
    tblConciliacion.setBackground(UITheme.BG_PRIMARY);

    tblDiario.setForeground(UITheme.TEXT_PRIMARY);
    tblPorPV.setForeground(UITheme.TEXT_PRIMARY);
    tblPorAseguradora.setForeground(UITheme.TEXT_PRIMARY);
    tblConciliacion.setForeground(UITheme.TEXT_PRIMARY);
}

    private void generarReportesDelDia() {
        Date hoy = new Date();
        dateChooserInicio.setDate(hoy);
        dateChooserFin.setDate(hoy);
        generarReportes();
    }
    
    private void generarReportes() {
        Date fechaInicio = dateChooserInicio.getDate();
        Date fechaFin = dateChooserFin.getDate();
        
        if (fechaInicio == null || fechaFin == null) {
            JOptionPane.showMessageDialog(this, 
                "Seleccione las fechas para el reporte", 
                "Fechas requeridas", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        generarReporteDiario(fechaInicio, fechaFin);
        generarReportePorPV(fechaInicio, fechaFin);
        generarReportePorAseguradora(fechaInicio, fechaFin);
        generarConciliacionPagos(fechaFin);
    }
    
    private void generarReporteDiario(Date inicio, Date fin) {
        Map<String, Object> reporte = reportController.getReporteDiario(inicio);
        
        String[] columnas = {"Métrica", "Valor"};
        Object[][] datos = {
            {"Fecha", new SimpleDateFormat("dd/MM/yyyy").format(inicio)},
            {"Total Ventas", reporte.get("totalVentas")},
            {"Total Prima (S/.)", String.format("S/ %.2f", reporte.get("totalPrima"))},
            {"Total Comisiones PV (S/.)", String.format("S/ %.2f", reporte.get("totalComisiones"))},
            {"Total Cobrado (S/.)", String.format("S/ %.2f", reporte.get("totalCobrado"))},
            {"Utilidad Neta (S/.)", String.format("S/ %.2f", 
                (Double) reporte.get("totalCobrado") - (Double) reporte.get("totalComisiones"))}
        };
        
        DefaultTableModel model = new DefaultTableModel(datos, columnas);
        tblDiario.setModel(model);
        tblDiario.getColumnModel().getColumn(0).setPreferredWidth(150);
        tblDiario.getColumnModel().getColumn(1).setPreferredWidth(150);
        tblDiario.setFont(new Font("Arial", Font.PLAIN, 12));
        tblDiario.setRowHeight(25);
    }
    
private void generarReportePorPV(Date inicio, Date fin) {
    SwingWorker<java.util.List<Map<String,Object>>, Void> worker = new SwingWorker<>() {
        @Override protected java.util.List<Map<String,Object>> doInBackground() {
            return reportController.getReportePorPuntoVenta(inicio, fin);
        }
        @Override protected void done() {
            try {
                java.util.List<Map<String,Object>> reporte = get();
                String[] columnas = {"Punto de Venta", "Ciudad", "Cantidad", "Total Ventas (S/.)", "Comisiones (S/.)", "Cobrado (S/.)"};
                Object[][] datos = new Object[reporte.size()][6];

                for (int i = 0; i < reporte.size(); i++) {
                    Map<String,Object> row = reporte.get(i);
                    datos[i][0] = row.get("puntoVenta");
                    datos[i][1] = row.get("ciudad");
                    datos[i][2] = row.get("cantidadVentas");
                    datos[i][3] = String.format("S/ %.2f", row.get("totalVentas"));
                    datos[i][4] = String.format("S/ %.2f", row.get("totalComisiones"));
                    datos[i][5] = String.format("S/ %.2f", row.get("totalCobrado"));
                }

                tblPorPV.setModel(new DefaultTableModel(datos, columnas));
                tblPorPV.getColumnModel().getColumn(0).setPreferredWidth(150);
                tblPorPV.getColumnModel().getColumn(1).setPreferredWidth(80);
                tblPorPV.getColumnModel().getColumn(2).setPreferredWidth(70);
                tblPorPV.getColumnModel().getColumn(3).setPreferredWidth(100);
                tblPorPV.getColumnModel().getColumn(4).setPreferredWidth(100);
                tblPorPV.getColumnModel().getColumn(5).setPreferredWidth(100);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(ReportesPanel.this,
                    "Error al generar reporte por PV: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    };
    worker.execute();
}
 
private void generarReportePorAseguradora(Date inicio, Date fin) {
    SwingWorker<java.util.List<Map<String,Object>>, Void> worker = new SwingWorker<>() {
        @Override protected java.util.List<Map<String,Object>> doInBackground() {
            return reportController.getReportePorAseguradora(inicio, fin);
        }
        @Override protected void done() {
            try {
                java.util.List<Map<String,Object>> reporte = get();
                String[] columnas = {"Aseguradora", "Cantidad", "Total Prima (S/.)", "Total Comisiones (S/.)"};
                Object[][] datos = new Object[reporte.size()][4];

                for (int i = 0; i < reporte.size(); i++) {
                    Map<String,Object> row = reporte.get(i);
                    datos[i][0] = row.get("aseguradora");
                    datos[i][1] = row.get("cantidad");
                    datos[i][2] = String.format("S/ %.2f", row.get("totalPrima"));
                    datos[i][3] = String.format("S/ %.2f", row.get("totalComisiones"));
                }

                tblPorAseguradora.setModel(new DefaultTableModel(datos, columnas));
                tblPorAseguradora.getColumnModel().getColumn(0).setPreferredWidth(120);
                tblPorAseguradora.getColumnModel().getColumn(1).setPreferredWidth(80);
                tblPorAseguradora.getColumnModel().getColumn(2).setPreferredWidth(100);
                tblPorAseguradora.getColumnModel().getColumn(3).setPreferredWidth(100);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(ReportesPanel.this,
                    "Error al generar reporte por aseguradora: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    };
    worker.execute();
}

    
private void generarConciliacionPagos(Date fecha) {
    SwingWorker<java.util.List<Map<String,Object>>, Void> worker = new SwingWorker<>() {
        @Override protected java.util.List<Map<String,Object>> doInBackground() {
            return reportController.getConciliacionPagos(fecha);
        }
        @Override protected void done() {
            try {
                java.util.List<Map<String,Object>> reporte = get();
                String[] columnas = {"N° Operación", "Banco", "Tipo", "Monto (S/.)", "Ventas Pagadas", "PVs"};
                Object[][] datos = new Object[reporte.size()][6];

                for (int i = 0; i < reporte.size(); i++) {
                    Map<String,Object> row = reporte.get(i);
                    datos[i][0] = row.get("operationNumber");
                    datos[i][1] = row.get("banco");
                    datos[i][2] = row.get("tipo");
                    datos[i][3] = String.format("S/ %.2f", row.get("monto"));
                    datos[i][4] = row.get("ventasPagadas");
                    datos[i][5] = row.get("puntosVenta");
                }

                tblConciliacion.setModel(new DefaultTableModel(datos, columnas));
                tblConciliacion.getColumnModel().getColumn(0).setPreferredWidth(120);
                tblConciliacion.getColumnModel().getColumn(1).setPreferredWidth(80);
                tblConciliacion.getColumnModel().getColumn(2).setPreferredWidth(80);
                tblConciliacion.getColumnModel().getColumn(3).setPreferredWidth(100);
                tblConciliacion.getColumnModel().getColumn(4).setPreferredWidth(100);
                tblConciliacion.getColumnModel().getColumn(5).setPreferredWidth(200);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(ReportesPanel.this,
                    "Error al generar conciliación: " + e.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    };
    worker.execute();
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
