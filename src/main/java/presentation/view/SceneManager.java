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

import presentation.components.CategoryPage;
import presentation.components.ListItem;
import presentation.components.MaterialPage;
import presentation.components.ProfilePage;
import presentation.GUILogger;

public class SceneManager {
    private static SceneManager instance;
    private BorderPane current;
    private GridPane header;
    private HBox footer;
    private boolean logged;
    private Stage primaryStage;

    private SceneManager(){
    }

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

    public void displayCategory(int id) throws IOException {
        if (!instance.logged){
            setScreen(SCREEN_LOGIN);
        } else {
            CategoryRepository repo = new CategoryRepository();
            Category c = repo.findById(id);
            if (c == null) {
                GUILogger.warn("DNE: Tried to go to category with id " + id);
                displayErrorPage("This category does not exist.", SCREEN_HOME, "Go to home page");
            } else {
//                 VBox vbox = new VBox();

//                 vbox.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());
//                 vbox.setSpacing(12);
//                 vbox.setPadding(new Insets(20, 20, 20, 20));
//                 Text title = new Text(c.getCategoryName());
//                 title.getStyleClass().add("heading3");
//                 title.getStyleClass().add("secondary");

//                 Text author = new Text("Course by " + c.getCreator().getFullName());
//                 VBox header = new VBox();
//                 header.getChildren().addAll(title, author);

//                 vbox.getChildren().add(header);

//                 List<StudyMaterial> creatorMaterials = repo.findMaterialsByUserInCategory(c.getCreator(), c);
//                 if (!creatorMaterials.isEmpty()) {
//                     Text text = new Text("Materials from " + c.getCreator().getFullName());
//                     text.getStyleClass().add("heading4");
//                     text.getStyleClass().add("secondary");

//                     vbox.getChildren().addAll(
//                             text,
//                             MaterialCard.materialCardScrollHBox(creatorMaterials));
//                 }

//                 List<StudyMaterial> otherMaterials = repo.findMaterialsExceptUserInCategory(c.getCreator(), c);
//                 if (!otherMaterials.isEmpty()) {
//                     GUILogger.info(String.valueOf(otherMaterials.size()));
//                     Text text = new Text("Materials from others");
//                     text.getStyleClass().add("heading4");
//                     text.getStyleClass().add("secondary");

//                     vbox.getChildren().addAll(
//                             text,
//                             MaterialCard.materialCardScrollHBox(otherMaterials));
//                 }

//                 instance.current.setCenter(vbox);
                CategoryPage.setPage(c);
            }
        }
    }

    public void displayMaterial(int id) throws IOException {
        if (!instance.logged){
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

        MaterialPage.setPage(s);
    }

    public void displayProfile(int id) throws IOException {
        if (!instance.logged){
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

    public void showMaterialsWithTag(Tag tag) {
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
            list.forEach(sm -> {
                buttonList.add(ListItem.listItemGraphic(sm));
            });
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

            String resourcePath = switch (screen) {
                case SCREEN_COURSES -> "/fxml/courses.fxml";
                case SCREEN_PROFILE -> "/fxml/profile.fxml";
                case SCREEN_FIND -> "/fxml/search.fxml";
                case SCREEN_UPLOAD -> "/fxml/upload.fxml";
                default -> "/fxml/home.fxml";
            };

            base.setContent(FXMLLoader.load(Objects.requireNonNull(SceneManager.class.getResource(resourcePath))));
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