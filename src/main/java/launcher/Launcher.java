package launcher;

import infrastructure.config.DatabaseInitializer;
import presentation.view.StudyShelfApplication;

public class Launcher {
    public static void main(String[] args) {
        DatabaseInitializer dbInit = new DatabaseInitializer();
        dbInit.initializeRolesAndPermissions();
        StudyShelfApplication.launch(StudyShelfApplication.class);
    }
}