package presentation.view;

import domain.model.User;
import domain.service.Session;
import presentation.utility.CustomAlert;

import java.io.IOException;
import java.util.ResourceBundle;

import static presentation.view.Screen.SCREEN_PROFILE;

public class CurrentUserManager {
    public static User get() {
        return Session.getInstance().getCurrentUser();
    }

    public static void logout(){
        ResourceBundle rb = LanguageManager.getInstance().getBundle();
        if (CustomAlert.confirm(rb.getString("alertLoggingOut"), rb.getString("alertAreYouSure"), rb.getString("alertLogoutSubtitle"), true)) {
            Session.getInstance().logout();
            SceneManager.getInstance().logout();
        }
    }
}
