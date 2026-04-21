
package Modelo.Entidades;


public class ComisionRegla {
    private int id;
    private String aseguradora;
    private String tipoVehiculo;
    private String canal;
    private double porcentaje;
    private boolean activo;
    
    public ComisionRegla() {}
    
    public ComisionRegla(String aseguradora, String tipoVehiculo, String canal, double porcentaje) {
        this.aseguradora = aseguradora;
        this.tipoVehiculo = tipoVehiculo;
        this.canal = canal;
        this.porcentaje = porcentaje;
        this.activo = true;
    }
    
    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    
    public String getAseguradora() { return aseguradora; }
    public void setAseguradora(String aseguradora) { this.aseguradora = aseguradora; }
    
    public String getTipoVehiculo() { return tipoVehiculo; }
    public void setTipoVehiculo(String tipoVehiculo) { this.tipoVehiculo = tipoVehiculo; }
    
    public String getCanal() { return canal; }
    public void setCanal(String canal) { this.canal = canal; }
    
    public double getPorcentaje() { return porcentaje; }
    public void setPorcentaje(double porcentaje) { this.porcentaje = porcentaje; }
    
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
