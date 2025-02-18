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
        loadAllMaterials();
    }

    private void loadAllMaterials() {
        mainVBoxCourses.getChildren().clear();
        List<Category> categories = categoryRepo.findAll();

        for (Category c : categories) {
            List<StudyMaterial> materials = categoryRepo.findMaterialsByCategory(c);
            if (!materials.isEmpty()) {
                Label title = new Label(c.getCategoryName());
                title.getStyleClass().add("label3");
                mainVBoxCourses.getChildren().add(title);

                ScrollPane pane = MaterialCard.materialCardScrollHBox(materials);
                mainVBoxCourses.getChildren().add(pane);
            }
        }
    }
}
