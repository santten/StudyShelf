package presentation.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Hyperlink;
import javafx.scene.layout.VBox;
import presentation.view.SceneManager;
import presentation.view.SubScreen;

import java.io.IOException;
import java.util.Objects;

import static presentation.view.SubScreen.*;

public class ProfileController {
    SceneManager sm = SceneManager.getInstance();

    public ProfileController() throws IOException {
    }

    @FXML private VBox subVBox;

    @FXML private Hyperlink link_toProfile;
    @FXML private Hyperlink link_toUploaded;
    @FXML private Hyperlink link_toReviews;
    @FXML private Hyperlink link_toAccountSettings;
    @FXML private Hyperlink link_toDelete;
    @FXML private Hyperlink link_toLogOut;


    private void setSubScreen(SubScreen scr) throws IOException {
        subVBox.getChildren().clear();
        String resourcePath;

        switch (scr) {
            case PROFILE_UPLOADED:
                resourcePath = "/fxml/profile/myUploads.fxml";
                break;
            default:
                resourcePath = "/fxml/profile/myProfile.fxml";
                break;
        }

        subVBox.getChildren().add(FXMLLoader.load(Objects.requireNonNull(SceneManager.class.getResource(resourcePath))));
    }

    @FXML
    private void initialize() throws IOException {
        setSubScreen(PROFILE_PROFILE);

        link_toLogOut.setOnAction( (e) -> {
            try { sm.logout(); }
            catch (IOException ex) { throw new RuntimeException(ex); }
        });

        link_toProfile.setOnAction( (e) -> {
            try { setSubScreen(PROFILE_PROFILE); }
            catch (IOException ex) { throw new RuntimeException(ex); }
        });

        link_toUploaded.setOnAction( (e) -> {
            try { setSubScreen(PROFILE_UPLOADED); }
            catch (IOException ex) { throw new RuntimeException(ex); }
        });
    }
}