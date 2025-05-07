package model;

import javafx.beans.property.*;
import java.sql.Date;

public class Mantenimiento_Sala {

    private IntegerProperty idMantenimiento;
    private IntegerProperty idSala;
    private ObjectProperty<Date> fechaMantenimiento;
    private StringProperty detalle;
    private StringProperty tecnicoResponsable;

    public Mantenimiento_Sala(int idMantenimiento, int idSala, Date fechaMantenimiento,
                           String detalle, String tecnicoResponsable) {
        this.idMantenimiento = new SimpleIntegerProperty(idMantenimiento);
        this.idSala = new SimpleIntegerProperty(idSala);
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

    // Getter y setter para idSala
    public IntegerProperty idSalaProperty() {
        return idSala;
    }

    public int getIdSala() {
        return idSala.get();
    }

    public void setIdSala(int idSala) {
        this.idSala.set(idSala);
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
