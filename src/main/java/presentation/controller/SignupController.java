package presentation.controller;

import domain.model.Role;
import domain.model.RoleType;
import domain.model.User;
import domain.service.PasswordService;
import infrastructure.repository.RoleRepository;
import infrastructure.repository.UserRepository;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import presentation.components.PasswordFieldToggle;
import presentation.view.SceneManager;

import java.io.IOException;
import java.util.Objects;

import static presentation.view.Screen.SCREEN_LOGIN;
import static presentation.view.Screen.SCREEN_SIGNUP;

public class SignupController {
    SceneManager sm = SceneManager.getInstance();

    private TextField emailField;
    private TextField firstNameField;
    private TextField lastNameField;
    private PasswordField passwordField;
    private PasswordField reenterPasswordField;
    private MenuButton roleMenuButton;
    private Button btn_signup;
    public Hyperlink link_toLogin;
    private Label errorLabel;

    public BorderPane initialize() {
        BorderPane bp = new BorderPane();

        VBox vbox = new VBox();
        vbox.getStylesheets().add(Objects.requireNonNull(SceneManager.class.getResource("/css/style.css")).toExternalForm());

        /* StudyShelf logo */
        Text logo = new Text("StudyShelf");
        logo.getStyleClass().add("error");
        logo.getStyleClass().add("title");
        vbox.getChildren().add(logo);

        /* e-mail */
        Label emailLabel = new Label("E-mail");
        emailField = new TextField();
        emailField.setMaxWidth(240);
        VBox emailBox = new VBox(emailLabel, emailField);
        vbox.getChildren().add(emailBox);

        /* first name */
        firstNameField = new TextField();
        firstNameField.setMaxWidth(240);
        Label firstNameLabel = new Label("First Name");
        VBox firstNameBox = new VBox(firstNameLabel, firstNameField);
        vbox.getChildren().add(firstNameBox);

        /* last name */
        lastNameField = new TextField();
        lastNameField.setMaxWidth(240);
        Label lastNameLabel = new Label("Last Name");
        VBox lastNameBox = new VBox(lastNameLabel, lastNameField);
        vbox.getChildren().add(lastNameBox);

        /* password */
        Label pwLabel = new Label("Password");
        passwordField = new PasswordField();
        VBox passwordBox = new VBox(pwLabel, (PasswordFieldToggle.create(passwordField, 240)));

        Label reEnterLabel = new Label("Re-enter Password");
        reenterPasswordField = new PasswordField();
        VBox reEnterBox = new VBox(reEnterLabel, (PasswordFieldToggle.create(reenterPasswordField, 240)));

        vbox.getChildren().addAll(passwordBox, reEnterBox);

        /* role menu */
        Label roleMenuLabel = new Label("Role");
        roleMenuButton = new MenuButton("Choose Your Role");
        roleMenuButton.getItems().addAll(
                new MenuItem("Student"),
                new MenuItem("Teacher")
        );
        roleMenuButton.setMinWidth(240);
        for (MenuItem item : roleMenuButton.getItems()) {
            item.setOnAction(e -> roleMenuButton.setText(item.getText()));
        }
        VBox roleMenuBox = new VBox(roleMenuLabel, roleMenuButton);
        vbox.getChildren().add(roleMenuBox);

        /* error label space */
        errorLabel = new Label();
        errorLabel.getStyleClass().add("error");
        vbox.getChildren().add(errorLabel);

        /* sign up*/
        btn_signup = new Button("Sign Up");
        btn_signup.getStyleClass().add("btnS");
        btn_signup.setOnAction(e -> {
            if (validateForm()) {
                handleSignup();
                try {
                    sm.setScreen(SCREEN_LOGIN);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
        vbox.getChildren().add(btn_signup);

        /* link to login */
        link_toLogin = new Hyperlink("New here? Sign up now!");
        link_toLogin.setOnAction(e -> {
            try {
                sm.setScreen(SCREEN_LOGIN);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        vbox.getChildren().add(link_toLogin);

        vbox.setSpacing(4);
        vbox.setAlignment(Pos.CENTER);

        vbox.setMaxWidth(200);
        vbox.setMaxHeight(200);

        bp.setCenter(vbox);
        BorderPane.setAlignment(vbox, Pos.CENTER);
        bp.setMinWidth(800);
        bp.setMinHeight(600);

        return bp;
    };

    private boolean validateForm() {
        boolean firstNameExists = !firstNameField.getText().isEmpty();
        if (!firstNameExists) { errorLabel.setText("First name is required"); }

        boolean lastNameExists = !lastNameField.getText().isEmpty();
        if (!lastNameExists) { errorLabel.setText("Last name is required"); }

        boolean validEmail = emailField.getText().matches("^[\\w-.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
        if (!validEmail){ errorLabel.setText("Invalid email address"); }

        boolean passwordMatch = passwordField.getText().equals(reenterPasswordField.getText());
        if (!passwordMatch){ errorLabel.setText("Re-entered password doesn't match"); }

        boolean roleChosen = !roleMenuButton.getText().equals("Choose Your Role");
        if (!roleChosen){ errorLabel.setText("You must choose a role"); }

        return firstNameExists && lastNameExists &&
                validEmail && passwordMatch && roleChosen;
    }

    private void handleSignup() {
        try {
            String email = emailField.getText();
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String password = passwordField.getText();
            String selectedRole = roleMenuButton.getText();


            System.out.println("[DB] Starting user creation transaction");


            RoleType roleType;
            try {
                roleType = RoleType.valueOf(selectedRole.toUpperCase());
            } catch (IllegalArgumentException e) {
                System.out.println("[DB] Invalid role selected: " + selectedRole);
                return;
            }

            RoleRepository roleRepository = new RoleRepository();
            Role userRole = roleRepository.findByName(roleType);

            System.out.println("[DB] Checking role: " + selectedRole);

            if (userRole == null) {
                userRole = roleRepository.save(new Role(roleType));
                System.out.println("[DB] Created new role: " + selectedRole);
            }

            PasswordService passwordService = new PasswordService();
            String hashedPassword = passwordService.hashPassword(password);
            User newUser = new User(firstName, lastName, email, hashedPassword, userRole);
            UserRepository userRepository = new UserRepository();
            User savedUser = userRepository.save(newUser);

            System.out.println("[DB] User saved successfully - ID: " + savedUser.getUserId());
            System.out.println("[DB] User details: " + firstName + " " + lastName + " (" + email + ")");

            sm.setScreen(SCREEN_LOGIN);

        } catch (Exception e) {
            System.out.println("[DB] Error in user creation: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

