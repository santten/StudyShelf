package presentation.controller;

import domain.model.Category;
import domain.model.StudyMaterial;
import domain.service.CategoryService;
import domain.service.PermissionService;
import infrastructure.repository.CategoryRepository;
import infrastructure.repository.StudyMaterialRepository;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import presentation.components.MaterialCard;
import presentation.view.CurrentUserManager;
import presentation.utility.SVGContents;
import presentation.view.SceneManager;

import java.io.IOException;
import java.util.List;

import static domain.model.RoleType.STUDENT;
import static presentation.view.Screen.SCREEN_COURSES;
import static presentation.view.Screen.SCREEN_FIND;

// HomeController is a controller class which controls the home page of the application.
public class HomeController {
    @FXML private VBox mainVBoxHome;

    private final CategoryRepository cRepository = new CategoryRepository();
    private final CategoryService categoryServ = new CategoryService(cRepository, new PermissionService());

    private final StudyMaterialRepository smRepository = new StudyMaterialRepository();

    @FXML
    private void initialize() {
        mainVBoxHome.setSpacing(10);

        loadHeader();
        if (CurrentUserManager.get().getRole().getName() != STUDENT){
            loadPendingApprovalScreen();
        }
        loadLatestMaterials();
        loadBestReviewedMaterials();
        loadRecentlyReviewedMaterials();
        loadSearchSuggestionBox();
    }

    private void loadHeader() {
        HBox header = new HBox();

        Text title = new Text("Welcome, " + CurrentUserManager.get().getFullName() + "!");
        title.getStyleClass().addAll("heading2", "error");
        header.getChildren().add(title);

        mainVBoxHome.getChildren().addAll(header);
    }

    private void loadPendingApprovalScreen() {
        List<Category> pendingCategories = categoryServ.getOwnedCategoriesWithPending(CurrentUserManager.get());

        if (!pendingCategories.isEmpty()){
            VBox vbox = new VBox();

            Text title = new Text("You have courses with pending materials");
            title.getStyleClass().addAll("heading3", "primary");

            for (Category category : pendingCategories){
                Button btn = new Button();
                HBox graphic = new HBox();

                SVGPath svg = new SVGPath();

                SceneManager sm = SceneManager.getInstance();
                svg.setContent(SVGContents.school());
                btn.setOnAction(e -> sm.displayCategory(category.getCategoryId()));
                SVGContents.setScale(svg, 1.2);
                svg.getStyleClass().add("secondary");

                Label label = new Label(category.getCategoryName());
                label.getStyleClass().add("label4");
                label.getStyleClass().add("secondary");

                graphic.setSpacing(8);
                graphic.setAlignment(Pos.CENTER_LEFT);

                int amount = cRepository.findPendingMaterialsByCategory(category).size();
                Label sub = new Label(amount + " material" + (amount > 1 ? "s" : "") +  " waiting for approval");

                graphic.getChildren().addAll(svg, label, sub);

                btn.getStyleClass().add("buttonEmpty");
                btn.setGraphic(graphic);

                vbox.getChildren().add(btn);
            }

            mainVBoxHome.getChildren().addAll(title, vbox);
        }
    }

    private void loadLatestMaterials() {
        List<StudyMaterial> list = smRepository.findLatestWithLimit(10);
        if (!list.isEmpty()) {
            VBox vbox = new VBox();

            Text title = new Text("Newest Materials in StudyShelf");
            title.getStyleClass().addAll("heading3", "primary-light");

            vbox.getChildren().addAll(title, MaterialCard.materialCardScrollHBox(list));
            vbox.setSpacing(10);

            mainVBoxHome.getChildren().addAll(vbox);
        }
    }

    private void loadBestReviewedMaterials() {
        List<StudyMaterial> list = smRepository.findBestReviewedMaterials(10);
        if (!list.isEmpty()) {
            VBox vbox = new VBox();

            Text title = new Text("Top Rated Materials in StudyShelf");
            title.getStyleClass().addAll("heading3", "primary");

            vbox.getChildren().addAll(title, MaterialCard.materialCardScrollHBox(list));
            vbox.setSpacing(10);

            mainVBoxHome.getChildren().addAll(vbox);
        }
    }

    private void loadRecentlyReviewedMaterials() {
        List<StudyMaterial> list = smRepository.findReviewedMaterialsByUserLatest10(CurrentUserManager.get());
        if (!list.isEmpty()) {
            VBox vbox = new VBox();

            Text title = new Text("Materials Reviewed by You Recently");
            title.getStyleClass().addAll("heading3", "primary-light");

            vbox.getChildren().addAll(title, MaterialCard.materialCardScrollHBox(list));
            vbox.setSpacing(10);

            mainVBoxHome.getChildren().addAll(vbox);
        }
    }

    private void loadSearchSuggestionBox() {
        HBox searchSuggestion = new HBox();

        SVGPath searchSvg = new SVGPath();
        searchSvg.setContent(SVGContents.search());
        SVGContents.setScale(searchSvg, 1.3);
        searchSvg.getStyleClass().add("primary");

        Text text = new Text("Looking for something else?");
        text.getStyleClass().addAll("heading3", "primary");

        Button button = new Button("Go to Search");
        button.setOnAction(e -> {
            SceneManager sm = SceneManager.getInstance();
            try {
                sm.setScreen(SCREEN_FIND);
            } catch (IOException ex) {
                sm.displayErrorPage("Couldn't go to search screen...", SCREEN_COURSES, "Go Home");
            }
        });
        button.getStyleClass().addAll("btnS");

        searchSuggestion.getChildren().addAll(searchSvg, text, button);
        searchSuggestion.setAlignment(Pos.CENTER_LEFT);
        searchSuggestion.setSpacing(8);
        mainVBoxHome.getChildren().add(searchSuggestion);
    }
}