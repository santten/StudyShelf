package presentation.controller;

import infrastructure.repository.StudyMaterialRepository;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

public class HomeController {
    StudyMaterialRepository test = new StudyMaterialRepository();

    @FXML
    private VBox mainVBoxHome;

}