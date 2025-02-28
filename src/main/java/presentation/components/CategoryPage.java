package presentation.components;

import domain.model.Category;
import domain.model.StudyMaterial;
import infrastructure.repository.CategoryRepository;
import javafx.geometry.Insets;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import presentation.GUILogger;
import presentation.view.SceneManager;

import java.util.List;
import java.util.Objects;

public class CategoryPage {
    public static void setPage(Category c){
        CategoryRepository repo = new CategoryRepository();

        VBox vbox = new VBox();

        vbox.getStylesheets().add(Objects.requireNonNull(CategoryPage.class.getResource("/css/style.css")).toExternalForm());
        vbox.setSpacing(12);
        vbox.setPadding(new Insets(20, 20, 20, 20));
        Text title = new Text(c.getCategoryName());
        title.getStyleClass().add("heading3");
        title.getStyleClass().add("secondary");

        Text author = new Text("Course by " + c.getCreator().getFullName());
        VBox header = new VBox();
        header.getChildren().addAll(title, author);

        vbox.getChildren().add(header);

        List<StudyMaterial> creatorMaterials = repo.findMaterialsByUserInCategory(c.getCreator(), c);
        if (!creatorMaterials.isEmpty()) {
            Text text = new Text("Materials from " + c.getCreator().getFullName());
            text.getStyleClass().add("heading4");
            text.getStyleClass().add("secondary");

            vbox.getChildren().addAll(
                    text,
                    MaterialCard.materialCardScrollHBox(creatorMaterials));
        }

        List<StudyMaterial> otherMaterials = repo.findMaterialsExceptUserInCategory(c.getCreator(), c);
        if (!otherMaterials.isEmpty()) {
            GUILogger.info(String.valueOf(otherMaterials.size()));
            Text text = new Text("Materials from others");
            text.getStyleClass().add("heading4");
            text.getStyleClass().add("secondary");

            vbox.getChildren().addAll(
                    text,
                    MaterialCard.materialCardScrollHBox(otherMaterials));
        }

        SceneManager.getInstance().setCenter(vbox);
    }
}