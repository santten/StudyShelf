package infrastructure.repository;


import domain.model.Rating;
import domain.model.StudyMaterial;
import infrastructure.config.DatabaseConnection;
import jakarta.persistence.EntityManager;

import java.util.List;

public class RatingRepository extends BaseRepository<Rating> {
    public Rating findById(int id) {
        EntityManager em = DatabaseConnection.getEntityManagerFactory().createEntityManager();
        try {
            return em.find(Rating.class, id);
        } finally {
            em.close();
        }
    }

    public List<Rating> findByMaterial(StudyMaterial material) {
        return getEntityManager()
                .createQuery("SELECT r FROM Rating r WHERE r.studyMaterial = :material", Rating.class)
                .setParameter("material", material)
                .getResultList();
    }



}
