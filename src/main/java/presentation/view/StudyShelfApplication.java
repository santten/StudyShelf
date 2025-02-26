package presentation.view;

import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;

import static presentation.view.Screen.SCREEN_LOGIN;
import presentation.GUILogger;

public class StudyShelfApplication extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        GUILogger.setWarVisibility(true);
        GUILogger.setInfoVisibility(true);

        SceneManager sm = SceneManager.getInstance();
        sm.setPrimaryStage(primaryStage);
        sm.setScreen(SCREEN_LOGIN);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
