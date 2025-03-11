package domain.service;

import domain.model.Category;
import domain.model.StudyMaterial;
import domain.model.Tag;
import infrastructure.repository.CategoryRepository;
import infrastructure.repository.StudyMaterialRepository;
import infrastructure.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SearchServiceTest {

    @Mock
    private StudyMaterialRepository materialRepository;

    @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private TagRepository tagRepository;

    private SearchService searchService;

    @BeforeEach
    void setUp() {
        // Test the constructor by creating a new instance
        searchService = new SearchService(materialRepository, categoryRepository, tagRepository);

        // Verify the instance was created successfully
        assertNotNull(searchService, "SearchService should be initialized");
    }

    @Test
    void searchMaterials() {
        String query = "java";
        StudyMaterial material1 = mock(StudyMaterial.class);
        StudyMaterial material2 = mock(StudyMaterial.class);
        List<StudyMaterial> expectedMaterials = Arrays.asList(material1, material2);

        when(materialRepository.findByNameOrDescription(query))
                .thenReturn(expectedMaterials);

        List<StudyMaterial> result = searchService.searchMaterials(query);

        assertEquals(expectedMaterials, result, "Should return materials from repository");
        verify(materialRepository).findByNameOrDescription(query);
    }

    @Test
    void searchCategories() {
        String query = "programming";
        Category category1 = mock(Category.class);
        Category category2 = mock(Category.class);
        List<Category> expectedCategories = Arrays.asList(category1, category2);

        when(categoryRepository.findByName(query))
                .thenReturn(expectedCategories);

        List<Category> result = searchService.searchCategories(query);

        assertEquals(expectedCategories, result, "Should return categories from repository");
        verify(categoryRepository).findByName(query);
    }

    @Test
    void searchTags() {
        String query = "javascript";
        Tag tag1 = mock(Tag.class);
        Tag tag2 = mock(Tag.class);
        List<Tag> expectedTags = Arrays.asList(tag1, tag2);

        when(tagRepository.searchByName(query))
                .thenReturn(expectedTags);

        List<Tag> result = searchService.searchTags(query);

        assertEquals(expectedTags, result, "Should return tags from repository");
        verify(tagRepository).searchByName(query);
    }

    @Test
    void searchMaterials_EmptyResults() {

        String query = "nonexistent";
        List<StudyMaterial> emptyList = List.of();

        when(materialRepository.findByNameOrDescription(query))
                .thenReturn(emptyList);

        List<StudyMaterial> result = searchService.searchMaterials(query);

        assertEquals(emptyList, result, "Should return empty list when no materials found");
        verify(materialRepository).findByNameOrDescription(query);
    }

    @Test
    void searchCategories_EmptyResults() {
        String query = "nonexistent";
        List<Category> emptyList = List.of();

        when(categoryRepository.findByName(query))
                .thenReturn(emptyList);

        List<Category> result = searchService.searchCategories(query);

        assertEquals(emptyList, result, "Should return empty list when no categories found");
        verify(categoryRepository).findByName(query);
    }

    @Test
    void searchTags_EmptyResults() {
        String query = "nonexistent";
        List<Tag> emptyList = List.of();

        when(tagRepository.searchByName(query))
                .thenReturn(emptyList);

        List<Tag> result = searchService.searchTags(query);

        assertEquals(emptyList, result, "Should return empty list when no tags found");
        verify(tagRepository).searchByName(query);
    }
}
