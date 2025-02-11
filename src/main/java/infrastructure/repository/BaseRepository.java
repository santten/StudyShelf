package infrastructure.repository;

import infrastructure.config.DatabaseConnection;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;

public abstract class BaseRepository<T> {
    protected EntityManager getEntityManager() {
        return DatabaseConnection.getEntityManagerFactory().createEntityManager();
    }

    public T save(T entity) {
        EntityManager em = getEntityManager();
        EntityTransaction transaction = em.getTransaction();
        try {
            transaction.begin();
            em.persist(entity);
            transaction.commit();
            return entity;
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            throw e;
        } finally {
            em.close();
        }
    }
}
