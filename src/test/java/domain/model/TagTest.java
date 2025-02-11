package domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class TagTest {
    private Tag tag;
    private User user;

    @BeforeEach
    void setUp() {
        user = new User("Armas", "Nevolainen", "armas@gmail.com", "password");
        tag = new Tag("Java", user);
    }

    @Test
    void getTagId() {
        assertEquals(0, tag.getTagId()); // Before persistence
    }

    @Test
    void getTagName() {
        assertEquals("Java", tag.getTagName());
    }

    @Test
    void setTagName() {
        tag.setTagName("Advanced Java");
        assertEquals("Advanced Java", tag.getTagName());
    }

    @Test
    void getCreator() {
        assertEquals(user, tag.getCreator());
    }

    @Test
    void setCreator() {
        User newCreator = new User("Matti",  "Valovirta", "matti@test.com" + System.currentTimeMillis(), "password");
        tag.setCreator(newCreator);
        assertEquals(newCreator, tag.getCreator());
    }

    @Test
    void getMaterials() {
        assertNotNull(tag.getMaterials());
        assertTrue(tag.getMaterials().isEmpty());
    }

    @Test
    void setMaterials() {
        Set<StudyMaterial> materials = new HashSet<>();
        StudyMaterial newMaterial = new StudyMaterial(
                user,
                "Advanced Java",
                "Advanced Java Programming",
                "anotherlink",
                15f,
                "PDF",
                LocalDateTime.now(),
                MaterialStatus.PENDING
        );
        tag.setMaterials(materials);
        assertEquals(materials, tag.getMaterials());
    }
}
