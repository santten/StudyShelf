package infrastructure.repository;

import domain.model.Role;
import infrastructure.config.DatabaseConnection;
import jakarta.persistence.EntityManager;


public class RoleRepository  extends BaseRepository<Role> {
    public Role findById(Long id) {
        EntityManager em = DatabaseConnection.getEntityManagerFactory().createEntityManager();
        try {
            return em.find(Role.class, id);
        } finally {
            em.close();
        }
    }


    public Role save(Role role) {
        EntityManager em = getEntityManager();
        Role existingRole = findById(role.getId());

        if (existingRole != null) {
            return existingRole;
        }

        try {
            em.getTransaction().begin();
            em.persist(role);
            em.getTransaction().commit();
            return role;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
