package presentation.controller;

import domain.model.Role;
import domain.model.RoleType;
import domain.model.User;
import domain.service.PasswordService;
import infrastructure.repository.RoleRepository;
import infrastructure.repository.UserRepository;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import presentation.components.PasswordFieldToggle;
import presentation.utility.GUILogger;
import presentation.utility.StyleClasses;
import presentation.view.LanguageManager;
import presentation.view.SceneManager;

import java.util.Map;
import java.util.Objects;
import java.util.ResourceBundle;

import static presentation.utility.EmailValidator.isValidEmail;
import static presentation.view.Screen.SCREEN_LOGIN;

public class SignupController {
    SceneManager sm = SceneManager.getInstance();
    ResourceBundle rb = LanguageManager.getInstance().getBundle();
    private static final Logger logger = LoggerFactory.getLogger(SignupController.class);

    private TextField emailField;
    private TextField firstNameField;
    private TextField lastNameField;
    private PasswordField passwordField;
    private PasswordField reenterPasswordField;
    private MenuButton roleMenuButton;
    private Label errorLabel;

    public BorderPane initialize() {
        BorderPane bp = new BorderPane();

        VBox vbox = new VBox();
        vbox.getStylesheets().add(Objects.requireNonNull(SceneManager.class.getResource("/css/style.css")).toExternalForm());

        /* StudyShelf logo */
        Text logoLabel = new Text("StudyShelf");
        logoLabel.getStyleClass().addAll(StyleClasses.ERROR, StyleClasses.TITLE);
        vbox.getChildren().add(logoLabel);

        /* e-mail */
        Label emailLabel = new Label(rb.getString("eMail"));
        emailField = new TextField();
        emailField.setMaxWidth(240);
        VBox emailBox = new VBox(emailLabel, emailField);
        vbox.getChildren().add(emailBox);

        /* first name */
        firstNameField = new TextField();
        firstNameField.setMaxWidth(240);
        Label firstNameLabel = new Label(rb.getString("firstName"));
        VBox firstNameBox = new VBox(firstNameLabel, firstNameField);
        vbox.getChildren().add(firstNameBox);

        /* last name */
        lastNameField = new TextField();
        lastNameField.setMaxWidth(240);
        Label lastNameLabel = new Label(rb.getString("lastName"));
        VBox lastNameBox = new VBox(lastNameLabel, lastNameField);
        vbox.getChildren().add(lastNameBox);

        /* password */
        Label pwLabel = new Label(rb.getString("password"));
        passwordField = new PasswordField();
        VBox passwordBox = new VBox(pwLabel, (PasswordFieldToggle.create(passwordField, 240)));

        Label reEnterLabel = new Label(rb.getString("passwordAgain"));
        reenterPasswordField = new PasswordField();
        VBox reEnterBox = new VBox(reEnterLabel, (PasswordFieldToggle.create(reenterPasswordField, 240)));

        vbox.getChildren().addAll(passwordBox, reEnterBox);

        /* role menu */
        Label roleMenuLabel = new Label(rb.getString("role"));
        roleMenuButton = new MenuButton(rb.getString("rolePrompt"));
        roleMenuButton.getItems().addAll(
                new MenuItem(rb.getString("student")),
                new MenuItem(rb.getString("teacher"))
        );
        roleMenuButton.setMinWidth(240);
        for (MenuItem item : roleMenuButton.getItems()) {
            item.setOnAction(e -> roleMenuButton.setText(item.getText()));
        }
        VBox roleMenuBox = new VBox(roleMenuLabel, roleMenuButton);
        vbox.getChildren().add(roleMenuBox);

        /* error label space */
        errorLabel = new Label();
        errorLabel.getStyleClass().add(StyleClasses.ERROR);
        vbox.getChildren().add(errorLabel);

        /* sign up*/
        Button btnSignup = new Button(rb.getString("signup"));
        btnSignup.getStyleClass().add(StyleClasses.BTN_S);
        btnSignup.setOnAction(e -> {
            if (validateForm()) {
                handleSignup();
                sm.setScreen(SCREEN_LOGIN);
            }
        });
        vbox.getChildren().add(btnSignup);

        Hyperlink linkToLogin = new Hyperlink(rb.getString("signupToLogin"));
        linkToLogin.setOnAction(e -> sm.setScreen(SCREEN_LOGIN));

        vbox.getChildren().addAll(linkToLogin);
        vbox.setSpacing(4);
        vbox.setAlignment(Pos.CENTER);

        vbox.setMaxWidth(200);
        vbox.setMaxHeight(200);

        bp.setCenter(vbox);
        BorderPane.setAlignment(vbox, Pos.CENTER);
        bp.setMinWidth(800);
        bp.setMinHeight(600);

        return bp;
    }

    private boolean validateForm() {
        boolean firstNameExists = !firstNameField.getText().isEmpty();
        if (!firstNameExists) { errorLabel.setText(rb.getString("error.noFirstName")); }

        boolean lastNameExists = !lastNameField.getText().isEmpty();
        if (!lastNameExists) { errorLabel.setText(rb.getString("error.noLastName")); }

        boolean validEmail = isValidEmail(emailField.getText());
        if (!validEmail){ errorLabel.setText(rb.getString("error.invalidEmail")); }

        boolean passwordMatch = passwordField.getText().equals(reenterPasswordField.getText());
        if (!passwordMatch){ errorLabel.setText(rb.getString("error.pwMatch")); }

        boolean roleChosen = !roleMenuButton.getText().equals("Choose Your Role");
        if (!roleChosen){ errorLabel.setText(rb.getString("error.noRole")); }

        return firstNameExists && lastNameExists &&
                validEmail && passwordMatch && roleChosen;
    }

    private void handleSignup() {
        try {
            String email = emailField.getText();
            String firstName = firstNameField.getText();
            String lastName = lastNameField.getText();
            String password = passwordField.getText();

            String displayedRole = roleMenuButton.getText();

            // Mapping of displayed role names to English role names
            Map<String, String> roleMapping = Map.of(
                    rb.getString("student"), "Student",
                    rb.getString("teacher"), "Teacher"
            );

            String selectedRole = roleMapping.get(displayedRole);
            if (selectedRole == null) {
                GUILogger.warn("Invalid role selected: " + displayedRole);
                return;
            }

            RoleType roleType;
            roleType = RoleType.valueOf(selectedRole.toUpperCase());

            RoleRepository roleRepository = new RoleRepository();
            Role userRole = roleRepository.findByName(roleType);

            if (userRole == null) {
                userRole = roleRepository.save(new Role(roleType));
            }

            PasswordService passwordService = new PasswordService();
            String hashedPassword = passwordService.hashPassword(password);
            User newUser = new User(firstName, lastName, email, hashedPassword, userRole);
            UserRepository userRepository = new UserRepository();
            User savedUser = userRepository.save(newUser);

            logger.info("User saved successfully - ID: {}", savedUser.getUserId());
            logger.debug("User signup details: {} {}", firstName, lastName);
            logger.debug("User email: {}", email);
            sm.setScreen(SCREEN_LOGIN);

        } catch (Exception e) {
            logger.error("Error in user creation: {}", e.getMessage(), e);
        }
    }
}

