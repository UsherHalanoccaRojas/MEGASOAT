package Services;

import Modelo.DAO.PuntoVentaDAO;
import Modelo.Entidades.PuntoVenta;
import java.util.List;

public class PuntoVentaService {
    private PuntoVentaDAO dao;

    public PuntoVentaService() {
        this.dao = new PuntoVentaDAO();
    }

    // Insertar un nuevo Punto de Venta
    public boolean insertar(PuntoVenta pv) {
        return dao.insert(pv);
    }

    // Actualizar un Punto de Venta existente
    public boolean actualizar(PuntoVenta pv) {
        return dao.update(pv);
    }

    // Eliminar un Punto de Venta por ID
    public boolean eliminar(int id) {
        return dao.delete(id);
    }

    // Buscar Punto de Venta por ID
    public PuntoVenta buscarPorId(int id) {
        return dao.findById(id);
    }

    // Buscar Punto de Venta por teléfono
    public PuntoVenta buscarPorTelefono(String telefono) {
        return dao.findByTelefono(telefono);
    }

    // Buscar Punto de Venta por nombre
    public PuntoVenta buscarPorNombre(String nombre) {
        return dao.findByNombre(nombre);
    }

    // Obtener todos los Puntos de Venta
    public List<PuntoVenta> obtenerTodos() {
        return dao.findAll();
    }

    // Buscar por ciudad
    public List<PuntoVenta> buscarPorCiudad(String ciudad) {
        return dao.findByCiudad(ciudad);
    }

    // Actualizar el chatId de Telegram
// Actualizar el chatId de Telegram
public boolean actualizarChatId(int pvId, String chatId) {
    return dao.updateChatId(pvId, chatId);
}
// Buscar Punto de Venta por chatId de Telegram
public PuntoVenta buscarPorChatId(String chatId) {
    return dao.findByChatId(chatId);
}
// Cambiar estado activo/inactivo de un Punto de Venta
public boolean cambiarEstado(int pvId, boolean activo) {
    return dao.actualizarEstado(pvId, activo);
}

}
