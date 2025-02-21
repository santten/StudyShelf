package domain.service;

import domain.model.Rating;
import domain.model.StudyMaterial;
import domain.model.User;
import infrastructure.repository.RatingRepository;

public class RatingService {
    private final RatingRepository ratingRepository;

    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    public Rating rateMaterial(int ratingScore, StudyMaterial material, User user) {
        Rating rating = new Rating(ratingScore, material, user);
        return ratingRepository.save(rating);
    }

    public double getAverageRating(StudyMaterial material) {
        return ratingRepository.findByMaterial(material)
                .stream()
                .mapToInt(Rating::getRatingScore)
                .average()
                .orElse(0.0);
    }
}
