package Modelo.Entidades;

import java.util.Date;

public class Voucher {
    private int id;
    private String operationNumber;
    private String banco;
    private double monto;
    private String tipo;
    private Date fechaRegistro;
    private Date fechaOperacion;   
    private int registradoPor;
    private String observaciones;

    // Getters y setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getOperationNumber() { return operationNumber; }
    public void setOperationNumber(String operationNumber) { this.operationNumber = operationNumber; }

    public String getBanco() { return banco; }
    public void setBanco(String banco) { this.banco = banco; }

    public double getMonto() { return monto; }
    public void setMonto(double monto) { this.monto = monto; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public Date getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Date fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public Date getFechaOperacion() { return fechaOperacion; }   
    public void setFechaOperacion(Date fechaOperacion) { this.fechaOperacion = fechaOperacion; } 

    public int getRegistradoPor() { return registradoPor; }
    public void setRegistradoPor(int registradoPor) { this.registradoPor = registradoPor; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}
