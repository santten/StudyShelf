package presentation.view;

import domain.model.Category;
import domain.model.StudyMaterial;
import domain.model.Tag;
import domain.model.User;
import infrastructure.repository.CategoryRepository;
import infrastructure.repository.StudyMaterialRepository;
import infrastructure.repository.TagRepository;
import infrastructure.repository.UserRepository;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import presentation.components.ListItem;
import presentation.controller.*;
import presentation.utility.GUILogger;
import presentation.utility.StyleClasses;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static presentation.view.Screen.*;

public class SceneManager {
    private BorderPane current;
    private GridPane header;
    private HBox footer;
    private boolean logged;
    private Stage primaryStage;

    private SceneManager(){
        initializeComponents();
    }

    private static final class SceneManagerHolder {
        private static final SceneManager instance = new SceneManager();
    }

    public static SceneManager getInstance() {
        return SceneManagerHolder.instance;
    }

    private void initializeComponents() {
        try {
            this.primaryStage = new Stage();
            this.primaryStage.setResizable(false);
            this.header = FXMLLoader.load(Objects.requireNonNull(SceneManager.class.getResource("/fxml/header.fxml")));
            this.footer = FXMLLoader.load(Objects.requireNonNull(SceneManager.class.getResource("/fxml/footer.fxml")));
            this.logged = false;
            LoginController lPage = new LoginController();
            this.current = lPage.initialize();
        } catch (IOException e) {
            throw new IllegalStateException("Failed to initialize screen", e);
        }
    }

    public void displayCategory(int id) {
        if (CurrentUserManager.get() == null){
            setScreen(SCREEN_LOGIN);
            return;
        }

        CategoryRepository repo = new CategoryRepository();
        Category c = repo.findById(id);
        if (c == null) {
            GUILogger.warn("DNE: Tried to go to category with id " + id);
            displayErrorPage("This category does not exist.", SCREEN_HOME, "Go to home page");
        } else {
            CategoryPageController page = new CategoryPageController();
            page.setPage(c);
        }
    }

    public void displayMaterial(int id) {
        if (CurrentUserManager.get() == null){
            setScreen(SCREEN_LOGIN);
            return;
        }

        StudyMaterialRepository repo = new StudyMaterialRepository();
        StudyMaterial s = repo.findById(id);

        if (s == null) {
            GUILogger.warn("DNE: Tried to go to material with id " + id);
            displayErrorPage("This material doesn't exist.", SCREEN_COURSES, "Go to courses page");
            return;
        }

        StudyMaterialPageController page = new StudyMaterialPageController(s);
        page.displayPage();
    }

    public void displayProfile(int id) {
        if (CurrentUserManager.get() == null){
            setScreen(SCREEN_LOGIN);
            return;
        }

        UserRepository uRepo = new UserRepository();
        User u = uRepo.findById(id);

        if (u == null) {
            GUILogger.warn("DNE: Tried to go to user with id " + id);
            displayErrorPage("This user doesn't exist.", SCREEN_HOME, "Go to home page");
            return;
        }

        ProfilePageController.setPage(u);
    }

    public void displayErrorPage(String errorText, Screen redirectScreen, String redirectLabel) {
        VBox vbox = new VBox();
        vbox.setSpacing(12);
        vbox.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());

        Text title = new Text(":(");
        title.getStyleClass().add(StyleClasses.HEADING3);

        Text label = new Text(errorText);

        Hyperlink link = new Hyperlink(redirectLabel);
        link.setOnAction(event -> setScreen(redirectScreen));

        vbox.setPadding(new Insets(20, 20, 20, 20));

        vbox.getChildren().addAll(title, label, link);
        SceneManagerHolder.instance.current.setCenter(vbox);
    }

    public void showMaterialsWithTag(int tagId) {
        TagRepository repo = new TagRepository();
        Tag tag = repo.findById(tagId);

        VBox vbox = new VBox();
        vbox.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());
        vbox.setPadding(new Insets(20, 20, 20, 20));
        vbox.setSpacing(12);

        Label title = new Label("Materials tagged \"" + tag.getTagName() + "\"");
        title.getStyleClass().addAll(StyleClasses.LABEL3, StyleClasses.PRIMARY_LIGHT);
        vbox.getChildren().add(title);

        StudyMaterialRepository sRepo = new StudyMaterialRepository();
        List<StudyMaterial> list = sRepo.findByTag(tag);
        if (!list.isEmpty()){
            List<Node> buttonList = new ArrayList<>();
            list.forEach(sm -> buttonList.add(ListItem.listItemGraphic(sm)));
            vbox.getChildren().add(ListItem.toListView(buttonList));
        } else {
            vbox.getChildren().add(new Text("No materials exist with this tag yet!"));
        }

        SceneManagerHolder.instance.current.setCenter(vbox);
    }

    public void setScreen(Screen screen) {
        GUILogger.info("Setting screen: " + screen + " called from: " + Thread.currentThread().getStackTrace()[2]);
        if (screen == SCREEN_SIGNUP){
            SignupController sPage = new SignupController();
            SceneManagerHolder.instance.current = sPage.initialize();
        }
        else if (!SceneManagerHolder.instance.logged || screen == SCREEN_LOGIN) {
            LoginController lPage = new LoginController();
            SceneManagerHolder.instance.current = lPage.initialize();
        } else {
            BorderPane bp = new BorderPane();
            bp.setTop(SceneManagerHolder.instance.header);
            bp.setBottom(SceneManagerHolder.instance.footer);
            SceneManagerHolder.instance.current = bp;

            ScrollPane base = new ScrollPane();
            base.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            base.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

            switch (screen) {
                case SCREEN_COURSES:
                    try {
                        base.setContent(FXMLLoader.load(Objects.requireNonNull(SceneManager.class.getResource("/fxml/courses.fxml"))));
                    } catch (IOException e) {
                        throw new IllegalStateException(e);
                    }
                    break;
                case SCREEN_FIND:
                    try {
                        base.setContent(FXMLLoader.load(Objects.requireNonNull(SceneManager.class.getResource("/fxml/search.fxml"))));
                    } catch (IOException e) {
                        throw new IllegalStateException(e);
                    }
                    break;
                case SCREEN_PROFILE:
                    MyProfileController pPage = new MyProfileController();
                    pPage.initialize(base);
                    break;
                case SCREEN_UPLOAD:
                    UploadController uPage = new UploadController();
                    uPage.initialize(base);
                    break;
                default:
                    try {
                        base.setContent(FXMLLoader.load(Objects.requireNonNull(SceneManager.class.getResource("/fxml/home.fxml"))));
                    } catch (IOException e) {
                        throw new IllegalStateException(e);
                    }
                    break;
            }

            SceneManagerHolder.instance.current.setCenter(base);
        }

        GUILogger.info("Displaying " + screen);

        primaryStage.setTitle("StudyShelf");
        primaryStage.setScene(new Scene(SceneManagerHolder.instance.current, 800, 600));
        primaryStage.show();
    }

    public void setCenter(Node node){
        SceneManagerHolder.instance.current.setCenter(node);
    }

    public void login() {
        if (SceneManagerHolder.instance.logged) {
            GUILogger.warn("User is already logged in");
        } else {
            SceneManagerHolder.instance.logged = true;
            SceneManagerHolder.instance.setScreen(SCREEN_HOME);
        }
    }

    public void logout() {
        if (SceneManagerHolder.instance.logged){
            SceneManagerHolder.instance.logged = false;
            setScreen(SCREEN_LOGIN);
        } else {
            GUILogger.warn("User is already logged out");
        }
    }
}