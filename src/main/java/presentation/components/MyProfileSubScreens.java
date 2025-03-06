package presentation.components;

import domain.model.Category;
import domain.model.Review;
import domain.model.StudyMaterial;
import domain.model.User;
import domain.service.Session;
import infrastructure.repository.CategoryRepository;
import infrastructure.repository.ReviewRepository;
import infrastructure.repository.StudyMaterialRepository;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import presentation.GUILogger;
import presentation.view.SceneManager;

import java.io.IOException;
import java.util.List;

import static javafx.scene.shape.FillRule.EVEN_ODD;
import static presentation.view.Screen.*;

public class MyProfileSubScreens {
    public static VBox Files(){
        User u = Session.getInstance().getCurrentUser();

        VBox base = new VBox(createTitle("My Uploads", "primary-light"));

        base.setMinWidth(560);
        base.setMaxWidth(560);

        StudyMaterialRepository sRepo = new StudyMaterialRepository();
        List<StudyMaterial> list = sRepo.findByUser(u);
        if (list.isEmpty()){
            Text text = new Text("You haven't uploaded any materials yet!");
            Hyperlink link = new Hyperlink("Go upload a material");

            link.setOnAction(e -> {
                SceneManager sm = SceneManager.getInstance();
                try {
                    sm.setScreen(SCREEN_UPLOAD);
                } catch (IOException ex) {
                    sm.displayErrorPage("Something went wrong.", SCREEN_PROFILE, "Go back");
                }
            });

            base.getChildren().addAll(text, link);
        } else {
            List<Node> hboxlist = new java.util.ArrayList<>(List.of());

            list.forEach(s -> {
                HBox left = new HBox();
                left.setSpacing(8);

                SVGPath svg = new SVGPath();
                svg.setContent(SVGContents.file());
                svg.getStyleClass().add("primary-light");
                svg.setFillRule(EVEN_ODD);
                Label title = new Label(s.getName());
                title.setMaxWidth(460);
                title.getStyleClass().addAll("label4", "primary-light");

                left.setAlignment(Pos.CENTER_LEFT);
                left.getChildren().addAll(svg, title);

                Button buttonL = new Button();
                buttonL.setGraphic(left);
                buttonL.setMinWidth(508);

                Button buttonR = new Button();

                SVGPath svgDel = new SVGPath();
                svgDel.setContent(SVGContents.delete());
                svgDel.getStyleClass().add("error");
                SVGContents.setScale(svgDel, 1.5);

                buttonR.setGraphic(svgDel);
                buttonR.setMinWidth(30);
                buttonR.setMaxWidth(30);
                buttonR.setMinHeight(30);

                buttonR.setOnAction(e -> {
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "This can not be undone.", ButtonType.YES, ButtonType.NO);
                    alert.setHeaderText("Are you sure you want to delete your material \"" + s.getName() + "\"?");
                    alert.setTitle("Deleting material \"" + s.getName() + "\"");

                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.YES) {
                            GUILogger.info("Deleting material \"" + s.getName() + "\"");
                        }
                    });
                });

                buttonL.setOnAction(e -> {
                    SceneManager sm = SceneManager.getInstance();
                    sm.displayMaterial(s.getMaterialId());
                });

                buttonL.setAlignment(Pos.CENTER_LEFT);
                buttonR.setAlignment(Pos.CENTER);
                buttonR.getStyleClass().add("buttonEmpty");
                buttonL.getStyleClass().add("buttonEmpty");

                hboxlist.add(new HBox(buttonL, buttonR));
            });

            base.getChildren().add(ListItem.toListView(hboxlist, 420));
        }

        return base;
    }

    public static VBox Courses(){
        User u = Session.getInstance().getCurrentUser();
        VBox base = new VBox(createTitle("My Courses", "secondary-light"));
        base.setMinWidth(560);
        base.setMaxWidth(560);

        CategoryRepository cRepo = new CategoryRepository();
        List<Category> list = cRepo.findCategoriesByUser(u);
        if (list.isEmpty()){
            Text text = new Text("You haven't created any courses yet!");
            Hyperlink link = new Hyperlink("Go create a new course");

            link.setOnAction(e -> {
                SceneManager sm = SceneManager.getInstance();
                try {
                    sm.setScreen(SCREEN_UPLOAD);
                } catch (IOException ex) {
                    sm.displayErrorPage("Something went wrong.", SCREEN_PROFILE, "Go back");
                }
            });

            base.getChildren().addAll(text, link);
        } else {
            List<Node> hboxlist = new java.util.ArrayList<>(List.of());

            list.forEach(c -> {
                HBox left = new HBox();
                left.setSpacing(8);

                SVGPath svg = new SVGPath();
                svg.setContent(SVGContents.school());
                svg.getStyleClass().add("secondary-light");
                svg.setFillRule(EVEN_ODD);
                Label title = new Label(c.getCategoryName());
                title.setMaxWidth(460);
                title.getStyleClass().addAll("label4", "secondary-light");

                left.setAlignment(Pos.CENTER_LEFT);
                left.getChildren().addAll(svg, title);

                Button buttonL = new Button();
                buttonL.setGraphic(left);
                buttonL.setMinWidth(508);

                SVGPath svgDel = new SVGPath();
                svgDel.setContent(SVGContents.delete());
                svgDel.getStyleClass().add("error");
                SVGContents.setScale(svgDel, 1.5);

                buttonL.setOnAction(e -> {
                    SceneManager sm = SceneManager.getInstance();
                    sm.displayCategory(c.getCategoryId());
                });

                buttonL.setAlignment(Pos.CENTER_LEFT);
                buttonL.getStyleClass().add("buttonEmpty");

                hboxlist.add(new HBox(buttonL));
            });

            base.getChildren().add(ListItem.toListView(hboxlist, 420));
        }

        return base;
    }

    public static VBox Reviews(){
        User u = Session.getInstance().getCurrentUser();

        VBox base = new VBox(createTitle("My Reviews", "warning"));

        base.setMinWidth(560);
        base.setMaxWidth(560);

        ReviewRepository rRepo = new ReviewRepository();
        List<Review> list = rRepo.findByUser(u);

        if (list.isEmpty()){
            Text text1 = new Text("You haven't reviewed any materials yet!");
            Text text2 = new Text("You can review a material from any material page that isn't yours.");

            base.getChildren().addAll(text1, text2);
        } else {
            List<Node> hboxlist = new java.util.ArrayList<>(List.of());

            list.forEach(r -> {
                HBox left = new HBox();
                left.setSpacing(8);

                SVGPath svg = new SVGPath();
                svg.setContent(SVGContents.file());
                svg.getStyleClass().add("warning");
                svg.setFillRule(EVEN_ODD);
                Label title = new Label(r.getStudyMaterial().getName());
                title.setMaxWidth(460);
                title.getStyleClass().addAll("label4", "warning");

                left.setAlignment(Pos.CENTER_LEFT);
                left.getChildren().addAll(svg, title);

                Button buttonL = new Button();
                buttonL.setGraphic(left);
                buttonL.setMinWidth(508);

                Button buttonR = new Button();

                SVGPath svgDel = new SVGPath();
                svgDel.setContent(SVGContents.delete());
                svgDel.getStyleClass().add("error");
                SVGContents.setScale(svgDel, 1.5);

                buttonR.setGraphic(svgDel);
                buttonR.setMinWidth(30);
                buttonR.setMaxWidth(30);
                buttonR.setMinHeight(30);

                buttonR.setOnAction(e ->
                    GUILogger.info("Delete button clicked on material review.")
                );

                buttonL.setOnAction(e -> {
                    SceneManager sm = SceneManager.getInstance();
                    sm.displayMaterial(r.getStudyMaterial().getMaterialId());
                });

                buttonL.setAlignment(Pos.CENTER_LEFT);
                buttonR.setAlignment(Pos.CENTER);
                buttonR.getStyleClass().add("buttonEmpty");
                buttonL.getStyleClass().add("buttonEmpty");
                hboxlist.add(new HBox(buttonL, buttonR));
            });

            base.getChildren().add(ListItem.toListView(hboxlist, 420));
        }

        return base;
    }

    public static VBox Settings(){
        VBox base = new VBox(createTitle("Profile Settings", "primary"));
        base.setMinWidth(600);

        Text text = new Text("Here the user can change their basic account settings (account deletion, password change ETC)");
        base.getChildren().addAll(text);

        return base;
    }

    private static Label createTitle(String text, String color) {
        Label label = new Label(text);
        label.getStyleClass().addAll("label3", color);
        return label;
    }

}