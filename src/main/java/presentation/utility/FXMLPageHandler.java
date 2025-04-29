package presentation.utility;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.ScrollPane;
import presentation.view.SceneManager;

import java.io.IOException;
import java.util.Objects;

public class FXMLPageHandler {
    public static void setUp(String path){
        ScrollPane base = new ScrollPane();
        base.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        base.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        try {
            base.setContent(FXMLLoader.load(Objects.requireNonNull(SceneManager.class.getResource(path))));
            SceneManager.SceneManagerHolder.instance.current.setCenter(base);
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }
}
