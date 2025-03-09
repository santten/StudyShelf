package presentation.utility;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

public class CustomAlert {
    public static void show(javafx.scene.control.Alert.AlertType alertType, String title, String message) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static boolean confirm(String heading, String mainText, String details, boolean optionNo) {
        Alert alert = optionNo ? new Alert(Alert.AlertType.CONFIRMATION, details, ButtonType.YES, ButtonType.NO)
                : new Alert(Alert.AlertType.CONFIRMATION, details, ButtonType.YES);

        alert.setHeaderText(mainText);
        alert.setTitle(heading);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.YES;
    }
}