package domain.service;

import domain.model.Category;
import domain.model.StudyMaterial;
import domain.model.Tag;
import infrastructure.repository.CategoryRepository;
import infrastructure.repository.StudyMaterialRepository;
import infrastructure.repository.TagRepository;

import java.util.List;

public class SearchService {
    private final StudyMaterialRepository materialRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    public SearchService(StudyMaterialRepository materialRepository, CategoryRepository categoryRepository, TagRepository tagRepository) {
        this.materialRepository = materialRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;

    }

    public List<StudyMaterial> searchMaterials(String query) {
        return materialRepository.findByNameOrDescription(query);
    }

    public List<Category> searchCategories(String query) {
        return categoryRepository.findByName(query);
    }

    public List<Tag> searchTags(String query) {
        return tagRepository.searchByName(query);
    }

}
