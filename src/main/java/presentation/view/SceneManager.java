package presentation.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.BorderPane;
import javafx.scene.text.Font;

import java.io.IOException;
import java.util.Objects;

public class SceneManager {
    private static SceneManager instance;
    private BorderPane base;

    private SceneManager(){
        }

    public static SceneManager getInstance() throws IOException {
      if (instance == null){
          instance = new SceneManager();
          instance.base = FXMLLoader.load(Objects.requireNonNull(SceneManager.class.getResource("/fxml/container.fxml")));
          instance.base.setTop(FXMLLoader.load(Objects.requireNonNull(SceneManager.class.getResource("/fxml/header.fxml"))));
          instance.base.setBottom(FXMLLoader.load(Objects.requireNonNull(SceneManager.class.getResource("/fxml/footer.fxml"))));
      }
      return instance;
    }

    public void setScene(Screen screen) throws IOException {
        switch(screen){
            case HOME:
                instance.base.setCenter(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/homeContent.fxml"))));
                break;
            case TEST2:
                instance.base.setCenter(FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/test2.fxml"))));
                break;
        }
    }

   public BorderPane getCurBase(){
        return instance.base;
   }
}
