package Controller;

import Modelo.DAO.UsuarioDAO;
import Modelo.Entidades.Usuario;
import java.util.List;

public class UsuarioController {
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    public List<Usuario> listarTodos() {
        return usuarioDAO.findAll();
    }

    public boolean crearUsuario(Usuario u) {
        return usuarioDAO.insert(u);
    }

    public boolean actualizarUsuario(Usuario u) {
        return usuarioDAO.update(u);
    }

    public boolean cambiarPassword(int id, String newPassword) {
        return usuarioDAO.updatePassword(id, newPassword);
    }

    public boolean eliminarUsuario(int id) {
        return usuarioDAO.delete(id);
    }
}