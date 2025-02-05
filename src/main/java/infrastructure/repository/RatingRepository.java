package infrastructure.repository;


import domain.model.Rating;
import infrastructure.config.DatabaseConnection;
import jakarta.persistence.EntityManager;

public class RatingRepository extends BaseRepository<Rating> {
    public Rating findById(int id) {
        EntityManager em = DatabaseConnection.getEntityManagerFactory().createEntityManager();
        try {
            return em.find(Rating.class, id);
        } finally {
            em.close();
        }
    }
}
