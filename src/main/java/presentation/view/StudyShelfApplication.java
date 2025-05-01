package presentation.view;

import javafx.application.Application;
import javafx.stage.Stage;
import presentation.utility.GUILogger;

import java.util.Locale;

import static presentation.enums.ScreenType.SCREEN_LOGIN;

public class StudyShelfApplication extends Application {
    @Override
    public void start(Stage primaryStage) {
        GUILogger.setWarVisibility(true);
        GUILogger.setInfoVisibility(true);

        LanguageManager.getInstance().setLanguage(new Locale("en", "US"));

        SceneManager sm = SceneManager.getInstance();
        sm.setScreen(SCREEN_LOGIN);
    }
}
