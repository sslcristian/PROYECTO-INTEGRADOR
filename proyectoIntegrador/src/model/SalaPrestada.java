package model;

import java.sql.Timestamp;

public class SalaPrestada {
    private int idPrestamoS;
    private int idSolicitudS;
    private int idSala;
    private Timestamp fechaInicio; 
    private Timestamp fechaFin;     
    private String observaciones;

    public SalaPrestada(int idPrestamoS, int idSolicitudS, int idSala, Timestamp fechaInicio, Timestamp fechaFin, String observaciones) {
        this.idPrestamoS = idPrestamoS;
        this.idSolicitudS = idSolicitudS;
        this.idSala = idSala;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.observaciones = observaciones;
    }

    // Getters y Setters
    public int getIdPrestamoS() {
        return idPrestamoS;
    }
    public void setIdPrestamoS(int idPrestamoS) {
        this.idPrestamoS = idPrestamoS;
    }

    public int getIdSolicitudS() {
        return idSolicitudS;
    }
    public void setIdSolicitudS(int idSolicitudS) {
        this.idSolicitudS = idSolicitudS;
    }

    public int getIdSala() {
        return idSala;
    }
    public void setIdSala(int idSala) {
        this.idSala = idSala;
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
