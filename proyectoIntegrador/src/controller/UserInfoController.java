package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import model.Usuario;

public class UserInfoController {

    @FXML private Label lblNombre;
    @FXML private Label lblCedula;
    @FXML private Label lblCorreo;
    @FXML private Label lblTelefono;
    @FXML private Label lblDepartamento;
    @FXML private Label lblTipoUsuario;

    public void setUsuario(Usuario usuario) {
        if (usuario != null) {
            lblNombre.setText(usuario.getNombre());
            lblCedula.setText(String.valueOf(usuario.getCedula()));
            lblCorreo.setText(usuario.getCorreo());
            lblTelefono.setText(usuario.getTelefono());
            lblDepartamento.setText(usuario.getDepartamento());
            lblTipoUsuario.setText(usuario.getTipoUsuario());
        }
    }
}
