package model;

import java.sql.Timestamp;

public class EquipoPrestado {
    private int idPrestamoE;
    private int idSolicitudE;
    private int idEquipo;
    private Timestamp fechaInicio;  
    private Timestamp fechaFin;     
    private String observaciones;

    public EquipoPrestado(int idPrestamoE, int idSolicitudE, int idEquipo, Timestamp fechaInicio, Timestamp fechaFin, String observaciones) {
        this.idPrestamoE = idPrestamoE;
        this.idSolicitudE = idSolicitudE;
        this.idEquipo = idEquipo;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.observaciones = observaciones;
    }

    // Getters y setters
    public int getIdPrestamoE() {
        return idPrestamoE;
    }

    public void setIdPrestamoE(int idPrestamoE) {
        this.idPrestamoE = idPrestamoE;
    }

    public int getIdSolicitudE() {
        return idSolicitudE;
    }

    public void setIdSolicitudE(int idSolicitudE) {
        this.idSolicitudE = idSolicitudE;
    }

    public int getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(int idEquipo) {
        this.idEquipo = idEquipo;
    }

    public Timestamp getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Timestamp fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Timestamp getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Timestamp fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}
