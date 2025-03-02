package domain.service;

import domain.model.Category;
import domain.model.StudyMaterial;
import infrastructure.repository.CategoryRepository;
import domain.model.PermissionType;
import domain.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CategoryService {
    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);
    private final CategoryRepository repository;
    private final PermissionService permissionService;

    public CategoryService(CategoryRepository repository, PermissionService permissionService) {
        this.repository = repository;
        this.permissionService = permissionService;
    }

    public List<StudyMaterial> getMaterialsByCategory(Category category) {
        return repository.findMaterialsByCategory(category);
    }

    // CREATE_CATEGORY
    public Category createCategory(User user, Category category) {
        if (!permissionService.hasPermission(user, PermissionType.CREATE_CATEGORY)) {
            throw new SecurityException("You do not have permission to create a category.");
        }

        logger.info("User {} created a new category: {}", user.getEmail(), category.getCategoryName());
        return repository.save(category);
    }

    public Category updateCategory(User user, Category updatedCategory) {
        Category existingCategory = repository.findById(updatedCategory.getCategoryId());
        if (existingCategory == null) {
            throw new RuntimeException("Category not found");
        }

        // UPDATE_COURSE_CATEGORY
        boolean isCourseOwner = user.equals(existingCategory.getCreator());
        boolean canUpdateCourseCategory = isCourseOwner && permissionService.hasPermission(user, PermissionType.UPDATE_COURSE_CATEGORY);
        // UPDATE_ANY_CATEGORY
        boolean canUpdateAnyCategory = permissionService.hasPermission(user, PermissionType.UPDATE_ANY_CATEGORY);

        if (!(canUpdateCourseCategory || canUpdateAnyCategory)) {
            throw new SecurityException("You do not have permission to update this category.");
        }

        existingCategory.setCategoryName(updatedCategory.getCategoryName());
        logger.info("User {} updated category: {}", user.getEmail(), existingCategory.getCategoryName());
        return repository.save(existingCategory);
    }

    // READ_CATEGORIES
    public List<Category> getCategories() {
        return repository.findAll();
    }

    public void deleteCategory(User user, int categoryId) {
        Category category = repository.findById(categoryId);
        if (category == null) {
            throw new RuntimeException("Category not found.");
        }

        // DELETE_COURSE_CATEGORY
        boolean isCourseOwner = user.equals(category.getCreator());
        boolean canDeleteCourseCategory = isCourseOwner && permissionService.hasPermission(user, PermissionType.DELETE_COURSE_CATEGORY);
        //
        boolean canDeleteAnyCategory = permissionService.hasPermission(user, PermissionType.DELETE_ANY_CATEGORY);

        if (!(canDeleteCourseCategory || canDeleteAnyCategory)) {
            throw new SecurityException("You do not have permission to delete this category.");
        }
        logger.info("User {} deleted category: {}", user.getEmail(), category.getCategoryName());
        repository.delete(category);
    }

}

