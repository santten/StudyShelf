package infrastructure.repository;

import domain.model.*;
import infrastructure.config.DatabaseConnection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
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

    public void deleteByUser(User user) {
        EntityManager em = getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            List<Tag> tags = em.createQuery("SELECT t FROM Tag t WHERE t.creator = :user", Tag.class)
                    .setParameter("user", user)
                    .getResultList();

            for (Tag tag : tags) {
                List<StudyMaterial> materials = em.createQuery("SELECT m FROM StudyMaterial m JOIN m.tags t WHERE t = :tag", StudyMaterial.class)
                        .setParameter("tag", tag)
                        .getResultList();

                for (StudyMaterial material : materials) {
                    material.getTags().remove(tag);
                    em.merge(material);
                }

                Tag managedTag = em.find(Tag.class, tag.getTagId());
                if (managedTag != null) {
                    em.remove(managedTag);
                }
            }

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
