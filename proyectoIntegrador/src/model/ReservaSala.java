package model;

import java.time.LocalDate;

public class ReservaSala {
    // Datos de la sala
    private int idSala;
    private String nombreSala;
    private int capacidad;
    private String softwareDisponible;
    private String hardwareEspecial;
    private String ubicacion;
    private String estado;

    // Datos de la reserva (préstamo)
    private int idPrestamoS;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String observaciones;

    public ReservaSala() { }

    // Getters y setters
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

    public int getIdPrestamoS() { return idPrestamoS; }
    public void setIdPrestamoS(int idPrestamoS) { this.idPrestamoS = idPrestamoS; }

    public LocalDate getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(LocalDate fechaInicio) { this.fechaInicio = fechaInicio; }

    public LocalDate getFechaFin() { return fechaFin; }
    public void setFechaFin(LocalDate fechaFin) { this.fechaFin = fechaFin; }

    public String getObservaciones() { return observaciones; }
    public void setObservaciones(String observaciones) { this.observaciones = observaciones; }
}