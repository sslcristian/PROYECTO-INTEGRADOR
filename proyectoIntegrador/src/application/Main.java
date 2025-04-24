package application;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.fxml.FXMLLoader;
import javafx.scene.image.Image;

public class Main extends Application {

    private static BorderPane rootLayout;
    private static Stage primaryStage;

    @Override
    public void start(Stage stage) {
        try {
            primaryStage = stage;

          
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainMenu.fxml"));
            rootLayout = loader.load();

          
            Scene scene = new Scene(rootLayout);
            scene.getStylesheets().add(getClass().getResource("/view/style.css").toExternalForm());

            loadIcon();

            primaryStage.setScene(scene);
            primaryStage.setTitle("Sistema de Reservas UDI");
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadIcon() {
        try {
            Image icon = new Image(getClass().getResourceAsStream("/application/icons/UDI.png"));
            if (!icon.isError()) {
                primaryStage.getIcons().add(icon);
            } else {
                System.out.println("Error: No se pudo cargar el icono.");
            }
        } catch (Exception e) {
            System.out.println("Error al cargar el icono: " + e.getMessage());
        }
    }

    public static void loadView(String fxmlFile) {
        try {
            FXMLLoader loader = new FXMLLoader(Main.class.getResource(fxmlFile));
            Parent view = loader.load();
            rootLayout.setCenter(view);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void loadScene(String fxmlFile) {
        try {
            Parent root = FXMLLoader.load(Main.class.getResource(fxmlFile));
            Scene scene = primaryStage.getScene();

            if (scene == null) {
                scene = new Scene(root, 600, 600);
                primaryStage.setScene(scene);
            } else {
                scene.setRoot(root);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
