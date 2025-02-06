package presentation.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

import static presentation.view.Screen.*;
import presentation.logger.GUILogger;

public class SceneManager {
    private static SceneManager instance;
    private BorderPane current;
    private GridPane header;
    private HBox footer;
    private boolean logged;
    private ScrollPane scrollpane;
    private Stage primaryStage;

    private SceneManager(){
    }

    public static SceneManager getInstance() throws IOException {
        if (instance == null){
            instance = new SceneManager();
            instance.current = FXMLLoader.load(Objects.requireNonNull(SceneManager.class.getResource("/fxml/login.fxml")));
            instance.header = FXMLLoader.load(Objects.requireNonNull(SceneManager.class.getResource("/fxml/header.fxml")));
            instance.footer = FXMLLoader.load(Objects.requireNonNull(SceneManager.class.getResource("/fxml/footer.fxml")));
            instance.logged = false;
        }
        return instance;
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

            String resourcePath;

            switch(screen){
                case SCREEN_COURSES:
                    resourcePath = "/fxml/courses.fxml";
                    break;
                case SCREEN_PROFILE:
                    resourcePath = "/fxml/profile.fxml";
                    break;
                case SCREEN_FIND:
                    resourcePath = "/fxml/search.fxml";
                    break;
                default: /* defaults to home screen */
                    resourcePath = "/fxml/home.fxml";
                    break;
            }

            GUILogger.info("Switching to show content at " + resourcePath);
            base.setContent(FXMLLoader.load(Objects.requireNonNull(SceneManager.class.getResource(resourcePath))));
            instance.current.setCenter(base);
        }

        primaryStage.setTitle("StudyShelf");
        primaryStage.setScene(new Scene(instance.current, 800, 600));
        primaryStage.show();
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