package presentation.controller;

import domain.model.Category;
import domain.model.StudyMaterial;
import domain.model.User;
import infrastructure.repository.CategoryRepository;
import infrastructure.repository.StudyMaterialRepository;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import presentation.components.ListItem;
import presentation.components.TextLabels;
import presentation.utility.StyleClasses;
import presentation.view.LanguageManager;
import presentation.view.SceneManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import static presentation.components.ListItem.listItemGraphic;

public class ProfilePageController {
    public static final ResourceBundle rb = LanguageManager.getInstance().getBundle();

    private ProfilePageController(){}

    public static void setPage(User u){
        VBox base = new VBox();
        base.getStylesheets().add(Objects.requireNonNull(ProfilePageController.class.getResource("/css/style.css")).toExternalForm());
        base.setSpacing(12);
        base.setPadding(new Insets(20, 20, 20, 20));

        Label title = new Label(u.getFullName());
        title.getStyleClass().addAll(StyleClasses.LABEL3, StyleClasses.ERROR);
        title.setWrapText(true);
        title.setMaxWidth(600);

        base.getChildren().addAll(title, TextLabels.getUserRoleLabel(u));

        CategoryRepository cRepo = new CategoryRepository();
        List<Category> userCategories = cRepo.findCategoriesByUser(u);

        if (!userCategories.isEmpty()) {
            Label cTitle = new Label(rb.getString("courses"));
            cTitle.getStyleClass().addAll(StyleClasses.LABEL4, StyleClasses.SECONDARY);

            List<Category> categoryList = cRepo.findCategoriesByUser(u);
            List<Node> buttonList = new ArrayList<>();

            categoryList.forEach(c -> buttonList.add(listItemGraphic(c)));
            ListView<Node> cView = ListItem.toListView(buttonList);

            base.getChildren().addAll(cTitle, cView);
        }

        StudyMaterialRepository sRepo = new StudyMaterialRepository();
        List<StudyMaterial> userMaterials = sRepo.findByUser(u);

        if (!userMaterials.isEmpty()) {
            Label mTitle = new Label(rb.getString("materials"));
            mTitle.getStyleClass().addAll(StyleClasses.PRIMARY_LIGHT, StyleClasses.LABEL4);

            List<Node> buttonList = new ArrayList<>();

            userMaterials.forEach(m -> buttonList.add(listItemGraphic(m)));
            ListView<Node> mView = ListItem.toListView(buttonList);

            base.getChildren().addAll(mTitle, mView);
        }

        ScrollPane wrapper = new ScrollPane(base);
        wrapper.setFitToWidth(true);
        SceneManager.getInstance().setCenter(wrapper);
    }
}
