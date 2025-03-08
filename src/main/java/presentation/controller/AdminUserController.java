package presentation.controller;

import domain.model.User;
import domain.service.UserService;
import domain.service.Session;
import infrastructure.repository.UserRepository;
import infrastructure.repository.RoleRepository;
import domain.service.PasswordService;
import domain.service.JWTService;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import java.util.List;

// Only for admin users
// AdminUserController handles user management operations for admin users (view and delete).
public class AdminUserController {
    private final UserService userService = new UserService(
            new UserRepository(),
            new RoleRepository(),
            new PasswordService(),
            new JWTService()
    );

    @FXML
    private ListView<User> userListView;

    @FXML
    public void initialize() {
        if (!isAdmin()) {
            showAlert(Alert.AlertType.ERROR, "Permission Denied", "You do not have permission to access this page.");
            return;
        }
        loadUsers();
    }

    private void loadUsers() {
        userListView.getItems().clear();
        List<User> users = userService.getAllUsers();
        userListView.getItems().addAll(users);
        userListView.setCellFactory(param -> new UserListCell());
    }

    private boolean isAdmin() {
        User currentUser = Session.getInstance().getCurrentUser();
        return currentUser != null && currentUser.isAdmin();
    }

    private class UserListCell extends ListCell<User> {
        private final HBox content = new HBox();
        private final Label userLabel = new Label();
        private final Button deleteButton = new Button("Delete");

        public UserListCell() {
            super();
            deleteButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
            deleteButton.setOnAction(event -> {
                User user = getItem();
                if (user != null) {
                    deleteUser(user);
                }
            });

            HBox.setHgrow(userLabel, Priority.ALWAYS);
            content.getChildren().addAll(userLabel, deleteButton);
        }

        @Override
        protected void updateItem(User user, boolean empty) {
            super.updateItem(user, empty);
            if (user == null || empty) {
                setGraphic(null);
            } else {
                userLabel.setText(user.getEmail() + " - " + user.getFirstName() + " " + user.getLastName());
                setGraphic(content);
            }
        }
    }

    private void deleteUser(User user) {
        // Prevent deleting own account
        if (user.equals(Session.getInstance().getCurrentUser())) {
            showAlert(Alert.AlertType.ERROR, "Action Denied", "You cannot delete your own account.");
            return;
        }

        // Confirmation
        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirm Deletion");
        confirmDialog.setHeaderText("Delete User: " + user.getFirstName() + " " + user.getLastName());
        confirmDialog.setContentText("Are you sure you want to delete this user?");

        // Delete user if confirmed
        if (confirmDialog.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            try {
                userService.deleteUser(user);
                showAlert(Alert.AlertType.INFORMATION, "User Deleted", "User " + user.getEmail() + " has been deleted.");
                loadUsers();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Deletion Failed", "Could not delete user: " + e.getMessage());
            }
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
