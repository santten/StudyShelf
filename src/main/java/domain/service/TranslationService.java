package domain.service;

import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class TranslationService {
    private static final Logger logger = LoggerFactory.getLogger(TranslationService.class);
    private static final List<String> SUPPORTED_LANGUAGES = List.of("en", "fi", "ru", "zh");
    private Translate translate;

    public TranslationService() {
        initializeTranslationService();
    }

    private void initializeTranslationService() {
        Properties properties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("credentials/translate-api.properties")) {
            if (input == null) {
                logger.error("Unable to find translate-api.properties");
                throw new RuntimeException("API keys file not found");
            }

            properties.load(input);
            String apiKey = properties.getProperty("google.translate.api.key");

            if (apiKey == null || apiKey.trim().isEmpty()) {
                logger.error("Google Translate API key not found in properties file");
                throw new RuntimeException("API key not configured");
            }

            TranslateOptions options = TranslateOptions.newBuilder()
                    .setApiKey(apiKey)
                    .build();

            this.translate = options.getService();
            logger.info("Translation service initialized successfully");
        } catch (IOException e) {
            logger.error("Failed to load API key", e);
            throw new RuntimeException("Translation service initialization failed", e);
        }
    }

    public Map<String, String> translateToAllLanguages(String text, String sourceLanguage) {
        Map<String, String> translations = new HashMap<>();

        translations.put(sourceLanguage, text);

        // Translate to all other supported languages
        for (String targetLang : SUPPORTED_LANGUAGES) {
            if (!targetLang.equals(sourceLanguage)) {
                try {
                    Translation translation = translate.translate(
                            text,
                            Translate.TranslateOption.sourceLanguage(sourceLanguage),
                            Translate.TranslateOption.targetLanguage(targetLang)
                    );
                    translations.put(targetLang, translation.getTranslatedText());
                } catch (Exception e) {
                    logger.error("Translation failed for language: " + targetLang, e);
                    translations.put(targetLang, text);
                }
            }
        }

        return translations;
    }

    public String translateText(String text, String sourceLanguage, String targetLanguage) {
        if (sourceLanguage.equals(targetLanguage)) {
            return text;
        }

        try {
            Translation translation = translate.translate(
                    text,
                    Translate.TranslateOption.sourceLanguage(sourceLanguage),
                    Translate.TranslateOption.targetLanguage(targetLanguage)
            );
            return translation.getTranslatedText();
        } catch (Exception e) {
            logger.error("Translation failed", e);
            return text;
        }
    }

    public String translate(String text, String sourceLanguage, String targetLanguage) {
        if (text == null || text.trim().isEmpty()) {
            return "";
        }

        try {
            com.google.cloud.translate.Translation translation = translate.translate(
                    text,
                    com.google.cloud.translate.Translate.TranslateOption.sourceLanguage(sourceLanguage),
                    com.google.cloud.translate.Translate.TranslateOption.targetLanguage(targetLanguage)
            );

            return translation.getTranslatedText();
        } catch (Exception e) {
            logger.error("Translation failed: {}", e.getMessage());
            throw new RuntimeException("Translation failed", e);
        }
    }
}
