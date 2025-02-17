package domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReviewTest {
    private Review review;
    private User user;
    private StudyMaterial material;

    @BeforeEach
    void setUp() {
        user = new User("Armas", "Nevolainen", "armas@gmail.com", "password",new Role(RoleType.STUDENT));
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
        review = new Review("Confused", material, user);
    }
    @Test
    void getReviewId() {
        assertEquals(0, review.getReviewId());
    }

    @Test
    void getReviewText() {
        assertEquals("Confused", review.getReviewText());
    }

    @Test
    void setReviewText() {
        review.setReviewText("Not so confused");
    }

    @Test
    void getStudyMaterial() {
        assertEquals(material, review.getStudyMaterial());
    }

    @Test
    void setStudyMaterial() {
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
        review.setStudyMaterial(newMaterial);
        assertEquals(newMaterial, review.getStudyMaterial());

    }

    @Test
    void getUser() {
        assertEquals(user, review.getUser());
    }

    @Test
    void setUser() {
        User newUser = new User("Matti", "Valovirta", "matti@test.com", "password",new Role(RoleType.TEACHER));
        review.setUser(newUser);
        assertEquals(newUser, review.getUser());
    }
}