package infrastructure.repository;

import domain.model.Category;
import domain.model.Permission;
import domain.model.Tag;
import infrastructure.config.DatabaseConnection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;

public class TagRepository extends BaseRepository<Tag> {
    public TagRepository() {
        super(Tag.class);
    }

    //    constructor for testing
    public TagRepository(EntityManagerFactory emf) {
        super(Tag.class, emf);
    }

    public List<Tag> findAll() {
        EntityManager em = DatabaseConnection.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT t FROM Tag t", Tag.class)
                    .getResultList();
        } finally {
            em.close();
        }
    }

    public Tag findByName(String tagName) {
        EntityManager em = DatabaseConnection.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT t FROM Tag t WHERE LOWER(t.tagName) = LOWER(:tagName)", Tag.class)
                    .setParameter("tagName", tagName)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public List<Tag> searchByName(String tagName) {
        EntityManager em = DatabaseConnection.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT t FROM Tag t WHERE LOWER(t.tagName) LIKE LOWER(:tagName)", Tag.class)
                    .setParameter("tagName", "%" + tagName + "%")
                    .getResultList();
        } finally {
            em.close();
        }
    }

}
