package domain.service;

import domain.model.Category;
import domain.model.StudyMaterial;
import infrastructure.repository.CategoryRepository;
import infrastructure.repository.StudyMaterialRepository;

import java.util.List;

public class SearchService {
    private final StudyMaterialRepository materialRepository;
    private final CategoryRepository categoryRepository;

    public SearchService(StudyMaterialRepository materialRepository, CategoryRepository categoryRepository) {
        this.materialRepository = materialRepository;
        this.categoryRepository = categoryRepository;
    }

    public List<StudyMaterial> searchMaterials(String query) {
        return materialRepository.findByNameOrDescription(query);
    }

    public List<Category> searchCategories(String query) {
        return categoryRepository.findByName(query);
    }
}
