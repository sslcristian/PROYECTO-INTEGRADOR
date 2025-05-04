package model;

import java.sql.Date;

public class EquipoAudiovisual {
    private int idEquipo;
    private String nombre;
    private String tipo;
    private String estado;
    private String ubicacion;
    private String marca;
    private String modelo;
    private Date fechaAdquisicion;

    // Constructor con ID (para actualizaciones y lecturas desde BD)
    public EquipoAudiovisual(int idEquipo, String nombre, String tipo, String estado, String ubicacion,
                             String marca, String modelo, Date fechaAdquisicion) {
        this.idEquipo = idEquipo;
        this.nombre = nombre;
        this.tipo = tipo;
        this.estado = estado;
        this.ubicacion = ubicacion;
        this.marca = marca;
        this.modelo = modelo;
        this.fechaAdquisicion = fechaAdquisicion;
    }

    // Constructor sin ID (para inserciones nuevas, donde el ID lo genera la BD)
    public EquipoAudiovisual(String nombre, String tipo, String estado, String ubicacion,
                             String marca, String modelo, Date fechaAdquisicion) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.estado = estado;
        this.ubicacion = ubicacion;
        this.marca = marca;
        this.modelo = modelo;
        this.fechaAdquisicion = fechaAdquisicion;
    }

    // Getters y Setters
    public int getIdEquipo() { return idEquipo; }
    public void setIdEquipo(int idEquipo) { this.idEquipo = idEquipo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public String getUbicacion() { return ubicacion; }
    public void setUbicacion(String ubicacion) { this.ubicacion = ubicacion; }
    public String getMarca() { return marca; }
    public void setMarca(String marca) { this.marca = marca; }
    public String getModelo() { return modelo; }
    public void setModelo(String modelo) { this.modelo = modelo; }
    public Date getFechaAdquisicion() { return fechaAdquisicion; }
    public void setFechaAdquisicion(Date fechaAdquisicion) { this.fechaAdquisicion = fechaAdquisicion; }
}
