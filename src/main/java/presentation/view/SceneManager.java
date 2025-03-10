package presentation.view;
import domain.model.StudyMaterial;

import domain.model.Tag;
import domain.model.User;
import domain.model.Category;

import domain.service.Session;
import infrastructure.repository.StudyMaterialRepository;
import infrastructure.repository.TagRepository;
import infrastructure.repository.UserRepository;
import infrastructure.repository.CategoryRepository;

import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static presentation.view.Screen.*;

import presentation.components.*;
import presentation.utility.GUILogger;

public class SceneManager {
    private static SceneManager instance;
    private BorderPane current;
    private GridPane header;
    private HBox footer;
    private boolean logged;
    private Stage primaryStage;

    public static SceneManager getInstance() {
        if (instance == null){
            instance = new SceneManager();

            try {
                instance.initializeComponents();
            } catch (IOException e) {
                throw new RuntimeException("Failed to load FXML components", e);
            }
        }
        return instance;
    }

    private void initializeComponents() throws IOException {
        instance.current = FXMLLoader.load(Objects.requireNonNull(SceneManager.class.getResource("/fxml/login.fxml")));
        instance.header = FXMLLoader.load(Objects.requireNonNull(SceneManager.class.getResource("/fxml/header.fxml")));
        instance.footer = FXMLLoader.load(Objects.requireNonNull(SceneManager.class.getResource("/fxml/footer.fxml")));
        instance.logged = false;
    }

    public void displayCategory(int id) {
        if (Session.getInstance().getCurrentUser() == null){
            try { setScreen(SCREEN_LOGIN); }
            catch (IOException e){ throw new RuntimeException("Failed to load FXML components", e);}
            return;
        }

        CategoryRepository repo = new CategoryRepository();
        Category c = repo.findById(id);
        if (c == null) {
            GUILogger.warn("DNE: Tried to go to category with id " + id);
            displayErrorPage("This category does not exist.", SCREEN_HOME, "Go to home page");
        } else {
            CategoryPage page = new CategoryPage();
            page.setPage(c);
        }
    }

    public void displayMaterial(int id) {
        if (Session.getInstance().getCurrentUser() == null){
            try { setScreen(SCREEN_LOGIN); }
            catch (IOException e){ throw new RuntimeException("Failed to load FXML components", e);}
            return;
        }

        StudyMaterialRepository repo = new StudyMaterialRepository();
        StudyMaterial s = repo.findById(id);

        if (s == null) {
            GUILogger.warn("DNE: Tried to go to material with id " + id);
            displayErrorPage("This material doesn't exist.", SCREEN_COURSES, "Go to courses page");
            return;
        }

        MaterialPage page = new MaterialPage(s);
        page.displayPage();
    }

    public void displayProfile(int id) {
        if (Session.getInstance().getCurrentUser() == null){
            try { setScreen(SCREEN_LOGIN); }
            catch (IOException e){ throw new RuntimeException("Failed to load FXML components", e);}
            return;
        }

        UserRepository uRepo = new UserRepository();
        User u = uRepo.findById(id);

        if (u == null) {
            GUILogger.warn("DNE: Tried to go to user with id " + id);
            displayErrorPage("This user doesn't exist.", SCREEN_HOME, "Go to home page");
            return;
        }

        ProfilePage.setPage(u);
    }

    public void displayErrorPage(String errorText, Screen redirectScreen, String redirectLabel) {
        VBox vbox = new VBox();
        vbox.setSpacing(12);
        vbox.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());

        Text title = new Text(":(");
        title.getStyleClass().add("heading3");

        Text label = new Text(errorText);

        Hyperlink link = new Hyperlink(redirectLabel);
        link.setOnAction(event -> {
            try {
                setScreen(redirectScreen);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        vbox.setPadding(new Insets(20, 20, 20, 20));

        vbox.getChildren().addAll(title, label, link);
        instance.current.setCenter(vbox);
    }

    public void showMaterialsWithTag(int tagId) {
        TagRepository repo = new TagRepository();
        Tag tag = repo.findById(tagId);

        VBox vbox = new VBox();
        vbox.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());
        vbox.setPadding(new Insets(20, 20, 20, 20));
        vbox.setSpacing(12);

        Label title = new Label("Materials tagged \"" + tag.getTagName() + "\"");
        title.getStyleClass().addAll("label3", "primary-light");
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

        instance.current.setCenter(vbox);
    }

    public void setScreen(Screen screen) throws IOException {
        if (!instance.logged){
            instance.current = FXMLLoader.load(Objects.requireNonNull(SceneManager.class.getResource(screen == SCREEN_SIGNUP ? "/fxml/signup.fxml" : "/fxml/login.fxml")));
        } else {
            BorderPane bp = new BorderPane();
            bp.setTop(instance.header);
            bp.setBottom(instance.footer);
            instance.current = bp;

            ScrollPane base = new ScrollPane();
            base.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            base.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

            switch (screen) {
                case SCREEN_COURSES:
                    base.setContent(FXMLLoader.load(Objects.requireNonNull(SceneManager.class.getResource("/fxml/courses.fxml"))));
                    break;
                case SCREEN_FIND:
                    base.setContent(FXMLLoader.load(Objects.requireNonNull(SceneManager.class.getResource("/fxml/search.fxml"))));
                    break;
                case SCREEN_PROFILE:
                    MyProfilePage pPage = new MyProfilePage();
                    pPage.initialize(base);
                    break;
                case SCREEN_UPLOAD:
                    UploadPage uPage = new UploadPage();
                    uPage.initialize(base);
                    break;
                default:
                    base.setContent(FXMLLoader.load(Objects.requireNonNull(SceneManager.class.getResource("/fxml/home.fxml"))));
                    break;
            }

            instance.current.setCenter(base);
        }

        GUILogger.info("Displaying " + screen);

        primaryStage.setTitle("StudyShelf");
        primaryStage.setScene(new Scene(instance.current, 800, 600));
        primaryStage.show();
    }

    public void setCenter(Node node){
        instance.current.setCenter(node);
    }

    public void setPrimaryStage(Stage primaryStage){
        primaryStage.setResizable(false);
        instance.primaryStage = primaryStage;
    }

    public void login() throws IOException {
        if (instance.logged) {
            GUILogger.warn("User is already logged in");
        } else {
            instance.logged = true;
            instance.setScreen(SCREEN_HOME);
        }
    }

    public void logout() throws IOException {
        if (instance.logged){
            instance.logged = false;
            instance.setScreen(SCREEN_LOGIN);
        } else {
            GUILogger.warn("User is already logged out");
        }
    }
}