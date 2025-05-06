package model;

import javafx.beans.property.*;
import java.sql.Date;

public class SalaPrestada {
    private final IntegerProperty idPrestamoS;
    private final IntegerProperty idSolicitudS;
    private final IntegerProperty idSala;
    private final ObjectProperty<Date> fechaInicio;
    private final ObjectProperty<Date> fechaFin;
    private final StringProperty observaciones;

    // Constructor principal con todos los campos
    public SalaPrestada(int idPrestamoS, int idSolicitudS, int idSala, Date fechaInicio, Date fechaFin, String observaciones) {
        this.idPrestamoS = new SimpleIntegerProperty(idPrestamoS);
        this.idSolicitudS = new SimpleIntegerProperty(idSolicitudS);
        this.idSala = new SimpleIntegerProperty(idSala);
        this.fechaInicio = new SimpleObjectProperty<>(fechaInicio);
        this.fechaFin = new SimpleObjectProperty<>(fechaFin);
        this.observaciones = new SimpleStringProperty(observaciones);
    }

    // Constructor sin `idPrestamoS` (útil para inserciones en la BD)
    public SalaPrestada(int idSolicitudS, int idSala, Date fechaInicio, Date fechaFin, String observaciones) {
        this.idPrestamoS = new SimpleIntegerProperty(0); // Se asignará en BD
        this.idSolicitudS = new SimpleIntegerProperty(idSolicitudS);
        this.idSala = new SimpleIntegerProperty(idSala);
        this.fechaInicio = new SimpleObjectProperty<>(fechaInicio);
        this.fechaFin = new SimpleObjectProperty<>(fechaFin);
        this.observaciones = new SimpleStringProperty(observaciones);
    }

    // Getters como `Property` para JavaFX
    public IntegerProperty idPrestamoSProperty() { return idPrestamoS; }
    public IntegerProperty idSolicitudSProperty() { return idSolicitudS; }
    public IntegerProperty idSalaProperty() { return idSala; }
    public ObjectProperty<Date> fechaInicioProperty() { return fechaInicio; }
    public ObjectProperty<Date> fechaFinProperty() { return fechaFin; }
    public StringProperty observacionesProperty() { return observaciones; }

    // Métodos getter estándar
    public int getIdPrestamoS() { return idPrestamoS.get(); }
    public int getIdSolicitudS() { return idSolicitudS.get(); }
    public int getIdSala() { return idSala.get(); }
    public Date getFechaInicio() { return fechaInicio.get(); }
    public Date getFechaFin() { return fechaFin.get(); }
    public String getObservaciones() { return observaciones.get(); }

    // Métodos setter estándar
    public void setIdPrestamoS(int idPrestamoS) { this.idPrestamoS.set(idPrestamoS); }
    public void setIdSolicitudS(int idSolicitudS) { this.idSolicitudS.set(idSolicitudS); }
    public void setIdSala(int idSala) { this.idSala.set(idSala); }
    public void setFechaInicio(Date fechaInicio) { this.fechaInicio.set(fechaInicio); }
    public void setFechaFin(Date fechaFin) { this.fechaFin.set(fechaFin); }
    public void setObservaciones(String observaciones) { this.observaciones.set(observaciones); }
}
