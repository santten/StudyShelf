package presentation.utility;

import javafx.scene.control.TextField;

public class UITools {
    private UITools(){}

    public static void limitInputLength(TextField field, int limit){
        field.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > limit) {
                field.setText(oldValue);
            }
        });
    }
}
