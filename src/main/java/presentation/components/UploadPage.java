package presentation.components;

import domain.model.Category;
import domain.model.StudyMaterial;
import domain.model.Tag;
import domain.model.User;
import domain.service.*;
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
import presentation.GUILogger;
import presentation.view.SceneManager;
import presentation.view.UITools;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static domain.model.PermissionType.CREATE_CATEGORY;
import static domain.model.PermissionType.CREATE_RESOURCE;

public class UploadPage {
    private VBox vbox;

    private File file;

    private Button btn_uploadMaterial;
    private TextField field_title;
    private TextArea field_description;
    private ChoiceBox<Category> choice_category;
    private CheckBox uploadAgreement;

    private final TagService tagService = new TagService(new TagRepository(), new PermissionService());
    private final Set<String> pendingTags = new HashSet<>();


    public void initialize(ScrollPane wrapper) {
        initialize(wrapper, null);
    }

    public void initialize(ScrollPane wrapper, Category presetCategory) {
        setVBox(new VBox());

        User user = Session.getInstance().getCurrentUser();

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
        User user = Session.getInstance().getCurrentUser();

        /* title area */
        Text title = new Text("Upload File");
        title.getStyleClass().addAll("heading3", "primary-light");
        getVBox().getChildren().add(title);

        /* create upload button box */
        HBox uploadButtonHBox = new HBox();
        btn_uploadMaterial = new Button("Upload");
        btn_uploadMaterial.getStyleClass().add("btnS");
        btn_uploadMaterial.setDisable(true);

        Text text_uploadingAs = new Text("Uploading As " + user.getFullName());
        text_uploadingAs.getStyleClass().addAll("primary-light");
        uploadButtonHBox.setSpacing(8);
        uploadButtonHBox.setAlignment(Pos.CENTER_LEFT);
        uploadButtonHBox.getChildren().addAll(btn_uploadMaterial, text_uploadingAs);

        /* create fileChooser */
        HBox chooseFileHBox = new HBox();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Add Material File");
        fileChooser.getExtensionFilters().addAll();
        Text fileTitle = new Text("");
        Button btn_getFile = getBtnGetFile(fileChooser, fileTitle, btn_uploadMaterial);
        chooseFileHBox.setSpacing(8);
        chooseFileHBox.setAlignment(Pos.CENTER_LEFT);
        chooseFileHBox.getChildren().addAll(btn_getFile, fileTitle);

        /* file title */
        field_title = new TextField();
        field_title.textProperty().addListener((obs, oldText, newText) ->
                btn_uploadMaterial.setDisable(checkUploadButtonCondition())
        );
        UITools.limitInputLength(field_title, 100);

        /* file description */
        field_description = new TextArea();
        field_description.setWrapText(true);
        field_description.setMaxHeight(80);

        /* */
        choice_category = new ChoiceBox<>();
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
                }
            }

            @Override
            public Category fromString(String s) {
                return null;
            }
        });

        if (presetCategory != null) {
            choice_category.getSelectionModel().select(presetCategory);
        }

        choice_category.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> btn_uploadMaterial.setDisable(checkUploadButtonCondition())
        );

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

        uploadAgreement = new CheckBox("By uploading this file, I declare that this material goes along with the rules of StudyShelf.");
        uploadAgreement.selectedProperty().addListener((obs, wasSelected, isSelected) ->
                btn_uploadMaterial.setDisable(checkUploadButtonCondition())
        );

        btn_uploadMaterial.setOnAction(event -> {
            btn_uploadMaterial.setDisable(true);

            try {
                byte[] content = Files.readAllBytes(file.toPath());
                String filename = file.getName();
                User uploader = Session.getInstance().getCurrentUser();
                String name = field_title.getText();
                String description = field_description.getText();
                Category category = choice_category.getValue();
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

        gp.add(gridLabel("File", true), 0, 0);
        gp.add(chooseFileHBox, 1, 0);

        gp.add(gridLabel("Title", true), 0, 1);
        gp.add(field_title, 1, 1);

        gp.add(gridLabel("Description", false), 0, 2);
        gp.add(field_description, 1, 2);

        gp.add(gridLabel("Course", true), 0, 3);
        gp.add(choice_category, 1, 3);

        gp.add(gridLabel("Tags", false), 0, 4);
        gp.add(tagArea, 1, 4);

        gp.add(uploadAgreement, 1, 5);

        gp.add(uploadButtonHBox, 1, 6);

        getVBox().getChildren().add(gp);
    }

    private TextFlow gridLabel(String text, boolean required) {
        TextFlow base = new TextFlow();

        Text textNode = new Text(text);
        base.getChildren().add(textNode);
        textNode.setStyle("-fx-font-size: 1.1em;");

        if (required) {
            Text requiredStar = new Text("*");
            requiredStar.getStyleClass().add("error");
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
        return field_title.getText().isEmpty() ||
                !uploadAgreement.isSelected() || file == null ||
                choice_category.getValue() == null;
    }

    private Button getBtnGetFile(FileChooser fileChooser, Text fileTitle, Button btn_uploadMaterial) {
        Button btn_getFile = new Button("Choose File");
        btn_getFile.setOnAction(e -> {
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
        return btn_getFile;
    }

    private void addTagChip(String tagName, TextFlow tagChips) {
        HBox chip = new HBox();
        chip.setAlignment(Pos.CENTER);

        HBox chipContent = new HBox();
        chipContent.getStyleClass().add("tagNotClickable");

        Text text = new Text(tagName);
        text.getStyleClass().add("light");

        Button removeBtn = new Button("×");
        removeBtn.getStyleClass().add("tagRemoveBtn");

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
        Text title = new Text("Create Course");
        title.getStyleClass().addAll("heading3", "secondary-light");
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

        gp.add(gridLabel("Course Name", true), 0, 0);

        Button btn = new Button("Create Course");
        btn.getStyleClass().add("btnS");
        btn.setDisable(true);

        Text warningText = new Text();
        warningText.getStyleClass().add("error");

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
            SceneManager.getInstance().displayCategory(cat.getCategoryId());
        });

        HBox row1HBox = new HBox();
        row1HBox.getChildren().addAll(btn, warningText);
        row1HBox.setAlignment(Pos.CENTER_LEFT);
        row1HBox.setSpacing(8);
        gp.add(row1HBox, 1, 1);

        getVBox().getChildren().add(gp);
    }

    private boolean checkDoubleCategoryName(String str){
        User u = Session.getInstance().getCurrentUser();
        CategoryRepository courseRepo = new CategoryRepository();
        List<Category> all = courseRepo.findCategoriesByUser(u);

        Stream<Category> matchingCategory = all.stream()
                .filter(category -> category.getCategoryName().equals(str));

        return matchingCategory.findFirst().isPresent();
    }
}