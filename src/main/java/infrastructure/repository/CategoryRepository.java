package infrastructure.repository;

import domain.model.Category;
import infrastructure.config.DatabaseConnection;
import jakarta.persistence.EntityManager;

public class CategoryRepository extends BaseRepository<Category>  {
    public Category findById(int id) {
        EntityManager em = DatabaseConnection.getEntityManagerFactory().createEntityManager();
        try {
            return em.find(Category.class, id);
        } finally {
            em.close();
        }
    }
}
