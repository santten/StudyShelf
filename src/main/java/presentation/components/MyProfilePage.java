package presentation.components;

import domain.model.PermissionType;
import domain.model.User;
import domain.service.Session;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import presentation.view.SubScreen;

import java.util.Objects;

import static presentation.view.SubScreen.*;

public class MyProfilePage {
    private HBox hBoxBase;
    private VBox menuVBox;
    private VBox contentVBox;

    public void initialize(ScrollPane wrapper) {
        initialize(wrapper, PROFILE_FILES);
    }

    public void initialize(ScrollPane wrapper, SubScreen subScreen) {
        setHBox(new HBox());
        setMenuVBox(new VBox());
        setContentVBox(new VBox());

        User user = Session.getInstance().getCurrentUser();

        setUpMenu();
        setUpContent(subScreen);

        getHBox().getChildren().addAll(getMenuVBox(), getContentVBox());
        wrapper.setContent(getHBox());
    }

    private HBox getHBox() {
        return this.hBoxBase;
    }

    private void setHBox(HBox hbox) {
        hbox.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());
        hbox.setPadding(new Insets(20, 20, 20, 20));
        hbox.setSpacing(12);
        this.hBoxBase = hbox;
    }

    private VBox getMenuVBox() {
        return this.menuVBox;
    }

    private void setMenuVBox(VBox menuVBox) {
        this.menuVBox = menuVBox;
    }

    private VBox getContentVBox() {
        return this.contentVBox;
    }

    private void setContentVBox(VBox contentVBox) {
        this.contentVBox = contentVBox;
    }

    private void setUpMenu(){
        VBox base = getMenuVBox();
        base.getChildren().clear();
        base.getChildren().add(new Text("My profile"));
        addMenuLink("My Materials", PROFILE_FILES);
        if (Session.getInstance().getCurrentUser().hasPermission(PermissionType.CREATE_CATEGORY)) {
            addMenuLink("My Courses", PROFILE_COURSES);
        }
        addMenuLink("My Reviews", PROFILE_REVIEWS);

        base.getChildren().add(new Text("My profile"));
        base.getChildren().add(new Text("Delete Account"));
        base.getChildren().add(new Text("Log Out"));
    }

    private void setUpContent(SubScreen subScreen) {
        VBox base = getContentVBox();
        base.getChildren().clear();
        base.getChildren().add(new Text(subScreen.toString()));
    }

    private void addMenuLink(String text, SubScreen destination){
        Hyperlink link = new Hyperlink(text);
        link.setOnAction(e -> setUpContent(destination));
        link.getStyleClass().add("profileLink");
        getMenuVBox().getChildren().add(link);
    }
}
