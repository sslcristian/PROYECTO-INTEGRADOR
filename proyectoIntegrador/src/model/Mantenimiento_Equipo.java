package model;

import java.sql.Date;

public class Mantenimiento_Equipo {
    private int idMantenimiento;
    private int idEquipo;
    private Date fechaMantenimiento;
    private String detalle;
    private String tecnicoResponsable;

    public Mantenimiento_Equipo(int idMantenimiento, int idEquipo, Date fechaMantenimiento,
                           String detalle, String tecnicoResponsable) {
        this.idMantenimiento = idMantenimiento;
        this.idEquipo = idEquipo;
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

    public int getIdEquipo() {
        return idEquipo;
    }

    public void setIdEquipo(int idEquipo) {
        this.idEquipo = idEquipo;
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
