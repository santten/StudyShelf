package presentation.controller;

import domain.service.PermissionService;
import domain.service.Session;
import domain.model.PermissionType;
import domain.model.User;

import javafx.scene.control.Alert;
import presentation.utility.CustomAlert;
import presentation.view.SceneManager;

import java.io.IOException;

import static presentation.view.Screen.SCREEN_PROFILE;

public abstract class BaseController {
    protected final PermissionService permissionService = new PermissionService();

    // This method is used to get the current user
    protected User getCurrentUser() {
        return Session.getInstance().getCurrentUser();
    }

    protected boolean isAdmin() {
        User user = getCurrentUser();
        return user != null && user.isAdmin();
    }

    protected boolean isTeacher() {
        User user = getCurrentUser();
        return user != null && user.isTeacher();
    }

    protected boolean isStudent() {
        User user = getCurrentUser();
        return user != null && user.isStudent();
    }

    protected boolean hasPermission(PermissionType permissionType) {
        User user = getCurrentUser();
        return user != null && permissionService.hasPermission(user, permissionType);
    }

    protected void enforcePermission(PermissionType permissionType) {
        if (!hasPermission(permissionType)) {
            throw new SecurityException("You do not have permission: " + permissionType);
        }
    }

    protected boolean hasPermissionOnEntity(PermissionType permissionType, int entityOwnerId) {
        User user = getCurrentUser();
        return user != null && permissionService.hasPermissionOnEntity(user, permissionType, entityOwnerId);
    }

    protected void enforcePermissionOnEntity(PermissionType permissionType, int entityOwnerId) {
        if (!hasPermissionOnEntity(permissionType, entityOwnerId)) {
            throw new SecurityException("You do not have permission to modify this entity.");
        }
    }

    protected void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void logout(){
        if (CustomAlert.confirm("Logging out", "Log out from StudyShelf?", "You're welcome back anytime!", true)) {
            Session.getInstance().logout();
            try {
                SceneManager.getInstance().logout();
            } catch (IOException ex) {
                SceneManager.getInstance().displayErrorPage("Something went wrong when logging out...", SCREEN_PROFILE, "Go back");
                throw new RuntimeException(ex);
            }
        }
    }
}
