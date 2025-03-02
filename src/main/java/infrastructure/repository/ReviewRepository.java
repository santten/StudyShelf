package infrastructure.repository;

import domain.model.Review;
import domain.model.StudyMaterial;
import domain.model.User;
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

    public List<Review> findByUser(User u) {
        EntityManager em = DatabaseConnection.getEntityManagerFactory().createEntityManager();
        try {
            TypedQuery<Review> query = em.createQuery(
                    "SELECT r FROM Review r WHERE r.user = :user",
                    Review.class
            );
            query.setParameter("user", u);
            return query.getResultList();
        } finally {
            em.close();
        }
    }
}