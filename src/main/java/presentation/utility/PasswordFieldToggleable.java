package presentation.utility;

import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.shape.SVGPath;

public class PasswordFieldToggleable {
    public static HBox create(PasswordField passwordField, int fixedWidth) {
        ToggleButton toggleButton = new ToggleButton();

        toggleButton.getStyleClass().add("buttonEmpty");
        SVGPath svg = new SVGPath();
        SVGContents.setScale(svg, 1.2);
        svg.setContent(SVGContents.eye());
        toggleButton.setGraphic(svg);

        svg.getStyleClass().add("primary-light");

        TextField textField = new TextField();

        passwordField.setMinWidth(fixedWidth);
        passwordField.setMaxWidth(fixedWidth);
        textField.setMinWidth(fixedWidth);
        textField.setMaxWidth(fixedWidth);

        textField.managedProperty().bind(textField.visibleProperty());
        textField.visibleProperty().bind(toggleButton.selectedProperty());
        textField.textProperty().bindBidirectional(passwordField.textProperty());

        passwordField.managedProperty().bind(passwordField.visibleProperty());
        passwordField.visibleProperty().bind(toggleButton.selectedProperty().not());

        HBox hbox = new HBox();
        hbox.getChildren().add(0, passwordField);

        toggleButton.setMinWidth(30);
        toggleButton.setMaxWidth(30);

        hbox.getChildren().add(1, toggleButton);

        toggleButton.setOnAction(e -> {
            svg.getStyleClass().add("primary-light");

            if (toggleButton.isSelected()) {
                hbox.getChildren().remove(passwordField);
                hbox.getChildren().add(0, textField);
                svg.setContent(SVGContents.noEye());
                toggleButton.setGraphic(svg);
            } else {
                hbox.getChildren().remove(textField);
                hbox.getChildren().add(0, passwordField);
                svg.setContent(SVGContents.eye());
                toggleButton.setGraphic(svg);
            }
        });

        return hbox;
    }
}