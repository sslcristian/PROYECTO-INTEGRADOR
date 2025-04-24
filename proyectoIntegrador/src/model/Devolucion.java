package model;

import java.sql.Date;
import java.sql.Time;

public class Devolucion {
    private int idDevolucion;
    private int idSolicitud;
    private Date fechaDevolucion;
    private Time horaDevolucion;
    private String estadoRecurso;
    private String observaciones;

    public Devolucion(int idDevolucion, int idSolicitud, Date fechaDevolucion, Time horaDevolucion,
                      String estadoRecurso, String observaciones) {
        this.idDevolucion = idDevolucion;
        this.idSolicitud = idSolicitud;
        this.fechaDevolucion = fechaDevolucion;
        this.horaDevolucion = horaDevolucion;
        this.estadoRecurso = estadoRecurso;
        this.observaciones = observaciones;
    }

    public int getIdDevolucion() {
        return idDevolucion;
    }

    public void setIdDevolucion(int idDevolucion) {
        this.idDevolucion = idDevolucion;
    }

    public int getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud(int idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    public Date getFechaDevolucion() {
        return fechaDevolucion;
    }

    public void setFechaDevolucion(Date fechaDevolucion) {
        this.fechaDevolucion = fechaDevolucion;
    }

    public Time getHoraDevolucion() {
        return horaDevolucion;
    }

    public void setHoraDevolucion(Time horaDevolucion) {
        this.horaDevolucion = horaDevolucion;
    }

    public String getEstadoRecurso() {
        return estadoRecurso;
    }

    public void setEstadoRecurso(String estadoRecurso) {
        this.estadoRecurso = estadoRecurso;
    }

    public String getObservaciones() {
        return observaciones;
    }

    public void setObservaciones(String observaciones) {
        this.observaciones = observaciones;
    }
}