
package  Controller;


import Services.ReportService;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class ReportController {
    private ReportService reportService;
    
    public ReportController() {
        this.reportService = new ReportService();
    }
    
    public Map<String, Object> getReporteDiario(Date fecha) {
        return reportService.getReporteDiario(fecha);
    }
    
    public List<Map<String, Object>> getReportePorPuntoVenta(Date fechaInicio, Date fechaFin) {
        return reportService.getReportePorPuntoVenta(fechaInicio, fechaFin);
    }
    
    public List<Map<String, Object>> getReportePorAseguradora(Date fechaInicio, Date fechaFin) {
        return reportService.getReportePorAseguradora(fechaInicio, fechaFin);
    }
    
    public List<Map<String, Object>> getConciliacionPagos(Date fecha) {
        return reportService.getConciliacionPagos(fecha);
    }
    
    public void exportarReporteExcel(List<Map<String, Object>> datos, String titulo, String filePath) {
        // Implementar exportación a Excel usando Apache POI
        // Este método se puede implementar según necesidad
    }
}
