package presentation.controller;

import domain.model.Category;
import domain.model.StudyMaterial;
import domain.model.User;
import domain.service.CategoryService;
import domain.service.GoogleDriveService;
import domain.service.PermissionService;
import domain.service.StudyMaterialService;
import infrastructure.repository.CategoryRepository;
import infrastructure.repository.StudyMaterialRepository;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import presentation.components.MaterialCard;
import presentation.utility.GUILogger;
import presentation.utility.SVGContents;
import presentation.utility.StyleClasses;
import presentation.view.CurrentUserManager;
import presentation.view.LanguageManager;
import presentation.view.SceneManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import static javafx.scene.shape.FillRule.EVEN_ODD;
import static presentation.view.Screen.SCREEN_HOME;

public class CategoryPageController {
    private List<StudyMaterial> pendingMaterials;
    private List<StudyMaterial> ownerMaterials;
    private List<StudyMaterial> otherMaterials;

    private final VBox pendingContainer;
    private final Text pendingMaterialLabel;
    private final VBox otherMaterialsContainer;

    private final HBox titleLabelHBox;

    private final ResourceBundle rb = LanguageManager.getInstance().getBundle();

    public CategoryPageController() {
        this.pendingMaterials = new ArrayList<>();
        this.ownerMaterials = new ArrayList<>();
        this.otherMaterials = new ArrayList<>();

        this.pendingContainer = new VBox();
        this.pendingMaterialLabel = new Text();

        this.otherMaterialsContainer = new VBox();

        this.titleLabelHBox = new HBox();
    }

    private List<StudyMaterial> getPendingMaterials() {
        return this.pendingMaterials;
    }
    private List<StudyMaterial> getOwnerMaterials() {
        return this.ownerMaterials;
    }
    private List<StudyMaterial> getOtherMaterials() {
        return this.otherMaterials;
    }
    private HBox getTitleLabelHBox() { return this.titleLabelHBox; }

    private VBox getPendingContainer() {
        return this.pendingContainer;
    }

    private VBox getOtherMaterialsContainer() {
        return this.otherMaterialsContainer;
    }

    private void setPendingMaterials(List<StudyMaterial> pendingMaterials) {
        this.pendingMaterials = pendingMaterials;
    }
    private void setOwnerMaterials(List<StudyMaterial> ownerMaterials) {
        this.ownerMaterials = ownerMaterials;
    }
    private void setOtherMaterials(List<StudyMaterial> otherMaterials) {
        this.otherMaterials = otherMaterials;
    }

    public void setPage(Category c){
        VBox vbox = new VBox();

        vbox.getStylesheets().add(Objects.requireNonNull(CategoryPageController.class.getResource("/css/style.css")).toExternalForm());
        vbox.setSpacing(12);
        vbox.setPadding(new Insets(20, 20, 20, 20));

        Text title = new Text(c.getCategoryName());
        title.getStyleClass().addAll(StyleClasses.HEADING3, StyleClasses.SECONDARY_LIGHT);
        getTitleLabelHBox().getChildren().add(title);

        Text author = new Text(String.format(rb.getString("courseBy"), c.getCreator().getFullName()));
        VBox header = new VBox();

        Button addMaterialButton = new Button();
        addMaterialButton.getStyleClass().add(StyleClasses.BUTTON_GREEN_BASE);

        SVGPath addSVG = new SVGPath();
        addSVG.setFillRule(EVEN_ODD);
        addSVG.setContent(SVGContents.CREATE);
        addSVG.getStyleClass().addAll(StyleClasses.LIGHT);

        Text addText = new Text(rb.getString("addMaterialToThisCourse"));
        addText.getStyleClass().addAll(StyleClasses.HEADING5, StyleClasses.LIGHT);

        addMaterialButton.setOnAction(e -> {
            ScrollPane scrollPane = new ScrollPane();
            UploadController page = new UploadController();
            page.initialize(scrollPane, c);
            SceneManager sm = SceneManager.getInstance();
            sm.setCenter(scrollPane);
        });

        HBox addMaterialHBox = new HBox(addSVG, addText);
        addMaterialHBox.setAlignment(Pos.CENTER_LEFT);
        addMaterialHBox.setSpacing(8);
        addMaterialButton.setGraphic(addMaterialHBox);

        User u = CurrentUserManager.get();

        boolean isEditable = u.getUserId() == c.getCreator().getUserId() || u.isAdmin();

        if (isEditable) {
            Button deleteBtn = new Button();
            deleteBtn.getStyleClass().add(StyleClasses.BUTTON_EMPTY);
            SVGPath svgDelete = new SVGPath();
            svgDelete.setContent(SVGContents.DELETE);
            svgDelete.getStyleClass().add(StyleClasses.ERROR);
            SVGContents.setScale(svgDelete, 1.4);
            deleteBtn.setGraphic(svgDelete);

            deleteBtn.setOnAction(e3 -> {
                if (CategoryController.deleteCategory(c)) {
                    SceneManager.getInstance().setScreen(SCREEN_HOME);
                }
            });

            Button editTitle = new Button();
            editTitle.getStyleClass().add(StyleClasses.BUTTON_EMPTY);
            SVGPath svgEdit = new SVGPath();
            svgEdit.setContent(SVGContents.EDIT);
            svgEdit.getStyleClass().add(StyleClasses.SECONDARY_LIGHT);
            SVGContents.setScale(svgEdit, 1.4);
            editTitle.setGraphic(svgEdit);

            getTitleLabelHBox().getChildren().clear();
            getTitleLabelHBox().getChildren().addAll(title, editTitle, deleteBtn);
            editTitle.setOnAction(e1 -> {
                TextField titleArea = new TextField(c.getCategoryName());
                titleArea.setMinWidth(540);

                Button saveTitle = new Button();
                saveTitle.getStyleClass().add(StyleClasses.BUTTON_EMPTY);
                SVGPath svgSave = new SVGPath();
                svgSave.setContent(SVGContents.SAVE);
                svgSave.getStyleClass().add(StyleClasses.SECONDARY_LIGHT);
                SVGContents.setScale(svgSave, 1.4);
                saveTitle.setGraphic(svgSave);

                CategoryService categoryServ = new CategoryService(new CategoryRepository(), new PermissionService());
                saveTitle.setOnAction(e2 -> {
                    categoryServ.updateTitle(CurrentUserManager.get(), c, titleArea.getText());

                    title.setText(titleArea.getText());

                    getTitleLabelHBox().getChildren().clear();
                    getTitleLabelHBox().getChildren().addAll(title, editTitle, deleteBtn);
                });

                getTitleLabelHBox().getChildren().clear();
                getTitleLabelHBox().getChildren().addAll(titleArea, saveTitle, deleteBtn);
            });
        }

        header.setSpacing(12);
        header.getChildren().addAll(getTitleLabelHBox(), author, addMaterialButton);

        vbox.getChildren().add(header);

        if (u.getUserId() == c.getCreator().getUserId()) {
            setUpApproveView(c, vbox);
        }

        CategoryService cServ = new CategoryService(new CategoryRepository(), new PermissionService());
        List<StudyMaterial> approvedMaterials = cServ.getApprovedMaterialsByCategory(CurrentUserManager.get(), c);

        setOwnerMaterials(approvedMaterials.stream()
                .filter(sm -> sm.getUploader().getUserId() == c.getCreator().getUserId())
                .collect(Collectors.toList()));

        if (!getOwnerMaterials().isEmpty()) {
            Text text = new Text(String.format(rb.getString("materialsFromCourseCreator"), c.getCreator().getFullName()));
            text.getStyleClass().addAll(StyleClasses.HEADING4, StyleClasses.PRIMARY);

            vbox.getChildren().addAll(
                    text,
                    MaterialCard.materialCardScrollHBox(ownerMaterials));
        }

        setOtherMaterials(approvedMaterials.stream()
                .filter(sm -> sm.getUploader().getUserId() != c.getCreator().getUserId())
                .collect(Collectors.toList()));

        updateOtherMaterials();
        vbox.getChildren().add(getOtherMaterialsContainer());

        ScrollPane sp = new ScrollPane(vbox);
        SceneManager.getInstance().setCenter(sp);
    }

    public void setUpApproveView(Category c, VBox page){
        CategoryService cServ = new CategoryService(new CategoryRepository(), new PermissionService());
        setPendingMaterials(cServ.getPendingMaterialsByCategory(CurrentUserManager.get(), c));
        GUILogger.info(pendingMaterials.size() + " materials found pending approval.");

        Text pendingTitle = new Text(rb.getString("pendingMaterials"));
        pendingTitle.getStyleClass().addAll(StyleClasses.HEADING4, StyleClasses.WARNING);

        page.getChildren().add(pendingTitle);
        setMaterialAmountLabel();
        page.getChildren().add(pendingMaterialLabel);

        if (!getPendingMaterials().isEmpty()) {
            getPendingMaterials().forEach(sm -> getPendingContainer().getChildren().add(pendingMaterialItem(sm)));
            page.getChildren().add(getPendingContainer());
        }
    }

    public HBox pendingMaterialItem(StudyMaterial s){
        HBox base = new HBox();

        Button content = new Button();
        content.setOnAction(e -> {
            SceneManager sm = SceneManager.getInstance();
            sm.displayMaterial(s.getMaterialId());
        });

        Label materialTitle = new Label(s.getName());
        materialTitle.getStyleClass().addAll(StyleClasses.LABEL4, StyleClasses.PRIMARY_LIGHT);

        String role = switch (s.getUploader().getRole().getName()) {
            case TEACHER -> rb.getString("teacher");
            case ADMIN -> rb.getString("admin");
            default -> rb.getString("student");
        };

        Text materialInfo = new Text(String.format(rb.getString("fileTypeUploadedByUserWithRole"), s.getFileType(), s.getUploader().getFullName(), role));

        HBox graphic = new HBox(materialTitle, materialInfo);
        graphic.setSpacing(12);
        graphic.setAlignment(Pos.CENTER_LEFT);

        content.setGraphic(graphic);
        content.getStyleClass().add(StyleClasses.BUTTON_EMPTY);

        Button approvalButton = new Button();
        approvalButton.setOnAction(e -> {
            StudyMaterialService smServ = new StudyMaterialService(new GoogleDriveService(), new StudyMaterialRepository(), new PermissionService());
            smServ.approveMaterial(CurrentUserManager.get(), s);
            getPendingContainer().getChildren().remove(base);
            pendingMaterials.remove(s);
            otherMaterials.add(s);
            updateOtherMaterials();
            setMaterialAmountLabel();
        });

        approvalButton.getStyleClass().add(StyleClasses.BUTTON_EMPTY);
        SVGPath approveSvg = new SVGPath();
        approveSvg.setContent(SVGContents.APPROVE);
        approveSvg.getStyleClass().add(StyleClasses.SECONDARY_LIGHT);
        approveSvg.setFillRule(EVEN_ODD);
        approvalButton.setGraphic(approveSvg);
        approvalButton.setMaxWidth(20);

        Button rejectButton = new Button();
        rejectButton.setOnAction(e -> {
            StudyMaterialService smServ = new StudyMaterialService(new GoogleDriveService(), new StudyMaterialRepository(), new PermissionService());
            smServ.rejectMaterial(CurrentUserManager.get(), s);
            getPendingContainer().getChildren().remove(base);
            pendingMaterials.remove(s);
            otherMaterials.add(s);
            setMaterialAmountLabel();
        });

        rejectButton.getStyleClass().add(StyleClasses.BUTTON_EMPTY);
        SVGPath rejectSvg = new SVGPath();
        rejectSvg.setContent(SVGContents.REJECT);
        rejectSvg.getStyleClass().addAll(StyleClasses.ERROR);
        rejectSvg.setFillRule(EVEN_ODD);
        rejectButton.setGraphic(rejectSvg);
        rejectButton.setMaxWidth(20);

        base.getChildren().addAll(approvalButton, rejectButton, content);
        return base;
    }

    public void setMaterialAmountLabel() {
        if (getPendingMaterials().isEmpty()) {
            pendingMaterialLabel.setText(rb.getString("noPending"));
            pendingMaterialLabel.getStyleClass().add(StyleClasses.PRIMARY_LIGHT);
        } else {
            pendingMaterialLabel.setText(String.format(rb.getString("xMaterialsPendingApproval"), getPendingMaterials().size()));
            pendingMaterialLabel.getStyleClass().clear();
        }
    }

    public void updateOtherMaterials(){
        getOtherMaterialsContainer().getChildren().clear();

        if (!getOtherMaterials().isEmpty()) {
            Text text = new Text(rb.getString("materialsFromOthers"));
            text.getStyleClass().addAll(StyleClasses.HEADING4, StyleClasses.SECONDARY);

            getOtherMaterialsContainer().getChildren().addAll(
                    text,
                    MaterialCard.materialCardScrollHBox(otherMaterials));
        }
    }
}