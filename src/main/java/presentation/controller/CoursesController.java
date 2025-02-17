package presentation.controller;

import domain.model.Category;
import domain.model.StudyMaterial;
import infrastructure.repository.CategoryRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import presentation.components.MaterialCard;
import presentation.logger.GUILogger;

import java.util.List;

public class CoursesController {
    private final CategoryRepository categoryRepo = new CategoryRepository();

    @FXML private VBox mainVBoxCourses;

    @FXML
    private void initialize() {
        List<Category> categories = categoryRepo.findAll();
        GUILogger.info("Loading categories: " + categories.size());

        for (Category c : categories) {
            Label title = new Label();

            title.setText(c.getCategoryName());
            title.getStyleClass().add("label3");

            mainVBoxCourses.getChildren().add(title);

            List<StudyMaterial> materials  = categoryRepo.findMaterialsByCategory(c);
            GUILogger.info("Loading materials " + materials.size() + " for category " + c.getCategoryName());
            ScrollPane pane = MaterialCard.materialCardScrollHBox(materials);

            mainVBoxCourses.getChildren().add(pane);
        }
    }
}
