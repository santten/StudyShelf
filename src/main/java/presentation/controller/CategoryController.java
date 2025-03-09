package presentation.controller;

import domain.model.Category;
import domain.model.PermissionType;
import domain.model.StudyMaterial;
import domain.model.User;
import domain.service.*;
import infrastructure.repository.CategoryRepository;
import infrastructure.repository.StudyMaterialRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import presentation.utility.CustomAlert;

import java.util.List;

import static javafx.scene.control.Alert.AlertType.WARNING;

public class CategoryController extends BaseController {
    private final CategoryService categoryService = new CategoryService(new CategoryRepository(), new PermissionService());

    @FXML private VBox categoryContainer;
    @FXML private TextField categoryInputField;
    @FXML private Button addCategoryButton;
    @FXML private ListView<Button> categoryListView;

    @FXML
    private void initialize() {
        addCategoryButton.setOnAction(e -> createCategory());
        categoryInputField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                createCategory();
            }
        });
        loadCategories();
    }

    private void createCategory() {
        if (!hasPermission(PermissionType.CREATE_CATEGORY)) {
            showAlert(Alert.AlertType.ERROR, "Permission Denied", "You do not have permission to create categories.");
            return;
        }

        String categoryName = categoryInputField.getText().trim();
        if (categoryName.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Error", "Category name cannot be empty.");
            return;
        }

        User user = Session.getInstance().getCurrentUser();
        Category newCategory = new Category(categoryName, user);
        categoryService.createCategory(user, newCategory);
        categoryInputField.clear();
        loadCategories();
    }

    private void loadCategories() {
        categoryListView.getItems().clear();
        List<Category> categories = categoryService.getCategories();
        for (Category category : categories) {
            Button categoryButton = new Button(category.getCategoryName());
            categoryButton.setOnAction(e -> editCategory(category));
            categoryButton.setContextMenu(createDeleteMenu(category));
            categoryListView.getItems().add(categoryButton);
        }
    }

    private void editCategory(Category category) {
        User user = Session.getInstance().getCurrentUser();
        boolean canUpdate = hasPermission(PermissionType.UPDATE_ANY_CATEGORY) ||
                (hasPermission(PermissionType.UPDATE_COURSE_CATEGORY) && category.getCreator().equals(user));

        if (!canUpdate) {
            showAlert(Alert.AlertType.ERROR, "Permission Denied", "You do not have permission to update this category.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(category.getCategoryName());
        dialog.setTitle("Edit Category");
        dialog.setHeaderText("Edit category name:");
        dialog.setContentText("New name:");

        dialog.showAndWait().ifPresent(newName -> {
            if (!newName.trim().isEmpty()) {
                category.setCategoryName(newName);
                categoryService.updateCategory(user, category);
                loadCategories();
            }
        });
    }

    private ContextMenu createDeleteMenu(Category category) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Delete Category");

        deleteItem.setOnAction(e -> {
            User user = Session.getInstance().getCurrentUser();
            boolean canDelete = hasPermission(PermissionType.DELETE_ANY_CATEGORY) ||
                    (hasPermission(PermissionType.DELETE_COURSE_CATEGORY) && category.getCreator().equals(user));

            if (!canDelete) {
                showAlert(Alert.AlertType.ERROR, "Permission Denied", "You do not have permission to delete this category.");
                return;
            }

            categoryService.deleteCategory(user, category.getCategoryId());
            loadCategories();
        });

        contextMenu.getItems().add(deleteItem);
        return contextMenu;
    }

    public static boolean deleteCategory(Category c) {
        if (CustomAlert.confirm("Deleting Course", "Are you sure you want to delete course \"" + c.getCategoryName() + "\"?", "This will delete all of the materials in it and can not be undone.", true)) {
            User user = Session.getInstance().getCurrentUser();
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
