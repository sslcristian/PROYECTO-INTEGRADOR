package model;

public class SolicitudComboDTO {
    private int idSolicitud;
    private int cedulaUsuario;

    public SolicitudComboDTO(int idSolicitud, int cedulaUsuario) {
        this.idSolicitud = idSolicitud;
        this.cedulaUsuario = cedulaUsuario;
    }

    public int getIdSolicitud() {
        return idSolicitud;
    }

    public int getCedulaUsuario() {
        return cedulaUsuario;
    }

    @Override
    public String toString() {
        return idSolicitud + " - " + cedulaUsuario;
    }
}