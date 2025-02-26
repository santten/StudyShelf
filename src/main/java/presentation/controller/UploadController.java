package presentation.controller;

import domain.model.Category;
import domain.model.RoleType;
import domain.model.User;
import domain.service.GoogleDriveService;
import domain.service.Session;
import domain.service.StudyMaterialService;
import infrastructure.repository.CategoryRepository;
import infrastructure.repository.StudyMaterialRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import presentation.GUILogger;
import presentation.view.SceneManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.stream.Stream;

import static domain.model.RoleType.STUDENT;
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

    @FXML public Button btn_createCourse;
    @FXML public TextField field_courseName;

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

    private void setUpFileUpload(){
        User curUser = Session.getInstance().getCurrentUser();
        text_uploadingAs.setText("Uploading as " + curUser.getFirstName() + " " + curUser.getLastName());

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Add Material File");

        // TODO: these should be decided
        fileChooser.getExtensionFilters().addAll();

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

    private void setUpCourseUpload(){
        Text createCourseLabel = new Text("Create New Course");
        createCourseLabel.getStyleClass().addAll("heading3", "secondary-light");
        VBox.setMargin(createCourseLabel, new Insets(12, 0, 12, 0));

        ColumnConstraints coll1 = new ColumnConstraints();
        coll1.setPrefWidth(86);

        ColumnConstraints coll2 = new ColumnConstraints();
        coll2.setPrefWidth(473);

        GridPane gp = new GridPane();
        gp.getColumnConstraints().addAll(coll1, coll2);
        gp.setHgap(10);
        gp.setVgap(10);

        TextField field_courseName = new TextField();

        gp.add(field_courseName, 1, 0);

        Text label = new Text("Name");
        gp.add(label, 0, 0);
        label.setStyle("-fx-padding: 4");
        GridPane.setHalignment(label, HPos.RIGHT);
        GridPane.setValignment(label, VPos.BASELINE);

        Button btn = new Button("Create Course");
        btn.getStyleClass().add("btnS");
        btn.setDisable(true);

        Text warningText = new Text();
        warningText.getStyleClass().add("error");
        gp.add(warningText, 1, 2);

        field_courseName.textProperty().addListener((obs, oldText, newText) -> {
                boolean bool = checkDoubleCategoryName(field_courseName.getText());
                btn.setDisable(newText == null || newText.isEmpty() || bool);
                warningText.setText(bool ? "You already have a course with this name." : "");
        });

        btn.setOnAction(e -> {
           Category cat = new Category();
           cat.setCategoryName(field_courseName.getText());
           cat.setCreator(Session.getInstance().getCurrentUser());
           CategoryRepository categoryRepository = new CategoryRepository();
           categoryRepository.save(cat);
            try {
                SceneManager.getInstance().displayCategory(cat.getCategoryId());
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        gp.add(btn, 1, 1);

        gp.setPrefWidth(560);

        mainVBoxUpload.getChildren().addAll(createCourseLabel, gp);
    }

    private boolean checkButtonCondition(){
        return !(choice_category.getValue() != null && currentFile != null && checkbox_uploadAgreement.isSelected() && !field_title.getText().isEmpty());
    }

    private boolean checkDoubleCategoryName(String str){
        User u = Session.getInstance().getCurrentUser();
        CategoryRepository courseRepo = new CategoryRepository();
        List<Category> all = courseRepo.findCategoriesByUser(u);

        Stream<Category> matchingCategory = all.stream()
                .filter(category -> category.getCategoryName().equals(str));

        return matchingCategory.findFirst().isPresent();
    }

    @FXML private void initialize(){
        setUpFileUpload();

        RoleType curUserRole = Session.getInstance().getCurrentUser().getRole().getName();
        if (curUserRole != STUDENT) setUpCourseUpload();

        btn_uploadMaterial.setOnAction(e -> {
            try {
                byte[] content = Files.readAllBytes(currentFile.toPath());
                String filename = currentFile.getName();
                User uploader = Session.getInstance().getCurrentUser();
                String name = field_title.getText();
                String description = field_desc.getText();
                Category category = choice_category.getValue();

                StudyMaterialService materialService = new StudyMaterialService(
                        new GoogleDriveService(),
                        new StudyMaterialRepository()
                );

                materialService.uploadMaterial(
                        content,
                        filename,
                        uploader,
                        name,
                        description,
                        category
                );

                SceneManager.getInstance().setScreen(SCREEN_COURSES);
            } catch (IOException ex) {
                GUILogger.warn("Failed to upload material: " + ex.getMessage());
            }
        });
    }
}