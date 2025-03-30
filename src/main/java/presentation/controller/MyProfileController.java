package presentation.controller;

import domain.model.*;
import domain.service.*;
import infrastructure.repository.*;
import jakarta.persistence.RollbackException;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import presentation.view.CurrentUserManager;
import presentation.utility.CustomAlert;
import presentation.components.PasswordFieldToggle;
import presentation.utility.SVGContents;
import presentation.view.LanguageManager;
import presentation.view.SceneManager;

import java.awt.*;
import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;
import java.util.function.Supplier;

import static presentation.controller.SubScreen.*;
import static presentation.view.Screen.*;

public class MyProfileController {
    private HBox hBoxBase;
    private VBox menuVBox;
    private VBox contentVBox;
    private final VBox materialContainer = new VBox();
    private final VBox courseContainer = new VBox();
    private final VBox ratingContainer = new VBox();

    ResourceBundle rb = LanguageManager.getInstance().getBundle();

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

        Text headingMyProfile = new Text(rb.getString("myProfile"));
        base.getChildren().add(headingMyProfile);
        headingMyProfile.getStyleClass().addAll("heading3", "light");

        addMenuLink(rb.getString("myMaterials"), PROFILE_FILES);
        if (CurrentUserManager.get().hasPermission(PermissionType.CREATE_CATEGORY)) {
            addMenuLink(rb.getString("myCourses"), PROFILE_COURSES);
        }
        addMenuLink(rb.getString("myRatings"), PROFILE_REVIEWS);

        Text headingSettings = new Text(rb.getString("settings"));
        base.getChildren().addAll(new Separator(), headingSettings);
        headingSettings.getStyleClass().addAll("heading3", "light");

        addMenuLink(rb.getString("profileSettings"), PROFILE_SETTINGS);
        addMenuLink(rb.getString("profileDelete"), PROFILE_DELETE);

        Hyperlink link = new Hyperlink(rb.getString("logOut"));
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
            case PROFILE_DELETE:
                setUpProfileDelete(base);
                break;
            default:
                setUpMyMaterials(base);
        }
    }

    private void setUpMyMaterials(VBox base) {
        Text heading = new Text(rb.getString("myMaterials"));
        heading.getStyleClass().addAll("heading3", "primary-light");

        User user = CurrentUserManager.get();
        StudyMaterialService smServ = new StudyMaterialService(new GoogleDriveService(), new StudyMaterialRepository(), new PermissionService());
        List<StudyMaterial> materials = smServ.findByUser(user);

        getMaterialContainer().getChildren().clear();

        if (materials.isEmpty()) {
            Hyperlink link = new Hyperlink(rb.getString("uploadMaterialPrompt"));
            link.setOnAction(evt -> {
                try {
                    SceneManager.getInstance().setScreen(SCREEN_UPLOAD);
                } catch (IOException e) {
                    SceneManager.getInstance().displayErrorPage(rb.getString("error.vague"), SCREEN_PROFILE, rb.getString("redirectBack"));
                }
            });
            getMaterialContainer().getChildren().addAll(new Text(rb.getString("noMyMaterials")), link);
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
                    getMaterialContainer().getChildren().add(new Text(rb.getString("noMyMaterials")));
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
        Text heading = new Text(rb.getString("displaySettings"));
        heading.getStyleClass().addAll("heading3", "error");
        base.getChildren().addAll(heading, new Text(rb.getString("nameDisclaimer")));
        base.setSpacing(8);

        User curUser = CurrentUserManager.get();
        UserService uServ = new UserService(new UserRepository(), new RoleRepository(), new PasswordService(), new JWTService());

        Text warningField = new Text("");
        warningField.getStyleClass().add("error");

        TextField firstNameField = new TextField(curUser.getFirstName());
        base.getChildren().add(createFieldBox(rb.getString("firstName"),
                firstNameField,
                curUser::getFirstName,
                () -> uServ.updateUserFirstName(curUser, firstNameField.getText()),
                warningField,
                rb.getString("error.cantSaveFirstName"))
        );

        TextField lastNameField = new TextField(curUser.getLastName());
        base.getChildren().add(createFieldBox(rb.getString("lastName"),
                lastNameField,
                curUser::getLastName, () ->
                uServ.updateUserLastName(curUser, lastNameField.getText()),
                warningField,
                rb.getString("error.cantSaveLastName")
        ));

        TextField emailField = new TextField(curUser.getEmail());
        base.getChildren().addAll(createFieldBox(rb.getString("eMail"),
                emailField,
                curUser::getEmail,
                () -> uServ.updateUserEmail(curUser, emailField.getText()),
                warningField,
                rb.getString("error.emailAlreadyRegistered")));

        setUpPasswordSettings(base);
    }

    private void setUpProfileDelete(VBox base) {
        Text heading = new Text(rb.getString("profileDelete"));
        heading.getStyleClass().addAll("heading3", "error");

        TextFlow disclaimer = new TextFlow(new Text(rb.getString("profileDeleteDisclaimer")));
        disclaimer.setPrefWidth(500);
        disclaimer.setMaxWidth(500);
        disclaimer.setMinWidth(500);

        Button buttonDelete = new Button(rb.getString("profileDelete"));
        buttonDelete.setDisable(true);
        buttonDelete.getStyleClass().add("btnSError");

        buttonDelete.setOnAction(e -> {
            User user = CurrentUserManager.get();
            UserService userServ = new UserService(new UserRepository(), new RoleRepository(), new PasswordService(), new JWTService());
            userServ.deleteOwnUser(user);
            try {
                SceneManager.getInstance().setScreen(SCREEN_LOGIN);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        CheckBox checkbox = new CheckBox(rb.getString("profileDeleteCheckbox"));

        checkbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            buttonDelete.setDisable(!newValue);
        });

        base.setSpacing(8);
        base.getChildren().addAll(heading, disclaimer, checkbox, buttonDelete);
    }

    private HBox createFieldBox(String labelText,
                                TextField textField,
                                Supplier<String> valueSupplier,
                                Runnable action,
                                Text warningField,
                                String warningString) {
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
                button.setText(rb.getString("save"));
                button.getStyleClass().removeAll("btnPlain");
                button.getStyleClass().add("btnS");
            }
        });

        button.setOnAction(e -> {
            try {
                action.run();
                button.setText("✔");
                button.getStyleClass().removeAll("btnS");
                button.getStyleClass().add("btnPlain");
                button.setDisable(true);
            } catch (Exception ex) {
                warningField.setText("ok");
            }
        });

        HBox box = new HBox(label, textField, button);
        box.setSpacing(8);
        return box;
    }

    private void setUpPasswordSettings(VBox base){
        User curUser = CurrentUserManager.get();
        UserService uServ = new UserService(new UserRepository(), new RoleRepository(), new PasswordService(), new JWTService());

        Text headingPW = new Text(rb.getString("changePassword"));
        headingPW.getStyleClass().addAll("heading3", "error");
        base.getChildren().addAll(headingPW);

        Label labelOldPW = new Label(rb.getString("oldPassword"));
        labelOldPW.getStyleClass().addAll("label4");
        labelOldPW.setMinWidth(120);
        labelOldPW.setMaxWidth(120);

        PasswordField oldPWField = new PasswordField();
        oldPWField.setPromptText(rb.getString("oldPassword"));
        oldPWField.setMinWidth(280);

        Label labelNewPW = new Label(rb.getString("newPassword"));
        labelNewPW.getStyleClass().addAll("label4");
        labelNewPW.setMinWidth(120);
        labelNewPW.setMaxWidth(120);

        PasswordField newPWField = new PasswordField();
        newPWField.setPromptText(rb.getString("newPassword"));
        newPWField.setMinWidth(280);

        Label pwLabel = new Label("");
        pwLabel.getStyleClass().addAll("secondary-light");

        Button pwButton = new Button(rb.getString("changePassword"));
        pwButton.setOnAction(e -> {
            if (uServ.updateUserPassword(curUser, oldPWField.getText(), newPWField.getText())){
                pwLabel.getStyleClass().clear();
                pwLabel.getStyleClass().addAll("secondary-light");
                pwLabel.setText(rb.getString("pwChangeSuccess"));
                pwButton.setDisable(true);
            } else {
                pwLabel.getStyleClass().clear();
                pwLabel.getStyleClass().addAll("error");
                pwLabel.setText(rb.getString("pwChangeFailOldWrong"));
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
        Text heading = new Text(rb.getString("myRatings"));
        heading.getStyleClass().addAll("heading3", "warning");

        User user = CurrentUserManager.get();
        RatingService rServ = new RatingService(new RatingRepository(), new PermissionService());
        List<Rating> ratings = rServ.getRatingsByUser(user);

        getRatingContainer().getChildren().clear();

        if (ratings.isEmpty()) {
            getRatingContainer().getChildren().addAll(new Text(rb.getString("noRatingsCTA")));
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
                if (CustomAlert.confirm(rb.getString("alertHeadingDeleteReview"), rb.getString("alertAreYouSureDeleteReview"), rb.getString("alertNoUndoDeleteReview"), true)) {
                    if (new RatingController().deleteRatingAndReview(CurrentUserManager.get(), r.getStudyMaterial())) {
                        getRatingContainer().getChildren().remove(item);
                    }

                    if (getRatingContainer().getChildren().isEmpty()) {
                        getRatingContainer().getChildren().add(new Text(rb.getString("noRatingsCTA")));
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
        Text heading = new Text(rb.getString("myCourses"));
        heading.getStyleClass().addAll("heading3", "secondary-light");

        User user = CurrentUserManager.get();
        CategoryService cServ = new CategoryService(new CategoryRepository(), new PermissionService());
        List<Category> courses = cServ.getCategoriesByUser(user);

        getCourseContainer().getChildren().clear();

        if (courses.isEmpty()) {
            Hyperlink link = new Hyperlink(rb.getString("makeCourse"));
            link.setOnAction(evt -> {
                try {
                    SceneManager.getInstance().setScreen(SCREEN_UPLOAD);
                } catch (IOException e) {
                    SceneManager.getInstance().displayErrorPage(rb.getString("error.vague"), SCREEN_PROFILE, rb.getString("redirectBack"));
                }
            });
            getCourseContainer().getChildren().addAll(new Text(rb.getString("noMyCourses")), link);
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
                    getCourseContainer().getChildren().add(new Text(rb.getString("noMyCourses")));
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
