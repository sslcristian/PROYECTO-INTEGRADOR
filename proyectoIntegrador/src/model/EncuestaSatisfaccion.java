package model;

import java.sql.Date;

public class EncuestaSatisfaccion {
    private int idEncuesta;
    private long idUsuario;
    private int idSolicitud;
    private int calificacion;
    private String comentarios;
    private Date fechaRespuesta;

    public EncuestaSatisfaccion(int idEncuesta, long idUsuario, int idSolicitud, int calificacion,
                                String comentarios, Date fechaRespuesta) {
        this.idEncuesta = idEncuesta;
        this.idUsuario = idUsuario;
        this.idSolicitud = idSolicitud;
        this.calificacion = calificacion;
        this.comentarios = comentarios;
        this.fechaRespuesta = fechaRespuesta;
    }

    public int getIdEncuesta() {
        return idEncuesta;
    }

    public void setIdEncuesta(int idEncuesta) {
        this.idEncuesta = idEncuesta;
    }

    public long getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(long idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdSolicitud() {
        return idSolicitud;
    }

    public void setIdSolicitud(int idSolicitud) {
        this.idSolicitud = idSolicitud;
    }

    public int getCalificacion() {
        return calificacion;
    }

    public void setCalificacion(int calificacion) {
        this.calificacion = calificacion;
    }

    public String getComentarios() {
        return comentarios;
    }

    public void setComentarios(String comentarios) {
        this.comentarios = comentarios;
    }

    public Date getFechaRespuesta() {
        return fechaRespuesta;
    }

    public void setFechaRespuesta(Date fechaRespuesta) {
        this.fechaRespuesta = fechaRespuesta;
    }
}
