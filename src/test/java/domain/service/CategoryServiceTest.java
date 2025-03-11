package domain.service;

import domain.model.*;
import infrastructure.repository.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockedStatic;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CategoryServiceTest {

    private CategoryService categoryService;
    private CategoryRepository categoryRepository;
    private PermissionService permissionService;

    private User teacherUser;   // Teacher user who usually has CREATE_CATEGORY, UPDATE_COURSE_CATEGORY, DELETE_COURSE_CATEGORY
    private User adminUser;     // Admin user who usually has UPDATE_ANY_CATEGORY, DELETE_ANY_CATEGORY
    private User studentUser;   // Student user with no special permissions

    private Category testCategory;

    @BeforeEach
    void setUp() {
        categoryRepository = Mockito.mock(CategoryRepository.class);
        permissionService = Mockito.mock(PermissionService.class);

        categoryService = new CategoryService(categoryRepository, permissionService);

        // Mock roles
        Role teacherRole = new Role(RoleType.TEACHER);
        Role adminRole = new Role(RoleType.ADMIN);
        Role studentRole = new Role(RoleType.STUDENT);

        teacherUser = new User("TFirst", "TLast", "teacher@example.com", "password", teacherRole);
        adminUser = new User("AFirst", "ALast", "admin@example.com", "password", adminRole);
        studentUser = new User("SFirst", "SLast", "student@example.com", "password", studentRole);

        // Test category
        testCategory = new Category();
        testCategory.setCategoryName("Test Category");
        testCategory.setCreator(teacherUser); // The teacher is the creator (course owner)
    }

    @Test
    @DisplayName("Successfully retrieve all categories (READ_CATEGORIES)")
    void testGetCategories() {
        List<Category> mockCategories = List.of(testCategory);
        when(categoryRepository.findAll()).thenReturn(mockCategories);

        List<Category> result = categoryService.getCategories();

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("Test Category", result.get(0).getCategoryName());
        verify(categoryRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Teacher with CREATE_CATEGORY permission can successfully create a category")
    void testCreateCategory_WithPermission() {
        when(permissionService.hasPermission(teacherUser, PermissionType.CREATE_CATEGORY))
                .thenReturn(true);

        Category newCategory = new Category();
        newCategory.setCategoryName("New Teacher Category");

        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

        Category savedCategory = categoryService.createCategory(teacherUser, newCategory);

        assertNotNull(savedCategory);
        assertEquals("New Teacher Category", savedCategory.getCategoryName());
        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Teacher without CREATE_CATEGORY permission fails to create category")
    void testCreateCategory_WithoutPermission() {
        when(permissionService.hasPermission(teacherUser, PermissionType.CREATE_CATEGORY))
                .thenReturn(false);

        Category newCategory = new Category();
        newCategory.setCategoryName("Fail Category");

        assertThrows(SecurityException.class, () ->
                categoryService.createCategory(teacherUser, newCategory)
        );

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Teacher (course owner) with UPDATE_COURSE_CATEGORY permission can successfully update category")
    void testUpdateCategory_AsCourseOwner() {
        when(categoryRepository.findById(testCategory.getCategoryId())).thenReturn(testCategory);
        when(permissionService.hasPermission(teacherUser, PermissionType.UPDATE_COURSE_CATEGORY))
                .thenReturn(true);

//        Category updatedCategory = new Category();
//        updatedCategory.setCategoryName("Updated Category Name");
        Category updatedCategory = new Category(testCategory.getCategoryId(), "Updated Category Name", teacherUser);

        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

        Category result = categoryService.updateCategory(teacherUser, updatedCategory);
        assertEquals("Updated Category Name", result.getCategoryName());

        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Admin with UPDATE_ANY_CATEGORY permission can successfully update category")
    void testUpdateCategory_AsAdmin() {
        when(categoryRepository.findById(testCategory.getCategoryId())).thenReturn(testCategory);
        when(permissionService.hasPermission(adminUser, PermissionType.UPDATE_COURSE_CATEGORY))
                .thenReturn(false);
        when(permissionService.hasPermission(adminUser, PermissionType.UPDATE_ANY_CATEGORY))
                .thenReturn(true);

        Category updatedCategory = new Category();
        updatedCategory.setCategoryName("Admin Updated Category");

        when(categoryRepository.save(any(Category.class))).thenAnswer(inv -> inv.getArgument(0));

        Category result = categoryService.updateCategory(adminUser, updatedCategory);
        assertEquals("Admin Updated Category", result.getCategoryName());

        verify(categoryRepository, times(1)).save(any(Category.class));
    }

    @Test
    @DisplayName("Updating a category without any permission throws SecurityException")
    void testUpdateCategory_NoPermission() {
        when(categoryRepository.findById(testCategory.getCategoryId())).thenReturn(testCategory);
        when(permissionService.hasPermission(studentUser, PermissionType.UPDATE_COURSE_CATEGORY))
                .thenReturn(false);
        when(permissionService.hasPermission(studentUser, PermissionType.UPDATE_ANY_CATEGORY))
                .thenReturn(false);

        Category updatedCategory = new Category();
        updatedCategory.setCategoryName("Student tries update");

        assertThrows(SecurityException.class, () -> {
            categoryService.updateCategory(studentUser, updatedCategory);
        });

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Successfully get the study materials of the category")
    void testGetMaterialsByCategory() {
        List<StudyMaterial> mockMaterials = new ArrayList<>();
        mockMaterials.add(new StudyMaterial());
        mockMaterials.add(new StudyMaterial());

        when(categoryRepository.findMaterialsByCategory(testCategory)).thenReturn(mockMaterials);

        List<StudyMaterial> result = categoryService.getMaterialsByCategory(testCategory);
        assertEquals(2, result.size());
        verify(categoryRepository, times(1)).findMaterialsByCategory(testCategory);
    }

    @Test
    @DisplayName("Teacher (course owner) can delete their own category with DELETE_COURSE_CATEGORY permission")
    void testDeleteCategory_AsCourseOwner() {
        when(categoryRepository.findById(testCategory.getCategoryId())).thenReturn(testCategory);
        when(permissionService.hasPermission(teacherUser, PermissionType.DELETE_COURSE_CATEGORY))
                .thenReturn(true);

        categoryService.deleteCategory(teacherUser, testCategory.getCategoryId());
        verify(categoryRepository, times(1)).delete(testCategory);
    }

    @Test
    @DisplayName("Admin can delete any category with DELETE_ANY_CATEGORY permission")
    void testDeleteCategory_AsAdmin() {
        when(categoryRepository.findById(testCategory.getCategoryId())).thenReturn(testCategory);
        when(permissionService.hasPermission(adminUser, PermissionType.DELETE_ANY_CATEGORY))
                .thenReturn(true);

        categoryService.deleteCategory(adminUser, testCategory.getCategoryId());
        verify(categoryRepository, times(1)).delete(testCategory);
    }

    @Test
    @DisplayName("Deleting a category without permission throws SecurityException")
    void testDeleteCategory_NoPermission() {
        when(categoryRepository.findById(testCategory.getCategoryId())).thenReturn(testCategory);
        when(permissionService.hasPermission(studentUser, PermissionType.DELETE_COURSE_CATEGORY))
                .thenReturn(false);
        when(permissionService.hasPermission(studentUser, PermissionType.DELETE_ANY_CATEGORY))
                .thenReturn(false);

        assertThrows(SecurityException.class, () ->
                categoryService.deleteCategory(studentUser, testCategory.getCategoryId())
        );

        verify(categoryRepository, never()).delete(any(Category.class));
    }

    @Test
    @DisplayName("Deleting a non-existent category throws RuntimeException")
    void testDeleteCategory_CategoryNotFound() {
        when(categoryRepository.findById(9999)).thenReturn(null);

        assertThrows(RuntimeException.class, () -> {
            categoryService.deleteCategory(teacherUser, 9999);
        });

        verify(categoryRepository, never()).delete(any(Category.class));
    }

    @Test
    @DisplayName("Updating a non-existent category throws RuntimeException")
    void testUpdateCategory_CategoryNotFound() {
        when(categoryRepository.findById(anyInt())).thenReturn(null);

        Category updatedCategory = new Category();
        updatedCategory.setCategoryName("Non-existent Category");

        assertThrows(RuntimeException.class, () -> {
            categoryService.updateCategory(adminUser, updatedCategory);
        });

        verify(categoryRepository, never()).save(any(Category.class));
    }

    @Test
    @DisplayName("Get owned categories with pending materials")
    void testGetOwnedCategoriesWithPending() {
        // Arrange
        List<Category> teacherCategories = List.of(testCategory);
        when(categoryRepository.findCategoriesByUser(teacherUser)).thenReturn(teacherCategories);
        when(categoryRepository.countPendingMaterialsByCategory(testCategory)).thenReturn(2L);

        // Act
        List<Category> result = categoryService.getOwnedCategoriesWithPending(teacherUser);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testCategory, result.get(0));
        verify(categoryRepository).findCategoriesByUser(teacherUser);
        verify(categoryRepository).countPendingMaterialsByCategory(testCategory);
    }

    @Test
    @DisplayName("Get owned categories with no pending materials")
    void testGetOwnedCategoriesWithNoPending() {
        // Arrange
        List<Category> teacherCategories = List.of(testCategory);
        when(categoryRepository.findCategoriesByUser(teacherUser)).thenReturn(teacherCategories);
        when(categoryRepository.countPendingMaterialsByCategory(testCategory)).thenReturn(0L);

        // Act
        List<Category> result = categoryService.getOwnedCategoriesWithPending(teacherUser);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.size());
        verify(categoryRepository).findCategoriesByUser(teacherUser);
        verify(categoryRepository).countPendingMaterialsByCategory(testCategory);
    }

    @Test
    @DisplayName("Get categories by user")
    void testGetCategoriesByUser() {
        // Arrange
        List<Category> expectedCategories = List.of(testCategory);
        when(categoryRepository.findCategoriesByUser(teacherUser)).thenReturn(expectedCategories);

        // Act
        List<Category> result = categoryService.getCategoriesByUser(teacherUser);

        // Assert
        assertEquals(expectedCategories, result);
        verify(categoryRepository).findCategoriesByUser(teacherUser);
    }

    @Test
    @DisplayName("Update category title as course owner with permission")
    void testUpdateTitle_AsCourseOwner() {
        // Arrange
        when(permissionService.hasPermission(teacherUser, PermissionType.UPDATE_COURSE_CATEGORY))
                .thenReturn(true);

        // Act
        categoryService.updateTitle(teacherUser, testCategory, "Updated Title");

        // Assert
        verify(categoryRepository).updateCategoryTitle(testCategory.getCategoryId(), "Updated Title");
    }



    @Test
    @DisplayName("Get pending materials by category with permission")
    void testGetPendingMaterialsByCategory_WithPermission() {
        // Arrange
        List<StudyMaterial> pendingMaterials = new ArrayList<>();
        pendingMaterials.add(new StudyMaterial());

        when(permissionService.hasPermission(teacherUser, PermissionType.APPROVE_RESOURCE))
                .thenReturn(true);
        when(categoryRepository.findPendingMaterialsByCategory(testCategory))
                .thenReturn(pendingMaterials);

        // Act
        List<StudyMaterial> result = categoryService.getPendingMaterialsByCategory(teacherUser, testCategory);

        // Assert
        assertEquals(pendingMaterials, result);
        verify(permissionService).hasPermission(teacherUser, PermissionType.APPROVE_RESOURCE);
        verify(categoryRepository).findPendingMaterialsByCategory(testCategory);
    }

    @Test
    @DisplayName("Get pending materials by category without permission throws exception")
    void testGetPendingMaterialsByCategory_WithoutPermission() {
        // Arrange
        when(permissionService.hasPermission(studentUser, PermissionType.APPROVE_RESOURCE))
                .thenReturn(false);

        // Act & Assert
        assertThrows(SecurityException.class, () ->
                categoryService.getPendingMaterialsByCategory(studentUser, testCategory)
        );

        verify(permissionService).hasPermission(studentUser, PermissionType.APPROVE_RESOURCE);
        verify(categoryRepository, never()).findPendingMaterialsByCategory(any());
    }

    @Test
    @DisplayName("Get approved materials by category")
    void testGetApprovedMaterialsByCategory() {
        // Arrange
        List<StudyMaterial> approvedMaterials = new ArrayList<>();
        approvedMaterials.add(new StudyMaterial());

        when(categoryRepository.findApprovedMaterialsByCategory(testCategory))
                .thenReturn(approvedMaterials);

        // Act
        List<StudyMaterial> result = categoryService.getApprovedMaterialsByCategory(teacherUser, testCategory);

        // Assert
        assertEquals(approvedMaterials, result);
        verify(categoryRepository).findApprovedMaterialsByCategory(testCategory);
    }




}
