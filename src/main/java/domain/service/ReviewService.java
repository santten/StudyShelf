package domain.service;

import domain.model.Review;
import domain.model.StudyMaterial;
import domain.model.User;
import infrastructure.repository.ReviewRepository;

import java.util.List;

public class ReviewService {
    private final ReviewRepository reviewRepository;

    public ReviewService(ReviewRepository reviewRepository) {
        this.reviewRepository = reviewRepository;
    }

    public Review addReview(User user, StudyMaterial material, String text) {
        if (user == null || material == null || text == null || text.trim().isEmpty()) {
            throw new IllegalArgumentException("Invalid input: user, material, and text must not be null or empty");
        }
        Review review = new Review(text, material, user);
        return reviewRepository.save(review);
    }

    public List<Review> getReviewsForMaterial(StudyMaterial material) {
        if (material == null) {
            throw new IllegalArgumentException("Material cannot be null");
        }
        return reviewRepository.findByStudyMaterial(material);
    }
}


