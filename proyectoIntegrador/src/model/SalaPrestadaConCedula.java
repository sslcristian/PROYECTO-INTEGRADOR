package model;

import java.sql.Date;

public class SalaPrestadaConCedula extends SalaPrestada {
    private long cedulaUsuario;

    public SalaPrestadaConCedula(int idPrestamoS, int idSolicitudS, int idSala, Date fechaInicio, Date fechaFin, String observaciones, long cedulaUsuario) {
        super(idPrestamoS, idSolicitudS, idSala, fechaInicio, fechaFin, observaciones);
        this.cedulaUsuario = cedulaUsuario;
    }

    public long getCedulaUsuario() {
        return cedulaUsuario;
    }

    public void setCedulaUsuario(long cedulaUsuario) {
        this.cedulaUsuario = cedulaUsuario;
    }
}