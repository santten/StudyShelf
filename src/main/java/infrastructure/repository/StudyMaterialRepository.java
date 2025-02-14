package infrastructure.repository;

import domain.model.Permission;
import domain.model.StudyMaterial;
import infrastructure.config.DatabaseConnection;
import jakarta.persistence.EntityManager;

import java.util.List;


public class StudyMaterialRepository extends BaseRepository<StudyMaterial> {
    public StudyMaterial findById(int id) {
        EntityManager em = DatabaseConnection.getEntityManagerFactory().createEntityManager();
        try {
            return em.find(StudyMaterial.class, id);
        } finally {
            em.close();
        }
    }

    public List<StudyMaterial> findAllStudyMaterials() {
        EntityManager em = DatabaseConnection.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT s FROM StudyMaterial s", StudyMaterial.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

}
