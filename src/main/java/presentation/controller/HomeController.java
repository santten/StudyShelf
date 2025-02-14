package presentation.controller;

import domain.model.Category;
import domain.model.StudyMaterial;
import domain.service.CategoryService;
import infrastructure.repository.CategoryRepository;
import javafx.fxml.FXML;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;

import java.util.List;

public class HomeController {
    private CategoryRepository categoryRepo = new CategoryRepository();

    @FXML
    private ListView<Category> categoryList;
    @FXML
    private ListView<StudyMaterial> materialList;

    @FXML
    private void initialize() {
        List<Category> categories = categoryRepo.findAll();
        System.out.println("Loading categories: " + categories.size());
        categoryList.getItems().addAll(categories);

        categoryList.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                List<StudyMaterial> materials = newVal.getMaterials();
                System.out.println("Loading materials for category: " + materials.size());
                materialList.getItems().setAll(materials);
            }
        });

        categoryList.setCellFactory(lv -> new ListCell<Category>() {
            @Override
            protected void updateItem(Category category, boolean empty) {
                super.updateItem(category, empty);
                setText(empty ? null : category.getCategoryName());
            }
        });

        materialList.setCellFactory(lv -> new ListCell<StudyMaterial>() {
            @Override
            protected void updateItem(StudyMaterial material, boolean empty) {
                super.updateItem(material, empty);
                setText(empty ? null : material.getName());
            }
        });
    }

}
