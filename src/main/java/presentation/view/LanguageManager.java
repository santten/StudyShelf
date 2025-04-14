package presentation.view;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;
import java.util.ResourceBundle;
import java.util.prefs.Preferences;

public class LanguageManager {
    private static final Logger logger = LoggerFactory.getLogger(LanguageManager.class);
    private Locale language;
    private final Preferences preferences;

    private LanguageManager() {
        preferences = Preferences.userNodeForPackage(LanguageManager.class);

        String savedLanguage = preferences.get("language", "en");
        this.language = new Locale(savedLanguage);
    }

    private static final class InstanceHolder {
        private static final LanguageManager instance = new LanguageManager();
    }

    public static LanguageManager getInstance() {
        return InstanceHolder.instance;
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
        return ResourceBundle.getBundle("messages", InstanceHolder.instance.getLanguage());
    }

    public String getCurrentLanguage() {
        return language.getLanguage();
    }
}

