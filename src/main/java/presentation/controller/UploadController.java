package presentation.controller;

import domain.model.Category;
import domain.model.MaterialStatus;
import domain.model.StudyMaterial;
import domain.model.User;
import domain.service.Session;
import infrastructure.repository.CategoryRepository;
import infrastructure.repository.StudyMaterialRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import presentation.logger.GUILogger;
import presentation.view.SceneManager;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import static presentation.view.Screen.SCREEN_COURSES;

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

    File currentFile;

    private void setCategoryChoices(){
        List<Category> categories = new CategoryRepository().findAll();
        GUILogger.info("Loading categories: " + categories.size());
        ObservableList<Category> categoriesObservableList = FXCollections.observableList(categories);

        choice_category.setItems(categoriesObservableList);
        choice_category.setConverter(new StringConverter<>() {
            @Override
            public String toString(Category category) {
                if (category != null) {
                    return category.getCategoryName() + " (" + category.getCreator().getFullName() + ")";
                } else {
                    return "";
            }}

            @Override
            public Category fromString(String s) {
                return null; // not used
            }
        });
    }

    private void setUp(){
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
                currentFile = selectedFile;
            } else {
                currentFile = null;
            }
            btn_uploadMaterial.setDisable(checkButtonCondition());
        });

        checkbox_uploadAgreement.selectedProperty().addListener((obs, wasSelected, isSelected) ->
                btn_uploadMaterial.setDisable(checkButtonCondition())
        );

        field_title.textProperty().addListener((obs, oldText, newText) ->
                btn_uploadMaterial.setDisable(checkButtonCondition())
        );

        setCategoryChoices();
    }

    private boolean checkButtonCondition(){
        return !(choice_category.getValue() != null && currentFile != null && checkbox_uploadAgreement.isSelected() && !field_title.getText().isEmpty());
    };

    @FXML private void initialize(){
        setUp();
        StudyMaterialRepository materialRepo = new StudyMaterialRepository();

        btn_uploadMaterial.setOnAction(e -> {
            StudyMaterial material = new StudyMaterial(
                    Session.getInstance().getCurrentUser(),
                    field_title.getText(),
                    field_desc.getText(),
                    currentFile.getAbsolutePath(),
                    currentFile.getTotalSpace(),
                    getExtension(currentFile),
                    LocalDateTime.now(),
                    MaterialStatus.PENDING
            );
            material.setCategory(choice_category.getValue());
            materialRepo.save(material);
            SceneManager sm = SceneManager.getInstance();
            try {
                sm.setScreen(SCREEN_COURSES);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    private String getExtension(File currentFile) {
        int lastDotIndex = currentFile.getName().lastIndexOf('.');

        if (lastDotIndex > 0 && lastDotIndex < currentFile.getName().length() - 1) {
            return currentFile.getName().substring(lastDotIndex + 1);
        } else {
            GUILogger.warn("No file extension found");
            return "";
        }
    }
}
