package presentation.controller;

import domain.model.Category;
import domain.model.StudyMaterial;
import domain.model.Tag;
import domain.service.SearchService;
import infrastructure.repository.CategoryRepository;
import infrastructure.repository.StudyMaterialRepository;
import infrastructure.repository.TagRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import presentation.components.ListItem;

import java.util.List;

public class SearchController {

    private final SearchService searchService = new SearchService(
            new StudyMaterialRepository(),
            new CategoryRepository(),
            new TagRepository()
    );
    public CheckBox checkbox_includeMaterials;
    public CheckBox checkbox_includeCategories;
    public CheckBox checkbox_includeTags;

    @FXML private TextField searchField;
    @FXML private Button searchButton;
    
    @FXML private ListView<Button> resultsListView;

    @FXML
    private void initialize() {
        searchButton.setOnAction(e -> performSearch());
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

    private void performSearch() {
        String query = searchField.getText();
        if (!query.isEmpty()) {
            resultsListView.getItems().clear();

            if (checkbox_includeMaterials.isSelected()) {
                List<StudyMaterial> materialResults = searchService.searchMaterials(query);
                materialResults.forEach(material -> resultsListView.getItems().add(ListItem.listItemGraphic(material)));
            }

            if (checkbox_includeCategories.isSelected()) {
                List<Category> categoryResults = searchService.searchCategories(query);
                categoryResults.forEach(category ->
                        resultsListView.getItems().add(ListItem.listItemGraphic(category))
                );
            }

            if (checkbox_includeTags.isSelected()) {
                List<Tag> tagResults = searchService.searchTags(query);
                tagResults.forEach(tag ->
                        resultsListView.getItems().add(ListItem.listItemGraphic(tag))
                );
            }
        }
    }
}
