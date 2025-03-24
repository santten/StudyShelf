package presentation.controller;

import domain.model.User;
import domain.service.PasswordService;
import domain.service.Session;
import infrastructure.repository.UserRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import presentation.view.SceneManager;

import java.io.IOException;

import static presentation.view.Screen.SCREEN_SIGNUP;

public class LoginController {
    SceneManager sm = SceneManager.getInstance();

    @FXML
    private Button btn_login;
    @FXML
    public Hyperlink link_toSignup;
    @FXML
    private Label errorLabel;
    @FXML
    private TextField emailField;
    @FXML
    private PasswordField passwordField;

    @FXML
    public void initialize(BorderPane base) {
        btn_login.setOnAction((e) -> {
            handleLogin();
        });

        link_toSignup.setOnAction((e) -> {
            try {
                sm.setScreen(SCREEN_SIGNUP);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    @FXML
    private void handleLogin() {
        String email = emailField.getText();
        String password = passwordField.getText();

        UserRepository userRepository = new UserRepository();
        User user = userRepository.findByEmail(email);

        if (user != null) {
            PasswordService passwordService = new PasswordService();
            if (passwordService.checkPassword(password, user.getPassword())) {
                try {
                    // Store logged in user for the session
                    Session.getInstance().setCurrentUser(user);
                    sm.login();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                // Show login error message
                errorLabel.setText("Wrong password");
                errorLabel.setVisible(true);
            }
        } else {
            errorLabel.setText("Invalid e-mail");
            errorLabel.setVisible(true);
        }
    }
}