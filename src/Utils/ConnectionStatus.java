package Utils;

public class ConnectionStatus {
    public static final String CONECTADO = "Conectado";
    public static final String RECON = "Reconectando...";
    public static final String SIN_CONEXION = "Sin conexión";

    private static String estado = CONECTADO;

    public static synchronized void setEstado(String nuevo) {
        estado = nuevo;
    }

    public static synchronized String getEstado() {
        return estado;
    }
}
