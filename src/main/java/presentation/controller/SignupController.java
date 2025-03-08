package presentation.controller;

import domain.model.Role;
import domain.model.RoleType;
import domain.model.User;
import domain.service.PasswordService;
import infrastructure.repository.UserRepository;
import infrastructure.repository.RoleRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import presentation.view.SceneManager;

import java.io.IOException;

import static presentation.view.Screen.SCREEN_LOGIN;

public class SignupController {
    SceneManager sm = SceneManager.getInstance();

    @FXML private TextField emailField;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private PasswordField passwordField;
    @FXML private PasswordField reenterPasswordField;
    @FXML private MenuButton roleMenuButton;
    @FXML private Button btn_signup;
    @FXML public Hyperlink link_toLogin;

    public SignupController() throws IOException {
    }

    @FXML
    private void initialize() {

        for (MenuItem item : roleMenuButton.getItems()) {
            item.setOnAction(e -> roleMenuButton.setText(item.getText()));
        }

        btn_signup.setOnAction(e -> {
            if (!roleMenuButton.getText().equals("Choose Your Role")) {
                handleSignup();
                try {
                    sm.setScreen(SCREEN_LOGIN);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        link_toLogin.setOnAction(e -> {
            try {
                sm.setScreen(SCREEN_LOGIN);
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    @FXML
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

