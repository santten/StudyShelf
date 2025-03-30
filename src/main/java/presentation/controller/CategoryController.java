package presentation.controller;

import domain.model.Category;
import domain.model.PermissionType;
import domain.model.User;
import domain.service.CategoryService;
import domain.service.PermissionService;
import infrastructure.repository.CategoryRepository;
import presentation.view.CurrentUserManager;
import presentation.utility.CustomAlert;

import static javafx.scene.control.Alert.AlertType.WARNING;

public class CategoryController {
    public static boolean deleteCategory(Category c) {
        if (CustomAlert.confirm("Deleting Course", "Are you sure you want to delete course \"" + c.getCategoryName() + "\"?", "This will delete all of the materials in it and can not be undone.", true)) {
            User user = CurrentUserManager.get();
            if (!(user.getUserId() == c.getCreator().getUserId() || user.hasPermission(PermissionType.DELETE_ANY_CATEGORY))) {
                CustomAlert.show(WARNING, "Permission Denied", "You do not have permission to delete this course.");
            }
            new CategoryService(new CategoryRepository(), new PermissionService()).deleteCategory(user, c.getCategoryId());

            return true;
        } else {
            return false;
        }
    }
}
