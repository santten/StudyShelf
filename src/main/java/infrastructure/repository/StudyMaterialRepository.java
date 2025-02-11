package infrastructure.repository;

import domain.model.StudyMaterial;
import infrastructure.config.DatabaseConnection;
import jakarta.persistence.EntityManager;


public class StudyMaterialRepository extends BaseRepository<StudyMaterial> {


    public StudyMaterial findById(int id) {
        EntityManager em = DatabaseConnection.getEntityManagerFactory().createEntityManager();
        try {
            return em.find(StudyMaterial.class, id);
        } finally {
            em.close();
        }
    }
}
