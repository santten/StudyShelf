package presentation.components;

import domain.model.*;
import domain.service.*;

import infrastructure.repository.CategoryRepository;
import infrastructure.repository.RatingRepository;
import infrastructure.repository.StudyMaterialRepository;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;

import presentation.controller.BaseController;
import presentation.controller.StudyMaterialController;
import presentation.controller.CategoryController;
import presentation.controller.RatingController;

import presentation.utility.CustomAlert;
import presentation.utility.GUILogger;
import presentation.utility.SVGContents;
import presentation.view.SceneManager;
import presentation.view.SubScreen;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static presentation.view.Screen.*;
import static presentation.view.SubScreen.*;

public class MyProfilePage {
    private HBox hBoxBase;
    private VBox menuVBox;
    private VBox contentVBox;
    private final VBox materialContainer = new VBox();
    private final VBox courseContainer = new VBox();
    private final VBox ratingContainer = new VBox();

    public void initialize(ScrollPane wrapper) {
        initialize(wrapper, PROFILE_FILES);
    }

    public void initialize(ScrollPane wrapper, SubScreen subScreen) {
        setHBox(new HBox());
        setMenuVBox(new VBox());
        setContentVBox(new VBox());

        setUpMenu();
        setUpContent(subScreen);

        getHBox().getChildren().addAll(getMenuVBox(), getContentVBox());
        wrapper.setContent(getHBox());
    }

    private HBox getHBox() {
        return this.hBoxBase;
    }

    private void setHBox(HBox hbox) {
        hbox.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());
        hbox.setPadding(new Insets(20, 20, 20, 20));
        hbox.setSpacing(24);
        this.hBoxBase = hbox;
    }

    private VBox getMenuVBox() {
        return this.menuVBox;
    }
    private void setMenuVBox(VBox menuVBox) {
        this.menuVBox = menuVBox;
    }
    private VBox getContentVBox() {
        return this.contentVBox;
    }
    private void setContentVBox(VBox contentVBox) {
        this.contentVBox = contentVBox;
    }

    private void setUpMenu() {
        VBox base = getMenuVBox();
        base.getChildren().clear();
        base.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/profile.css")).toExternalForm());
        base.getStyleClass().add("menuBox");

        Text headingMyProfile = new Text("My profile");
        base.getChildren().add(headingMyProfile);
        headingMyProfile.getStyleClass().addAll("heading3", "light");

        addMenuLink("My Materials", PROFILE_FILES);
        if (Session.getInstance().getCurrentUser().hasPermission(PermissionType.CREATE_CATEGORY)) {
            addMenuLink("My Courses", PROFILE_COURSES);
        }
        addMenuLink("My Ratings", PROFILE_REVIEWS);

        Text headingSettings = new Text("Settings");
        base.getChildren().addAll(new Separator(), headingSettings);
        headingSettings.getStyleClass().addAll("heading3", "light");

        addMenuLink("Profile Settings", PROFILE_SETTINGS);

        Hyperlink link = new Hyperlink("Log Out");
        link.setOnAction(e -> BaseController.logout());

        link.getStyleClass().add("profileLink");
        getMenuVBox().getChildren().addAll(new Separator(), link, new Separator());

        getMenuVBox().setSpacing(4);
    }

    private void setUpContent(SubScreen subScreen) {
        VBox base = getContentVBox();
        base.getChildren().clear();
        switch (subScreen) {
            case PROFILE_COURSES:
                setUpMyCourses(base);
                break;
            case PROFILE_REVIEWS:
                setUpMyRatings(base);
                break;
            case PROFILE_SETTINGS:
                setUpMySettings(base);
                break;
            default:
                setUpMyMaterials(base);
        }
    }

    private void setUpMyMaterials(VBox base) {
        Text heading = new Text("My Materials");
        heading.getStyleClass().addAll("heading3", "primary-light");

        User user = Session.getInstance().getCurrentUser();
        StudyMaterialService smServ = new StudyMaterialService(new GoogleDriveService(), new StudyMaterialRepository(), new PermissionService());
        List<StudyMaterial> materials = smServ.findByUser(user);

        getMaterialContainer().getChildren().clear();

        if (materials.isEmpty()) {
            Hyperlink link = new Hyperlink("Upload a material");
            link.setOnAction(evt -> {
                try {
                    SceneManager.getInstance().setScreen(SCREEN_UPLOAD);
                } catch (IOException e) {
                    SceneManager.getInstance().displayErrorPage("Something went wrong", SCREEN_PROFILE, "Go back");
                }
            });
            getMaterialContainer().getChildren().addAll(new Text("You haven't uploaded any materials yet!"), link);
        }


        for (StudyMaterial sm : materials) {
            HBox item = new HBox();
            Button btn = new Button();

            SVGPath svgPath = new SVGPath();
            svgPath.getStyleClass().add("primary");
            SVGContents.setScale(svgPath, 1.3);
            svgPath.setContent(SVGContents.file());

            Text text = new Text(sm.getName());
            text.getStyleClass().addAll("heading4", "primary");

            btn.getStyleClass().add("buttonEmpty");

            btn.setOnAction(e -> SceneManager.getInstance().displayMaterial(sm.getMaterialId()));
            btn.setMinWidth(540);
            btn.setMaxWidth(540);

            HBox hbox = new HBox(text);
            hbox.setAlignment(Pos.CENTER_LEFT);
            btn.setGraphic(hbox);

            Button deleteBtn = new Button();

            SVGPath svgPathDelete = new SVGPath();
            svgPathDelete.getStyleClass().add("error");
            SVGContents.setScale(svgPathDelete, 1.3);
            svgPathDelete.setContent(SVGContents.delete());
            deleteBtn.getStyleClass().add("buttonEmpty");
            deleteBtn.setGraphic(svgPathDelete);

            deleteBtn.setOnAction(e -> {
                if (StudyMaterialController.deleteMaterial(sm)) {
                    getMaterialContainer().getChildren().remove(item);
                }

                if (getMaterialContainer().getChildren().isEmpty()) {
                    getMaterialContainer().getChildren().add(new Text("No materials left!"));
                }
            });

            item.getChildren().addAll(svgPath, btn, deleteBtn);
            item.setAlignment(Pos.CENTER_LEFT);
            item.getStyleClass().add("profileListItem");

            getMaterialContainer().getChildren().add(item);
        }

        base.getChildren().addAll(heading, getMaterialContainer());
    }

    private VBox getMaterialContainer() { return this.materialContainer; }
    private VBox getCourseContainer() { return this.courseContainer; }
    private VBox getRatingContainer() { return this.ratingContainer; }

    private void setUpMySettings(VBox base) {
        Text heading = new Text("Settings");
        heading.getStyleClass().addAll("heading3", "error");

        base.getChildren().add(heading);
    }

    private void setUpMyRatings(VBox base) {
        Text heading = new Text("My Ratings");
        heading.getStyleClass().addAll("heading3", "warning");

        User user = Session.getInstance().getCurrentUser();
        RatingService rServ = new RatingService(new RatingRepository(), new PermissionService());
        List<Rating> ratings = rServ.getRatingsByUser(user);

        getRatingContainer().getChildren().clear();

        if (ratings.isEmpty()) {
            getRatingContainer().getChildren().addAll(new Text("You haven't created any ratings yet! \nFind a Study Material to start."));
        }

        for (Rating r : ratings) {
            HBox item = new HBox();
            Button btn = new Button();

            int score = r.getRatingScore();
            HBox starContainer = new HBox();

            for (int i = 0; i < 5; i++){
                SVGPath svgPath = new SVGPath();
                SVGContents.setScale(svgPath, 1.1);
                svgPath.setContent(SVGContents.star());
                if (i < score) {
                    svgPath.getStyleClass().add("warning");
                } else {
                    svgPath.getStyleClass().add("light-darker");
                }
                starContainer.getChildren().add(svgPath);
            }
            starContainer.setSpacing(6);

            Text text = new Text("   " + r.getStudyMaterial().getName());
            text.getStyleClass().addAll("heading4", "warning");

            btn.getStyleClass().add("buttonEmpty");

            btn.setOnAction(e -> SceneManager.getInstance().displayMaterial(r.getStudyMaterial().getMaterialId()));
            btn.setMinWidth(540);
            btn.setMaxWidth(540);

            HBox hbox = new HBox(starContainer, text);
            hbox.setAlignment(Pos.CENTER_LEFT);
            btn.setGraphic(hbox);

            Button deleteBtn = new Button();

            SVGPath svgPathDelete = new SVGPath();
            svgPathDelete.getStyleClass().add("error");
            SVGContents.setScale(svgPathDelete, 1.3);
            svgPathDelete.setContent(SVGContents.delete());
            deleteBtn.getStyleClass().add("buttonEmpty");
            deleteBtn.setGraphic(svgPathDelete);

            deleteBtn.setOnAction(e -> {
                if (CustomAlert.confirm("Deleting Review", "Are you sure you want do delete your review for this Study Material?", "This can not be undone, but you can always write a new one.", true)) {
                    if (new RatingController().deleteRatingAndReview(Session.getInstance().getCurrentUser(), r.getStudyMaterial())) {
                        getRatingContainer().getChildren().remove(item);
                    }

                    if (getRatingContainer().getChildren().isEmpty()) {
                        getRatingContainer().getChildren().add(new Text("No ratings left!"));
                    }
                }
            });

            item.getChildren().addAll(btn, deleteBtn);
            item.setAlignment(Pos.CENTER_LEFT);
            item.getStyleClass().add("profileListItem");

            getRatingContainer().getChildren().add(item);
        }

        base.getChildren().addAll(heading, getRatingContainer());
    }

    private void setUpMyCourses(VBox base) {
        Text heading = new Text("My Courses");
        heading.getStyleClass().addAll("heading3", "secondary-light");

        User user = Session.getInstance().getCurrentUser();
        CategoryService cServ = new CategoryService(new CategoryRepository(), new PermissionService());
        List<Category> courses = cServ.getCategoriesByUser(user);

        getCourseContainer().getChildren().clear();

        if (courses.isEmpty()) {
            Hyperlink link = new Hyperlink("Make a course");
            link.setOnAction(evt -> {
                try {
                    SceneManager.getInstance().setScreen(SCREEN_UPLOAD);
                } catch (IOException e) {
                    SceneManager.getInstance().displayErrorPage("Something went wrong", SCREEN_PROFILE, "Go back");
                }
            });
            getCourseContainer().getChildren().addAll(new Text("You haven't created any courses yet!"), link);
        }

        for (Category c : courses) {
            HBox item = new HBox();
            Button btn = new Button();

            SVGPath svgPath = new SVGPath();
            svgPath.getStyleClass().add("secondary");
            SVGContents.setScale(svgPath, 1.3);
            svgPath.setContent(SVGContents.school());

            Text text = new Text(c.getCategoryName());
            text.getStyleClass().addAll("heading4", "secondary");

            btn.getStyleClass().add("buttonEmpty");

            btn.setOnAction(e -> SceneManager.getInstance().displayCategory(c.getCategoryId()));
            btn.setMinWidth(540);
            btn.setMaxWidth(540);

            HBox hbox = new HBox(text);
            hbox.setAlignment(Pos.CENTER_LEFT);
            btn.setGraphic(hbox);

            Button deleteBtn = new Button();

            SVGPath svgPathDelete = new SVGPath();
            svgPathDelete.getStyleClass().add("error");
            SVGContents.setScale(svgPathDelete, 1.3);
            svgPathDelete.setContent(SVGContents.delete());
            deleteBtn.getStyleClass().add("buttonEmpty");
            deleteBtn.setGraphic(svgPathDelete);

            deleteBtn.setOnAction(e -> {
                if (CategoryController.deleteCategory(c)) {
                    getCourseContainer().getChildren().remove(item);
                }

                if (getCourseContainer().getChildren().isEmpty()) {
                    getCourseContainer().getChildren().add(new Text("No courses left!"));
                }
            });

            item.getChildren().addAll(svgPath, btn, deleteBtn);
            item.setAlignment(Pos.CENTER_LEFT);
            item.getStyleClass().add("profileListItem");

            getCourseContainer().getChildren().add(item);
        }

        base.getChildren().addAll(heading, getCourseContainer());

    }

    private void addMenuLink(String text, SubScreen destination) {
        Hyperlink link = new Hyperlink(text);
        link.setOnAction(e -> setUpContent(destination));
        link.getStyleClass().add("profileLink");
        getMenuVBox().getChildren().add(link);
    }
}
