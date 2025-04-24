package model;

import java.sql.Date;

public class Mantenimiento_Sala {
    private int idMantenimiento;
    private int idSala;
    private Date fechaMantenimiento;
    private String detalle;
    private String tecnicoResponsable;

    public Mantenimiento_Sala(int idMantenimiento, int idSala, Date fechaMantenimiento,
                           String detalle, String tecnicoResponsable) {
        this.idMantenimiento = idMantenimiento;
        this.idSala = idSala;
        this.fechaMantenimiento = fechaMantenimiento;
        this.detalle = detalle;
        this.tecnicoResponsable = tecnicoResponsable;
    }

    public int getIdMantenimiento() {
        return idMantenimiento;
    }

    public void setIdMantenimiento(int idMantenimiento) {
        this.idMantenimiento = idMantenimiento;
    }

    public int getIdSala() {
        return idSala;
    }

    public void setIdSala(int idSala) {
        this.idSala = idSala;
    }

    public Date getFechaMantenimiento() {
        return fechaMantenimiento;
    }

    public void setFechaMantenimiento(Date fechaMantenimiento) {
        this.fechaMantenimiento = fechaMantenimiento;
    }

    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }

    public String getTecnicoResponsable() {
        return tecnicoResponsable;
    }

    public void setTecnicoResponsable(String tecnicoResponsable) {
        this.tecnicoResponsable = tecnicoResponsable;
    }
}
