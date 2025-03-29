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
import presentation.utility.GUILogger;
import presentation.view.LanguageManager;
import presentation.view.SceneManager;

import java.util.List;
import java.util.ResourceBundle;

public class CoursesController {
    private final CategoryRepository categoryRepo = new CategoryRepository();
    private ResourceBundle rb = LanguageManager.getInstance().getBundle();

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

            Button button = new Button(rb.getString("seeCoursePage"));
            button.getStyleClass().add("btnXS");

            HBox hbox = new HBox(button, new Text(String.format(rb.getString("courseBy"), c.getCreator().getFullName())));
            hbox.setSpacing(8);

            button.setOnAction(e -> {
                SceneManager.getInstance().displayCategory(c.getCategoryId());
            });

            courseContainer.getChildren().addAll(title, hbox);

            List<StudyMaterial> materials  = categoryRepo.findMaterialsByCategory(c);
            GUILogger.info("Loading materials " + materials.size() + " for category " + c.getCategoryName());
            if (materials.isEmpty()){
                courseContainer.getChildren().add(new Text (rb.getString("noMaterials")));
            } else {
                courseContainer.getChildren().add(MaterialCard.materialCardScrollHBox(materials));
            }

            mainVBoxCourses.getChildren().add(courseContainer);
        }
    }
}