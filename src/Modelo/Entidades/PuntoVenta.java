package Modelo.Entidades;

import java.util.Date;

public class PuntoVenta {
    private int id;
    private String codigo;  
    private String nombre;
    private String telefono;
    private String ciudad;
    private String direccion;
    private Double comisionPersonalizada;
    private boolean activo;
    private Date fechaRegistro;
    private String telegramChatId; 

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getCiudad() { return ciudad; }
    public void setCiudad(String ciudad) { this.ciudad = ciudad; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public Double getComisionPersonalizada() { return comisionPersonalizada; }
    public void setComisionPersonalizada(Double comisionPersonalizada) { this.comisionPersonalizada = comisionPersonalizada; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public Date getFechaRegistro() { return fechaRegistro; }
    public void setFechaRegistro(Date fechaRegistro) { this.fechaRegistro = fechaRegistro; }

    public String getTelegramChatId() { return telegramChatId; }
    public void setTelegramChatId(String telegramChatId) { this.telegramChatId = telegramChatId; }
}
