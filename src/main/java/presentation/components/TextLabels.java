package presentation.components;

import domain.model.RoleType;
import domain.model.User;
import javafx.scene.control.Label;
import presentation.utility.StyleClasses;
import presentation.view.LanguageManager;

import java.util.ResourceBundle;

public class TextLabels {
    private TextLabels(){}

    public static Label getUserRoleLabel(User u){
        ResourceBundle rb = LanguageManager.getInstance().getBundle();
        RoleType role = u.getRole().getName();
        String styleClass;
        String roleName = switch (role) {
            case TEACHER -> {
                styleClass = StyleClasses.ERROR_TAG_LABEL;
                yield rb.getString("teacher");
            }
            case ADMIN -> {
                styleClass = StyleClasses.WARNING_TAG_LABEL;
                yield rb.getString("admin");
            }
            default -> {
                styleClass = StyleClasses.SECONDARY_TAG_LABEL;
                yield rb.getString("student");
            }
        };

        Label roleLabel = new Label(roleName);
        roleLabel.getStyleClass().add(styleClass);

        return roleLabel;
    }
}
