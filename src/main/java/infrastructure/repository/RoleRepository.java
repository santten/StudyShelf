package infrastructure.repository;

import domain.model.Role;
import infrastructure.config.DatabaseConnection;
import jakarta.persistence.EntityManager;


public class RoleRepository  extends BaseRepository<Role> {
    public Role findById(int id) {
        EntityManager em = DatabaseConnection.getEntityManagerFactory().createEntityManager();
        try {
            return em.find(Role.class, id);
        } finally {
            em.close();
        }
    }
}
