package presentation.controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

import java.io.IOException;

import static presentation.controller.Screen.TEST2;

public class TestController {
    @FXML
    private Button TEST_BTN;

    @FXML
    private void initialize(){
        TEST_BTN.setText("hel");
        TEST_BTN.setOnAction((ActionEvent event) -> {
            try {
                SceneManager sm = SceneManager.getInstance();
                sm.setScene(TEST2);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }
}
