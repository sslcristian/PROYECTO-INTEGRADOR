package model;

import java.util.Date;

public class SolicitudInfo {
    private long idSolicitud;
    private Date fechaInicio;
    private Date fechaFin;
    private String estado;

    // Para sala
    private Long idSala;
    private String nombreSala;
    private String ubicacionSala;

    // Para equipo
    private Long idEquipo;
    private String nombreEquipo;
    private String tipoEquipo;
    private String ubicacionEquipo;

    // Getters y Setters

    public long getIdSolicitud() {
        return idSolicitud;
    }
    public void setIdSolicitud(long idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }
    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }
    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }

    // Sala
    public Long getIdSala() {
        return idSala;
    }
    public void setIdSala(Long idSala) {
        this.idSala = idSala;
    }

    public String getNombreSala() {
        return nombreSala;
    }
    public void setNombreSala(String nombreSala) {
        this.nombreSala = nombreSala;
    }

    public String getUbicacionSala() {
        return ubicacionSala;
    }
    public void setUbicacionSala(String ubicacionSala) {
        this.ubicacionSala = ubicacionSala;
    }

    // Equipo
    public Long getIdEquipo() {
        return idEquipo;
    }
    public void setIdEquipo(Long idEquipo) {
        this.idEquipo = idEquipo;
    }

    public String getNombreEquipo() {
        return nombreEquipo;
    }
    public void setNombreEquipo(String nombreEquipo) {
        this.nombreEquipo = nombreEquipo;
    }

    public String getTipoEquipo() {
        return tipoEquipo;
    }
    public void setTipoEquipo(String tipoEquipo) {
        this.tipoEquipo = tipoEquipo;
    }

    public String getUbicacionEquipo() {
        return ubicacionEquipo;
    }
    public void setUbicacionEquipo(String ubicacionEquipo) {
        this.ubicacionEquipo = ubicacionEquipo;
    }
}