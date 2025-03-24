package presentation.components;

import domain.model.RoleType;
import domain.model.User;
import javafx.scene.control.Label;

public class TextLabels {
    public static Label getUserRoleLabel(User u){
        RoleType role = u.getRole().getName();
        String formatted = role.toString().charAt(0) + role.toString().substring(1).toLowerCase();

        Label roleLabel = new Label(formatted);
        String styleClass = switch (role) {
            case TEACHER -> "errorTagLabel";
            case ADMIN -> "warningTagLabel";
            default -> "secondaryTagLabel"; // student
        };
        roleLabel.getStyleClass().add(styleClass);


        return roleLabel;
    }
}
