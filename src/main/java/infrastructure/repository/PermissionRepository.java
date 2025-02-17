package infrastructure.repository;

import domain.model.Permission;
import domain.service.PermissionService;
import infrastructure.config.DatabaseConnection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PermissionRepository {
    private static final Logger logger = LoggerFactory.getLogger(PermissionService.class);

    public Permission save(Permission permission) {
        EntityManager em = DatabaseConnection.getEntityManagerFactory().createEntityManager();
        em.getTransaction().begin();
        em.persist(permission);
        em.getTransaction().commit();
        return permission;
    }

    public Permission findById(Long id) {
        EntityManager em = DatabaseConnection.getEntityManagerFactory().createEntityManager();
        try {
            return em.find(Permission.class, id);
        } finally {
            em.close();
        }
    }

    public List<Permission> findAll() {
        EntityManager em = DatabaseConnection.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Permission p", Permission.class).getResultList();
        } finally {
            em.close();
        }
    }

    public void delete(Long id) {
        EntityManager em = DatabaseConnection.getEntityManagerFactory().createEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            Permission permission = em.find(Permission.class, id);
            if (permission != null) {
                em.remove(permission);
            }
            transaction.commit();
        } catch (Exception e) {
            transaction.rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }

    public Permission findByName(String name) {
        EntityManager em = DatabaseConnection.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT p FROM Permission p WHERE p.name = :name", Permission.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (Exception e) {
            logger.error("Permission {} NOT found: ", name);
            return null;
        } finally {
            em.close();
        }
    }
}
