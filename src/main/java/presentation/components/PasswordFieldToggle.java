package presentation.components;

import javafx.geometry.Pos;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.SVGPath;
import presentation.utility.SVGContents;
import presentation.utility.StyleClasses;

public class PasswordFieldToggle {
    private PasswordFieldToggle(){}

    public static StackPane create(PasswordField passwordField, int fixedWidth) {
        ToggleButton toggleButton = new ToggleButton();

        toggleButton.getStyleClass().add(StyleClasses.BUTTON_EMPTY);
        SVGPath svg = new SVGPath();
        SVGContents.setScale(svg, 1.2);
        svg.setContent(SVGContents.EYE);
        toggleButton.setGraphic(svg);

        svg.getStyleClass().add(StyleClasses.PRIMARY_LIGHT);

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
            svg.getStyleClass().add(StyleClasses.PRIMARY_LIGHT);

            if (toggleButton.isSelected()) {
                stackPane.getChildren().remove(passwordField);
                stackPane.getChildren().add(0, textField);
                svg.setContent(SVGContents.NO_EYE);
                toggleButton.setGraphic(svg);
            } else {
                stackPane.getChildren().remove(textField);
                stackPane.getChildren().add(0, passwordField);
                svg.setContent(SVGContents.EYE);
                toggleButton.setGraphic(svg);
            }
        });

        return stackPane;
    }
}