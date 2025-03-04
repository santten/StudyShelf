package presentation.controller;

import domain.model.PermissionType;
import domain.model.Tag;
import domain.model.User;
import domain.service.PermissionService;
import domain.service.Session;
import domain.service.TagService;
import infrastructure.repository.TagRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;
import presentation.GUILogger;
import java.util.List;

public class TagController extends BaseController {
    private final TagService tagService = new TagService(new TagRepository(), new PermissionService());

    @FXML private VBox tagContainer;
    @FXML private TextField tagInputField;
    @FXML private Button addTagButton;
    @FXML private ListView<Button> tagListView;

    @FXML
    private void initialize() {
        addTagButton.setOnAction(e -> createTag());
        tagInputField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                createTag();
            }
        });
        loadTags();
    }

    private void createTag() {
        if (!hasPermission(PermissionType.CREATE_TAG)) {
            showAlert(Alert.AlertType.ERROR, "Permission Denied", "You do not have permission to create tags.");
            return;
        }

        String tagName = tagInputField.getText().trim();
        if (tagName.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Error", "Tag name cannot be empty.");
            return;
        }

        User user = Session.getInstance().getCurrentUser();
        Tag newTag = tagService.createTag(tagName, user);
        tagInputField.clear();
        loadTags();
    }

    private void loadTags() {
        tagListView.getItems().clear();
        List<Tag> tags = tagService.getTags(Session.getInstance().getCurrentUser());
        for (Tag tag : tags) {
            Button tagButton = new Button(tag.getTagName());
            tagButton.setOnAction(e -> editTag(tag));
            tagButton.setContextMenu(createDeleteMenu(tag));
            tagListView.getItems().add(tagButton);
        }
    }

    private void editTag(Tag tag) {
        User user = Session.getInstance().getCurrentUser();
        boolean canUpdate = hasPermission(PermissionType.UPDATE_ANY_TAG) ||
                (hasPermission(PermissionType.UPDATE_OWN_TAG) && tag.getCreator().equals(user));

        if (!canUpdate) {
            showAlert(Alert.AlertType.ERROR, "Permission Denied", "You do not have permission to update this tag.");
            return;
        }

        TextInputDialog dialog = new TextInputDialog(tag.getTagName());
        dialog.setTitle("Edit Tag");
        dialog.setHeaderText("Edit tag name:");
        dialog.setContentText("New name:");

        dialog.showAndWait().ifPresent(newName -> {
            if (!newName.trim().isEmpty()) {
                tagService.updateTag(tag, newName, user);
                loadTags();
            }
        });
    }

    private ContextMenu createDeleteMenu(Tag tag) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem deleteItem = new MenuItem("Delete Tag");

        deleteItem.setOnAction(e -> {
            User user = Session.getInstance().getCurrentUser();
            boolean canDelete = hasPermission(PermissionType.DELETE_ANY_TAG) ||
                    (hasPermission(PermissionType.DELETE_COURSE_TAG) && tag.getCreator().equals(user));

            if (!canDelete) {
                showAlert(Alert.AlertType.ERROR, "Permission Denied", "You do not have permission to delete this tag.");
                return;
            }

            tagService.deleteTag(tag, user);
            loadTags();
        });

        contextMenu.getItems().add(deleteItem);
        return contextMenu;
    }
}
