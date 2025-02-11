package domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class StudyMaterialTest {
    private StudyMaterial material;
    private User user;
    private Category category;
    private Set<Tag> tags;
    private Set<Rating> ratings;

    @BeforeEach
    void setUp() {
        user = new User("Armas", "Nevolainen", "armas@gmail.com", "password");
        category = new Category("Java Programming", user);
        material = new StudyMaterial(
                user,
                "Java for dummies",
                "Introduction to Java Programming for dummies",
                "materials/java-dumb.pdf",
                1.5f,
                "PDF",
                LocalDateTime.now(),
                MaterialStatus.PENDING
        );
        tags = new HashSet<>();
        ratings = new HashSet<>();

    }

    @Test
    void getMaterialId() {
        assertEquals(0, material.getMaterialId());
    }

    @Test
    void setMaterialId() {
        material.setMaterialId(1);
        assertEquals(1, material.getMaterialId());
    }

    @Test
    void getUploader() {
        assertEquals(user, material.getUploader());
    }

    @Test
    void setUploader() {
        User uploader = new User("Matti", "Valovirta", "matti@test.com", "password");
    }

    @Test
    void getName() {
        assertEquals("Java for dummies", material.getName());
    }

    @Test
    void setName() {
        material.setName("Java for geniuses");
        assertEquals("Java for geniuses", material.getName());
    }

    @Test
    void getDescription() {
        assertEquals("Introduction to Java Programming for dummies", material.getDescription());
    }

    @Test
    void setDescription() {
        material.setDescription("Introduction to Java Programming NOT for dummies");
        assertEquals("Introduction to Java Programming NOT for dummies", material.getDescription());
    }

    @Test
    void getLink() {
        assertEquals("materials/java-dumb.pdf", material.getLink());
    }

    @Test
    void setLink() {
        material.setLink("materials/java-smart.pdf");
        assertEquals("materials/java-smart.pdf", material.getLink());
    }

    @Test
    void getFileSize() {
        assertEquals(1.5f, material.getFileSize());
    }

    @Test
    void setFileSize() {
        material.setFileSize(20f);
        assertEquals(20f, material.getFileSize());
    }

    @Test
    void getFileType() {
        assertEquals("PDF", material.getFileType());
    }

    @Test
    void setFileType() {
        material.setFileType("txt");
        assertEquals("txt", material.getFileType());
    }

    @Test
    void getTimestamp() {
        LocalDateTime timestamp = LocalDateTime.now();
        material.setTimestamp(timestamp);
        assertEquals(timestamp, material.getTimestamp());
    }

    @Test
    void setTimestamp() {
        LocalDateTime newTimestamp = LocalDateTime.now().plusDays(1);
        material.setTimestamp(newTimestamp);
        assertEquals(newTimestamp, material.getTimestamp());
    }


    @Test
    void getStatus() {
        assertEquals(MaterialStatus.PENDING, material.getStatus());
    }

    @Test
    void setStatus() {
        material.setStatus(MaterialStatus.PENDING);
        assertEquals(MaterialStatus.PENDING, material.getStatus());
    }

    @Test
    void getCategory() {
        material.setCategory(category);
        assertEquals(category, material.getCategory());
    }

    @Test
    void setCategory() {
        Category newCategory = new Category("Object Oriented Programming", user);
        material.setCategory(newCategory);
        assertEquals(newCategory, material.getCategory());
    }

    @Test
    void getTags() {
        Tag tag = new Tag("Java", user);
        tags.add(tag);
        material.setTags(tags);
        assertEquals(tags, material.getTags());
    }

    @Test
    void setTags() {
        Set<Tag> newTags = new HashSet<>();
        Tag tag = new Tag("Programming", user);
        newTags.add(tag);
        material.setTags(newTags);
        assertEquals(newTags, material.getTags());
    }

    @Test
    void getRatings() {
        Rating rating = new Rating(5, material, user);
        ratings.add(rating);
        material.setRatings(ratings);
        assertEquals(ratings, material.getRatings());
    }

    @Test
    void setRatings() {
        Set<Rating> newRatings = new HashSet<>();
        Rating rating = new Rating(4, material, user);
        newRatings.add(rating);
        material.setRatings(newRatings);
        assertEquals(newRatings, material.getRatings());
    }
}