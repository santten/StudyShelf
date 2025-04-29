package presentation.controller;

import domain.model.StudyMaterial;
import domain.model.Tag;
import infrastructure.repository.StudyMaterialRepository;
import infrastructure.repository.TagRepository;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import presentation.components.BreadCrumb;
import presentation.components.ListItem;
import presentation.utility.StyleClasses;
import presentation.view.SceneManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TaggedPageController implements PageController {
    private final Tag tag;

    public TaggedPageController(Tag tag){
        this.tag = tag;
    }

    @Override
    public void setPage() {
        TagRepository repo = new TagRepository();
        Tag tag = repo.findById(this.tag.getTagId());

        VBox vbox = new VBox();
        vbox.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());
        vbox.setPadding(new Insets(20, 20, 20, 20));
        vbox.setSpacing(12);

        vbox.getChildren().add(new BreadCrumb().makeBreadCrumb());

        Label title = new Label("Materials tagged \"" + tag.getTagName() + "\"");
        title.getStyleClass().addAll(StyleClasses.LABEL3, StyleClasses.PRIMARY_LIGHT);
        vbox.getChildren().add(title);

        StudyMaterialRepository sRepo = new StudyMaterialRepository();
        List<StudyMaterial> list = sRepo.findByTag(tag);
        if (!list.isEmpty()){
            List<Node> buttonList = new ArrayList<>();
            list.forEach(sm -> buttonList.add(ListItem.listItemGraphic(sm)));
            vbox.getChildren().add(ListItem.toListView(buttonList));
        } else {
            vbox.getChildren().add(new Text("No materials exist with this tag yet!"));
        }

        SceneManager.SceneManagerHolder.instance.current.setCenter(vbox);
    }

    @Override
    public String getPageName() {
        return "#" + this.tag.getTagName();
    }
}
