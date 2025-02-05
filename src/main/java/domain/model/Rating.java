package domain.model;

import jakarta.persistence.*;
@Entity
@Table(name = "Rating")
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int ratingId;
    private int ratingScore;
    @ManyToOne
    @JoinColumn(name = "materialId")
    private StudyMaterial studyMaterial;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    //Default constructor
    public Rating() {}
    //Constructor
    public Rating(int ratingScore,StudyMaterial studyMaterial, User user) {
        this.ratingScore = ratingScore;
        this.studyMaterial = studyMaterial;
        this.user = user;
    }
    //Getters and setters
    public int getRatingId() {
        return ratingId;
    }
    public int getRatingScore() {
        return ratingScore;
    }
    public void setRatingScore(int ratingScore) {
        this.ratingScore = ratingScore;
    }
    public StudyMaterial getStudyMaterial() {
        return studyMaterial;
    }

    public void setStudyMaterial(StudyMaterial studyMaterial) {
        this.studyMaterial = studyMaterial;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
