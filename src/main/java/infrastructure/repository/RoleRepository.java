package infrastructure.repository;

import domain.model.Role;
import domain.model.RoleType;
import infrastructure.config.DatabaseConnection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;



public class RoleRepository  extends BaseRepository<Role> {
    public Role findById(int id) {
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

    public Role findByName(RoleType roleType) {
        EntityManager em = DatabaseConnection.getEntityManagerFactory().createEntityManager();
        try {
            return em.createQuery("SELECT r FROM Role r WHERE r.name = :name", Role.class)
                    .setParameter("name", roleType)
                    .getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

}
