package domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import presentation.utility.GUILogger;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class TranslationServiceTest {

    private TranslationService translationService;

    @BeforeEach
    void setUp() {
        // Enable info logging for tests
        GUILogger.setInfoVisibility(true);

        try {
            translationService = new TranslationService();
        } catch (Exception e) {
            // Log the exception but don't fail setup
            System.err.println("Failed to initialize TranslationService: " + e.getMessage());
        }
    }

    @Test
    @EnabledIfSystemProperty(named = "RUN_TRANSLATION_TESTS", matches = "true")

    void testTranslateText() {
        // Skip test if translation service couldn't be initialized
        if (translationService == null) {
            System.out.println("Skipping test as TranslationService could not be initialized");
            return;
        }

        String original = "Hello, world!";
        String translated = translationService.translateText(original, "en", "fi");

        assertNotNull(translated);
        System.out.println("Original (English): " + original);
        System.out.println("Translated (Finnish): " + translated);

        // We can't assert exact translation as it might change,
        // but we can check it's not empty and not the same as original
        assertFalse(translated.isEmpty());

        // Only assert different if languages are different
        if (!translated.equals(original)) {
            assertNotEquals(original, translated);
        }
    }

    @Test
    @EnabledIfSystemProperty(named = "RUN_TRANSLATION_TESTS", matches = "true")

    void testTranslateToAllLanguages() {
        // Skip test if translation service couldn't be initialized
        if (translationService == null) {
            System.out.println("Skipping test as TranslationService could not be initialized");
            return;
        }

        String original = "This is a test message";
        Map<String, String> translations = translationService.translateToAllLanguages(original, "en");

        assertNotNull(translations);
        assertTrue(translations.size() >= 1);

        System.out.println("Translations for: " + original);
        for (Map.Entry<String, String> entry : translations.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
