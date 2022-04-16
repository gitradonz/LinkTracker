package edu.pablorios.linktracker.utils;

import javafx.scene.control.Alert;

// Clase útil para utilizar mensajes
public class MessageUtils {

    // Método para mostrar un error en la carga de URL's
    public static void showError(){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ERROR");
        alert.setHeaderText("Process error");
        alert.setContentText("No URL list loaded");
        alert.showAndWait();
    }

    // Método para mostrar un mensaje de cargado de webs correcto
    public static void showMessageOK(int cantidadPags) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("COMPLETED");
        alert.setHeaderText("Completed");
        alert.setContentText("All the URL's loaded successfully.\n"+
                "Number of URL's loaded: "+cantidadPags);
        alert.showAndWait();
    }

    public static void showMessageAlreadyLoaded() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("ERROR");
        alert.setHeaderText("Error loading files");
        alert.setContentText("You can only load 1 file\n" +
                "(Clear and then try it again).");
        alert.showAndWait();
    }
}
