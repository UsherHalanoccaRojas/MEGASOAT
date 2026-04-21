package  Controller;


import Modelo.DAO.ComisionReglaDAO;
import Modelo.Entidades.ComisionRegla;
import java.util.List;

public class ConfigController {
    private ComisionReglaDAO comisionDAO;
    
    public ConfigController() {
        this.comisionDAO = new ComisionReglaDAO();
    }
    
    public List<ComisionRegla> listarReglas() {
        return comisionDAO.findAll();
    }
    
    public boolean guardarRegla(ComisionRegla regla) {
        if (regla.getId() > 0) {
            return comisionDAO.update(regla);
        } else {
            return comisionDAO.insert(regla);
        }
    }
    
    public boolean eliminarRegla(int id) {
        return comisionDAO.delete(id);
    }
    
    public ComisionRegla buscarRegla(String aseguradora, String tipoVehiculo, String canal) {
        return comisionDAO.findBy(aseguradora, tipoVehiculo, canal);
    }
}
