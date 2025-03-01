package domain.service;

import domain.model.*;
import infrastructure.repository.StudyMaterialRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Set;

public class StudyMaterialService {
    private final GoogleDriveService driveService;
    private final StudyMaterialRepository repository;

    public StudyMaterialService(GoogleDriveService driveService, StudyMaterialRepository repository) {
        this.driveService = driveService;
        this.repository = repository;
    }

    public StudyMaterial uploadMaterial(byte[] content, String filename, User uploader, String name, String description, Category category, Set<Tag> tags) throws IOException {
        String fileType = Files.probeContentType(Path.of(filename));
        if (fileType == null) {
            fileType = "application/octet-stream";
        }
        String fileUrl = driveService.uploadFile(content, filename, fileType);

        PreviewGeneratorService previewGenerator = new PreviewGeneratorService();
        byte[] preview = previewGenerator.generatePreview(content, fileType);

        StudyMaterial material = new StudyMaterial(
                uploader,
                name,
                description,
                fileUrl,
                content.length / 1024f,
                fileType,
                LocalDateTime.now(),
                MaterialStatus.PENDING
        );
        material.setCategory(category);
        material.setPreviewImage(preview);
        material.getTags().addAll(tags);

        return repository.save(material);
    }
    public void downloadMaterial(StudyMaterial material, File saveLocation) throws IOException {
        byte[] content = driveService.downloadFile(material.getLink());
        Files.write(saveLocation.toPath(), content);
    }

    public StudyMaterial updateMaterial(StudyMaterial material) {
        return repository.update(material);
    }

}
