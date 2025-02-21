package domain.model;

import jakarta.persistence.*;
@Entity
@Table(name = "ratings")
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer ratingId;
    @ManyToOne
    @JoinColumn(name = "materialId")
    private StudyMaterial studyMaterial;

    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;

    @Column(name = "ratingScore")
    private int ratingScore;

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
