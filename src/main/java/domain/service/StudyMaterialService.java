package domain.service;

import domain.model.MaterialStatus;
import domain.model.StudyMaterial;
import domain.model.User;
import infrastructure.repository.StudyMaterialRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;

public class StudyMaterialService {
    private final GoogleDriveService driveService;
    private final StudyMaterialRepository repository;

    public StudyMaterialService(GoogleDriveService driveService, StudyMaterialRepository repository) {
        this.driveService = driveService;
        this.repository = repository;
    }

    public StudyMaterial uploadMaterial(byte[] content, String filename, User uploader, String name, String description) throws IOException {
        String fileType = Files.probeContentType(Path.of(filename));
        String fileUrl = driveService.uploadFile(content, filename, fileType);

        StudyMaterial material = new StudyMaterial(
                uploader,
                name,
                description,
                fileUrl,
                content.length / 1024f,
                fileType,
                LocalDateTime.now(),
                MaterialStatus.PENDING  // New materials start as PENDING
        );

        return repository.save(material);
    }
}
