package presentation.view;

import javafx.application.Application;
import javafx.scene.Scene;

import javafx.stage.Stage;

import java.io.IOException;

import static presentation.view.Screen.HOME;

public class StudyShelf extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        SceneManager sm = SceneManager.getInstance();

        sm.setScene(HOME);

        primaryStage.setTitle("StudyShelf");
        primaryStage.setWidth(800);
        primaryStage.setHeight(600);

        primaryStage.setMinWidth(800);
        primaryStage.setMinHeight(600);

        primaryStage.setScene(new Scene(sm.getCurBase()));

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
