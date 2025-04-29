package presentation.components;

import domain.model.Tag;
import javafx.scene.control.Button;
import presentation.utility.StyleClasses;
import presentation.view.SceneManager;

import java.util.Objects;

public class TagButton {
    private TagButton(){}

    public static Button getBtn(Tag tag){
        Button btn = new Button(tag.getTagName());
        btn.getStylesheets().add(Objects.requireNonNull(TagButton.class.getResource("/css/style.css")).toExternalForm());
        btn.getStyleClass().add(StyleClasses.TAG);

        btn.setOnAction(e -> {
            SceneManager sm = SceneManager.getInstance();
            sm.setScreen(tag);
        });

        return btn;
    }
}
