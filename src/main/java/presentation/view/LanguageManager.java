package presentation.view;

import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageManager {
    private static LanguageManager instance;
    private Locale language;

    public static LanguageManager getInstance() {
        if (instance == null) {
            instance = new LanguageManager();
            instance.setLanguage(new Locale("en", "US"));
        }
        return instance;
    }

    public Locale getLanguage() {
        return language;
    }

    public void setLanguage(Locale language) {
        this.language = language;
    }

    public ResourceBundle getBundle() {
        return ResourceBundle.getBundle("messages", instance.getLanguage());
    }
}
