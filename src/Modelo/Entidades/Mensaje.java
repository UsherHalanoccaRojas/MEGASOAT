package Modelo.Entidades;

import java.util.Date;

public class Mensaje {
    private int id;
    private int pvId;
    private String pvNombre;
    private Integer usuarioId;      // puede ser null
    private String tipo;
    private String contenido;
    private String direccion;
    private boolean leido;
    private Date fechaEnvio;        // fecha de envío
    private Date fechaLeido;        // fecha de lectura
    private String archivoUrl;      // adjunto opcional

    // Tipos predefinidos
    public static final String TIPO_TEXTO = "texto";
    public static final String TIPO_IMAGEN = "imagen";
    public static final String TIPO_PDF = "pdf";
    public static final String TIPO_AUDIO = "audio";
    public static final String TIPO_UBICACION = "ubicacion";

    // Direcciones
    public static final String DIRECCION_ENTRANTE = "entrante";
    public static final String DIRECCION_SALIENTE = "saliente";

    public Mensaje() {
        this.fechaEnvio = new Date();
        this.leido = false;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getPvId() { return pvId; }
    public void setPvId(int pvId) { this.pvId = pvId; }

    public String getPvNombre() { return pvNombre; }
    public void setPvNombre(String pvNombre) { this.pvNombre = pvNombre; }

    public Integer getUsuarioId() { return usuarioId; }
    public void setUsuarioId(Integer usuarioId) { this.usuarioId = usuarioId; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public String getContenido() { return contenido; }
    public void setContenido(String contenido) { this.contenido = contenido; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public boolean isLeido() { return leido; }
    public void setLeido(boolean leido) { this.leido = leido; }

    public Date getFechaEnvio() { return fechaEnvio; }
    public void setFechaEnvio(Date fechaEnvio) { this.fechaEnvio = fechaEnvio; }

    public Date getFechaLeido() { return fechaLeido; }
    public void setFechaLeido(Date fechaLeido) { this.fechaLeido = fechaLeido; }

    public String getArchivoUrl() { return archivoUrl; }
    public void setArchivoUrl(String archivoUrl) { this.archivoUrl = archivoUrl; }
    
    @Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Mensaje)) return false;
    Mensaje m = (Mensaje) o;
    return this.id == m.id;
}

@Override
public int hashCode() {
    return Integer.hashCode(id);
}

}

