package presentation.controller;

import domain.model.Category;
import domain.model.StudyMaterial;
import domain.model.Tag;
import domain.model.User;
import domain.service.GoogleDriveService;
import domain.service.PermissionService;
import domain.service.StudyMaterialService;
import domain.service.TagService;
import infrastructure.repository.CategoryRepository;
import infrastructure.repository.StudyMaterialRepository;
import infrastructure.repository.TagRepository;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import presentation.utility.GUILogger;
import presentation.utility.StyleClasses;
import presentation.utility.UITools;
import presentation.view.CurrentUserManager;
import presentation.view.LanguageManager;
import presentation.view.SceneManager;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static domain.model.PermissionType.CREATE_CATEGORY;
import static domain.model.PermissionType.CREATE_RESOURCE;

public class UploadController {
    private VBox vbox;

    private File file;

    private TextField fieldTitle;
    private TextArea fieldDescription;

    private ChoiceBox<Category> choiceCategory;
    private CheckBox uploadAgreement;

    private final TagService tagService = new TagService(new TagRepository(), new PermissionService());
    private final Set<String> pendingTags = new HashSet<>();

    public final ResourceBundle rb = LanguageManager.getInstance().getBundle();

    public void initialize(ScrollPane wrapper) {
        initialize(wrapper, null);
    }

    public void initialize(ScrollPane wrapper, Category presetCategory) {
        setVBox(new VBox());

        User user = CurrentUserManager.get();

        if (user.hasPermission(CREATE_RESOURCE)) {
            setUpFileUpload(presetCategory);
        }

        if (user.hasPermission(CREATE_CATEGORY) && presetCategory == null) {
            setUpCourseCreation();
        }

        wrapper.setContent(getVBox());
    }

    private VBox getVBox() {
        return this.vbox;
    }

    private void setVBox(VBox vbox) {
        vbox.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());
        vbox.setPadding(new Insets(20, 20, 20, 20));
        vbox.setSpacing(12);
        this.vbox = vbox;
    }

    private void setFile(File file) {
        this.file = file;
    }

    private void setUpFileUpload(Category presetCategory) {
        User user = CurrentUserManager.get();

        /* title area */
        Text title = new Text(rb.getString("uploadFile"));
        title.getStyleClass().addAll(StyleClasses.HEADING3, StyleClasses.PRIMARY_LIGHT);
        getVBox().getChildren().add(title);

        /* create upload button box */
        HBox uploadButtonHBox = new HBox();
        Button btnUploadMaterial = new Button(rb.getString("upload"));
        btnUploadMaterial.getStyleClass().add(StyleClasses.BTN_S);
        btnUploadMaterial.setDisable(true);

        Text textUploadingAs = new Text(String.format(rb.getString("uploadingAs"), user.getFullName()));
        textUploadingAs.getStyleClass().add(StyleClasses.PRIMARY_LIGHT);
        uploadButtonHBox.setSpacing(8);
        uploadButtonHBox.setAlignment(Pos.CENTER_LEFT);
        uploadButtonHBox.getChildren().addAll(btnUploadMaterial, textUploadingAs);

        /* create fileChooser */
        HBox chooseFileHBox = new HBox();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(rb.getString("addMaterialFile"));
        fileChooser.getExtensionFilters().addAll();
        Text fileTitle = new Text("");
        Button btnGetFile = getBtnGetFile(fileChooser, fileTitle, btnUploadMaterial);
        chooseFileHBox.setSpacing(8);
        chooseFileHBox.setAlignment(Pos.CENTER_LEFT);
        chooseFileHBox.getChildren().addAll(btnGetFile, fileTitle);

        /* file title */
        fieldTitle = new TextField();
        fieldTitle.textProperty().addListener((obs, oldText, newText) ->
                btnUploadMaterial.setDisable(checkUploadButtonCondition())
        );
        UITools.limitInputLength(fieldTitle, 100);

        /* file description */
        fieldDescription = new TextArea();
        fieldDescription.setWrapText(true);
        fieldDescription.setMaxHeight(80);

        setUpChoiceBox(btnUploadMaterial);

        if (presetCategory != null) {
            choiceCategory.getSelectionModel().select(presetCategory);
        }

        /* tags */
        TextFlow tagChips = new TextFlow();
        TextField manualTagInput = new TextField();
        manualTagInput.setOnAction(e -> {
            String tagText = manualTagInput.getText().trim();
            if (!tagText.isEmpty() && !pendingTags.contains(tagText.toLowerCase())) {
                addTagChip(tagText, tagChips);
                manualTagInput.clear();
            }
        });

        UITools.limitInputLength(manualTagInput, 80);

        VBox tagArea = new VBox();
        tagArea.setSpacing(12);
        tagArea.getChildren().addAll(manualTagInput, tagChips);

        uploadAgreement = new CheckBox(rb.getString("uploadAgreement"));
        uploadAgreement.selectedProperty().addListener((obs, wasSelected, isSelected) ->
                btnUploadMaterial.setDisable(checkUploadButtonCondition())
        );

        btnUploadMaterial.setOnAction(event -> {
            btnUploadMaterial.setDisable(true);
            uploadMaterial();
        });

        GridPane gp = new GridPane();
        gp.setVgap(12);

        ColumnConstraints col1 = new ColumnConstraints();
        col1.setHalignment(HPos.RIGHT);
        col1.setPrefWidth(120);

        ColumnConstraints col2 = new ColumnConstraints();
        col2.setHalignment(HPos.LEFT);
        col2.setPrefWidth(600);

        gp.getColumnConstraints().addAll(col1, col2);

        gp.add(gridLabel(rb.getString("fileFieldLabel"), true), 0, 0);
        gp.add(chooseFileHBox, 1, 0);

        gp.add(gridLabel(rb.getString("titleFieldLabel"), true), 0, 1);
        gp.add(fieldTitle, 1, 1);

        gp.add(gridLabel(rb.getString("descriptionFieldLabel"), false), 0, 2);
        gp.add(fieldDescription, 1, 2);

        gp.add(gridLabel(rb.getString("courseFieldLabel"), true), 0, 3);
        gp.add(choiceCategory, 1, 3);

        gp.add(gridLabel(rb.getString("tags"), false), 0, 4);
        gp.add(tagArea, 1, 4);

        gp.add(uploadAgreement, 1, 5);

        gp.add(uploadButtonHBox, 1, 6);

        getVBox().getChildren().add(gp);
    }

    private void uploadMaterial(){
        try {
            byte[] content = Files.readAllBytes(file.toPath());
            String filename = file.getName();
            User uploader = CurrentUserManager.get();
            String name = fieldTitle.getText();
            String description = fieldDescription.getText();
            Category category = choiceCategory.getValue();
            Set<Tag> materialTags = pendingTags.stream()
                    .map(tagName -> tagService.createTag(tagName, uploader))
                    .collect(Collectors.toSet());

            StudyMaterialService materialService = new StudyMaterialService(
                    new GoogleDriveService(),
                    new StudyMaterialRepository(),
                    new PermissionService()
            );

            StudyMaterial material = materialService.uploadMaterial(
                    content,
                    filename,
                    uploader,
                    name,
                    description,
                    category,
                    materialTags
            );

            material.getTags().addAll(materialTags);
            materialService.updateMaterial(material);

            SceneManager.getInstance().displayMaterial(material.getMaterialId());
        } catch (IOException ex) {
            GUILogger.warn("Failed to upload material: " + ex.getMessage());
        }
    }

    private void setUpChoiceBox(Button btnUploadMaterial) {
        choiceCategory.setMinWidth(600);
        List<Category> categories = new CategoryRepository().findAll();
        GUILogger.info("Loading categories: " + categories.size());
        ObservableList<Category> categoriesObservableList = FXCollections.observableList(categories);

        choiceCategory.setItems(categoriesObservableList);
        choiceCategory.setConverter(new StringConverter<>() {
            @Override
            public String toString(Category category) {
                if (category != null) {
                    return category.getCategoryName() + " (" + category.getCreator().getFullName() + ")";
                } else {
                    return "";
                }
            }

            @Override
            public Category fromString(String s) {
                return null;
            }
        });

        choiceCategory.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> btnUploadMaterial.setDisable(checkUploadButtonCondition())
        );
    }

    private TextFlow gridLabel(String text, boolean required) {
        TextFlow base = new TextFlow();

        Text textNode = new Text(text);
        base.getChildren().add(textNode);
        textNode.setStyle("-fx-font-size: 1.1em;");

        if (required) {
            Text requiredStar = new Text("*");
            requiredStar.getStyleClass().add(StyleClasses.ERROR);
            requiredStar.setStyle("-fx-font-weight: bolder; -fx-font-size: 1.1em;");
            base.getChildren().add(requiredStar);
        }

        base.getChildren().add(new Text("   "));

        base.setTextAlignment(TextAlignment.RIGHT);
        GridPane.setHalignment(base, HPos.RIGHT);
        GridPane.setValignment(base, VPos.TOP);

        return base;
    }

    private boolean checkUploadButtonCondition() {
        return fieldTitle.getText().isEmpty() ||
                !uploadAgreement.isSelected() || file == null ||
                choiceCategory.getValue() == null;
    }

    private Button getBtnGetFile(FileChooser fileChooser, Text fileTitle, Button btn_uploadMaterial) {
        Button btnGetFile = new Button(rb.getString("chooseFile"));
        btnGetFile.setOnAction(e -> {
            File selectedFile = fileChooser.showOpenDialog(null);
            if (selectedFile != null) {
                GUILogger.info("User selected file from " + selectedFile.getAbsolutePath());
                fileTitle.setText(selectedFile.getName());
                setFile(selectedFile);
            } else {
                setFile(null);
            }
            btn_uploadMaterial.setDisable(checkUploadButtonCondition());
        });
        return btnGetFile;
    }

    private void addTagChip(String tagName, TextFlow tagChips) {
        HBox chip = new HBox();
        chip.setAlignment(Pos.CENTER);

        HBox chipContent = new HBox();
        chipContent.getStyleClass().add(StyleClasses.TAG_NOT_CLICKABLE);

        Text text = new Text(tagName);
        text.getStyleClass().add(StyleClasses.LIGHT);

        Button removeBtn = new Button("×");
        removeBtn.getStyleClass().add(StyleClasses.TAG_REMOVE_BTN);

        removeBtn.setOnAction(e -> {
            tagChips.getChildren().remove(chip);
            pendingTags.remove(tagName.toLowerCase());
        });

        chipContent.getChildren().addAll(text, removeBtn);
        chip.getChildren().addAll(chipContent, new Text(" "));

        tagChips.getChildren().add(0, chip);
        pendingTags.add(tagName.toLowerCase());
    }

    private void setUpCourseCreation() {
        Text title = new Text(rb.getString("createCourse"));
        title.getStyleClass().addAll(StyleClasses.HEADING3, StyleClasses.SECONDARY_LIGHT);
        getVBox().getChildren().add(title);

        ColumnConstraints coll1 = new ColumnConstraints();
        coll1.setPrefWidth(120);

        ColumnConstraints coll2 = new ColumnConstraints();
        coll2.setPrefWidth(600);

        GridPane gp = new GridPane();
        gp.getColumnConstraints().addAll(coll1, coll2);
        gp.setVgap(12);

        TextField field_courseName = new TextField();
        UITools.limitInputLength(field_courseName, 50);

        gp.add(field_courseName, 1, 0);

        gp.add(gridLabel(rb.getString("courseName"), true), 0, 0);

        Button btn = new Button(rb.getString("createCourse"));
        btn.getStyleClass().add(StyleClasses.BTN_S);
        btn.setDisable(true);

        Text warningText = new Text();
        warningText.getStyleClass().add(StyleClasses.ERROR);

        User u = CurrentUserManager.get();
        CategoryRepository courseRepo = new CategoryRepository();
        List<Category> allOwned = courseRepo.findCategoriesByUser(u);

        field_courseName.textProperty().addListener((obs, oldText, newText) -> {
            boolean bool = checkDoubleCategoryName(field_courseName.getText(), allOwned);
            btn.setDisable(newText == null || newText.isEmpty() || bool);
            warningText.setText(bool ? rb.getString("duplicateCourseWarning") : "");
        });

        btn.setOnAction(e -> {
            Category cat = new Category();
            cat.setCategoryName(field_courseName.getText());
            cat.setCreator(CurrentUserManager.get());
            CategoryRepository categoryRepository = new CategoryRepository();
            categoryRepository.save(cat);
            SceneManager.getInstance().displayCategory(cat.getCategoryId());
        });

        HBox row1HBox = new HBox();
        row1HBox.getChildren().addAll(btn, warningText);
        row1HBox.setAlignment(Pos.CENTER_LEFT);
        row1HBox.setSpacing(8);
        gp.add(row1HBox, 1, 1);

        getVBox().getChildren().add(gp);
    }

    private boolean checkDoubleCategoryName(String str, List<Category> allOwned) {
        Stream<Category> matchingCategory = allOwned.stream()
                .filter(category -> category.getCategoryName().equals(str));

        return matchingCategory.findFirst().isPresent();
    }
}