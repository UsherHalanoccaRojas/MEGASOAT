package Utils;

import Modelo.Entidades.Usuario;

public class SesionActual {
    private static Usuario usuario;

    public static void setUsuario(Usuario u) {
        usuario = u;
    }

    public static Usuario getUsuario() {
        return usuario;
    }
}
