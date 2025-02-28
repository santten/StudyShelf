package presentation.controller;

import domain.model.Category;
import domain.model.StudyMaterial;
import infrastructure.repository.CategoryRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import presentation.components.MaterialCard;
import presentation.GUILogger;
import presentation.view.SceneManager;

import java.io.IOException;
import java.util.List;

public class CoursesController {
    private final CategoryRepository categoryRepo = new CategoryRepository();

    @FXML private VBox mainVBoxCourses;

    @FXML
    private void initialize() {
        loadAllMaterials();
    }

    private void loadAllMaterials() {
        mainVBoxCourses.getChildren().clear();
        List<Category> categories = categoryRepo.findAll();
        GUILogger.info("Loading categories: " + categories.size());

        for (Category c : categories) {
            VBox courseContainer = new VBox();
            courseContainer.setSpacing(10);

            Label title = new Label();

            title.setText(c.getCategoryName());
            title.getStyleClass().add("label3");
            title.getStyleClass().add("secondary");

            Button button = new Button("See Course Page");
            button.getStyleClass().add("btnXS");

            HBox hbox = new HBox(button, new Text("Course by " + c.getCreator().getFullName()));
            hbox.setSpacing(8);

            button.setOnAction(e -> {
                try {
                    SceneManager.getInstance().displayCategory(c.getCategoryId());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            });

            courseContainer.getChildren().addAll(title, hbox);

            List<StudyMaterial> materials  = categoryRepo.findMaterialsByCategory(c);
            GUILogger.info("Loading materials " + materials.size() + " for category " + c.getCategoryName());
            if (materials.isEmpty()){
                courseContainer.getChildren().add(new Text ("(This course doesn't contain any materials yet.)"));
            } else {
                courseContainer.getChildren().add(MaterialCard.materialCardScrollHBox(materials));
            }

            mainVBoxCourses.getChildren().add(courseContainer);
        }
    }
}
