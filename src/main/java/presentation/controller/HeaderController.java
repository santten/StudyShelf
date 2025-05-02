package presentation.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Tooltip;
import presentation.enums.ScreenType;
import presentation.view.SceneManager;

import static presentation.enums.ScreenType.*;
import static presentation.view.CurrentUserManager.logout;

public class HeaderController {
    @FXML Hyperlink linkLogo;

    @FXML Button btnToCourses;
    @FXML Button btnToSearch;
    @FXML Button btnToProfile;
    @FXML Button btnToUpload;

    @FXML Button btnLogOut;

    private void linkBtnToScreen(Button button, ScreenType screenType) {
        button.setOnAction( e -> SceneManager.getInstance().setScreen(screenType));
    }

    @FXML
    private void initialize() {
        linkBtnToScreen(btnToCourses, SCREEN_COURSES);
        linkBtnToScreen(btnToSearch, SCREEN_SEARCH);
        linkBtnToScreen(btnToProfile, SCREEN_PROFILE);
        linkBtnToScreen(btnToUpload, SCREEN_UPLOAD);

        linkLogo.setTooltip(new Tooltip("Home"));
        btnToCourses.setTooltip(new Tooltip("Courses"));
        btnToSearch.setTooltip(new Tooltip("Search"));
        btnToProfile.setTooltip(new Tooltip("Profile"));
        btnToUpload.setTooltip(new Tooltip("Upload"));
        btnLogOut.setTooltip(new Tooltip("Log Out"));

        linkLogo.setOnAction( e -> SceneManager.getInstance().setScreen(SCREEN_HOME));

        btnLogOut.setOnAction(e -> logout());
    }
}
