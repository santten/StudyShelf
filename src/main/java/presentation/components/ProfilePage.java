package presentation.components;

import domain.model.Category;
import domain.model.StudyMaterial;
import domain.model.User;
import infrastructure.repository.CategoryRepository;
import infrastructure.repository.StudyMaterialRepository;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import presentation.view.SceneManager;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static presentation.components.ListItem.listItemGraphic;

public class ProfilePage {
    public static void setPage(User u){
        VBox base = new VBox();
        base.getStylesheets().add(Objects.requireNonNull(ProfilePage.class.getResource("/css/style.css")).toExternalForm());
        base.setSpacing(12);
        base.setPadding(new Insets(20, 20, 20, 20));

        Label title = new Label(u.getFullName());
        title.getStyleClass().add("label3");
        title.getStyleClass().add("error");
        title.setWrapText(true);
        title.setMaxWidth(600);

        base.getChildren().addAll(title, TextLabels.getUserRoleLabel(u));

        CategoryRepository cRepo = new CategoryRepository();
        List<Category> userCategories = cRepo.findCategoriesByUser(u);

        if (!userCategories.isEmpty()) {
            Label cTitle = new Label("Courses");
            cTitle.getStyleClass().add("label4");
            cTitle.getStyleClass().add("secondary");

            List<Category> categoryList = cRepo.findCategoriesByUser(u);
            List<Node> buttonList = new ArrayList<>();

            categoryList.forEach(c -> buttonList.add(listItemGraphic(c)));
            ListView<Node> cView = ListItem.toListView(buttonList);

            base.getChildren().addAll(cTitle, cView);
        }

        StudyMaterialRepository sRepo = new StudyMaterialRepository();
        List<StudyMaterial> userMaterials = sRepo.findByUser(u);

        if (!userMaterials.isEmpty()) {
            Label mTitle = new Label("Materials");
            mTitle.getStyleClass().add("label4");
            mTitle.getStyleClass().add("primary-light");

            List<Node> buttonList = new ArrayList<>();

            userMaterials.forEach(m -> buttonList.add(listItemGraphic(m)));
            ListView<Node> mView = ListItem.toListView(buttonList);

            base.getChildren().addAll(mTitle, mView);
        }

        SceneManager.getInstance().setCenter(base);
    }
}
