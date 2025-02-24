package infrastructure.repository;

import domain.model.Permission;
import domain.model.Role;
import domain.model.RoleType;
import infrastructure.config.DatabaseConnection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.HashSet;
import java.util.Set;


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
        try {
            em.getTransaction().begin();

            Role existingRole = findByName(role.getName());
            if (existingRole != null) {
                return existingRole;
            }

            Set<Permission> mergedPermissions = new HashSet<>();
            for (Permission permission : role.getPermissions()) {
                Permission mergedPermission = em.find(Permission.class, permission.getId());
                if (mergedPermission != null) {
                    mergedPermissions.add(mergedPermission);
                }
            }

            role.getPermissions().clear();
            role.getPermissions().addAll(mergedPermissions);

            em.persist(role);
            em.flush();
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
