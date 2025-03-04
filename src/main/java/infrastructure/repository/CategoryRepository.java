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

import static domain.model.MaterialStatus.APPROVED;
import static domain.model.MaterialStatus.PENDING;

public class CategoryRepository extends BaseRepository<Category>  {
    public CategoryRepository() {
        super(Category.class);
    }

    public List<StudyMaterial> findMaterialsByCategory(Category category) {
        EntityManager em = getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<StudyMaterial> query = cb.createQuery(StudyMaterial.class);
            Root<StudyMaterial> root = query.from(StudyMaterial.class);
            query.where(cb.equal(root.get("category"), category));
            return em.createQuery(query).getResultList();
        } finally {
            em.close();
        }
    }

    public List<StudyMaterial> findPendingMaterialsByCategory(Category category) {
        EntityManager em = getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<StudyMaterial> query = cb.createQuery(StudyMaterial.class);
            Root<StudyMaterial> root = query.from(StudyMaterial.class);
            query.where(cb.equal(root.get("category"), category),
                    cb.equal(root.get("status"), PENDING));
            return em.createQuery(query).getResultList();
        } finally {
            em.close();
        }
    }

    public List<StudyMaterial> findApprovedMaterialsByCategory(Category category) {
        EntityManager em = getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<StudyMaterial> query = cb.createQuery(StudyMaterial.class);
            Root<StudyMaterial> root = query.from(StudyMaterial.class);
            query.where(cb.equal(root.get("category"), category),
                    cb.equal(root.get("status"), APPROVED));
            return em.createQuery(query).getResultList();
        } finally {
            em.close();
        }
    }

    public List<Category> findByName(String name) {
        EntityManager em = getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Category> query = cb.createQuery(Category.class);
            Root<Category> root = query.from(Category.class);
            query.where(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            return em.createQuery(query).getResultList();
        } finally {
            em.close();
        }
    }

    public List<Category> findCategoriesByUser(User user) {
        EntityManager em = getEntityManager();
        try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<Category> query = cb.createQuery(Category.class);
            Root<Category> root = query.from(Category.class);

            Predicate userPredicate = cb.equal(root.get("creator"), user);

            query.where(cb.and(userPredicate));
            return em.createQuery(query).getResultList();
        } finally {
            em.close();
        }
    }
}