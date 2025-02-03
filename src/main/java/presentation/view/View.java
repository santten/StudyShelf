package presentation.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class View extends Application {
    @Override
    public void start(Stage primaryStage) throws IOException {
        GridPane base = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/container.fxml")));

        GridPane header = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/header.fxml")));
        GridPane footer = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/footer.fxml")));

        Pane pane = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/test.fxml")));
        Pane pane2 = FXMLLoader.load(Objects.requireNonNull(getClass().getResource("/fxml/test2.fxml")));

        base.add(header, 0, 0);
        base.add(pane, 0, 1);
        base.add(footer, 0, 2);

        Scene scene = new Scene(base);

        primaryStage.setTitle("StudyShelf");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
