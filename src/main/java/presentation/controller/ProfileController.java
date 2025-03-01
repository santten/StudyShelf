package presentation.controller;

import domain.service.Session;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import presentation.GUILogger;
import presentation.view.SceneManager;
import presentation.view.SubScreen;
import presentation.components.MyProfileSubScreens;


import java.io.IOException;

import static domain.model.RoleType.STUDENT;
import static presentation.view.SubScreen.*;

public class ProfileController {
    SceneManager sm = SceneManager.getInstance();

    @FXML private VBox subVBox;
    @FXML private VBox vbox_profile_options;

    private void setSubScreen(SubScreen scr) {
        subVBox.getChildren().clear();

        Node item = switch (scr) {
            case PROFILE_COURSES -> MyProfileSubScreens.Courses();
            case PROFILE_REVIEWS -> MyProfileSubScreens.Reviews();
            case PROFILE_SETTINGS -> MyProfileSubScreens.Settings();
            default -> MyProfileSubScreens.Files();
        };

        subVBox.getChildren().add(item);
    }

    private void addLink(String text, SubScreen destination){
        Hyperlink link = new Hyperlink(text);
        link.setOnAction(e -> setSubScreen(destination));
        link.getStyleClass().add("profileLink");
        vbox_profile_options.getChildren().add(link);
    }

    @FXML
    private void initialize() {
        setSubScreen(PROFILE_FILES);
        vbox_profile_options.getChildren().clear();

        Label label1 = new Label("Profile");
        label1.getStyleClass().addAll("label4", "light");

        vbox_profile_options.getChildren().add(label1);

        addLink("My Files", PROFILE_FILES);
        if (Session.getInstance().getCurrentUser().getRole().getName() != STUDENT) addLink("My Courses", PROFILE_COURSES);
        addLink("My Reviews", PROFILE_REVIEWS);

        Label label2 = new Label("Settings");
        label2.getStyleClass().addAll("label4", "light");
        VBox.setMargin(label2, new Insets(12, 0, 0, 0));

        vbox_profile_options.getChildren().add(label2);

        addLink("Account Settings", PROFILE_SETTINGS);

        Hyperlink link_toLogOut = new Hyperlink("Log out");
        link_toLogOut.getStyleClass().addAll("profileLink", "logOutLink");

        link_toLogOut.setOnAction( (e) -> {
            try {
                sm.logout();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });

        VBox.setMargin(link_toLogOut, new Insets(12, 0, 0, 0));

        vbox_profile_options.getChildren().add(link_toLogOut);
    }
}