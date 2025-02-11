package infrastructure.repository;

import domain.model.User;
import infrastructure.config.DatabaseConnection;
import jakarta.persistence.EntityManager;


public class UserRepository extends BaseRepository<User> {
    public User findById(int id) {
        EntityManager em = DatabaseConnection.getEntityManagerFactory().createEntityManager();
        try {
            return em.find(User.class, id);
        } finally {
            em.close();
        }
    }
}
