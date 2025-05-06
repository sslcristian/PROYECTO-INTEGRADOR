package model;

import javafx.scene.control.ComboBox;
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

	 public static void clearSelectionAndFields(TableView<?> table, 
             TextField idSalaField, 
             TextField nombreField, 
             TextField capacidadField, 
             TextField softwareField, 
             TextField hardwareField, 
             TextField ubicacionField, 
             ComboBox<String> estadoComboBox) {
table.getSelectionModel().clearSelection();  // Limpiar la selección en la tabla

// Limpiar los campos de texto
idSalaField.clear();
nombreField.clear();
capacidadField.clear();
softwareField.clear();
hardwareField.clear();
ubicacionField.clear();

// Restablecer el ComboBox al valor por defecto
if (estadoComboBox != null) {
estadoComboBox.getSelectionModel().selectFirst();  // Seleccionar el primer valor
}
}
	 public static void clearSelectionAndFieldsE(TableView<?> tableView, TextField nombreField, TextField tipoField, 
             TextField estadoField, TextField ubicacionField, TextField marcaField, 
             TextField modeloField, DatePicker fechaAdquisicionPicker) {
// Limpiar la selección de la tabla
if (tableView != null) {
tableView.getSelectionModel().clearSelection();
}

// Limpiar los campos de texto
if (nombreField != null) nombreField.clear();
if (tipoField != null) tipoField.clear();
if (estadoField != null) estadoField.clear();
if (ubicacionField != null) ubicacionField.clear();
if (marcaField != null) marcaField.clear();
if (modeloField != null) modeloField.clear();

// Limpiar el campo de fecha
if (fechaAdquisicionPicker != null) fechaAdquisicionPicker.setValue(null);
}
	 public static void clearSelectionAndFields(
	            TableView<?> table,
	            TextField... textFields
	    ) {
	        table.getSelectionModel().clearSelection();
	        for (TextField tf : textFields) {
	            tf.clear();
	        }
	    }

	 
	    public static void clearSelectionAndFields(
	            TableView<?> table,
	            TextField tf1, TextField tf2, TextField tf3, TextField tf4, TextField tf5, TextField tf6,
	            ComboBox<String> estadoComboBox,
	            DatePicker datePicker
	    ) {
	        table.getSelectionModel().clearSelection();

	        tf1.clear();
	        tf2.clear();
	        tf3.clear();
	        tf4.clear();
	        tf5.clear();
	        tf6.clear();

	        if (estadoComboBox != null) {
	            estadoComboBox.getSelectionModel().selectFirst();
	        }

	        if (datePicker != null) {
	            datePicker.setValue(null);
	        }

	        if (tf1 != null) tf1.setDisable(false); // Rehabilitar ID si estaba desactivado
	    }
}
