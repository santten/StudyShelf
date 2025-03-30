package presentation.components;

import javafx.geometry.Pos;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import presentation.utility.SVGContents;

public class PasswordFieldToggle {
    public static StackPane create(PasswordField passwordField, int fixedWidth) {
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


        StackPane stackPane = new StackPane();
        stackPane.getChildren().addAll(passwordField, toggleButton);

        toggleButton.setMinWidth(30);
        toggleButton.setMaxWidth(30);

        stackPane.setMinWidth(fixedWidth);
        stackPane.setMaxWidth(fixedWidth);

        StackPane.setAlignment(toggleButton, Pos.CENTER_RIGHT);
        StackPane.setMargin(toggleButton, new javafx.geometry.Insets(0, 5, 0, 0));

        toggleButton.setOnAction(e -> {
            svg.getStyleClass().add("primary-light");

            if (toggleButton.isSelected()) {
                stackPane.getChildren().remove(passwordField);
                stackPane.getChildren().add(0, textField);
                svg.setContent(SVGContents.noEye());
                toggleButton.setGraphic(svg);
            } else {
                stackPane.getChildren().remove(textField);
                stackPane.getChildren().add(0, passwordField);
                svg.setContent(SVGContents.eye());
                toggleButton.setGraphic(svg);
            }
        });

        return stackPane;
    }
}