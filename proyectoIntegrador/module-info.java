module enrollmentSoftware {
	requires javafx.controls;
	requires javafx.fxml;
	requires java.sql;
	requires javafx.base;
	requires poi;
	requires poi.ooxml;
	opens controller;
	opens application to javafx.graphics, javafx.fxml;
	opens model to javafx.base;
}
