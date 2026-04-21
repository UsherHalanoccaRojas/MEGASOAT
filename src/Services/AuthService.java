package Services;

import Modelo.DAO.UsuarioDAO;
import Modelo.DAO.SesionDAO;
import Modelo.Entidades.Usuario;
import java.util.ArrayList;
import java.util.List;

public class AuthService {
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();
    private final SesionDAO sesionDAO = new SesionDAO();
    private static Usuario usuarioActual;
    private static String tokenActual;

    // 🔥 Lista de observadores
    private static List<SessionObserver> observers = new ArrayList<>();

    public Usuario login(String username, String password, boolean forzar) {
        Usuario u = usuarioDAO.findByUsername(username);
        if (u != null && u.getPassword().equals(password)) {
            if (sesionDAO.existeSesionActiva(u.getId())) {
                if (!forzar) {
                    return null;
                } else {
                    sesionDAO.cerrarSesiones(u.getId());
                }
            }
            tokenActual = sesionDAO.registrarSesion(u.getId());
            usuarioActual = u;
            return u;
        }
        return null;
    }

    public static Usuario getUsuarioActual() { return usuarioActual; }

    public static void logout() {
        if (tokenActual != null) {
            new SesionDAO().cerrarSesionPorToken(tokenActual);
        }
        usuarioActual = null;
        tokenActual = null;

        // 🔥 Notificar a todos los observadores
        for (SessionObserver obs : observers) {
            obs.onLogout();
        }
    }

    // 🔧 Método para registrar observadores
    public static void addObserver(SessionObserver obs) {
        observers.add(obs);
    }
}
