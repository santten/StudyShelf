package infrastructure.repository;

import domain.model.Review;
import infrastructure.config.DatabaseConnection;
import jakarta.persistence.EntityManager;


public class ReviewRepository extends BaseRepository<Review> {

    public Review findById(int id) {
        EntityManager em = DatabaseConnection.getEntityManagerFactory().createEntityManager();
        try {
            return em.find(Review.class, id);
        } finally {
            em.close();
        }
    }
}