package domain.model;


import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int reviewId;
    @Column(name = "ReviewText", length = 500)
    private String reviewText;
    @Column(name="Timestamp")
    private LocalDateTime timestamp = LocalDateTime.now();
    @ManyToOne
    @JoinColumn(name = "materialId")
    private StudyMaterial studyMaterial;
    @ManyToOne
    @JoinColumn(name = "userId")
    private User user;
    //Default constructor
    public Review() {}
    //Constructor
    public Review(String reviewText, StudyMaterial studyMaterial, User user) {
        this.reviewText = reviewText;
        this.studyMaterial = studyMaterial;
        this.user = user;
    }
    //Getters and setters
    public int getReviewId() {
        return reviewId;
    }
    public String getReviewText() {
        return reviewText;
    }
    public void setReviewText(String reviewText) {
        this.reviewText = reviewText;
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
