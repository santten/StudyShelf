package presentation.controller;

import domain.model.StudyMaterial;
import infrastructure.repository.StudyMaterialRepository;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

import java.util.List;

import static presentation.components.MaterialComponents.materialCardScrollHBox;

public class HomeController {
    StudyMaterialRepository test = new StudyMaterialRepository();

    @FXML
    private VBox mainVBoxHome;

    private String s;
    private final List<StudyMaterial> materialList = test.findAllStudyMaterials();

    public void initialize() {
        mainVBoxHome.getChildren().add(materialCardScrollHBox(materialList));
    }
}