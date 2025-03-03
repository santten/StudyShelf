package infrastructure.repository;


import domain.model.Rating;
import domain.model.StudyMaterial;
import infrastructure.config.DatabaseConnection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;

import java.util.List;

public class RatingRepository extends BaseRepository<Rating> {

    public RatingRepository() {
        super(Rating.class);
    }

    public List<Rating> findByMaterial(StudyMaterial material) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT r FROM Rating r WHERE r.studyMaterial = :material", Rating.class)
                    .setParameter("material", material)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Double findAverageRatingByMaterial(StudyMaterial material) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                            "SELECT AVG(r.ratingScore) FROM Rating r WHERE r.studyMaterial = :material", Double.class)
                    .setParameter("material", material)
                    .getSingleResult();
        } catch (NoResultException e) {
            return 0.0;
        } finally {
            em.close();
        }
    }
}
