package presentation.controller;

import domain.model.Category;
import domain.model.StudyMaterial;
import domain.model.User;
import infrastructure.repository.CategoryRepository;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import presentation.components.MaterialCard;
import presentation.logger.GUILogger;

import java.util.List;

public class HomeController {
    private final CategoryRepository categoryRepo = new CategoryRepository();

    @FXML private VBox mainVBox;

    @FXML
    private void initialize() {
        List<Category> categories = categoryRepo.findAll();
        GUILogger.info("Loading categories: " + categories.size());

        for (Category c : categories) {
            Label title = new Label();

            title.setText(c.getCategoryName());
            title.getStyleClass().add("label3");

            mainVBox.getChildren().add(title);

            List<StudyMaterial> materials  = categoryRepo.findMaterialsByCategory(c);
            GUILogger.info("Loading materials " + materials.size() + " for category " + c.getCategoryName());
            ScrollPane pane = MaterialCard.materialCardScrollHBox(materials);

            mainVBox.getChildren().add(pane);
        }
    }
}
