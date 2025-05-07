package model;

import javafx.beans.property.*;
import java.sql.Date;

public class Mantenimiento_Equipo {

    private IntegerProperty idMantenimiento;
    private IntegerProperty idEquipo;
    private ObjectProperty<Date> fechaMantenimiento;
    private StringProperty detalle;
    private StringProperty tecnicoResponsable;

    public Mantenimiento_Equipo(int idMantenimiento, int idEquipo, Date fechaMantenimiento,
                           String detalle, String tecnicoResponsable) {
        this.idMantenimiento = new SimpleIntegerProperty(idMantenimiento);
        this.idEquipo = new SimpleIntegerProperty(idEquipo);
        this.fechaMantenimiento = new SimpleObjectProperty<>(fechaMantenimiento);
        this.detalle = new SimpleStringProperty(detalle);
        this.tecnicoResponsable = new SimpleStringProperty(tecnicoResponsable);
    }

    // Getter y setter para idMantenimiento
    public IntegerProperty idMantenimientoProperty() {
        return idMantenimiento;
    }

    public int getIdMantenimiento() {
        return idMantenimiento.get();
    }

    public void setIdMantenimiento(int idMantenimiento) {
        this.idMantenimiento.set(idMantenimiento);
    }

    // Getter y setter para idEquipo
    public IntegerProperty idEquipoProperty() {
        return idEquipo;
    }

    public int getIdEquipo() {
        return idEquipo.get();
    }

    public void setIdEquipo(int idEquipo) {
        this.idEquipo.set(idEquipo);
    }

    // Getter y setter para fechaMantenimiento
    public ObjectProperty<Date> fechaMantenimientoProperty() {
        return fechaMantenimiento;
    }

    public Date getFechaMantenimiento() {
        return fechaMantenimiento.get();
    }

    public void setFechaMantenimiento(Date fechaMantenimiento) {
        this.fechaMantenimiento.set(fechaMantenimiento);
    }

    // Getter y setter para detalle
    public StringProperty detalleProperty() {
        return detalle;
    }

    public String getDetalle() {
        return detalle.get();
    }

    public void setDetalle(String detalle) {
        this.detalle.set(detalle);
    }

    // Getter y setter para tecnicoResponsable
    public StringProperty tecnicoResponsableProperty() {
        return tecnicoResponsable;
    }

    public String getTecnicoResponsable() {
        return tecnicoResponsable.get();
    }

    public void setTecnicoResponsable(String tecnicoResponsable) {
        this.tecnicoResponsable.set(tecnicoResponsable);
    }
}
