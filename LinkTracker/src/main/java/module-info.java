module edu.pablorios.linktracker {
    requires javafx.controls;
    requires javafx.fxml;

    opens edu.pablorios.linktracker to javafx.fxml;
    exports edu.pablorios.linktracker;
}