package Modelo.Entidades;

public class Usuario {
    private int id;
    private String username;
    private String password;
    private String nombreCompleto;
    private String email;
    private String telefono;
    private String rol;
    private boolean activo;

    // Roles
    public static final String ROL_SUPERADMIN = "SUPERADMIN";
    public static final String ROL_ADMIN      = "ADMIN";
    public static final String ROL_COMERCIAL  = "COMERCIAL";

    public Usuario() {}

    //  Permisos por rol 
    public boolean puedeUsarChat() {
        return true; // todos los roles
    }

    public boolean puedeGestionarPV() {
        return rol != null && (
            rol.equalsIgnoreCase(ROL_COMERCIAL) ||
            rol.equalsIgnoreCase(ROL_ADMIN) ||
            rol.equalsIgnoreCase(ROL_SUPERADMIN)
        );
    }

    public boolean puedeValidarPagos() {
        return rol != null && (
            rol.equalsIgnoreCase(ROL_ADMIN) ||
            rol.equalsIgnoreCase(ROL_SUPERADMIN)
        );
    }

    public boolean puedeExportarReportes() {
        return rol != null && (
            rol.equalsIgnoreCase(ROL_ADMIN) ||
            rol.equalsIgnoreCase(ROL_SUPERADMIN)
        );
    }

    public boolean puedeGestionarUsuarios() {
        return rol != null && rol.equalsIgnoreCase(ROL_SUPERADMIN);
    }

    // Getters y Setters 
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String n) { this.nombreCompleto = n; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String t) { this.telefono = t; }

    public String getRol() { return rol; }
    public void setRol(String rol) {
        // Normaliza a mayúsculas y elimina espacios
        this.rol = rol != null ? rol.trim().toUpperCase() : null;
    }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
}
