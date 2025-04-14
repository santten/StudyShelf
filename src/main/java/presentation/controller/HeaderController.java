package presentation.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import presentation.view.SceneManager;
import presentation.view.Screen;

import static presentation.view.CurrentUserManager.logout;
import static presentation.view.Screen.*;

public class HeaderController {
    @FXML Hyperlink linkLogo;

    @FXML Button btnToCourses;
    @FXML Button btnToSearch;
    @FXML Button btnToProfile;
    @FXML Button btnToUpload;

    @FXML Button btnLogOut;

    private void linkBtnToScreen(Button button, Screen screen) {
        button.setOnAction( e -> SceneManager.getInstance().setScreen(screen));
    }

    @FXML
    private void initialize() {
        linkBtnToScreen(btnToCourses, SCREEN_COURSES);
        linkBtnToScreen(btnToSearch, SCREEN_FIND);
        linkBtnToScreen(btnToProfile, SCREEN_PROFILE);
        linkBtnToScreen(btnToUpload, SCREEN_UPLOAD);

        linkLogo.setOnAction( e -> SceneManager.getInstance().setScreen(SCREEN_HOME));

        btnLogOut.setOnAction(e -> logout());
    }
}
