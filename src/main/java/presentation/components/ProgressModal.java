package presentation.components;

import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ProgressModal {
    private final Stage dialogStage;
    private final ProgressBar progressBar;

    public ProgressModal(Stage owner, String title, String message, Task<?> task) {
        dialogStage = new Stage();
        dialogStage.initOwner(owner);
        dialogStage.initModality(Modality.NONE);
        dialogStage.setTitle(title);

        Label label = new Label(message);
        progressBar = new ProgressBar();
        progressBar.setPrefWidth(300);
        progressBar.progressProperty().bind(task.progressProperty());

        VBox vbox = new VBox(16, label, progressBar);
        vbox.setAlignment(Pos.CENTER);
        vbox.setPadding(new Insets(24));

        dialogStage.setScene(new Scene(vbox));
        dialogStage.setResizable(false);

        task.setOnSucceeded(e -> dialogStage.close());
        task.setOnFailed(e -> dialogStage.close());
        task.setOnCancelled(e -> dialogStage.close());
    }

    public void show() {
        dialogStage.show();
    }
}
