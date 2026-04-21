package Modelo.Entidades;
import java.util.Date;

public class Venta {
    private int id;
    private int pvId;
    private String pvNombre;
    private String numeroPoliza;      
    private String placa;
    private String aseguradora;
    private String tipoVehiculo;
    private String canal;
    private double prima;
    private double comisionPV;
    private double comisionEmpresa;   
    private String estado;
    private String pdfUrl;
    private Date fechaEmision;
    private Date fechaVencimiento;   
    private Date fechaPago;
    private int voucherId;
    private String observaciones;     
    private int emitidoPor;          

    // Estados predefinidos
    public static final String ESTADO_EMITIDO = "Emitido";
    public static final String ESTADO_ESPERANDO_PAGO = "Esperando Pago";
    public static final String ESTADO_PAGADO = "Pagado";
    public static final String ESTADO_ANULADO = "Anulado";

    public Venta() {
        this.fechaEmision = new Date();
        this.estado = ESTADO_EMITIDO;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPvId() { return pvId; }
    public void setPvId(int pvId) { this.pvId = pvId; }

    public String getPvNombre() { return pvNombre; }
    public void setPvNombre(String pvNombre) { this.pvNombre = pvNombre; }

    public String getNumeroPoliza() { return numeroPoliza; }
    public void setNumeroPoliza(String numeroPoliza) { this.numeroPoliza = numeroPoliza; }

    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa != null ? placa.toUpperCase() : null; }

    public String getAseguradora() { return aseguradora; }
    public void setAseguradora(String aseguradora) { this.aseguradora = aseguradora; }

    public String getTipoVehiculo() { return tipoVehiculo; }
    public void setTipoVehiculo(String tipoVehiculo) { this.tipoVehiculo = tipoVehiculo; }

    public String getCanal() { return canal; }
    public void setCanal(String canal) { this.canal = canal; }

    public double getPrima() { return prima; }
    public void setPrima(double prima) { this.prima = prima; }

    public double getComisionPV() { return comisionPV; }
    public void setComisionPV(double comisionPV) { this.comisionPV = comisionPV; }

    public double getComisionEmpresa() { return comisionEmpresa; }
    public void setComisionEmpresa(double comisionEmpresa) { this.comisionEmpresa = comisionEmpresa; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public String getPdfUrl() { return pdfUrl; }
    public void setPdfUrl(String pdfUrl) { this.pdfUrl = pdfUrl; }

    public Date getFechaEmision() { return fechaEmision; }
    public void setFechaEmision(Date fechaEmision) { this.fechaEmision = fechaEmision; }

    public Date getFechaVencimiento() { return fechaVencimiento; }
    public void setFechaVencimiento(Date fechaVencimiento) { this.fechaVencimiento = fechaVencimiento; }

    public Date getFechaPago() { return fechaPago; }
    public void setFechaPago(Date fechaPago) { this.fechaPago = fechaPago; }

    public int getVoucherId() { return voucherId; }
    public void setVoucherId(int voucherId) { this.voucherId = voucherId; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }

    public int getEmitidoPor() { return emitidoPor; }
    public void setEmitidoPor(int emitidoPor) { this.emitidoPor = emitidoPor; }
}
