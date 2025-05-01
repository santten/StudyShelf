package presentation.view;

import domain.model.Category;
import domain.model.StudyMaterial;
import domain.model.Tag;
import domain.model.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import presentation.controller.*;
import presentation.enums.ScreenType;
import presentation.utility.GUILogger;

import java.io.IOException;
import java.util.Objects;

import static presentation.enums.ScreenType.*;

public class SceneManager {
    public BorderPane current;
    private GridPane header;
    private HBox footer;
    private boolean logged;
    private Stage primaryStage;
    private final ScreenHistory history = new ScreenHistory();

    private SceneManager(){
        initializeComponents();
    }

    public static final class SceneManagerHolder {
        public static final SceneManager instance = new SceneManager();
    }

    public static SceneManager getInstance() {
        return SceneManagerHolder.instance;
    }

    public ScreenHistory getHistory() {
        return history;
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

    public void setScreen(StudyMaterial m){
        if (CurrentUserManager.get() == null){
            setScreen(SCREEN_LOGIN);
            return;
        }

        StudyMaterialPageController controller = new StudyMaterialPageController(m);

        history.save(controller);

        controller.setPage();
    }

    public void setScreen(User u){
        if (CurrentUserManager.get() == null){
            setScreen(SCREEN_LOGIN);
            return;
        }

        ProfilePageController controller = new ProfilePageController(u);
        history.save(controller);

        controller.setPage();
    }

    public void setScreen(Tag tag){
        TaggedPageController controller = new TaggedPageController(tag);
        history.save(controller);

        controller.setPage();
    }

    public void setScreen(Category category) {
        if (CurrentUserManager.get() == null){
            setScreen(SCREEN_LOGIN);
            return;
        }

        CategoryPageController controller = new CategoryPageController(category);
        history.save(controller);
        controller.setPage();
    }

    public void setScreen(ScreenType screenType) {
        GUILogger.info("Setting screen: " + screenType + " called from: " + Thread.currentThread().getStackTrace()[2]);
        if (screenType == SCREEN_SIGNUP){
            SignupController sPage = new SignupController();
            SceneManagerHolder.instance.current = sPage.initialize();
        }
        else if (!SceneManagerHolder.instance.logged || screenType == SCREEN_LOGIN) {
            LoginController lPage = new LoginController();
            SceneManagerHolder.instance.current = lPage.initialize();
        } else {
            BorderPane bp = new BorderPane();
            bp.setTop(SceneManagerHolder.instance.header);
            bp.setBottom(SceneManagerHolder.instance.footer);
            SceneManagerHolder.instance.current = bp;

            PageController controller;
            switch (screenType) {
                case SCREEN_COURSES -> {
                    controller = new CoursesController();
                    history.reset();
                    history.save(controller);
                }
                case SCREEN_SEARCH -> {
                    controller = new SearchController();
                    history.reset();
                    history.save(controller);
                }
                case SCREEN_PROFILE -> {
                    controller = new MyProfileController();
                    history.reset();
                    history.save(controller);
                }
                case SCREEN_UPLOAD -> {
                    controller = new UploadController();
                    history.reset();
                }
                default -> {
                    controller = new HomeController();
                    history.reset();
                    history.save(controller);
                }
            }

            controller.setPage();
        }

        GUILogger.info("Displaying " + screenType);

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