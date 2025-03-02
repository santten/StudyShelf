package infrastructure.repository;

import domain.model.Category;
import domain.model.MaterialStatus;
import domain.model.StudyMaterial;
import domain.model.Tag;
import domain.model.User;
import infrastructure.config.DatabaseConnection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.List;


public class StudyMaterialRepository extends BaseRepository<StudyMaterial> {
    public StudyMaterialRepository() {
        super(StudyMaterial.class);
    }

    public List<StudyMaterial> findByNameOrDescription(String query) {
        EntityManager em = getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<StudyMaterial> cq = cb.createQuery(StudyMaterial.class);
            Root<StudyMaterial> root = cq.from(StudyMaterial.class);

            Predicate namePredicate = cb.like(cb.lower(root.get("name")), "%" + query.toLowerCase() + "%");
            Predicate descPredicate = cb.like(cb.lower(root.get("description")), "%" + query.toLowerCase() + "%");

            cq.where(cb.or(namePredicate, descPredicate));
            return em.createQuery(cq).getResultList();
        } finally {
            em.close();
        }
    }

    public List<StudyMaterial> findByTag(Tag tag) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<StudyMaterial> cq = cb.createQuery(StudyMaterial.class);
        Root<StudyMaterial> root = cq.from(StudyMaterial.class);

        Predicate tagPredicate = cb.isMember(tag, root.get("tags"));

        cq.where(cb.or(tagPredicate));
        return em.createQuery(cq).getResultList();
    }


    public List<StudyMaterial> findByUser(User user) {
        EntityManager em = getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<StudyMaterial> query = cb.createQuery(StudyMaterial.class);
            Root<StudyMaterial> root = query.from(StudyMaterial.class);

            Predicate userPredicate = cb.equal(root.get("uploader"), user);

            query.where(cb.and(userPredicate));
            return em.createQuery(query).getResultList();
        } finally {
            em.close();
        }
    }

    public List<StudyMaterial> findPendingMaterials() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT s FROM StudyMaterial s WHERE s.status = :status", StudyMaterial.class)
                    .setParameter("status", MaterialStatus.PENDING)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<StudyMaterial> findByStatus(MaterialStatus status) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT s FROM StudyMaterial s WHERE s.status = :status", StudyMaterial.class)
                    .setParameter("status", status)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public void updateMaterialStatus(int materialId, MaterialStatus status) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            StudyMaterial material = em.find(StudyMaterial.class, materialId);
            if (material != null) {
                material.setStatus(status);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public List<StudyMaterial> findReviewedMaterialsByUser(User user) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT s FROM StudyMaterial s WHERE s.uploader = :user AND s.status <> :status", StudyMaterial.class)
                    .setParameter("user", user)
                    .setParameter("status", MaterialStatus.PENDING)
                    .getResultList();
        } finally {
            em.close();
        }
    }
    public StudyMaterial update(StudyMaterial material) {
        EntityManager em = DatabaseConnection.getEntityManagerFactory().createEntityManager();
        try {
            em.getTransaction().begin();

            StudyMaterial managedMaterial = em.find(StudyMaterial.class, material.getMaterialId());

            managedMaterial.getTags().clear();
            managedMaterial.getTags().addAll(material.getTags());

            em.getTransaction().commit();
            return managedMaterial;
        } finally {
            em.close();
        }
    }
}
