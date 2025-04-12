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
import presentation.utility.SVGContents;
import presentation.utility.StyleClasses;
import presentation.view.CurrentUserManager;
import presentation.view.LanguageManager;
import presentation.view.SceneManager;

import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

import static domain.model.RoleType.STUDENT;
import static presentation.view.Screen.SCREEN_COURSES;
import static presentation.view.Screen.SCREEN_FIND;

// HomeController is a controller class which controls the home page of the application.
public class HomeController {
    @FXML private VBox mainVBoxHome;

    private final CategoryRepository cRepository = new CategoryRepository();
    private final CategoryService categoryServ = new CategoryService(cRepository, new PermissionService());

    private final StudyMaterialRepository smRepository = new StudyMaterialRepository();

    ResourceBundle rb = LanguageManager.getInstance().getBundle();

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

        Text title = new Text(String.format(rb.getString("welcome"), CurrentUserManager.get().getFullName()));
        title.getStyleClass().addAll(StyleClasses.HEADING2, StyleClasses.ERROR);
        header.getChildren().add(title);

        mainVBoxHome.getChildren().addAll(header);
    }

    private void loadPendingApprovalScreen() {
        List<Category> pendingCategories = categoryServ.getOwnedCategoriesWithPending(CurrentUserManager.get());

        if (!pendingCategories.isEmpty()){
            VBox vbox = new VBox();

            Text title = new Text(rb.getString("pendingMaterialsInfo"));
            title.getStyleClass().addAll(StyleClasses.HEADING3, StyleClasses.PRIMARY);

            for (Category category : pendingCategories){
                Button btn = new Button();
                HBox graphic = new HBox();

                SVGPath svg = new SVGPath();

                SceneManager sm = SceneManager.getInstance();
                svg.setContent(SVGContents.SCHOOL);
                btn.setOnAction(e -> sm.displayCategory(category.getCategoryId()));
                SVGContents.setScale(svg, 1.2);
                svg.getStyleClass().add(StyleClasses.SECONDARY);

                Label label = new Label(category.getCategoryName());
                label.getStyleClass().addAll(StyleClasses.LABEL4, StyleClasses.SECONDARY);

                graphic.setSpacing(8);
                graphic.setAlignment(Pos.CENTER_LEFT);

                int amount = cRepository.findPendingMaterialsByCategory(category).size();
                Label sub = new Label(amount > 1 ? String.format(rb.getString("pendingAmountPlural"), amount) : rb.getString("pendingAmountSingular"));

                graphic.getChildren().addAll(svg, label, sub);

                btn.getStyleClass().add(StyleClasses.BUTTON_EMPTY);
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

            Text title = new Text(rb.getString("latestMaterialsLabel"));
            title.getStyleClass().addAll(StyleClasses.HEADING3, StyleClasses.PRIMARY_LIGHT);

            vbox.getChildren().addAll(title, MaterialCard.materialCardScrollHBox(list));
            vbox.setSpacing(10);

            mainVBoxHome.getChildren().addAll(vbox);
        }
    }

    private void loadBestReviewedMaterials() {
        List<StudyMaterial> list = smRepository.findBestReviewedMaterials(10);
        if (!list.isEmpty()) {
            VBox vbox = new VBox();

            Text title = new Text(rb.getString("topRatedMaterialsLabel"));
            title.getStyleClass().addAll(StyleClasses.HEADING3, StyleClasses.PRIMARY);

            vbox.getChildren().addAll(title, MaterialCard.materialCardScrollHBox(list));
            vbox.setSpacing(10);

            mainVBoxHome.getChildren().addAll(vbox);
        }
    }

    private void loadRecentlyReviewedMaterials() {
        List<StudyMaterial> list = smRepository.findReviewedMaterialsByUserLatest10(CurrentUserManager.get());
        if (!list.isEmpty()) {
            VBox vbox = new VBox();

            Text title = new Text(rb.getString("recentlyReviewedMaterialsLabel"));
            title.getStyleClass().addAll(StyleClasses.HEADING3, StyleClasses.PRIMARY_LIGHT);

            vbox.getChildren().addAll(title, MaterialCard.materialCardScrollHBox(list));
            vbox.setSpacing(10);

            mainVBoxHome.getChildren().addAll(vbox);
        }
    }

    private void loadSearchSuggestionBox() {
        HBox searchSuggestion = new HBox();

        SVGPath searchSvg = new SVGPath();
        searchSvg.setContent(SVGContents.SEARCH);
        SVGContents.setScale(searchSvg, 1.3);
        searchSvg.getStyleClass().add(StyleClasses.PRIMARY);

        Text text = new Text(rb.getString("somethingElse"));
        text.getStyleClass().addAll(StyleClasses.HEADING3, StyleClasses.PRIMARY);

        Button button = new Button(rb.getString("redirectSearch"));
        button.setOnAction(e -> {
            SceneManager sm = SceneManager.getInstance();
            sm.setScreen(SCREEN_FIND);
        });
        button.getStyleClass().add(StyleClasses.BTN_S);

        searchSuggestion.getChildren().addAll(searchSvg, text, button);
        searchSuggestion.setAlignment(Pos.CENTER_LEFT);
        searchSuggestion.setSpacing(8);
        mainVBoxHome.getChildren().add(searchSuggestion);
    }
}