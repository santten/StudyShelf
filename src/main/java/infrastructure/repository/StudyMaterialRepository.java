package infrastructure.repository;

import domain.model.*;
import infrastructure.config.DatabaseConnection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.List;

import static domain.model.MaterialStatus.APPROVED;


public class StudyMaterialRepository extends BaseRepository<StudyMaterial> {
    public StudyMaterialRepository() {
        super(StudyMaterial.class);
    }

    //    constructor for testing
    public StudyMaterialRepository(EntityManagerFactory emf) {
        super(StudyMaterial.class, emf);
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
            return em.createQuery(
                            "SELECT DISTINCT s FROM StudyMaterial s " +
                                    "LEFT JOIN FETCH s.category c " +
                                    "LEFT JOIN FETCH c.creator " +
                                    "LEFT JOIN FETCH s.uploader " +
                                    "LEFT JOIN FETCH s.tags " +
                                    "WHERE s.status = :status", StudyMaterial.class)
                    .setParameter("status", status)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<StudyMaterial> findLatestWithLimit(int limit) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT s FROM StudyMaterial s WHERE s.status = :status ORDER BY s.timestamp DESC", StudyMaterial.class)
                    .setParameter("status", APPROVED)
                    .setMaxResults(limit)
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

    public void updateMaterialDescription(int materialId, String description) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            StudyMaterial material = em.find(StudyMaterial.class, materialId);
            if (material != null) {
                material.setDescription(description);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public void updateMaterialTitle(int materialId, String title) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            StudyMaterial material = em.find(StudyMaterial.class, materialId);
            if (material != null) {
                material.setName(title);
            }
            em.getTransaction().commit();
        } finally {
            em.close();
        }
    }

    public List<StudyMaterial> findReviewedMaterialsByUser(User user) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                            "SELECT DISTINCT r.studyMaterial FROM Rating r WHERE r.user = :user", StudyMaterial.class)
                    .setParameter("user", user)
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

    public List<StudyMaterial> findBestReviewedMaterials(int limit) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                            "SELECT r.studyMaterial FROM Rating r GROUP BY r.studyMaterial " +
                                    "ORDER BY AVG(r.ratingScore) ASC", StudyMaterial.class)
                    .setMaxResults(limit)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public List<StudyMaterial> findReviewedMaterialsByUserLatest10(User user) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery(
                            "SELECT DISTINCT r.studyMaterial FROM Rating r WHERE r.user = :user ORDER BY r.studyMaterial.timestamp DESC ", StudyMaterial.class)
                    .setParameter("user", user)
                    .setMaxResults(10)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    @Override
    public void delete(StudyMaterial entity) {
        EntityManager em = getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            new ReviewRepository().deleteByMaterial(entity);
            new RatingRepository().deleteByMaterial(entity);

            StudyMaterial mergedEntity = em.merge(entity);
            em.remove(mergedEntity);
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
