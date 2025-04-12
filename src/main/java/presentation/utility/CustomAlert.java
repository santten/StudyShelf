package presentation.utility;

import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.Objects;
import java.util.Optional;

public class CustomAlert {
    private CustomAlert(){}

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

        alert.getDialogPane().getStylesheets().add(Objects.requireNonNull(CustomAlert.class.getResource("/css/style.css")).toExternalForm());

        alert.setHeaderText(mainText);
        alert.setTitle(heading);

        Text text = new Text(mainText);
        text.setWrappingWidth(400);

        Text label = new Text(heading);
        label.getStyleClass().add(StyleClasses.HEADING4);

        VBox vbox = new VBox(label, text);
        vbox.setPadding(new Insets(10));

        alert.setHeaderText(null);
        alert.getDialogPane().setHeader(vbox);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.YES;
    }
}