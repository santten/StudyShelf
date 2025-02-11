package presentation.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import presentation.view.SceneManager;

import java.io.IOException;

import static presentation.view.Screen.SCREEN_LOGIN;

public class SignupController {

    SceneManager sm = SceneManager.getInstance();

    public SignupController() throws IOException {}

    @FXML
    private Button btn_signup;
    @FXML public Hyperlink link_toLogin;

    @FXML
    private void initialize(){
        btn_signup.setOnAction( (e) -> {
            try { sm.login(); }
            catch (IOException ex) { throw new RuntimeException(ex); }
        });

        link_toLogin.setOnAction( (e) -> {
            try { sm.setScreen(SCREEN_LOGIN); }
            catch (IOException ex) { throw new RuntimeException(ex); }
        });
    }
}
