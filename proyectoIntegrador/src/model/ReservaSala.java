package model;

public class ReservaSala {
    private Integer idSala;
    private String nombreSala;
    private Integer capacidad;
    private String softwareDisponible;
    private String hardwareEspecial;
    private String ubicacion;
    private String estado;

    // Constructor
    public ReservaSala(Integer idSala, String nombreSala, Integer capacidad, String softwareDisponible,
                       String hardwareEspecial, String ubicacion, String estado) {
        this.idSala = idSala;
        this.nombreSala = nombreSala;
        this.capacidad = capacidad;
        this.softwareDisponible = softwareDisponible;
        this.hardwareEspecial = hardwareEspecial;
        this.ubicacion = ubicacion;
        this.estado = estado;
    }

    // Getters y Setters
    public Integer getIdSala() {
        return idSala;
    }

    public void setIdSala(Integer idSala) {
        this.idSala = idSala;
    }

    public String getNombreSala() {
        return nombreSala;
    }

    public void setNombreSala(String nombreSala) {
        this.nombreSala = nombreSala;
    }

    public Integer getCapacidad() {
        return capacidad;
    }

    public void setCapacidad(Integer capacidad) {
        this.capacidad = capacidad;
    }

    public String getSoftwareDisponible() {
        return softwareDisponible;
    }

    public void setSoftwareDisponible(String softwareDisponible) {
        this.softwareDisponible = softwareDisponible;
    }

    public String getHardwareEspecial() {
        return hardwareEspecial;
    }

    public void setHardwareEspecial(String hardwareEspecial) {
        this.hardwareEspecial = hardwareEspecial;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}