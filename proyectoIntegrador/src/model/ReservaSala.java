package model;

import javafx.beans.property.*;

public class ReservaSala {

    private final IntegerProperty id;
    private final StringProperty sala;
    private final StringProperty estado;
    private final StringProperty usuario;
    private final StringProperty fecha;
    private final StringProperty horaInicio;
    private final StringProperty duracion;

    public ReservaSala(int id, String sala, String estado, String usuario, String fecha, String horaInicio, String duracion) {
        this.id = new SimpleIntegerProperty(id);
        this.sala = new SimpleStringProperty(sala);
        this.estado = new SimpleStringProperty(estado);
        this.usuario = new SimpleStringProperty(usuario);
        this.fecha = new SimpleStringProperty(fecha);
        this.horaInicio = new SimpleStringProperty(horaInicio);
        this.duracion = new SimpleStringProperty(duracion);
    }

    // Getters y setters para id
    public int getId() { return id.get(); }
    public void setId(int id) { this.id.set(id); }
    public IntegerProperty idProperty() { return id; }

    // Getters y setters para sala
    public String getSala() { return sala.get(); }
    public void setSala(String sala) { this.sala.set(sala); }
    public StringProperty salaProperty() { return sala; }

    // Getters y setters para estado
    public String getEstado() { return estado.get(); }
    public void setEstado(String estado) { this.estado.set(estado); }
    public StringProperty estadoProperty() { return estado; }

    // Getters y setters para usuario
    public String getUsuario() { return usuario.get(); }
    public void setUsuario(String usuario) { this.usuario.set(usuario); }
    public StringProperty usuarioProperty() { return usuario; }

    // Getters y setters para fecha
    public String getFecha() { return fecha.get(); }
    public void setFecha(String fecha) { this.fecha.set(fecha); }
    public StringProperty fechaProperty() { return fecha; }

    // Getters y setters para horaInicio
    public String getHoraInicio() { return horaInicio.get(); }
    public void setHoraInicio(String horaInicio) { this.horaInicio.set(horaInicio); }
    public StringProperty horaInicioProperty() { return horaInicio; }

    // Getters y setters para duracion
    public String getDuracion() { return duracion.get(); }
    public void setDuracion(String duracion) { this.duracion.set(duracion); }
    public StringProperty duracionProperty() { return duracion; }
}
