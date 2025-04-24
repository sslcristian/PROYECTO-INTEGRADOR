package model;

import java.sql.Date;
import java.time.LocalDateTime;

public class SolicitudPrestamo {
    private int idSolicitud;
    private long cedulaUsuario;
    private Date fechaSolicitud;
    private String tipoRecurso;
    private String detalleRecurso;
    private Date fechaUso;
    private LocalDateTime horaInicio;
    private LocalDateTime horaFin;
    private String estado;

    public SolicitudPrestamo(int idSolicitud, long cedulaUsuario, Date fechaSolicitud, String tipoRecurso, String detalleRecurso,
                             Date fechaUso, LocalDateTime horaInicio, LocalDateTime horaFin, String estado) {
        this.idSolicitud = idSolicitud;
        this.cedulaUsuario = cedulaUsuario;
        this.fechaSolicitud = fechaSolicitud;
        this.tipoRecurso = tipoRecurso;
        this.detalleRecurso = detalleRecurso;
        this.fechaUso = fechaUso;
        this.horaInicio = horaInicio;
        this.horaFin = horaFin;
        this.estado = estado;
    }

    public int getIdSolicitud() { return idSolicitud; }
    public void setIdSolicitud(int idSolicitud) { this.idSolicitud = idSolicitud; }
    public long getCedulaUsuario() { return cedulaUsuario; }
    public void setCedulaUsuario(long cedulaUsuario) { this.cedulaUsuario = cedulaUsuario; }
    public Date getFechaSolicitud() { return fechaSolicitud; }
    public void setFechaSolicitud(Date fechaSolicitud) { this.fechaSolicitud = fechaSolicitud; }
    public String getTipoRecurso() { return tipoRecurso; }
    public void setTipoRecurso(String tipoRecurso) { this.tipoRecurso = tipoRecurso; }
    public String getDetalleRecurso() { return detalleRecurso; }
    public void setDetalleRecurso(String detalleRecurso) { this.detalleRecurso = detalleRecurso; }
    public Date getFechaUso() { return fechaUso; }
    public void setFechaUso(Date fechaUso) { this.fechaUso = fechaUso; }
    public LocalDateTime getHoraInicio() { return horaInicio; }
    public void setHoraInicio(LocalDateTime horaInicio) { this.horaInicio = horaInicio; }
    public LocalDateTime getHoraFin() { return horaFin; }
    public void setHoraFin(LocalDateTime horaFin) { this.horaFin = horaFin; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
}

