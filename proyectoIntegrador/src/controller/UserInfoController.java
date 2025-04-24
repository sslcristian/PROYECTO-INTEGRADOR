package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import model.Usuario;

public class UserInfoController {

    @FXML
    private Label lblNombre;
    @FXML
    private Label lblCedula;
    @FXML
    private Label lblCorreo;
    @FXML
    private Label lblTelefono;
    @FXML
    private Label lblTipoUsuario;
    @FXML
    private Label lblDepartamento;

   
    public void setUsuario(Usuario usuario) {
        if (usuario != null) {
            lblNombre.setText("Nombre: " + usuario.getNombre());
            lblCedula.setText("Cédula: " + usuario.getCedula());
            lblCorreo.setText("Correo: " + usuario.getCorreo());
            lblTelefono.setText("Teléfono: " + usuario.getTelefono());
            lblTipoUsuario.setText("Tipo de Usuario: " + usuario.getTipoUsuario());
            lblDepartamento.setText("Departamento: " + usuario.getDepartamento());
        }
    }
}
