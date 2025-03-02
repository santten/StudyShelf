package infrastructure.repository;

import domain.model.Permission;
import domain.model.PermissionType;
import domain.service.PermissionService;
import infrastructure.config.DatabaseConnection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import java.util.List;

import jakarta.persistence.NoResultException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PermissionRepository extends BaseRepository<Permission> {
    private static final Logger logger = LoggerFactory.getLogger(PermissionService.class);

    public PermissionRepository() {
        super(Permission.class);
    }

    public Permission save(Permission permission) {
        return super.save(permission);
    }

    public Permission findById(int id) {
        return super.findById(id);
    }

    public List<Permission> findAll() {
        return super.findAll();
    }

    public void delete(int id) {
        Permission permission = findById(id);
        if (permission != null) {
            super.delete(permission);
        } else {
            logger.warn("Attempted to delete non-existent permission with ID {}", id);
        }
    }

    public Permission findByName(String name) {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT p FROM Permission p WHERE p.name = :name", Permission.class)
                    .setParameter("name", name)
                    .getSingleResult();
        } catch (NoResultException e) {
            logger.warn("Permission '{}' NOT found", name);
            return null;
        } finally {
            em.close();
        }
    }
}
