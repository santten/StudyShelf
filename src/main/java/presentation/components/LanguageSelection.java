package presentation.components;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

import java.util.Locale;
import java.util.function.Consumer;

public class LanguageSelection {
    public HBox getLanguageSelection(Consumer<Locale> switchLanguage) {
        HBox languageSwitcher = new HBox();

        Button usButton = LanguageButton.createButton("/images/gb.png", "English");
        usButton.setOnAction(e -> switchLanguage.accept(new Locale("en", "US")));

        Button fiButton = LanguageButton.createButton("/images/fi.png", "Suomi/Finnish");
        fiButton.setOnAction(e -> switchLanguage.accept(new Locale("fi", "FI")));

        Button ruButton = LanguageButton.createButton("/images/ru.png", "Russian");
        ruButton.setOnAction(e -> switchLanguage.accept(new Locale("ru", "RU")));

        Button cnButton = LanguageButton.createButton("/images/cn.png", "Chinese");
        cnButton.setOnAction(e -> switchLanguage.accept(new Locale("zh", "CN")));

        languageSwitcher.getChildren().addAll(usButton, fiButton, ruButton, cnButton);
        languageSwitcher.setAlignment(Pos.CENTER);

        return languageSwitcher;
    }
}
