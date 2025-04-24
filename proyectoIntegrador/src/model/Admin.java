package model;

public class Admin {
    private long cedula;
    private String nombre;
    private String correo;
    private String telefono;
    private String contraseñaAdmin;  // para validar en el registro (C12282025)
    private String departamento;
    private String contraseñaAdministrativo; // la contraseña real del admin

    public Admin(long cedula, String nombre, String correo, String telefono, String contraseñaAdmin, String departamento, String contraseñaAdministrativo) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.contraseñaAdmin = contraseñaAdmin;
        this.departamento = departamento;
        this.contraseñaAdministrativo = contraseñaAdministrativo;
    }

    // Getters y Setters
    public long getCedula() { return cedula; }
    public void setCedula(long cedula) { this.cedula = cedula; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }
    public String getContraseñaAdmin() { return contraseñaAdmin; }
    public void setContraseñaAdmin(String contraseñaAdmin) { this.contraseñaAdmin = contraseñaAdmin; }
    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }
    public String getContraseñaAdministrativo() { return contraseñaAdministrativo; }
    public void setContraseñaAdministrativo(String contraseñaAdministrativo) { this.contraseñaAdministrativo = contraseñaAdministrativo; }
}
