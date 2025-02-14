package domain.service;

import domain.model.Category;
import domain.model.StudyMaterial;
import infrastructure.repository.CategoryRepository;

import java.util.List;

public class CategoryService {
    private final CategoryRepository repository;

    public CategoryService(CategoryRepository repository) {
        this.repository = repository;
    }

    public List<StudyMaterial> getMaterialsByCategory(Category category) {
        return repository.findMaterialsByCategory(category);
    }
}

