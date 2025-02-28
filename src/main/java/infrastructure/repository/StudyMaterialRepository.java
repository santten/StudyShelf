package infrastructure.repository;

import domain.model.Category;
import domain.model.StudyMaterial;
import domain.model.User;
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

    public List<StudyMaterial> findAllStudyMaterials() {
        EntityManager em = DatabaseConnection.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT s FROM StudyMaterial s", StudyMaterial.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<StudyMaterial> findByUser(User user) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<StudyMaterial> query = cb.createQuery(StudyMaterial.class);
        Root<StudyMaterial> root = query.from(StudyMaterial.class);

        Predicate userPredicate = cb.equal(root.get("uploader"), user);

        query.where(cb.and(userPredicate));
        return em.createQuery(query).getResultList();
    }
}
