package presentation.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import presentation.view.SceneManager;

import java.io.IOException;

import static presentation.view.Screen.*;

public class HeaderController {
    @FXML Hyperlink link_logo;
    @FXML Button btn_toCourses;
    @FXML Button btn_toSearch;
    @FXML Button btn_toProfile;
    @FXML Button btn_logOut;

    @FXML
    private void initialize() throws IOException {
        SceneManager sm = SceneManager.getInstance();

        link_logo.setOnAction( (e) -> {
            try { sm.setScreen(SCREEN_HOME); }
            catch (IOException ex) { throw new RuntimeException(ex); }
        });

        btn_toCourses.setOnAction((e) -> {
            try { sm.setScreen(SCREEN_COURSES); }
            catch (IOException ex) { throw new RuntimeException(ex); }
        });

        btn_toSearch.setOnAction((e) -> {
            try { sm.setScreen(SCREEN_FIND); }
            catch (IOException ex) { throw new RuntimeException(ex); }
        });

        btn_toProfile.setOnAction((e) -> {
            try { sm.setScreen(SCREEN_PROFILE); }
            catch (IOException ex) { throw new RuntimeException(ex); }
        });

        btn_logOut.setOnAction((e) -> {
            try { sm.logout(); }
            catch (IOException ex) { throw new RuntimeException(ex); }
        });
    }
}
