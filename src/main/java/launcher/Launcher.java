package launcher;

import infrastructure.config.DatabaseInitializer;
import io.github.cdimascio.dotenv.Dotenv;
import presentation.view.StudyShelfApplication;

public class Launcher {
    public static void main(String[] args) {
        DatabaseInitializer dbInit = new DatabaseInitializer();
        dbInit.initializeRolesAndPermissions();
        StudyShelfApplication.launch(StudyShelfApplication.class);
    }
}