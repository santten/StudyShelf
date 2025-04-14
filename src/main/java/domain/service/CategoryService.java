package domain.service;

import domain.model.Category;
import domain.model.StudyMaterial;
import infrastructure.repository.CategoryRepository;
import domain.model.PermissionType;
import domain.model.User;
import infrastructure.repository.StudyMaterialRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

import static domain.model.RoleType.ADMIN;

/**
 * Service class for handling operations related to Category management,
 * such as creating, updating, deleting categories, and fetching associated study materials.
 */
public class CategoryService {
    private static final Logger logger = LoggerFactory.getLogger(CategoryService.class);
    private final CategoryRepository repository;
    private final PermissionService permissionService;

    /**
     * Constructs a new CategoryService with the specified repository and permission service.
     */
    public CategoryService(CategoryRepository repository, PermissionService permissionService) {
        this.repository = repository;
        this.permissionService = permissionService;
    }

    /**
     * Retrieves all study materials associated with the given category.
     */
    public List<StudyMaterial> getMaterialsByCategory(Category category) {
        return repository.findMaterialsByCategory(category);
    }

    /**
     * Retrieves pending materials in a category, only if user has approval permission.
     * @throws SecurityException if user lacks the required permission.
     */
    public List<StudyMaterial> getPendingMaterialsByCategory(User user, Category category) {
        if (!permissionService.hasPermission(user, PermissionType.APPROVE_RESOURCE)){
            throw new SecurityException("You do not have permission to view pending materials for this category.");
        }
        return repository.findPendingMaterialsByCategory(category);
    }

    /**
     * Returns approved study materials for a given category.
     */
    public List<StudyMaterial> getApprovedMaterialsByCategory(User currentUser, Category c) {
        return repository.findApprovedMaterialsByCategory(c);
    }

    // CREATE_CATEGORY
    /**
     * Creates a new category if the user has permission to do so.
     * @throws SecurityException if user lacks the permission.
     */
    public Category createCategory(User user, Category category) {
        if (!permissionService.hasPermission(user, PermissionType.CREATE_CATEGORY)) {
            throw new SecurityException("You do not have permission to create a category.");
        }

        logger.info("User {} created a new category: {}", user.getEmail(), category.getCategoryName());
        return repository.save(category);
    }

    /**
     * Updates an existing category if user is owner or has elevated permissions.
     * @throws SecurityException if not allowed to update.
     */
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

    /**
     * Fetches all available categories in the system.
     */
    // READ_CATEGORIES
    public List<Category> getCategories() {
        return repository.findAll();
    }

    /**
     * Deletes a category if the user is course owner or has higher permissions.
     * Also deletes any associated study materials.
     * @throws SecurityException if the user lacks deletion rights.
     */
    public void deleteCategory(User user, int categoryId) {
        Category category = repository.findById(categoryId);
        if (category == null) {
            throw new RuntimeException("Category not found.");
        }

    // DELETE_COURSE_CATEGORY
        boolean isCourseOwner = user.getUserId() == category.getCreator().getUserId();
        boolean canDeleteCourseCategory = isCourseOwner && permissionService.hasPermission(user, PermissionType.DELETE_COURSE_CATEGORY);
    // DELETE_ANY_CATEGORY
        boolean canDeleteAnyCategory = permissionService.hasPermission(user, PermissionType.DELETE_ANY_CATEGORY);

        if (!(canDeleteCourseCategory || canDeleteAnyCategory)) {
            throw new SecurityException("You do not have permission to delete this category.");
        }

        long materialCount = repository.countMaterialsByCategory(category);
        if (materialCount > 0) {
            StudyMaterialService smServ = new StudyMaterialService(new GoogleDriveService(), new StudyMaterialRepository(), new PermissionService());
            repository.findMaterialsByCategory(category).forEach(material -> {
                smServ.deleteMaterial(user, material);
            });
        }

        logger.info("User {} deleted category: {}", user.getEmail(), category.getCategoryName());
        repository.delete(category);
    }

    /**
     * Gets all user-owned categories that have at least one pending material.
     */
    public List<Category> getOwnedCategoriesWithPending(User u) {
        List<Category> allCategories = repository.findCategoriesByUser(u);
        List<Category> pendingCategories = new ArrayList<>();

        for (Category category : allCategories) {
            long pendingCount = repository.countPendingMaterialsByCategory(category);
            if (pendingCount > 0) {
                pendingCategories.add(category);
            }
        }

        return pendingCategories;
    }

    /**
     * Returns a list of categories created by the given user.
     */
    public List<Category> getCategoriesByUser(User user) {
        return repository.findCategoriesByUser(user);
    }

    /**
     * Updates the title of a category if the user is the creator or an admin.
     * @throws SecurityException if user is not authorized.
     */
    public void updateTitle(User user, Category c, String title) {
        if ((user.getUserId() == c.getCreator().getUserId()
                && permissionService.hasPermission(user, PermissionType.UPDATE_COURSE_CATEGORY))
                || user.getRole().getName() == ADMIN) {
            repository.updateCategoryTitle(c.getCategoryId(), title);
        } else {
            throw new SecurityException("You do not have permission to modify this title.");
        }
    }
}