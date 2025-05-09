package presentation.controller;

import domain.model.Category;
import domain.model.StudyMaterial;
import domain.model.Tag;
import domain.service.SearchService;
import infrastructure.repository.CategoryRepository;
import infrastructure.repository.StudyMaterialRepository;
import infrastructure.repository.TagRepository;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.text.Text;
import presentation.components.ListItem;
import presentation.view.LanguageManager;
import presentation.view.SceneManager;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

import static presentation.utility.FXMLPageHandler.setUp;

public class SearchController implements PageController{

    private final SearchService searchService = new SearchService(
            new StudyMaterialRepository(),
            new CategoryRepository(),
            new TagRepository()
    );

    @FXML private CheckBox checkboxIncludeMaterials;
    @FXML private CheckBox checkboxIncludeCategories;
    @FXML private CheckBox checkboxIncludeTags;
    @FXML private Text searchPageTitle;

    @FXML private TextField searchField;
    @FXML private Button searchButton;
    
    @FXML private ListView<Button> resultsListView;
    @FXML private Text resultsDisplayLabel;

    ResourceBundle rb = LanguageManager.getInstance().getBundle();

    public void setPage(){
        setUp("/fxml/search.fxml");
    }

    @Override
    public String getPageName() {
        return "Search";
    }

    @FXML
    private void initialize() {
        searchPageTitle.setText(rb.getString("search"));

        checkboxIncludeMaterials.setText(rb.getString("materials"));
        checkboxIncludeCategories.setText(rb.getString("courses"));
        checkboxIncludeTags.setText(rb.getString("tags"));

        searchButton.setOnAction(e -> performSearch());
        searchButton.setText(rb.getString("search"));

        searchField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                performSearch();
            }
        });

        resultsListView.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Button button, boolean empty) {
                super.updateItem(button, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(button);
                }
            }
        });
    }

    @FXML private void performSearch() {
        String query = searchField.getText();
        if (!query.isEmpty()) {
            resultsListView.getItems().clear();

            if (checkboxIncludeMaterials.isSelected()) {
                List<StudyMaterial> materialResults = searchService.searchMaterials(query);
                materialResults.forEach(material -> resultsListView.getItems().add(ListItem.listItemGraphic(material)));
            }

            if (checkboxIncludeCategories.isSelected()) {
                List<Category> categoryResults = searchService.searchCategories(query);
                categoryResults.forEach(category ->
                        resultsListView.getItems().add(ListItem.listItemGraphic(category))
                );
            }

            if (checkboxIncludeTags.isSelected()) {
                List<Tag> tagResults = searchService.searchTags(query);
                tagResults.forEach(tag ->
                        resultsListView.getItems().add(ListItem.listItemGraphic(tag))
                );
            }
        }

        if (resultsListView.getItems().isEmpty()){
            resultsDisplayLabel.setText(String.format(rb.getString("noResults"), query));
        } else {
            int amount = resultsListView.getItems().size();
            resultsDisplayLabel.setText(amount == 1 ? rb.getString("oneResult") : String.format(rb.getString("results"), amount));
        }
    }
}
