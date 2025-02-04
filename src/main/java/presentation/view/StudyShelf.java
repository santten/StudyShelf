package presentation.view;

import javafx.application.Application;
import javafx.scene.Scene;

import javafx.stage.Stage;
import presentation.controller.SceneManager;

import java.io.IOException;

import static presentation.controller.Screen.HOME;

public class StudyShelf extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {

        SceneManager sm = SceneManager.getInstance();

        sm.setScene(HOME);
        Scene scene = new Scene(sm.getCurBase());

        primaryStage.setTitle("StudyShelf");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
