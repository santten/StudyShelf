package presentation.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import presentation.view.SceneManager;

import java.io.IOException;

import static presentation.view.Screen.*;

public class LoginController {
    SceneManager sm = SceneManager.getInstance();

    public LoginController() throws IOException {
    }

    @FXML private Button btn_login;
    @FXML public Hyperlink link_toSignup;

    @FXML
    private void initialize(){
        btn_login.setOnAction( (e) -> {
            try { sm.login(); }
            catch (IOException ex) { throw new RuntimeException(ex); }
        });

        link_toSignup.setOnAction( (e) -> {
            try { sm.setScreen(SCREEN_SIGNUP); }
            catch (IOException ex) { throw new RuntimeException(ex); }
        });
    }
};
