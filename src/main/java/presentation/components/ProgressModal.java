package presentation.components;


import javafx.concurrent.Task;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import presentation.controller.CategoryPageController;
import presentation.utility.GUILogger;
import presentation.utility.StyleClasses;
import presentation.view.LanguageManager;
import presentation.view.SceneManager;

import java.util.Objects;
import java.util.ResourceBundle;

public class ProgressModal {
    private final static Stage dialogStage = new Stage();
    private final static VBox taskList = new VBox();
    private static final ResourceBundle rb = LanguageManager.getInstance().getBundle();

    private ProgressModal(){
        dialogStage.initOwner(SceneManager.getInstance().getPrimaryStage());
        dialogStage.initModality(Modality.NONE);
        dialogStage.setTitle(rb.getString("downloads"));

        Label title = new Label(rb.getString("downloads"));
        title.getStyleClass().add(StyleClasses.LABEL3);

        VBox vbox = new VBox(title, getTaskVBox());
        vbox.setPadding(new Insets(12, 12, 12, 12));

        dialogStage.setScene(new Scene(new ScrollPane(vbox)));
        dialogStage.getScene().getStylesheets().add(Objects.requireNonNull(CategoryPageController.class.getResource("/css/style.css")).toExternalForm());

        dialogStage.setWidth(450);
        dialogStage.setHeight(200);
    }

    public static final class ProgressModalHolder {
        public static final ProgressModal instance = new ProgressModal();
    }

    public static ProgressModal getInstance() {
        return ProgressModalHolder.instance;
    }

    public static VBox getTaskVBox(){
        return taskList;
    }

    public static void addTask(String materialName, Task<?> task){
        Label label = new Label(materialName);

        label.setMinWidth(160);
        label.setMaxWidth(160);

        ProgressBar progressBar = new ProgressBar();
        progressBar.setPrefWidth(200);
        progressBar.progressProperty().bind(task.progressProperty());

        HBox hbox = new HBox(12, progressBar, label);
        hbox.setPadding(new Insets(12, 0, 0,0));

        hbox.setAlignment(Pos.CENTER);

        taskList.getChildren().add(0, hbox);

        task.setOnSucceeded(e -> {
            GUILogger.info("success");
            progressBar.getStyleClass().add(StyleClasses.PROGRESS_SUCCESS);
        });

        task.setOnFailed(e -> {
            GUILogger.warn("failed");
            progressBar.getStyleClass().add(StyleClasses.PROGRESS_FAIL);
        });

        task.setOnCancelled(e -> GUILogger.warn("download cancelled"));
    }

    public void show() {
        dialogStage.show();
    }
}
