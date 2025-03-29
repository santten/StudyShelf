package presentation.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import presentation.view.LanguageManager;
import presentation.view.SceneManager;
import presentation.view.Screen;

import java.io.IOException;

import static presentation.view.CurrentUserManager.logout;
import static presentation.view.Screen.*;

public class HeaderController {
    @FXML Hyperlink link_logo;

    @FXML Button btn_toCourses;
    @FXML Button btn_toSearch;
    @FXML Button btn_toProfile;
    @FXML Button btn_toUpload;

    @FXML Button btn_logOut;

    private void linkBtnToScreen(Button button, Screen screen, SceneManager sm) {
        button.setOnAction( (e) -> {
            try { sm.setScreen(screen); }
            catch (IOException ex) { throw new RuntimeException(ex); }
        });
    }

    @FXML
    private void initialize() {
        SceneManager sm = SceneManager.getInstance();

        linkBtnToScreen(btn_toCourses, SCREEN_COURSES, sm);
        linkBtnToScreen(btn_toSearch, SCREEN_FIND, sm);
        linkBtnToScreen(btn_toProfile, SCREEN_PROFILE, sm);
        linkBtnToScreen(btn_toUpload, SCREEN_UPLOAD, sm);

        link_logo.setOnAction( (e) -> {
            try { sm.setScreen(SCREEN_HOME); }
            catch (IOException ex) { throw new RuntimeException(ex); }
        });

        btn_logOut.setOnAction(e -> logout());
    }
}
