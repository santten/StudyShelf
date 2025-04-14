package domain.service;

import domain.model.Category;
import domain.model.StudyMaterial;
import domain.model.Tag;
import infrastructure.repository.CategoryRepository;
import infrastructure.repository.StudyMaterialRepository;
import infrastructure.repository.TagRepository;

import java.util.List;

/**
 * Service class that provides search functionality across
 * study materials, categories, and tags.
 */
public class SearchService {
    private final StudyMaterialRepository materialRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;

    /**
     * Constructs the SearchService with required repositories.
     *
     * @param materialRepository Repository for study materials
     * @param categoryRepository Repository for categories
     * @param tagRepository      Repository for tags
     */
    public SearchService(StudyMaterialRepository materialRepository, CategoryRepository categoryRepository, TagRepository tagRepository) {
        this.materialRepository = materialRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;

    }

    /**
     * Searches for study materials by matching query in name or description.
     *
     * @param query Search keyword
     * @return List of matched StudyMaterial objects
     */
    public List<StudyMaterial> searchMaterials(String query) {
        return materialRepository.findByNameOrDescription(query);
    }

    /**
     * Searches for categories matching the query string.
     *
     * @param query Search keyword
     * @return List of matched Category objects
     */
    public List<Category> searchCategories(String query) {
        return categoryRepository.findByName(query);
    }

    /**
     * Searches for tags whose names match the query string.
     *
     * @param query Search keyword
     * @return List of matched Tag objects
     */
    public List<Tag> searchTags(String query) {
        return tagRepository.searchByName(query);
    }

}
