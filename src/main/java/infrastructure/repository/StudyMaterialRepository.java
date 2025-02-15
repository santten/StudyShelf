package infrastructure.repository;

import domain.model.StudyMaterial;
import infrastructure.config.DatabaseConnection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

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
    public List<StudyMaterial> findByNameOrDescription(String query) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<StudyMaterial> cq = cb.createQuery(StudyMaterial.class);
        Root<StudyMaterial> root = cq.from(StudyMaterial.class);

        Predicate namePredicate = cb.like(cb.lower(root.get("name")), "%" + query.toLowerCase() + "%");
        Predicate descPredicate = cb.like(cb.lower(root.get("description")), "%" + query.toLowerCase() + "%");

        cq.where(cb.or(namePredicate, descPredicate));
        return em.createQuery(cq).getResultList();
    }
}
