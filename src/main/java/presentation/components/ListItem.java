package presentation.components;

import domain.model.Category;
import domain.model.StudyMaterial;
import domain.model.User;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.SVGPath;
import presentation.logger.GUILogger;
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
                svg.setContent("M10 4.5a2 2 0 1 1-4 0a2 2 0 0 1 4 0m1.5 0a3.5 3.5 0 1 1-7 0a3.5 3.5 0 0 1 7 0m-9 8c0-.204.22-.809 1.32-1.459C4.838 10.44 6.32 10 8 10s3.162.44 4.18 1.041c1.1.65 1.32 1.255 1.32 1.459a1 1 0 0 1-1 1h-9a1 1 0 0 1-1-1m5.5-4c-3.85 0-7 2-7 4A2.5 2.5 0 0 0 3.5 15h9a2.5 2.5 0 0 0 2.5-2.5c0-2-3.15-4-7-4");
                color = "error";
                break;
            case CATEGORY:
                svg.setContent("M6.836 3.202L1.74 5.386a.396.396 0 0 0 0 .728l5.096 2.184a2.5 2.5 0 0 0 .985.202h.358a2.5 2.5 0 0 0 .985-.202l5.096-2.184a.396.396 0 0 0 0-.728L9.164 3.202A2.5 2.5 0 0 0 8.179 3h-.358a2.5 2.5 0 0 0-.985.202M1.5 7.642l1.5.644v3.228a2 2 0 0 0 1.106 1.789l.806.403a7 7 0 0 0 6.193.033l.909-.442a2 2 0 0 0 1.125-1.798V8.226l1.712-.734a1.896 1.896 0 0 0 0-3.484L9.755 1.823A4 4 0 0 0 8.179 1.5h-.358a4 4 0 0 0-1.576.323L1.15 4.008A1.9 1.9 0 0 0 0 5.75v4.5a.75.75 0 0 0 1.5 0zm3 3.872V8.929l1.745.748A4 4 0 0 0 7.821 10h.358a4 4 0 0 0 1.576-.323l1.884-.808v2.63a.5.5 0 0 1-.282.45l-.909.442a5.5 5.5 0 0 1-4.865-.027l-.807-.403a.5.5 0 0 1-.276-.447");
                color = "secondary";
                btn.setOnAction(e -> {
                    try {
                        sm.displayCategory(id);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });
                break;
            case MATERIAL:
                svg.setContent("M4.5 3h7A1.5 1.5 0 0 1 13 4.5v7a1.5 1.5 0 0 1-1.5 1.5h-7A1.5 1.5 0 0 1 3 11.5v-7A1.5 1.5 0 0 1 4.5 3m-3 1.5a3 3 0 0 1 3-3h7a3 3 0 0 1 3 3v7a3 3 0 0 1-3 3h-7a3 3 0 0 1-3-3zm3.75 5.498a.75.75 0 0 0 0 1.5h5.502a.75.75 0 0 0 0-1.5zM4.5 8a.75.75 0 0 1 .75-.75h5.502a.75.75 0 0 1 0 1.5H5.25A.75.75 0 0 1 4.5 8m.75-3.498a.75.75 0 0 0 0 1.5h5.502a.75.75 0 0 0 0-1.5z");
                color = "primary-light";
                btn.setOnAction(e -> {
                    try {
                        sm.displayMaterial(id);
                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                });
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

    public static ListView<Button> toListView(List<Button> list) {
        return toListView(list, 200);
    }

    public static ListView<Button> toListView(List<Button> list, int height) {
        ListView<Button> view = new ListView<>();

        list.forEach(btn -> {
            view.getItems().add(btn);
        });

        view.setMaxHeight(height);
        return view;
    }
}
