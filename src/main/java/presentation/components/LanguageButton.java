package presentation.components;

import javafx.scene.control.Button;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import presentation.utility.StyleClasses;

import java.util.Objects;

public class LanguageButton {
    private LanguageButton() {}

    public static Button createButton(String imagePath, String languageName){
        Button btn = new Button();
        Image image = new Image(Objects.requireNonNull(LanguageButton.class.getResourceAsStream(imagePath)));
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(20);
        imageView.setFitHeight(15);

        btn.setGraphic(imageView);
        btn.setPrefSize(20, 15);
        btn.getStyleClass().add(StyleClasses.BUTTON_EMPTY);

        Tooltip tooltip = new Tooltip(languageName);
        btn.setTooltip(tooltip);

        return btn;
    }
}
