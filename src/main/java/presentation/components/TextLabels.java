package presentation.components;

import domain.model.RoleType;
import domain.model.User;
import javafx.scene.control.Label;
import presentation.view.LanguageManager;

import java.util.ResourceBundle;

public class TextLabels {
    public static Label getUserRoleLabel(User u){
        ResourceBundle rb = LanguageManager.getInstance().getBundle();
        RoleType role = u.getRole().getName();
        String styleClass;
        String roleName = switch (role) {
            case TEACHER -> {
                styleClass = "errorTagLabel";
                yield rb.getString("teacher");
            }
            case ADMIN -> {
                styleClass = "warningTagLabel";
                yield rb.getString("admin");
            }
            default -> {
                styleClass = "secondaryTagLabel";
                yield rb.getString("student");
                // student
            }
        };

        Label roleLabel = new Label(roleName);
        roleLabel.getStyleClass().add(styleClass);

        return roleLabel;
    }
}
