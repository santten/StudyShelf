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
        user = new User("Armas", "Nevolainen", "armas@gmail.com", "password",new Role(RoleType.STUDENT));
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
        User uploader = new User("Matti", "Valovirta", "matti@test.com", "password",new Role(RoleType.TEACHER));
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

    @Test
    void getFileExtension() {
        material.setFileType("application/pdf");
        assertEquals("pdf", material.getFileExtension());

        material.setFileType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        assertEquals("docx", material.getFileExtension());

        material.setFileType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        assertEquals("xlsx", material.getFileExtension());

        material.setFileType("application/vnd.openxmlformats-officedocument.presentationml.presentation");
        assertEquals("pptx", material.getFileExtension());

        // Test image types
        material.setFileType("image/jpeg");
        assertEquals("jpg", material.getFileExtension());

        material.setFileType("image/png");
        assertEquals("png", material.getFileExtension());

        material.setFileType("application/x-zip-compressed");
        assertEquals("zip", material.getFileExtension());

        material.setFileType("application/x-7z-compressed");
        assertEquals("7z", material.getFileExtension());

        material.setFileType("application/x-rar-compressed");
        assertEquals("rar", material.getFileExtension());

        material.setFileType("application/x-tar");
        assertEquals("tar", material.getFileExtension());

        material.setFileType("application/gzip");
        assertEquals("gz", material.getFileExtension());

        material.setFileType("application/x-bzip2");
        assertEquals("bz2", material.getFileExtension());

        // Test text types
        material.setFileType("text/plain");
        assertEquals("txt", material.getFileExtension());

        material.setFileType("text/csv");
        assertEquals("csv", material.getFileExtension());

        material.setFileType("text/html");
        assertEquals("html", material.getFileExtension());

        material.setFileType("application/vnd.ms-powerpoint");
        assertEquals("ppt", material.getFileExtension());

        material.setFileType("application/vnd.ms-excel");
        assertEquals("xls", material.getFileExtension());

        material.setFileType("image/gif");
        assertEquals("gif", material.getFileExtension());

        material.setFileType("video/mp4");
        assertEquals("mp4", material.getFileExtension());

        material.setFileType("video/x-msvideo");
        assertEquals("avi", material.getFileExtension());

        material.setFileType("video/quicktime");
        assertEquals("mov", material.getFileExtension());

        material.setFileType("audio/mpeg");
        assertEquals("mp3", material.getFileExtension());

        // Test executable formats
        material.setFileType("application/java-archive");
        assertEquals("jar", material.getFileExtension());

        material.setFileType("application/x-msdownload");
        assertEquals("exe", material.getFileExtension());


        // Test an unknown file type
        material.setFileType("application/unknown");
        assertEquals("unknown", material.getFileExtension());

        // Test custom format
        material.setFileType("custom/format");
        assertEquals("format", material.getFileExtension());
    }

    @Test
    void testConstructorWithId() {
        // Test the constructor that takes an ID
        int materialId = 123;
        LocalDateTime timestamp = LocalDateTime.now();

        StudyMaterial materialWithId = new StudyMaterial(
                materialId,
                user,
                "Test Material",
                "Test Description",
                "materials/test.pdf",
                2.5f,
                "PDF",
                timestamp,
                MaterialStatus.APPROVED
        );

        assertEquals(materialId, materialWithId.getMaterialId());
        assertEquals(user, materialWithId.getUploader());
        assertEquals("Test Material", materialWithId.getName());
        assertEquals("Test Description", materialWithId.getDescription());
        assertEquals("materials/test.pdf", materialWithId.getLink());
        assertEquals(2.5f, materialWithId.getFileSize());
        assertEquals("PDF", materialWithId.getFileType());
        assertEquals(timestamp, materialWithId.getTimestamp());
        assertEquals(MaterialStatus.APPROVED, materialWithId.getStatus());
    }

    @Test
    void testSetUploader() {
        User newUploader = new User("Armas", "N", "a@gmail.com", "password", new Role(RoleType.TEACHER));

        material.setUploader(newUploader);
        assertEquals(newUploader, material.getUploader());

        assertNotEquals(user, material.getUploader());
        assertEquals("Armas", material.getUploader().getFirstName());
        assertEquals("N", material.getUploader().getLastName());
    }

    @Test
    void testPreviewImage() {
        // Create a test byte array
        byte[] testImage = "Test preview image data".getBytes();

        // Test setter
        material.setPreviewImage(testImage);

        // Test getter
        byte[] retrievedImage = material.getPreviewImage();

        // Verify
        assertNotNull(retrievedImage);
        assertArrayEquals(testImage, retrievedImage);

        // Test with null
        material.setPreviewImage(null);
        assertNull(material.getPreviewImage());
    }

}