package infrastructure.repository;

import domain.model.Tag;
import infrastructure.config.DatabaseConnection;
import jakarta.persistence.EntityManager;

public class TagRepository extends BaseRepository<Tag> {

    public Tag findById(int id) {
        EntityManager em = DatabaseConnection.getEntityManagerFactory().createEntityManager();
        try {
            return em.find(Tag.class, id);
        } finally {
            em.close();
        }
    }

}
