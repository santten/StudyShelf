package infrastructure.repository;

import domain.model.StudyMaterial;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import jakarta.persistence.NoResultException;
import jakarta.persistence.Query;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StudyMaterialTranslationRepository extends BaseRepository<StudyMaterial> {
    private static final Logger logger = LoggerFactory.getLogger(StudyMaterialTranslationRepository.class);

    public StudyMaterialTranslationRepository() {
        super(StudyMaterial.class);
    }

    public void saveTranslations(int materialId, Map<String, String> nameTranslations,
                                 Map<String, String> descriptionTranslations) {
        EntityManager em = getEntityManager();
        EntityTransaction transaction = em.getTransaction();

        try {
            transaction.begin();

            for (String languageCode : nameTranslations.keySet()) {
                String name = nameTranslations.get(languageCode);
                String description = descriptionTranslations.getOrDefault(languageCode, "");

                // Check if translation already exists
                Query query = em.createNativeQuery(
                        "SELECT COUNT(*) FROM study_material_translations " +
                                "WHERE material_id = ? AND language_code = ?");
                query.setParameter(1, materialId);
                query.setParameter(2, languageCode);

                Long count = ((Number) query.getSingleResult()).longValue();

                if (count > 0) {
                    // Update existing translation
                    Query updateQuery = em.createNativeQuery(
                            "UPDATE study_material_translations SET name = ?, description = ? " +
                                    "WHERE material_id = ? AND language_code = ?");
                    updateQuery.setParameter(1, name);
                    updateQuery.setParameter(2, description);
                    updateQuery.setParameter(3, materialId);
                    updateQuery.setParameter(4, languageCode);
                    updateQuery.executeUpdate();
                } else {
                    // Insert new translation
                    Query insertQuery = em.createNativeQuery(
                            "INSERT INTO study_material_translations (material_id, language_code, name, description) " +
                                    "VALUES (?, ?, ?, ?)");
                    insertQuery.setParameter(1, materialId);
                    insertQuery.setParameter(2, languageCode);
                    insertQuery.setParameter(3, name);
                    insertQuery.setParameter(4, description);
                    insertQuery.executeUpdate();
                }
            }

            transaction.commit();
            logger.info("Saved translations for material ID: {}", materialId);
        } catch (Exception e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            logger.error("Failed to save study material translations", e);
            throw new RuntimeException("Failed to save study material translations", e);
        } finally {
            em.close();
        }
    }

    public Map<String, String> getNameTranslations(int materialId) {
        return getTranslations(materialId, "name");
    }

    public Map<String, String> getDescriptionTranslations(int materialId) {
        return getTranslations(materialId, "description");
    }

    private Map<String, String> getTranslations(int materialId, String field) {
        EntityManager em = getEntityManager();
        Map<String, String> translations = new HashMap<>();

        try {
            Query query = em.createNativeQuery(
                    "SELECT language_code, " + field + " FROM study_material_translations " +
                            "WHERE material_id = ?");
            query.setParameter(1, materialId);

            List<Object[]> results = query.getResultList();

            for (Object[] row : results) {
                String languageCode = (String) row[0];
                String translatedText = (String) row[1];
                translations.put(languageCode, translatedText);
            }

            return translations;
        } catch (Exception e) {
            // Log as warning instead of error for expected conditions
            if (e instanceof NoResultException) {
                logger.warn("No translations found for material ID: {} and field: {}", materialId, field);
            } else {
                logger.error("Failed to get study material translations for field: {}", field, e);
            }
            return translations;
        } finally {
            em.close();
        }
    }
}
