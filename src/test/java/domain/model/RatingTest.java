package domain.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class RatingTest {
    private Rating rating;
    private User user;
    private StudyMaterial material;

    @BeforeEach
    void setUp() {
        user = new User("Armas", "Nevolainen", "armas@gmail.com", "password",new Role(RoleType.TEACHER));
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
        rating = new Rating(5, material, user);
    }

    /* @Test
    void getRatingId() {
        assertEquals(0, rating.getRatingId());
    } */

    @Test
    void getRatingScore() {
        assertEquals(5, rating.getRatingScore());
    }

    @Test
    void setRatingScore() {
        rating.setRatingScore(4);
        assertEquals(4, rating.getRatingScore());
    }

    @Test
    void getStudyMaterial() {
        assertEquals(material, rating.getStudyMaterial());
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
        rating.setStudyMaterial(newMaterial);
        assertEquals(newMaterial, rating.getStudyMaterial());
    }

    @Test
    void getUser() {
        assertEquals(user, rating.getUser());
    }

    @Test
    void setUser() {
        User newUser = new User("Matti", "Valovirta", "matti@test.com", "password",new Role(RoleType.TEACHER));
        rating.setUser(newUser);
        assertEquals(newUser, rating.getUser());
    }
}