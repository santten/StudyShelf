package infrastructure.repository;


import domain.model.Permission;
import domain.model.Rating;
import domain.model.StudyMaterial;
import domain.model.User;
import infrastructure.config.DatabaseConnection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;

import java.util.List;

public class RatingRepository extends BaseRepository<Rating> {

    public RatingRepository() {
        super(Rating.class);
    }

    //    constructor for testing
    public RatingRepository(EntityManagerFactory emf) {
        super(Rating.class, emf);
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

    public List<Rating> findByUser(User u) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT r FROM Rating r WHERE r.user = :user", Rating.class)
                    .setParameter("user", u)
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

    public boolean hasUserReviewedMaterial(User user, StudyMaterial material) {
        EntityManager em = getEntityManager();
        try {
            Long count = em.createQuery("SELECT COUNT(r) FROM Rating r WHERE r.user = :user AND r.studyMaterial = :material", Long.class)
                    .setParameter("user", user)
                    .setParameter("material", material)
                    .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }

    public void deleteByMaterial(StudyMaterial studyMaterial) {
        EntityManager em = getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.createQuery("DELETE FROM Rating r WHERE r.studyMaterial = :studyMaterial")
                    .setParameter("studyMaterial", studyMaterial)
                    .executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public List<Rating> findByUserAndMaterial(User user, StudyMaterial sm) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT r FROM Rating r WHERE r.user = :user AND r.studyMaterial = :studyMaterial", Rating.class)
                    .setParameter("user", user)
                    .setParameter("studyMaterial", sm)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public void deleteById(int id) {
        EntityManager em = getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.createQuery("DELETE FROM Rating r WHERE r.id = :id")
                    .setParameter("id", id)
                    .executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
