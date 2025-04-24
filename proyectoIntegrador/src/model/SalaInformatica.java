package model;

public class SalaInformatica {
    private int idSala;
    private String nombreSala;
    private int capacidad;
    private String softwareDisponible;
    private String hardwareEspecial;
    private String ubicacion;
    private String estado;

    public SalaInformatica(int idSala, String nombreSala, int capacidad, String softwareDisponible, String hardwareEspecial, String ubicacion, String estado) {
        this.idSala = idSala;
        this.nombreSala = nombreSala;
        this.capacidad = capacidad;
        this.softwareDisponible = softwareDisponible;
        this.hardwareEspecial = hardwareEspecial;
        this.ubicacion = ubicacion;
        this.estado = estado;
    }

    // Getters y Setters
    public int getIdSala() { return idSala; }
    public void setIdSala(int idSala) { this.idSala = idSala; }
    public String getNombreSala() { return nombreSala; }
    public void setNombreSala(String nombreSala) { this.nombreSala = nombreSala; }
    public int getCapacidad() { return capacidad; }
    public void setCapacidad(int capacidad) { this.capacidad = capacidad; }
    public String getSoftwareDisponible() { return softwareDisponible; }
    public void setSoftwareDisponible(String softwareDisponible) { this.softwareDisponible = softwareDisponible; }
    public String getHardwareEspecial() { return hardwareEspecial; }
    public void setHardwareEspecial(String hardwareEspecial) { this.hardwareEspecial = hardwareEspecial; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}