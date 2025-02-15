package presentation.controller;

import domain.model.Category;
import domain.model.StudyMaterial;
import domain.service.SearchService;
import infrastructure.repository.CategoryRepository;
import infrastructure.repository.StudyMaterialRepository;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import java.util.List;

public class SearchController {

    private final SearchService searchService = new SearchService(
            new StudyMaterialRepository(),
            new CategoryRepository()
    );

    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private CheckBox categoryOnlyCheckbox;
    @FXML private ListView<StudyMaterial> resultsListView;

    @FXML
    private void initialize() {
        searchButton.setOnAction(e -> performSearch());
        searchField.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ENTER) {
                performSearch();
            }
        });

        resultsListView.setCellFactory(lv -> new ListCell<StudyMaterial>() {
            @Override
            protected void updateItem(StudyMaterial material, boolean empty) {
                super.updateItem(material, empty);
                setText(empty ? null : material.getName());
            }
        });
    }

    private void performSearch() {
        String query = searchField.getText();
        if (!query.isEmpty()) {
            resultsListView.getItems().clear();

            if (categoryOnlyCheckbox.isSelected()) {
                // Search only from category
                List<Category> categoryResults = searchService.searchCategories(query);
                categoryResults.forEach(category ->
                        resultsListView.getItems().addAll(category.getMaterials())
                );
            } else {
                List<StudyMaterial> materialResults = searchService.searchMaterials(query);
                resultsListView.getItems().addAll(materialResults);
            }
        }
    }
}
