package presentation;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class StudyShelfApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        Label helloWorldLabel = new Label("StudyShelf");
        StackPane root = new StackPane();
        root.getChildren().add(helloWorldLabel);

        Scene scene = new Scene(root, 300, 200);

        primaryStage.setTitle("Welcome to Study Shelf");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
