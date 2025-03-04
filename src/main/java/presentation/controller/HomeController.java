package presentation.controller;

import infrastructure.repository.StudyMaterialRepository;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;

// HomeController is a controller class which controls the home page of the application.
public class HomeController {
    StudyMaterialRepository test = new StudyMaterialRepository();

    @FXML
    private VBox mainVBoxHome;

}