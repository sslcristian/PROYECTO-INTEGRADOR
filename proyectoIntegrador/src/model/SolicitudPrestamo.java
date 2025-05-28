package model;

import java.sql.Timestamp;

public class SolicitudPrestamo {
    private int idSolicitud;
    private long cedulaUsuario;
    private String detalleRecurso;
    private Timestamp fechaInicio;
    private Timestamp fechaFin;
    private String estado;
    private Integer idSala;    // Puede ser null
    private Integer idEquipo;  // Puede ser null

    public SolicitudPrestamo(int idSolicitud, long cedulaUsuario, String detalleRecurso,
                             Timestamp fechaInicio, Timestamp fechaFin, String estado,
                             Integer idSala, Integer idEquipo) {
        this.idSolicitud = idSolicitud;
        this.cedulaUsuario = cedulaUsuario;
        this.detalleRecurso = detalleRecurso;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.estado = estado;
        this.idSala = idSala;
        this.idEquipo = idEquipo;
    }

    public int getIdSolicitud() { return idSolicitud; }
    public void setIdSolicitud(int idSolicitud) { this.idSolicitud = idSolicitud; }

    public long getCedulaUsuario() { return cedulaUsuario; }
    public void setCedulaUsuario(long cedulaUsuario) { this.cedulaUsuario = cedulaUsuario; }

    public String getDetalleRecurso() { return detalleRecurso; }
    public void setDetalleRecurso(String detalleRecurso) { this.detalleRecurso = detalleRecurso; }

    public Timestamp getFechaInicio() { return fechaInicio; }
    public void setFechaInicio(Timestamp fechaInicio) { this.fechaInicio = fechaInicio; }

    public Timestamp getFechaFin() { return fechaFin; }
    public void setFechaFin(Timestamp fechaFin) { this.fechaFin = fechaFin; }

    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }

    public Integer getIdSala() { return idSala; }
    public void setIdSala(Integer idSala) { this.idSala = idSala; }

    public Integer getIdEquipo() { return idEquipo; }
    public void setIdEquipo(Integer idEquipo) { this.idEquipo = idEquipo; }

    // Métodos de ayuda para mostrar en TableView como String
    public String getIdSalaString() { return idSala != null ? idSala.toString() : ""; }
    public String getIdEquipoString() { return idEquipo != null ? idEquipo.toString() : ""; }

    public String getHoraInicioString() { return fechaInicio != null ? fechaInicio.toString() : ""; }
    public String getHoraFinString() { return fechaFin != null ? fechaFin.toString() : ""; }

    @Override
    public String toString() {
        return String.valueOf(idSolicitud);
    }
}