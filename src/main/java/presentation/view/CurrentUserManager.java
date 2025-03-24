package presentation.view;

import domain.model.User;
import domain.service.Session;
import presentation.utility.CustomAlert;

import java.io.IOException;

import static presentation.view.Screen.SCREEN_PROFILE;

public class CurrentUserManager {
    public static User get() {
        return Session.getInstance().getCurrentUser();
    }

    public static void logout(){
        if (CustomAlert.confirm("Logging out", "Log out from StudyShelf?", "You're welcome back anytime!", true)) {
            Session.getInstance().logout();
            try {
                SceneManager.getInstance().logout();
            } catch (IOException ex) {
                SceneManager.getInstance().displayErrorPage("Something went wrong when logging out...", SCREEN_PROFILE, "Go back");
                throw new RuntimeException(ex);
            }
        }
    }
}
