package Modelo.Entidades;

import java.util.Date;
import java.util.List;

public class Conversacion {
    private int id;   // 🔥 nuevo campo
    private PuntoVenta puntoVenta;
    private List<Mensaje> mensajes;
    private String estadoActual;
    private int noLeidos;
    private Date ultimoMensaje;
    private int mensajesNoLeidos = 0;

    public Conversacion() {}

    public Conversacion(PuntoVenta puntoVenta) {
        this.puntoVenta = puntoVenta;
        this.estadoActual = Venta.ESTADO_EMITIDO;
        this.noLeidos = 0;
    }

    // Getter y Setter para id
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public PuntoVenta getPuntoVenta() { return puntoVenta; }
    public void setPuntoVenta(PuntoVenta puntoVenta) { this.puntoVenta = puntoVenta; }

    public List<Mensaje> getMensajes() { return mensajes; }
    public void setMensajes(List<Mensaje> mensajes) { this.mensajes = mensajes; }

    public String getEstadoActual() { return estadoActual; }
    public void setEstadoActual(String estadoActual) { this.estadoActual = estadoActual; }

    public int getNoLeidos() { return noLeidos; }
    public void setNoLeidos(int noLeidos) { this.noLeidos = noLeidos; }

    public Date getUltimoMensaje() { return ultimoMensaje; }
    public void setUltimoMensaje(Date ultimoMensaje) { this.ultimoMensaje = ultimoMensaje; }

    public int getMensajesNoLeidos() { return mensajesNoLeidos; }
    public void setMensajesNoLeidos(int n) { this.mensajesNoLeidos = n; }

    @Override
    public String toString() {
        return puntoVenta.getNombre() + " - " + puntoVenta.getCiudad() +
               " [" + estadoActual + "]";
    }
}
