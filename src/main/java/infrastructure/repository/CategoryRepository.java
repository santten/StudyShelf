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

public class CategoryRepository extends BaseRepository<Category>  {
    public Category findById(int id) {
        EntityManager em = DatabaseConnection.getEntityManagerFactory().createEntityManager();
        try {
            return em.find(Category.class, id);
        } finally {
            em.close();
        }
    }
    public List<StudyMaterial> findMaterialsByCategory(Category category) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<StudyMaterial> query = cb.createQuery(StudyMaterial.class);
        Root<StudyMaterial> root = query.from(StudyMaterial.class);
        query.where(cb.equal(root.get("category"), category));
        return em.createQuery(query).getResultList();
    }
    public List<Category> findAll() {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Category> query = cb.createQuery(Category.class);
        Root<Category> root = query.from(Category.class);
        return em.createQuery(query).getResultList();
    }
    public List<Category> findByName(String name) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Category> query = cb.createQuery(Category.class);
        Root<Category> root = query.from(Category.class);
        query.where(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
        return em.createQuery(query).getResultList();
    }

    public List<StudyMaterial> findMaterialsByUserInCategory(User user, Category category) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<StudyMaterial> query = cb.createQuery(StudyMaterial.class);
        Root<StudyMaterial> root = query.from(StudyMaterial.class);

        Predicate userPredicate = cb.equal(root.get("uploader"), user);
        Predicate categoryPredicate = cb.equal(root.get("category"), category);

        query.where(cb.and(userPredicate, categoryPredicate));
        return em.createQuery(query).getResultList();
    }

    public List<StudyMaterial> findMaterialsExceptUserInCategory(User user, Category category) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<StudyMaterial> query = cb.createQuery(StudyMaterial.class);
        Root<StudyMaterial> root = query.from(StudyMaterial.class);

        Predicate userPredicate = cb.notEqual(root.get("uploader"), user);
        Predicate categoryPredicate = cb.equal(root.get("category"), category);

        query.where(cb.and(userPredicate, categoryPredicate));
        return em.createQuery(query).getResultList();
    }

    public List<Category> findCategoriesByUser(User user) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<Category> query = cb.createQuery(Category.class);
        Root<Category> root = query.from(Category.class);

        Predicate userPredicate = cb.equal(root.get("creator"), user);

        query.where(cb.and(userPredicate));
        return em.createQuery(query).getResultList();
    }
}