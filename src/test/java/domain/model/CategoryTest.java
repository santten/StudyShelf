package domain.model;

import static org.junit.jupiter.api.Assertions.*;
class CategoryTest {

    private Category category;
    private User creator;

    @org.junit.jupiter.api.BeforeEach
    void setUp() {
        creator = new User("Amir", "Dirin", "amir@test.com", "password",new Role(RoleType.TEACHER));
        category = new Category("Java Programming", creator);
    }

    @org.junit.jupiter.api.Test
    void getCategoryId() {
        assertNotNull(category);
        assertEquals(0, category.getCategoryId());
    }

    @org.junit.jupiter.api.Test
    void getCategoryName() {
        assertEquals("Java Programming", category.getCategoryName());
    }

    @org.junit.jupiter.api.Test
    void setCategoryName() {
        category.setCategoryName("Java for dummies");
        assertEquals("Java for dummies", category.getCategoryName());
    }

    @org.junit.jupiter.api.Test
    void getCreator() {
        assertEquals(creator, category.getCreator());
    }

    @org.junit.jupiter.api.Test
    void setCreator() {
        User newCreator = new User("Matti", "Valovirta", "matti@test.com", "password",new Role(RoleType.ADMIN));
        category.setCreator(newCreator);
        assertEquals(newCreator, category.getCreator());
    }
}