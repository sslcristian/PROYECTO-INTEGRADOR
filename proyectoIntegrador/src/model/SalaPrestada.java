package model;

import javafx.beans.property.*;
import java.sql.Timestamp;

public class SalaPrestada {
    private IntegerProperty idPrestamoS;
    private IntegerProperty idSolicitudS;
    private IntegerProperty idSala;
    private ObjectProperty<Timestamp> fechaInicio;
    private ObjectProperty<Timestamp> fechaFin;
    private StringProperty observaciones;

    // Constructor
    public SalaPrestada(int idPrestamoS, int idSolicitudS, int idSala, Timestamp fechaInicio, Timestamp fechaFin, String observaciones) {
        this.idPrestamoS = new SimpleIntegerProperty(idPrestamoS);
        this.idSolicitudS = new SimpleIntegerProperty(idSolicitudS);
        this.idSala = new SimpleIntegerProperty(idSala);
        this.fechaInicio = new SimpleObjectProperty<>(fechaInicio);
        this.fechaFin = new SimpleObjectProperty<>(fechaFin);
        this.observaciones = new SimpleStringProperty(observaciones);
    }

    // Getters para las propiedades de JavaFX
    public IntegerProperty idPrestamoSProperty() {
        return idPrestamoS;
    }

    public IntegerProperty idSolicitudSProperty() {
        return idSolicitudS;
    }

    public IntegerProperty idSalaProperty() {
        return idSala;
    }

    public ObjectProperty<Timestamp> fechaInicioProperty() {
        return fechaInicio;
    }

    public ObjectProperty<Timestamp> fechaFinProperty() {
        return fechaFin;
    }

    public StringProperty observacionesProperty() {
        return observaciones;
    }

    // Métodos para acceder a los valores directamente
    public int getIdPrestamoS() {
        return idPrestamoS.get();
    }

    public void setIdPrestamoS(int idPrestamoS) {
        this.idPrestamoS.set(idPrestamoS);
    }

    public int getIdSolicitudS() {
        return idSolicitudS.get();
    }

    public void setIdSolicitudS(int idSolicitudS) {
        this.idSolicitudS.set(idSolicitudS);
    }

    public int getIdSala() {
        return idSala.get();
    }

    public void setIdSala(int idSala) {
        this.idSala.set(idSala);
    }

    public Timestamp getFechaInicio() {
        return fechaInicio.get();
    }

    public void setFechaInicio(Timestamp fechaInicio) {
        this.fechaInicio.set(fechaInicio);
    }

    public Timestamp getFechaFin() {
        return fechaFin.get();
    }

    public void setFechaFin(Timestamp fechaFin) {
        this.fechaFin.set(fechaFin);
    }

    public String getObservaciones() {
        return observaciones.get();
    }

    public void setObservaciones(String observaciones) {
        this.observaciones.set(observaciones);
    }
}
