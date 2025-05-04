package model;

import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class FXUtils {
	public static <T> void clearSelectionAndFieldsE(TableView<T> table, TextField studentField, TextField courseField, DatePicker datePicker) {
        table.getSelectionModel().clearSelection();
        studentField.clear();
        courseField.clear();
        datePicker.setValue(null);

        studentField.setDisable(false);
        courseField.setDisable(false);
    }
	 public static <T> void clearSelectionAndFieldsS(TableView<T> table, TextField idField, TextField nameField, TextField emailField) {
	        table.getSelectionModel().clearSelection();  
	        idField.clear();  
	        nameField.clear();
	        emailField.clear();
	        idField.setDisable(false); 
	    }
	 public static <T> void clearSelectionAndFieldsC(TableView<T> table, TextField codeField, TextField nameField, TextField creditsField) {
		    table.getSelectionModel().clearSelection();  // 
		    codeField.clear();  
		    nameField.clear(); 
		    creditsField.clear(); 
		    codeField.setDisable(false);  
		}
	// Limpiar los campos de texto y desmarcar la selección de la tabla
	    public static void clearSelectionAndFieldsE(TableView<?> table, TextField nombreField, TextField tipoField, TextField estadoField, TextField ubicacionField, TextField marcaField, TextField modeloField, DatePicker fechaAdquisicionPicker) {
	        // Limpiar campos de texto
	        nombreField.clear();
	        tipoField.clear();
	        estadoField.clear();
	        ubicacionField.clear();
	        marcaField.clear();
	        modeloField.clear();
	        fechaAdquisicionPicker.setValue(null); // Limpiar la fecha

	        // Limpiar la selección de la tabla
	        table.getSelectionModel().clearSelection();
	    }

	    // Limpiar los campos de texto y desmarcar la selección de la tabla (para el caso de Salas)
	    public static void clearSelectionAndFieldsSA(TableView<?> table, TextField idSalaField, TextField nombreField, TextField capacidadField, TextField softwareField, TextField hardwareField, TextField ubicacionField, TextField estadoField) {
	        // Limpiar campos de texto
	        idSalaField.clear();
	        nombreField.clear();
	        capacidadField.clear();
	        softwareField.clear();
	        hardwareField.clear();
	        ubicacionField.clear();
	        estadoField.clear();

	        // Limpiar la selección de la tabla
	        table.getSelectionModel().clearSelection();
	    }
	
	 public static <T> void clearSelectionAndFieldsS(
			    TableView<T> table,
			    TextField idSalaField,
			    TextField nombreSalaField,
			    TextField capacidadField,
			    TextField softwareField,
			    TextField hardwareField,
			    TextField ubicacionField,
			    TextField estadoField
			) {
			    table.getSelectionModel().clearSelection();
			    idSalaField.clear();
			    nombreSalaField.clear();
			    capacidadField.clear();
			    softwareField.clear();
			    hardwareField.clear();
			    ubicacionField.clear();
			    estadoField.clear();

			    idSalaField.setDisable(false); // Habilita el campo de ID por si se desactiva al seleccionar
			}


}
