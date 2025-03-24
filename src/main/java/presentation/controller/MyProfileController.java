package presentation.controller;

import domain.model.*;
import domain.service.*;
import infrastructure.repository.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import presentation.view.CurrentUserManager;
import presentation.utility.CustomAlert;
import presentation.components.PasswordFieldToggle;
import presentation.utility.SVGContents;
import presentation.view.SceneManager;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

import static presentation.controller.SubScreen.*;
import static presentation.view.Screen.SCREEN_PROFILE;
import static presentation.view.Screen.SCREEN_UPLOAD;

public class MyProfileController {
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
        if (CurrentUserManager.get().hasPermission(PermissionType.CREATE_CATEGORY)) {
            addMenuLink("My Courses", PROFILE_COURSES);
        }
        addMenuLink("My Ratings", PROFILE_REVIEWS);

        Text headingSettings = new Text("Settings");
        base.getChildren().addAll(new Separator(), headingSettings);
        headingSettings.getStyleClass().addAll("heading3", "light");

        addMenuLink("Profile Settings", PROFILE_SETTINGS);

        Hyperlink link = new Hyperlink("Log Out");
        link.setOnAction(e -> CurrentUserManager.logout());

        link.getStyleClass().add("profileLink");
        getMenuVBox().getChildren().addAll(new Separator(), new Separator(), link, new Separator());

        getMenuVBox().setSpacing(4);
        getMenuVBox().setMaxHeight(100);
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

        User user = CurrentUserManager.get();
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
        Text heading = new Text("Display Settings");
        heading.getStyleClass().addAll("heading3", "error");
        base.getChildren().addAll(heading, new Text("Your first and last name are visible to other users."));
        base.setSpacing(8);

        User curUser = CurrentUserManager.get();
        UserService uServ = new UserService(new UserRepository(), new RoleRepository(), new PasswordService(), new JWTService());

        TextField firstNameField = new TextField(curUser.getFirstName());
        base.getChildren().add(createFieldBox("First Name", firstNameField, curUser::getFirstName, () -> {
            try {
                uServ.updateUserFirstName(curUser, firstNameField.getText());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }));

        TextField lastNameField = new TextField(curUser.getLastName());
        base.getChildren().add(createFieldBox("Last Name", lastNameField, curUser::getLastName, () -> {
            try {
                uServ.updateUserLastName(curUser, lastNameField.getText());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }));

        TextField emailField = new TextField(curUser.getEmail());
        base.getChildren().add(createFieldBox("E-Mail", emailField, curUser::getEmail, () -> {
            try {
                uServ.updateUserEmail(curUser, emailField.getText());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }));

        setUpPasswordSettings(base);
    }

    private HBox createFieldBox(String labelText, TextField textField, Supplier<String> valueSupplier, Runnable action) {
        Label label = new Label(labelText);
        label.getStyleClass().addAll("label4");
        label.setMinWidth(100);
        label.setMaxWidth(100);

        textField.setMinWidth(300);

        Button button = new Button("✔");
        button.getStyleClass().add("btnPlain");
        button.setDisable(true);

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.equals(valueSupplier.get())) {
                button.setDisable(true);
                button.setText("✔");
                button.getStyleClass().removeAll("btnS");
                button.getStyleClass().add("btnPlain");
                button.setDisable(true);
            } else {
                button.setDisable(false);
                button.setText("Save");
                button.getStyleClass().removeAll("btnPlain");
                button.getStyleClass().add("btnS");
            }
        });

        button.setOnAction(e -> {
            action.run();
            button.setText("✔");
            button.getStyleClass().removeAll("btnS");
            button.getStyleClass().add("btnPlain");
            button.setDisable(true);
        });

        HBox box = new HBox(label, textField, button);
        box.setSpacing(8);
        return box;
    }

    private void setUpPasswordSettings(VBox base){
        User curUser = CurrentUserManager.get();
        UserService uServ = new UserService(new UserRepository(), new RoleRepository(), new PasswordService(), new JWTService());

        Text headingPW = new Text("Change Password");
        headingPW.getStyleClass().addAll("heading3", "error");
        base.getChildren().addAll(headingPW);

        Label labelOldPW = new Label("Old Password");
        labelOldPW.getStyleClass().addAll("label4");
        labelOldPW.setMinWidth(120);
        labelOldPW.setMaxWidth(120);

        PasswordField oldPWField = new PasswordField();
        oldPWField.setPromptText("New Password");
        oldPWField.setMinWidth(280);

        Label labelNewPW = new Label("New Password");
        labelNewPW.getStyleClass().addAll("label4");
        labelNewPW.setMinWidth(120);
        labelNewPW.setMaxWidth(120);

        PasswordField newPWField = new PasswordField();
        newPWField.setPromptText("New Password");
        newPWField.setMinWidth(280);

        Label pwLabel = new Label("");
        pwLabel.getStyleClass().addAll("secondary-light");

        Button pwButton = new Button("Change Password");
        pwButton.setOnAction(e -> {
            if (uServ.updateUserPassword(curUser, oldPWField.getText(), newPWField.getText())){
                pwLabel.getStyleClass().clear();
                pwLabel.getStyleClass().addAll("secondary-light");
                pwLabel.setText("Password changed successfully!");
                pwButton.setDisable(true);
            } else {
                pwLabel.getStyleClass().clear();
                pwLabel.getStyleClass().addAll("error");
                pwLabel.setText("Your old password is wrong.");
                pwButton.setDisable(true);
            }
        });
        pwButton.setDisable(true);

        oldPWField.textProperty().addListener((observable, oldValue, newValue) -> {
            pwButton.setDisable(newValue.isEmpty() || newPWField.getText().isEmpty());
            pwLabel.setText("");
        });

        newPWField.textProperty().addListener((observable, oldValue, newValue) -> {
            pwButton.setDisable(newValue.isEmpty() || oldPWField.getText().isEmpty());
            pwLabel.setText("");
        });

        pwButton.getStyleClass().add("btnS");

        HBox oldPWHBox = new HBox(labelOldPW, PasswordFieldToggle.create(oldPWField, 280));
        oldPWHBox.setSpacing(8);
        HBox newPWHBox = new HBox(labelNewPW, PasswordFieldToggle.create(newPWField, 280));
        newPWHBox.setSpacing(8);
        HBox savePWhbox = new HBox(pwButton, pwLabel);
        savePWhbox.setSpacing(8);
        savePWhbox.setAlignment(Pos.CENTER_LEFT);

        base.getChildren().addAll(oldPWHBox, newPWHBox, savePWhbox);
    }

    private void setUpMyRatings(VBox base) {
        Text heading = new Text("My Ratings");
        heading.getStyleClass().addAll("heading3", "warning");

        User user = CurrentUserManager.get();
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
                    if (new RatingController().deleteRatingAndReview(CurrentUserManager.get(), r.getStudyMaterial())) {
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

        User user = CurrentUserManager.get();
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
