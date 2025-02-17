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

    public User save(User user) {
        EntityManager em = getEntityManager();
        try {
            em.getTransaction().begin();
            User savedUser = em.merge(user);
            em.getTransaction().commit();
            return savedUser;
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
