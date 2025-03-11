package infrastructure.repository;

import domain.model.Permission;
import domain.model.User;
import infrastructure.config.DatabaseConnection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;


public class UserRepository extends BaseRepository<User> {

    public UserRepository() {
        super(User.class);
    }

    //    constructor for testing
    public UserRepository(EntityManagerFactory emf) {
        super(User.class, emf);
    }

    public User findByEmail(String email) {
        EntityManager em = getEntityManager();
        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<User> query = cb.createQuery(User.class);
        Root<User> root = query.from(User.class);
        query.where(cb.equal(root.get("email"), email));

        try {
            return em.createQuery(query).getSingleResult();
        } catch (NoResultException e) {
            return null;
        } finally {
            em.close();
        }
    }

    public List<User> findAll() {
        EntityManager em = getEntityManager();
        try {
            return em.createQuery("SELECT u FROM User u", User.class).getResultList();
        } finally {
            em.close();
        }
    }

//    public User update(User user) {
//        EntityManager em = getEntityManager();
//        EntityTransaction transaction = em.getTransaction();
//        try {
//            transaction.begin();
//
//            User existingUser = em.find(User.class, user.getUserId());
//            if (existingUser == null) {
//                throw new IllegalArgumentException("User with ID " + user.getUserId() + " does not exist.");
//            }
//
//            User emailOwner = findByEmail(user.getEmail());
//            if (emailOwner != null && !emailOwner.getUserId().equals(user.getUserId())) {
//                throw new IllegalArgumentException("Email already taken!");
//            }
//
//            existingUser.setFirstName(user.getFirstName());
//            existingUser.setLastName(user.getLastName());
//            existingUser.setEmail(user.getEmail());
//            existingUser.setPassword(user.getPassword());
//            existingUser.setRole(user.getRole());
//
//            transaction.commit();
//            return existingUser;
//        } catch (Exception e) {
//            if (transaction.isActive()) {
//                transaction.rollback();
//            }
//            throw e;
//        } finally {
//            em.close();
//        }
//    }

    public User updateUserFields(int userId, String firstName, String lastName, String email) {
        EntityManager em = getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();

            User existingUser = em.find(User.class, Integer.valueOf(userId));
            if (existingUser == null) {
                throw new IllegalArgumentException("User with ID " + userId + " does not exist.");
            }

            User emailOwner = findByEmail(email);
            if (emailOwner != null && emailOwner.getUserId() != userId) {
                throw new IllegalArgumentException("Email already taken!");
            }

            existingUser.setFirstName(firstName);
            existingUser.setLastName(lastName);
            existingUser.setEmail(email);

            transaction.commit();
            return existingUser;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }

    public void updateUserPassword(int userId, String newPassword) {
        EntityManager em = getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            User user = em.find(User.class, Integer.valueOf(userId));
            if (user == null) {
                throw new IllegalArgumentException("User with ID " + userId + " does not exist.");
            }
            user.setPassword(newPassword);
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

    public void delete(User user) {
        if (user == null) {
            throw new IllegalArgumentException("User cannot be null.");
        }
        deleteById(user.getUserId());
    }

    public boolean deleteById(int id) {
        EntityManager em = getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            User user = em.find(User.class, Integer.valueOf(id));
            if (user == null) {
                return false;
            }
            em.remove(user);
            transaction.commit();
            return true;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            return false;
        } finally {
            if (em.isOpen()) {
                em.close();
            }
        }
    }
}
