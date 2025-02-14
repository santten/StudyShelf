package presentation.controller;

import domain.model.StudyMaterial;
import infrastructure.repository.StudyMaterialRepository;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.util.List;

import static presentation.components.MaterialComponents.materialCard;
import static presentation.components.MaterialComponents.materialCardHBox;

public class HomeController {
    StudyMaterialRepository test = new StudyMaterialRepository();

    @FXML
    private VBox mainVBox;

    private String s;
    private final List<StudyMaterial> materialList = test.findAllStudyMaterials();

    public void initialize() {
        mainVBox.getChildren().add(materialCardHBox(materialList));
    }
}