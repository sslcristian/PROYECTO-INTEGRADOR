package model;

public class Usuario {
    private long cedula;
    private String nombre;
    private String correo;
    private String telefono;
    private String tipoUsuario;
    private String departamento;
    private String contraseña;

    public Usuario(long cedula, String nombre, String correo, String telefono, String tipoUsuario, String departamento, String contraseña) {
        this.cedula = cedula;
        this.nombre = nombre;
        this.correo = correo;
        this.telefono = telefono;
        this.tipoUsuario = tipoUsuario;
        this.departamento = departamento;
        this.contraseña = contraseña;
    }

    public long getCedula() { return cedula; }
    public void setCedula(long cedula) { this.cedula = cedula; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getTipoUsuario() { return tipoUsuario; }
    public void setTipoUsuario(String tipoUsuario) { this.tipoUsuario = tipoUsuario; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    public String getContraseña() { return contraseña; }
    public void setContraseña(String contraseña) { this.contraseña = contraseña; }
}
