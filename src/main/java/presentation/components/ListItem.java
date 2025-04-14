package presentation.components;

import domain.model.Category;
import domain.model.StudyMaterial;
import domain.model.Tag;
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
import presentation.utility.GUILogger;
import presentation.utility.SVGContents;
import presentation.utility.StyleClasses;
import presentation.view.LanguageManager;
import presentation.view.SceneManager;

import java.util.List;
import java.util.ResourceBundle;

import static presentation.components.ItemType.*;

public class ListItem {
    public static final ResourceBundle rb = LanguageManager.getInstance().getBundle();

    private ListItem(){}

    public static Button listItemGraphic(StudyMaterial s){
        return layout(s.getName(), MATERIAL, String.format(rb.getString("fileUploadedByName"), s.getFileType(), s.getUploader().getFullName()), s.getMaterialId());
    }

    public static Button listItemGraphic(Category c) {
        return layout(c.getCategoryName(), CATEGORY, String.format(rb.getString("courseBy"), c.getCreator().getFullName()), c.getCategoryId());
    }

    public static Button listItemGraphic(User u) {
        return layout(u.getFullName(), USER, u.getRole().getName().toString(), u.getUserId());
    }

    public static Button listItemGraphic(Tag t) {
        String text = t.getMaterials().size() > 1 ? String.format(rb.getString("tagContainsPluralMaterials"), t.getMaterials().size()) : rb.getString("tagContainsSingularMaterial");
        return layout(t.getTagName(), TAG, text, t.getTagId());
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
                svg.setContent(SVGContents.USER);
                color = StyleClasses.ERROR;
                break;
            case CATEGORY:
                svg.setContent(SVGContents.SCHOOL);
                color = StyleClasses.SECONDARY;
                btn.setOnAction(e -> sm.displayCategory(id));
                break;
            case MATERIAL:
                svg.setContent(SVGContents.FILE);
                color = StyleClasses.PRIMARY_LIGHT;
                btn.setOnAction(e -> sm.displayMaterial(id));
                break;
            case TAG:
                svg.setContent(SVGContents.TAG);
                color = StyleClasses.PRIMARY;
                btn.setOnAction(e -> sm.showMaterialsWithTag(id));
                break;
            default:
                color = StyleClasses.ERROR;
                btn.setOnAction(e -> GUILogger.warn(rb.getString("error.nonExistentButton")));
        }

        svg.setFillRule(FillRule.EVEN_ODD);
        svg.setStyle("-fx-scale-y: 1.2; -fx-scale-x: 1.2;");
        svg.getStyleClass().add(color);

        Label label = new Label(text);
        label.setText(text);
        label.getStyleClass().add(StyleClasses.LABEL4);
        label.getStyleClass().add(color);

        header.setSpacing(8);
        header.setAlignment(Pos.CENTER_LEFT);

        Label sub = new Label(info);
        header.getChildren().addAll(svg, label);

        graphic.getChildren().addAll(header, sub);

        btn.getStyleClass().add(StyleClasses.LIST_ITEM_BTN);
        btn.setGraphic(graphic);

        return btn;
    }

    public static ListView<Node> toListView(List<Node> list) {
        return toListView(list, 200);
    }

    public static ListView<Node> toListView(List<Node> list, int height) {
        ListView<Node> view = new ListView<>();

        list.forEach(item -> view.getItems().add(item));

        view.setMaxHeight(height);

        view.setCellFactory(listView -> new ListCell<>() {
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
        });

        view.setMinHeight(height);
        view.setMaxHeight(height);
        return view;
    }
}
