package presentation.controller;

import domain.model.User;
import domain.service.*;
import infrastructure.repository.RoleRepository;
import infrastructure.repository.UserRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;

// Only fot the users (admin,teacher,student) to manage their own account
// UserController handles user-related operations for their own account.
public class UserController extends BaseController {
    private final UserService userService = new UserService(
            new UserRepository(),
            new RoleRepository(),
            new PasswordService(),
            new JWTService()
    );

    private final PasswordService passwordService;

    public UserController() {
        this.passwordService = new PasswordService();
    }

    // Update account
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private Button updateButton;

    // Change password
    @FXML private PasswordField confirmPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private Button updatePasswordButton;

    // Delete account
    @FXML private Button deleteButton;

    private User currentUser;

    @FXML
    public void initialize() {
        currentUser = Session.getInstance().getCurrentUser();
        if (currentUser != null) {
            firstNameField.setText(currentUser.getFirstName());
            lastNameField.setText(currentUser.getLastName());
            emailField.setText(currentUser.getEmail());
        }

        updateButton.setOnAction(e -> updateUserInfo());
        updatePasswordButton.setOnAction(e -> updatePassword());
        deleteButton.setOnAction(e -> deleteUserAccount());
    }

    private void updateUserInfo() {
        if (currentUser == null) return;

        currentUser.setFirstName(firstNameField.getText());
        currentUser.setLastName(lastNameField.getText());
        currentUser.setEmail(emailField.getText());

        try {
            userService.updateUser(currentUser,currentUser.getFirstName(), currentUser.getLastName(), currentUser.getEmail());
            showAlert(Alert.AlertType.INFORMATION, "Success", "Profile updated successfully.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Update Failed", "Failed to update profile: " + e.getMessage());
        }
    }

    private void updatePassword() {
        if (currentUser == null) return;

        String confirmPassword = confirmPasswordField.getText();
        String newPassword = newPasswordField.getText();

        if (!userService.checkPassword(confirmPassword, currentUser.getPassword())) {
            showAlert(Alert.AlertType.ERROR, "Invalid Password", "Current password is incorrect.");
            return;
        }

        if (newPassword.isEmpty() || newPassword.length() < 8) {
            showAlert(Alert.AlertType.ERROR, "Password Error", "New password must be at least 8 characters long.");
            return;
        }

        String hashedNewPassword = passwordService.hashPassword(newPassword);
        currentUser.setPassword(hashedNewPassword);

        try {
            userService.updateUserPassword(currentUser, newPassword);
            showAlert(Alert.AlertType.INFORMATION, "Success", "Password updated successfully.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Password Update Failed", "Failed to update password: " + e.getMessage());
        }
    }

    private void deleteUserAccount() {
        if (currentUser == null) return;

        try {
            userService.deleteUser(currentUser);
            Session.getInstance().logout();  // Log out after deleting account
            showAlert(Alert.AlertType.INFORMATION, "Account Deleted", "Your account has been successfully deleted.");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Deletion Failed", "Failed to delete account: " + e.getMessage());
        }
    }
}
