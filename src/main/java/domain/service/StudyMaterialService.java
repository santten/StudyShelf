package domain.service;

import domain.model.*;
import infrastructure.repository.StudyMaterialRepository;
import domain.model.PermissionType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.List;

import static domain.model.RoleType.ADMIN;

public class StudyMaterialService {
    private static final Logger logger = LoggerFactory.getLogger(StudyMaterialService.class);

    private final GoogleDriveService driveService;
    private final StudyMaterialRepository repository;
    private final PermissionService permissionService;

    public StudyMaterialService(GoogleDriveService driveService, StudyMaterialRepository repository, PermissionService permissionService) {
        this.driveService = driveService;
        this.repository = repository;
        this.permissionService = permissionService;
    }

    // CREATE_RESOURCE
    public StudyMaterial uploadMaterial(byte[] content, String filename, User uploader, String name, String description, Category category, Set<Tag> tags) throws IOException {
        if (!permissionService.hasPermission(uploader, PermissionType.CREATE_RESOURCE)) {
            throw new SecurityException("You do not have permission to upload study materials.");
        }

        String fileType = Files.probeContentType(Path.of(filename));
        if (fileType == null) {
            fileType = "application/octet-stream";
        }
        String fileUrl = driveService.uploadFile(content, filename, fileType);

        PreviewGeneratorService previewGenerator = new PreviewGeneratorService();
        byte[] preview = previewGenerator.generatePreview(content, fileType);

        // auto-approve materials submitted by course owner
        MaterialStatus status = (category.getCreator().getUserId() == uploader.getUserId()) ? MaterialStatus.APPROVED : MaterialStatus.PENDING;

        StudyMaterial material = new StudyMaterial(
                uploader,
                name,
                description,
                fileUrl,
                content.length / 1024f,
                fileType,
                LocalDateTime.now(),
                status
        );
        material.setCategory(category);
        material.setPreviewImage(preview);
        material.getTags().addAll(tags);

        logger.info("User {} uploaded new study material: {}", uploader.getEmail(), name);
        return repository.save(material);
    }

    // UPDATE_OWN_RESOURCE
    public StudyMaterial updateMaterial(User user, StudyMaterial updatedMaterial) {
        StudyMaterial existingMaterial = repository.findById(updatedMaterial.getMaterialId());
        if (existingMaterial == null) {
            throw new RuntimeException("Material not found.");
        }

        int ownerId = existingMaterial.getUploader().getUserId();
        boolean canUpdateOwn = permissionService.hasPermissionOnEntity(user, PermissionType.UPDATE_OWN_RESOURCE, ownerId);

        if (!canUpdateOwn) {
            logger.warn("User {} attempted to update study material {} without permission", user.getEmail(), existingMaterial.getName());
            throw new SecurityException("You do not have permission to update this study material.");
        }

        existingMaterial.setName(updatedMaterial.getName());
        existingMaterial.setDescription(updatedMaterial.getDescription());
        existingMaterial.setCategory(updatedMaterial.getCategory());
        existingMaterial.setStatus(MaterialStatus.PENDING);

        logger.info("User {} updated study material: {} (pending review)", user.getEmail(), existingMaterial.getName());
        return repository.save(existingMaterial);
    }

    // READ_RESOURCES
    public List<StudyMaterial> getAllResources(User user) {
        if (!permissionService.hasPermission(user, PermissionType.READ_RESOURCES)) {
            throw new SecurityException("You do not have permission to read study materials.");
        }
        return repository.findAll();
    }

    // READ_APPROVED_RESOURCES
    public List<StudyMaterial> getApprovedMaterials(User user) {
        if (!permissionService.hasPermission(user, PermissionType.READ_RESOURCES)) {
            throw new SecurityException("You do not have permission to read study materials.");
        }
        return repository.findByStatus(MaterialStatus.APPROVED);
    }

    // READ_PENDING_RESOURCES (For Reviewers Only)
    public List<StudyMaterial> getPendingMaterialsForReview(User user) {
        if (!permissionService.hasPermission(user, PermissionType.REVIEW_PENDING_RESOURCES)) {
            throw new SecurityException("You do not have permission to review pending materials.");
        }
        return repository.findByStatus(MaterialStatus.PENDING);
    }

//    public void downloadMaterial(StudyMaterial material, File saveLocation) throws IOException {
//        byte[] content = driveService.downloadFile(material.getLink());
//        Files.write(saveLocation.toPath(), content);
//        logger.info("Material {} downloaded to {}", material.getName(), saveLocation.getPath());
//    }

    public void downloadMaterial(User user, StudyMaterial material, File saveLocation) throws IOException {
//        boolean canDownloadPending = permissionService.hasPermission(user, PermissionType.REVIEW_PENDING_RESOURCES);
//        boolean canDownloadApproved = material.getStatus() == MaterialStatus.APPROVED;
//
//        if (!(canDownloadApproved || canDownloadPending)) {
//            throw new SecurityException("You do not have permission to download this material.");
//        }

        boolean canDownload = (material.getStatus() == MaterialStatus.APPROVED && permissionService.hasPermission(user, PermissionType.READ_RESOURCES))
                || permissionService.hasPermission(user, PermissionType.REVIEW_PENDING_RESOURCES);

        if (!canDownload) {
            throw new SecurityException("You do not have permission to download this material.");
        }

        byte[] content = driveService.downloadFile(material.getLink());
        Files.write(saveLocation.toPath(), content);
        logger.info("User {} downloaded material: {}", user.getEmail(), material.getName());
    }


    public void deleteMaterial(User user, StudyMaterial material) {
        int userId = user.getUserId();
        int courseOwnerId = material.getCategory().getCreator().getUserId();
        int materialOwnerId = material.getUploader().getUserId();

        // DELETE_OWN_RESOURCE
        boolean canDeleteOwn = permissionService.hasPermission(user, PermissionType.DELETE_OWN_RESOURCE) && (userId == materialOwnerId);
        // DELETE_COURSE_RESOURCE
        boolean canDeleteCourse = permissionService.hasPermission(user, PermissionType.DELETE_COURSE_RESOURCE) && (userId == courseOwnerId);
        //
        boolean canDeleteAny = permissionService.hasPermission(user, PermissionType.DELETE_ANY_RESOURCE);

        if (!(canDeleteOwn || canDeleteCourse || canDeleteAny)) {
            logger.warn("User {} attempted to delete study material {} without permission", user.getEmail(), material.getName());
            throw new SecurityException("You do not have permission to delete this study material.");
        }

        repository.delete(material);
        logger.info("User {} deleted study material: {}", user.getEmail(), material.getName());
    }

    // APPROVE_RESOURCE
    public void approveMaterial(User user, StudyMaterial material) {
        if (!permissionService.hasApprovalPermission(user)) {
            throw new SecurityException("You do not have permission to approve study materials.");
        }

        if (material.getStatus() != MaterialStatus.PENDING) {
            throw new IllegalStateException("This material has already been processed.");
        }

        material.setStatus(MaterialStatus.APPROVED);
        repository.updateMaterialStatus(material.getMaterialId(), MaterialStatus.APPROVED);

        logger.info("User {} approved study material: {}", user.getEmail(), material.getName());
    }

    // REJECT_RESOURCE
    public void rejectMaterial(User user, StudyMaterial material) {
        if (!permissionService.hasApprovalPermission(user)) {
            throw new SecurityException("You do not have permission to reject study materials.");
        }

        if (material.getStatus() != MaterialStatus.PENDING) {
            throw new IllegalStateException("This material has already been processed.");
        }

        material.setStatus(MaterialStatus.REJECTED);
        repository.updateMaterialStatus(material.getMaterialId(), MaterialStatus.REJECTED);

        logger.info("User {} rejected study material: {}", user.getEmail(), material.getName());
    }

    public StudyMaterial updateMaterial(StudyMaterial material) {
        return repository.update(material);
    }

    public void updateDescription(User user, StudyMaterial sm, String description) {
        if ((user.getUserId() == sm.getUploader().getUserId()
                && permissionService.hasPermission(user, PermissionType.UPDATE_OWN_RESOURCE))
                || user.getRole().getName() == ADMIN) {
            repository.updateMaterialDescription(sm.getMaterialId(), description);
        } else {
            throw new SecurityException("You do not have permission to modify this description.");
        }
    }

    public void updateTitle(User user, StudyMaterial sm, String title) {
        if ((user.getUserId() == sm.getUploader().getUserId()
                && permissionService.hasPermission(user, PermissionType.UPDATE_OWN_RESOURCE))
                || user.getRole().getName() == ADMIN) {
            repository.updateMaterialTitle(sm.getMaterialId(), title);
        } else {
            throw new SecurityException("You do not have permission to modify this title.");
        }
    }


    public List<StudyMaterial> findByUser(User user){
        if (!permissionService.hasPermission(user, PermissionType.READ_RESOURCES)) {
            throw new SecurityException("You do not have permission to view these study materials.");
        }
        return repository.findByUser(user);
    }
}
