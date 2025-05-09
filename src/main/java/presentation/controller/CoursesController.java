package presentation.controller;

import domain.model.Category;
import domain.model.StudyMaterial;
import infrastructure.repository.CategoryRepository;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import presentation.components.MaterialCard;
import presentation.utility.GUILogger;
import presentation.utility.StyleClasses;
import presentation.view.LanguageManager;
import presentation.view.SceneManager;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import static presentation.utility.FXMLPageHandler.setUp;

public class CoursesController implements PageController {
    private final CategoryRepository categoryRepo = new CategoryRepository();
    private final ResourceBundle rb = LanguageManager.getInstance().getBundle();

    public void setPage(){
        setUp("/fxml/courses.fxml");
    }

    @Override
    public String getPageName() {
        return "All Courses";
    }

    @FXML private VBox mainVBoxCourses;

    @FXML
    private void initialize() {
        loadAllMaterials();
    }

    private void loadAllMaterials() {
        mainVBoxCourses.getChildren().clear();
        List<Category> categories = categoryRepo.findAll();
        GUILogger.info("Loading categories: " + categories.size());

        if (categories.isEmpty()){
            mainVBoxCourses.getChildren().add(new Label(rb.getString("error.noCourses")));
        } else {
            for (Category c : categories) {
                VBox courseContainer = new VBox();
                courseContainer.setSpacing(10);

                Label title = new Label();

                title.setText(c.getCategoryName());
                title.getStyleClass().addAll(StyleClasses.LABEL3, StyleClasses.SECONDARY);

                Button button = new Button(rb.getString("seeCoursePage"));
                button.getStyleClass().add(StyleClasses.BTN_XS);

                HBox hbox = new HBox(button, new Text(String.format(rb.getString("courseBy"), c.getCreator().getFullName())));
                hbox.setSpacing(8);

                button.setOnAction(e -> SceneManager.getInstance().setScreen(c));

                courseContainer.getChildren().addAll(title, hbox);

                List<StudyMaterial> materials = categoryRepo.findApprovedMaterialsByCategory(c);
                GUILogger.info("Loading materials " + materials.size() + " for category " + c.getCategoryName());
                if (materials.isEmpty()) {
                    courseContainer.getChildren().add(new Text(rb.getString("noMaterials")));
                } else {
                    courseContainer.getChildren().add(MaterialCard.materialCardScrollHBox(materials));
                }

                mainVBoxCourses.getChildren().add(courseContainer);
            }
        }
    }
}