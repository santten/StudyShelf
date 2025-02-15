package presentation.controller;

import domain.model.Category;
import domain.model.User;
import domain.service.Session;
import infrastructure.repository.CategoryRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import presentation.logger.GUILogger;

import java.io.File;
import java.util.List;

public class UploadController {
    @FXML public VBox mainVBoxUpload;
    @FXML public TextField field_title;
    @FXML public TextArea field_desc;
    @FXML public Label text_uploadingAs;
    @FXML public Label label_fileTitle;
    @FXML public Button btn_uploadMaterial;
    @FXML public Button btn_getFile;
    @FXML public CheckBox checkbox_uploadAgreement;
    @FXML public ChoiceBox<Category> choice_category;

    private void setCategoryChoices(){
        List<Category> categories = new CategoryRepository().findAll();
        GUILogger.info("Loading categories: " + categories.size());
        ObservableList<Category> categoriesObservableList = FXCollections.observableList(categories);

        choice_category.setItems(categoriesObservableList);
    }

    @FXML private void initialize(){
        User curUser = Session.getInstance().getCurrentUser();
        text_uploadingAs.setText("Uploading as " + curUser.getFirstName() + " " + curUser.getLastName());

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Add Material File");
        // TODO: these should be decided
        fileChooser.getExtensionFilters().addAll();

        final boolean[] fileSelected = {false};

        btn_getFile.setOnAction(e -> {
           File selectedFile = fileChooser.showOpenDialog(null);
            if (selectedFile != null){
               GUILogger.info("User selected file from " + selectedFile.getAbsolutePath());
               label_fileTitle.setText(selectedFile.getName());
               fileSelected[0] = true;
           } else {
               fileSelected[0] = false;
            }
           btn_uploadMaterial.setDisable(!(fileSelected[0] && checkbox_uploadAgreement.isSelected() && !field_title.getText().isEmpty()));
        });

        checkbox_uploadAgreement.selectedProperty().addListener((obs, wasSelected, isSelected) ->
                btn_uploadMaterial.setDisable(!(fileSelected[0] && isSelected && !field_title.getText().isEmpty()))
        );

        field_title.textProperty().addListener((obs, oldText, newText) ->
                btn_uploadMaterial.setDisable(!(fileSelected[0] && checkbox_uploadAgreement.isSelected() && !newText.isEmpty()))
        );

        setCategoryChoices();
    }
}
