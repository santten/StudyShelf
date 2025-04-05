package presentation.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class LanguageManager {
    private static final Logger logger = LoggerFactory.getLogger(LanguageManager.class);
    private static LanguageManager instance;
    private Locale language;
    private final Preferences preferences;

    public static final List<LanguageOption> SUPPORTED_LANGUAGES = Arrays.asList(
            new LanguageOption("en", "English"),
            new LanguageOption("fi", "Finnish"),
            new LanguageOption("ru", "Russian"),
            new LanguageOption("zh", "Chinese")
    );

    private LanguageManager() {
        preferences = Preferences.userNodeForPackage(LanguageManager.class);
        // Load saved language or use default
        String savedLanguage = preferences.get("language", "en");
        this.language = new Locale(savedLanguage);
    }

    public static LanguageManager getInstance() {
        if (instance == null) {
            instance = new LanguageManager();
        }
        return instance;
    }

    public Locale getLanguage() {
        return language;
    }

    public void setLanguage(Locale language) {
        this.language = language;
        // Save language preference
        preferences.put("language", language.getLanguage());
        logger.info("Language changed to: {}", language.getLanguage());
    }

    public ResourceBundle getBundle() {
        return ResourceBundle.getBundle("messages", instance.getLanguage());
    }

    public String getCurrentLanguage() {
        return language.getLanguage();
    }

    public String getCurrentLanguageDisplayName() {
        return SUPPORTED_LANGUAGES.stream()
                .filter(lang -> lang.getCode().equals(getCurrentLanguage()))
                .findFirst()
                .map(LanguageOption::getDisplayName)
                .orElse("Unknown");
    }

    public boolean isSupported(String languageCode) {
        return SUPPORTED_LANGUAGES.stream()
                .anyMatch(lang -> lang.getCode().equals(languageCode));
    }

    public static class LanguageOption {
        private final String code;
        private final String displayName;

        public LanguageOption(String code, String displayName) {
            this.code = code;
            this.displayName = displayName;
        }

        public String getCode() {
            return code;
        }

        public String getDisplayName() {
            return displayName;
        }

        @Override
        public String toString() {
            return displayName;
        }
    }
}

