package infrastructure.repository;

import domain.model.Review;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ReviewTranslationRepository extends BaseRepository<Review> {
    private static final Logger logger = LoggerFactory.getLogger(ReviewTranslationRepository.class);

    public ReviewTranslationRepository() {
        super(Review.class);
    }

    public void saveTranslations(int reviewId, Map<String, String> textTranslations) {
        EntityManager em = getEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            for (Map.Entry<String, String> entry : textTranslations.entrySet()) {
                String languageCode = entry.getKey();
                String translatedText = entry.getValue();

                // Check if translation already exists
                Query query = em.createNativeQuery(
                        "SELECT COUNT(*) FROM review_translations " +
                                "WHERE review_id = ? AND language_code = ?");
                query.setParameter(1, reviewId);
                query.setParameter(2, languageCode);

                Long count = ((Number) query.getSingleResult()).longValue();

                if (count > 0) {
                    // Update existing translation
                    Query updateQuery = em.createNativeQuery(
                            "UPDATE review_translations SET review_text = ? " +
                                    "WHERE review_id = ? AND language_code = ?");
                    updateQuery.setParameter(1, translatedText);
                    updateQuery.setParameter(2, reviewId);
                    updateQuery.setParameter(3, languageCode);
                    updateQuery.executeUpdate();
                } else {
                    // Insert new translation
                    Query insertQuery = em.createNativeQuery(
                            "INSERT INTO review_translations (review_id, language_code, review_text) " +
                                    "VALUES (?, ?, ?)");
                    insertQuery.setParameter(1, reviewId);
                    insertQuery.setParameter(2, languageCode);
                    insertQuery.setParameter(3, translatedText);
                    insertQuery.executeUpdate();
                }
            }

            transaction.commit();
            logger.info("Saved translations for review ID: {}", reviewId);
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.error("Failed to save review translations", e);
            throw new RuntimeException("Failed to save review translations", e);
        } finally {
            em.close();
        }
    }

    public String getTranslation(int reviewId, String languageCode) {
        EntityManager em = getEntityManager();
        try {
            Query query = em.createNativeQuery(
                    "SELECT review_text FROM review_translations " +
                            "WHERE review_id = ? AND language_code = ?");
            query.setParameter(1, reviewId);
            query.setParameter(2, languageCode);
            Object result = query.getSingleResult();
            return result != null ? result.toString() : null;
        } catch (NoResultException e) {
            // This is an expected condition when no translation exists
            logger.info("No translation found for review {} in language {}", reviewId, languageCode);
            return null;
        } catch (Exception e) {
            // This is for unexpected errors
            logger.error("Failed to get review translation", e);
            return null;
        } finally {
            em.close();
        }
    }


    public Map<String, String> getAllTranslations(int reviewId) {
        EntityManager em = getEntityManager();
        Map<String, String> translations = new HashMap<>();

        try {
            Query query = em.createNativeQuery(
                    "SELECT language_code, review_text FROM review_translations " +
                            "WHERE review_id = ?");
            query.setParameter(1, reviewId);

            List<Object[]> results = query.getResultList();

            for (Object[] row : results) {
                String languageCode = (String) row[0];
                String translatedText = (String) row[1];
                translations.put(languageCode, translatedText);
            }

            return translations;
        } catch (Exception e) {
            logger.error("Failed to get review translations", e);
            return translations;
        } finally {
            em.close();
        }
    }
}
