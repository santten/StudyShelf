package presentation.components;

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
import presentation.controller.CategoryController;
import presentation.controller.UploadController;
import presentation.view.CurrentUserManager;
import presentation.utility.GUILogger;
import presentation.utility.SVGContents;
import presentation.view.SceneManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static javafx.scene.shape.FillRule.EVEN_ODD;
import static presentation.view.Screen.SCREEN_COURSES;
import static presentation.view.Screen.SCREEN_HOME;

public class CategoryPage {
    private List<StudyMaterial> pendingMaterials;
    private List<StudyMaterial> ownerMaterials;
    private List<StudyMaterial> otherMaterials;

    private final VBox pendingContainer;
    private final Text pendingMaterialLabel;
    private final VBox otherMaterialsContainer;

    private final HBox titleLabelHBox;

    public CategoryPage() {
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

        vbox.getStylesheets().add(Objects.requireNonNull(CategoryPage.class.getResource("/css/style.css")).toExternalForm());
        vbox.setSpacing(12);
        vbox.setPadding(new Insets(20, 20, 20, 20));

        Text title = new Text(c.getCategoryName());
        title.getStyleClass().addAll("heading3", "secondary-light");
        getTitleLabelHBox().getChildren().add(title);

        Text author = new Text("Course by " + c.getCreator().getFullName());
        VBox header = new VBox();

        Button addMaterialButton = new Button();
        addMaterialButton.getStyleClass().add("buttonGreenBase");

        SVGPath addSVG = new SVGPath();
        addSVG.setFillRule(EVEN_ODD);
        addSVG.setContent(SVGContents.create());
        addSVG.getStyleClass().addAll("light");

        Text addText = new Text("Add Your Material to this Course");
        addText.getStyleClass().addAll("heading5", "light");

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
            deleteBtn.getStyleClass().add("buttonEmpty");
            SVGPath svgDelete = new SVGPath();
            svgDelete.setContent(SVGContents.delete());
            svgDelete.getStyleClass().add("error");
            SVGContents.setScale(svgDelete, 1.4);
            deleteBtn.setGraphic(svgDelete);

            deleteBtn.setOnAction(e3 -> {
                if (CategoryController.deleteCategory(c)) {
                    try {
                        SceneManager.getInstance().setScreen(SCREEN_HOME);
                    } catch (IOException e) {
                        SceneManager.getInstance().displayErrorPage("Something went wrong when navigating to the home page...", SCREEN_COURSES, "Go to courses page");
                        throw new RuntimeException(e);
                    }
                }
            });

            Button editTitle = new Button();
            editTitle.getStyleClass().add("buttonEmpty");
            SVGPath svgEdit = new SVGPath();
            svgEdit.setContent(SVGContents.edit());
            svgEdit.getStyleClass().add("secondary-light");
            SVGContents.setScale(svgEdit, 1.4);
            editTitle.setGraphic(svgEdit);

            getTitleLabelHBox().getChildren().clear();
            getTitleLabelHBox().getChildren().addAll(title, editTitle, deleteBtn);
            editTitle.setOnAction(e1 -> {
                TextField titleArea = new TextField(c.getCategoryName());
                titleArea.setMinWidth(540);

                Button saveTitle = new Button();
                saveTitle.getStyleClass().add("buttonEmpty");
                SVGPath svgSave = new SVGPath();
                svgSave.setContent(SVGContents.save());
                svgSave.getStyleClass().add("secondary-light");
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
            Text text = new Text("Materials from " + c.getCreator().getFullName());
            text.getStyleClass().add("heading4");
            text.getStyleClass().add("primary");

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

        Text pendingTitle = new Text("Pending materials");
        pendingTitle.getStyleClass().add("heading4");
        pendingTitle.getStyleClass().add("warning");

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
        materialTitle.getStyleClass().addAll("label4", "primary-light");

        String role = s.getUploader().getRole().getName().toString();
        Text materialInfo = new Text(s.getFileType() + " uploaded by "+ s.getUploader().getFullName() + " (" + role.charAt(0) + role.substring(1).toLowerCase() + ")");

        HBox graphic = new HBox(materialTitle, materialInfo);
        graphic.setSpacing(12);
        graphic.setAlignment(Pos.CENTER_LEFT);

        content.setGraphic(graphic);
        content.getStyleClass().add("buttonEmpty");

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

        approvalButton.getStyleClass().add("buttonEmpty");
        SVGPath approveSvg = new SVGPath();
        approveSvg.setContent(SVGContents.approve());
        approveSvg.getStyleClass().addAll("btnHover", "secondary-light");
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

        rejectButton.getStyleClass().add("buttonEmpty");
        SVGPath rejectSvg = new SVGPath();
        rejectSvg.setContent(SVGContents.reject());
        rejectSvg.getStyleClass().addAll("btnHover", "error");
        rejectSvg.setFillRule(EVEN_ODD);
        rejectButton.setGraphic(rejectSvg);
        rejectButton.setMaxWidth(20);

        base.getChildren().addAll(approvalButton, rejectButton, content);
        return base;
    }

    public void setMaterialAmountLabel() {
        if (getPendingMaterials().isEmpty()) {
            pendingMaterialLabel.setText("Relax! This course doesn't have materials waiting for your approval right now.");
            pendingMaterialLabel.getStyleClass().add("primary-light");
        } else {
            pendingMaterialLabel.setText("You have " + getPendingMaterials().size() + " materials pending approval.");
            pendingMaterialLabel.getStyleClass().clear();
        }
    }

    public void updateOtherMaterials(){
        getOtherMaterialsContainer().getChildren().clear();

        if (!getOtherMaterials().isEmpty()) {
            Text text = new Text("Materials from others");
            text.getStyleClass().add("heading4");
            text.getStyleClass().add("secondary");

            getOtherMaterialsContainer().getChildren().addAll(
                    text,
                    MaterialCard.materialCardScrollHBox(otherMaterials));
        }
    }
}