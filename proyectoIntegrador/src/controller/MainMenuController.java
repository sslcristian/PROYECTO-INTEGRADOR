package controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.layout.StackPane;

import java.io.IOException;

public class MainMenuController {

    @FXML
    private StackPane contentPane;

    @FXML
    private javafx.scene.control.Button btnRegisterUser;
    @FXML
    private javafx.scene.control.Button btnRegisterAdmin;
    @FXML
    private javafx.scene.control.Button btnLoginUser;
    @FXML
    private javafx.scene.control.Button btnLoginAdmin;

    
    @FXML
    private void goToRegisterUser() {
        loadUI("/view/RegisterUserForm.fxml");
    }


    @FXML
    private void goToRegisterAdmin() {
        loadUI("/view/RegisterAdmin.fxml");
    }

    @FXML
    private void goToLoginUser() {
        loadUI("/view/LoginUser.fxml");
    }

    @FXML
    private void goToLoginAdmin() {
        loadUI("/view/LoginAdmin.fxml");
    }

    private void loadUI(String fxmlPath) {
        try {
            var resource = getClass().getResource(fxmlPath);
            System.out.println("Cargando: " + resource); 
            FXMLLoader loader = new FXMLLoader(resource);
            Parent node = loader.load();
            contentPane.getChildren().setAll(node);
        } catch (IOException | NullPointerException e) {
            System.out.println("No se pudo cargar la vista: " + fxmlPath);
            e.printStackTrace();
        }
    }

}

