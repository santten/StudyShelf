package infrastructure.repository;

import domain.model.Permission;
import domain.model.Role;
import domain.model.RoleType;
import infrastructure.config.DatabaseConnection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.HashSet;
import java.util.Set;


public class RoleRepository  extends BaseRepository<Role> {
    public RoleRepository() {
        super(Role.class);
    }

    //    constructor for testing
    public RoleRepository(EntityManagerFactory emf) {
        super(Role.class, emf);
    }

    @Override
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

            Role savedRole = super.save(role);
            em.getTransaction().commit();
            return savedRole;
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
        EntityManager em = getEntityManager();
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
