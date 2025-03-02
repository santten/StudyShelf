package infrastructure.repository;

import domain.model.Review;
import domain.model.StudyMaterial;
import infrastructure.config.DatabaseConnection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.util.List;


public class ReviewRepository extends BaseRepository<Review> {
    public ReviewRepository() {
        super(Review.class);
    }

    public List<Review> findByStudyMaterial(StudyMaterial material) {
        EntityManager em = getEntityManager();
        try {
            TypedQuery<Review> query = em.createQuery(
                    "SELECT r FROM Review r WHERE r.studyMaterial = :material",
                    Review.class
            );
            query.setParameter("material", material);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}