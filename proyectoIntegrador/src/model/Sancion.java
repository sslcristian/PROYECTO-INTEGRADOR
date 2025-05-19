package model;

import java.sql.Date;

public class Sancion {
    private int idSancion;
    private long cedulaUsuario;
    private double monto;
    private String motivo;
    private Date fecha;
    private String estado;

    public Sancion(int idSancion, long cedulaUsuario, double monto, String motivo, Date fecha, String estado) {
        this.idSancion = idSancion;
        this.cedulaUsuario = cedulaUsuario;
        this.monto = monto;
        this.motivo = motivo;
        this.fecha = fecha;
        this.estado = estado;
    }

    public int getIdSancion() {
        return idSancion;
    }

    public void setIdSancion(int idSancion) {
        this.idSancion = idSancion;
    }

    public long getCedulaUsuario() {
        return cedulaUsuario;
    }

    public void setCedulaUsuario(long cedulaUsuario) {
        this.cedulaUsuario = cedulaUsuario;
    }

    public double getMonto() {
        return monto;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
