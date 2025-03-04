package presentation.controller;

import domain.model.PermissionType;
import domain.model.RoleType;
import domain.model.User;
import domain.service.*;
import infrastructure.repository.RoleRepository;
import infrastructure.repository.UserRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import presentation.GUILogger;

import java.util.List;

import static domain.model.PermissionType.*;

public class UserController extends BaseController {
    private final UserService userService = new UserService(
            new UserRepository(),
            new RoleRepository(),
            new PasswordService(),
            new JWTService()
    );

    @FXML private ListView<String> userListView;
    @FXML private TextField firstNameField;
    @FXML private TextField lastNameField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ChoiceBox<RoleType> roleChoiceBox;
    @FXML private Button createUserButton;
    @FXML private Button updateUserButton;
    @FXML private Button deleteUserButton;

    private User selectedUser;

    @FXML
    private void initialize() {
        loadUsers();

        userListView.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                selectedUser = userService.findByEmail(newVal);
                populateUserDetails();
            }
        });

        updateUserButton.setOnAction(e -> updateUser());
        deleteUserButton.setOnAction(e -> deleteUser());

        roleChoiceBox.getItems().addAll(RoleType.ADMIN, RoleType.TEACHER, RoleType.STUDENT);
    }

    private void loadUsers() {
        if (!hasPermission(READ_ALL_USERS)) {
            showAlert(Alert.AlertType.ERROR, "Permission Denied", "You do not have permission to view all users.");
            return;
        }

        userListView.getItems().clear();
        List<User> users = userService.getAllUsers();
        users.forEach(user -> userListView.getItems().add(user.getEmail()));
    }

    private void updateUser() {
        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "No User Selected", "Please select a user to update.");
            return;
        }

        User currentUser = Session.getInstance().getCurrentUser();

        if (selectedUser.equals(currentUser)) {
            if (!hasPermission(UPDATE_OWN_USER)) {
                showAlert(Alert.AlertType.ERROR, "Permission Denied", "You do not have permission to update your own profile.");
                return;
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Permission Denied", "You can only update your own profile.");
            return;
        }

        selectedUser.setFirstName(firstNameField.getText());
        selectedUser.setLastName(lastNameField.getText());
        selectedUser.setEmail(emailField.getText());

        userService.updateUser( selectedUser, selectedUser.getFirstName(), selectedUser.getLastName(), selectedUser.getEmail(), passwordField.getText());
        showAlert(Alert.AlertType.INFORMATION, "Success", "User updated successfully.");
        loadUsers();
    }

    private void deleteUser() {
        if (selectedUser == null) {
            showAlert(Alert.AlertType.WARNING, "No User Selected", "Please select a user to delete.");
            return;
        }

        User currentUser = Session.getInstance().getCurrentUser();

        if (selectedUser.equals(currentUser)) {
            if (!hasPermission(DELETE_OWN_USER)) {
                showAlert(Alert.AlertType.ERROR, "Permission Denied", "You do not have permission to delete your own account.");
                return;
            }
        } else if (!hasPermission(DELETE_ANY_USER)) {
            showAlert(Alert.AlertType.ERROR, "Permission Denied", "You do not have permission to delete other users.");
            return;
        }

        userService.deleteUser(selectedUser);
        showAlert(Alert.AlertType.INFORMATION, "Success", "User deleted successfully.");
        loadUsers();
    }

    private void populateUserDetails() {
        if (selectedUser != null) {
            firstNameField.setText(selectedUser.getFirstName());
            lastNameField.setText(selectedUser.getLastName());
            emailField.setText(selectedUser.getEmail());
            roleChoiceBox.setValue(selectedUser.getRole().getName());
        }
    }

}
