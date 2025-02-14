import presentation.view.StudyShelfApplication;

import domain.model.*;
import domain.service.StudyMaterialService;
import infrastructure.repository.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import domain.service.GoogleDriveService;
import java.nio.file.Files;
import java.nio.file.Path;

public class Launcher {
    public static void main(String[] args) {
        StudyShelfApplication.launch(StudyShelfApplication.class);
    }
}