package presentation.controller;

import domain.model.User;
import domain.service.PasswordService;
import domain.service.Session;
import infrastructure.repository.UserRepository;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import presentation.components.PasswordFieldToggle;
import presentation.utility.GUILogger;
import presentation.view.SceneManager;

import java.io.IOException;
import java.util.Objects;

import static presentation.view.Screen.SCREEN_SIGNUP;

public class LoginController {
    SceneManager sm = SceneManager.getInstance();

    private Button btn_login;
    public Hyperlink link_toSignup;
    private Label errorLabel;
    private TextField emailField;
    private PasswordField passwordField;

    public BorderPane initialize() {
        GUILogger.info("initializing");
        BorderPane bp = new BorderPane();

        VBox vbox = new VBox();
        vbox.getStylesheets().add(Objects.requireNonNull(SceneManager.class.getResource("/css/style.css")).toExternalForm());

        Text logo = new Text("StudyShelf");
        logo.getStyleClass().add("error");
        logo.getStyleClass().add("title");

        Label emailLabel = new Label("E-mail");

        emailField = new TextField();
        emailField.setMaxWidth(240);

        VBox emailBox = new VBox(emailLabel, emailField);

        Label pwLabel = new Label("Password");

        passwordField = new PasswordField();

        VBox pwBox = new VBox(pwLabel, (PasswordFieldToggle.create(passwordField, 240)));

        errorLabel = new Label();
        errorLabel.getStyleClass().add("error");

        btn_login = new Button("Login");
        btn_login.getStyleClass().add("btnS");

        link_toSignup = new Hyperlink("New here? Sign up now!");

        HBox hbox = new HBox(btn_login, link_toSignup);
        hbox.setSpacing(8);
        hbox.setAlignment(Pos.CENTER_LEFT);

        vbox.getChildren().addAll(logo,
                emailBox,
                pwBox,
                errorLabel,
                btn_login,
                link_toSignup);
        vbox.setSpacing(4);
        vbox.setAlignment(Pos.CENTER);

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

        vbox.setMaxWidth(200);
        vbox.setMaxHeight(200);

        bp.setCenter(vbox);
        BorderPane.setAlignment(vbox, Pos.CENTER);
        bp.setMinWidth(800);
        bp.setMinHeight(600);
        return bp;
    }

    @FXML
    private void handleLogin() {
        errorLabel.setText("");
        String email = emailField.getText();
        String password = passwordField.getText();

        UserRepository userRepository = new UserRepository();
        User user = userRepository.findByEmail(email);

        if (user != null) {
            PasswordService passwordService = new PasswordService();
            if (passwordService.checkPassword(password, user.getPassword())) {
                try {
                    Session.getInstance().setCurrentUser(user);
                    sm.login();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            } else {
                errorLabel.setText("Wrong password!");
                errorLabel.setVisible(true);
            }
        } else {
            errorLabel.setText("E-Mail isn't registered to StudyShelf");
            errorLabel.setVisible(true);
        }
    }
}