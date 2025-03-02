package presentation.components;

import domain.model.Category;
import domain.model.StudyMaterial;
import domain.model.User;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.SVGPath;
import javafx.util.Callback;
import presentation.GUILogger;
import presentation.view.SceneManager;

import java.io.IOException;
import java.util.List;

import static presentation.components.ItemType.*;

public class ListItem {
    public static Button listItemGraphic(StudyMaterial s){
        return layout(s.getName(), MATERIAL, s.getFileType() + ", uploaded by " + s.getUploader().getFullName(), s.getMaterialId());
    }

    public static Button listItemGraphic(Category c) {
        return layout(c.getCategoryName(), CATEGORY, "Course by " + c.getCreator().getFullName(), c.getCategoryId());
    }

    public static Button listItemGraphic(User u) {
        return layout(u.getFullName(), USER, "girl whatever", 21);
    }

    public static Button layout(String text, ItemType type, String info, int id){
        Button btn = new Button();
        VBox graphic = new VBox();
        HBox header = new HBox();

        String color;
        SVGPath svg = new SVGPath();

        SceneManager sm = SceneManager.getInstance();

        switch (type){
            case USER:
                svg.setContent(SVGContents.user());
                color = "error";
                break;
            case CATEGORY:
                svg.setContent(SVGContents.school());
                color = "secondary";
                btn.setOnAction(e -> {
                    sm.displayCategory(id);
                });
                break;
            case MATERIAL:
                svg.setContent(SVGContents.file());
                color = "primary-light";
                btn.setOnAction(e -> sm.displayMaterial(id));
                break;
            default:
                color = "error";
                btn.setOnAction(e -> GUILogger.warn("This button layout type is set wrong: button shouldn't exist"));
        }

        svg.setFillRule(FillRule.EVEN_ODD);
        svg.setStyle("-fx-scale-y: 1.2; -fx-scale-x: 1.2;");
        svg.getStyleClass().add(color);

        Label label = new Label(text);
        label.setText(text);
        label.getStyleClass().add("label4");
        label.getStyleClass().add(color);

        header.setSpacing(8);
        header.setAlignment(Pos.CENTER_LEFT);

        Label sub = new Label(info);
        header.getChildren().addAll(svg, label);

        graphic.getChildren().addAll(header, sub);

        btn.getStyleClass().add("listItemBtn");
        btn.setGraphic(graphic);

        return btn;
    }

    public static ListView<Node> toListView(List<Node> list) {
        return toListView(list, 200);
    }

    public static ListView<Node> toListView(List<Node> list, int height) {
        ListView<Node> view = new ListView<>();

        list.forEach(item -> {
            view.getItems().add(item);
        });

        view.setMaxHeight(height);

        view.setCellFactory(new Callback<ListView<Node>, ListCell<Node>>() {
                                @Override
                                public ListCell<Node> call(ListView<Node> listView) {
                                    return new ListCell<Node>() {
                                        @Override
                                        protected void updateItem(Node item, boolean empty) {
                                            super.updateItem(item, empty);
                                            if (empty || item == null) {
                                                setGraphic(null);
                                            } else {
                                                setGraphic(item);
                                                setPadding(new Insets(0, 0, 0, 0));
                                            }
                                        }
                                    };
                                }
                            });

        view.setMinHeight(height);
        view.setMaxHeight(height);
        return view;
    }
}
