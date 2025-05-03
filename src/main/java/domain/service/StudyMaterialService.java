package domain.service;

import domain.model.*;
import infrastructure.repository.StudyMaterialRepository;
import infrastructure.repository.StudyMaterialTranslationRepository;
import domain.model.PermissionType;
import javafx.application.Platform;
import javafx.concurrent.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import presentation.view.LanguageManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.List;

import static domain.model.RoleType.ADMIN;

/**
 * Service class responsible for handling study material operations
 * including upload, download, review, update, and deletion.
 * Integrates with Google Drive and manages permission checks.
 */
public class StudyMaterialService {
    private static final Logger logger = LoggerFactory.getLogger(StudyMaterialService.class);

    private final GoogleDriveService driveService;
    private final StudyMaterialRepository repository;
    private final PermissionService permissionService;

    /**
     * Constructs a new StudyMaterialService.
     *
     * @param driveService      Google Drive service for file storage
     * @param repository        Repository for study materials
     * @param permissionService Permission service for authorization
     */
    public StudyMaterialService(GoogleDriveService driveService, StudyMaterialRepository repository, PermissionService permissionService) {
        this.driveService = driveService;
        this.repository = repository;
        this.permissionService = permissionService;
    }

    /**
     * Uploads a new study material and generates translations and preview image.
     *
     * @param content     File content in bytes
     * @param filename    Name of the file
     * @param uploader    User uploading the file
     * @param name        Display name of the material
     * @param description Description of the material
     * @param category    Category to which the material belongs
     * @param tags        Tags associated with the material
     * @return Uploaded StudyMaterial
     * @throws IOException if file or preview generation fails
     */
    public StudyMaterial uploadMaterial(byte[] content, String filename, User uploader, String name, String description, Category category, Set<Tag> tags) throws IOException {
        checkUploadPermission(uploader);

        String fileType = resolveFileType(filename);
        String fileUrl = driveService.uploadFile(content, filename, fileType);
        byte[] preview = new PreviewGeneratorService().generatePreview(content, fileType);

        MaterialStatus status = determineStatus(category, uploader);
        StudyMaterial material = buildMaterial(uploader, name, description, fileUrl, fileType, preview, category, status, tags);

        StudyMaterial savedMaterial = repository.save(material);
        generateAndSaveTranslations(name, description, savedMaterial);

        logger.info("User {} uploaded new study material: {}", uploader.getEmail(), name);
        return savedMaterial;
    }

    /**
     * Checks whether the user has permission to upload material.
     *
     * @param uploader user attempting the upload
     */
    private void checkUploadPermission(User uploader) {
        if (!permissionService.hasPermission(uploader, PermissionType.CREATE_RESOURCE)) {
            throw new SecurityException("You do not have permission to upload study materials.");
        }
    }

    /**
     * Resolves MIME type from filename.
     *
     * @param filename name of the file
     * @return MIME type as string
     * @throws IOException if probing content type fails
     */
    private String resolveFileType(String filename) throws IOException {
        String fileType = Files.probeContentType(Path.of(filename));
        return (fileType == null) ? "application/octet-stream" : fileType;
    }

    /**
     * Determines the material status (APPROVED or PENDING).
     *
     * @param category material category
     * @param uploader uploading user
     * @return MaterialStatus depending on user ownership
     */
    private MaterialStatus determineStatus(Category category, User uploader) {
        return (category.getCreator().getUserId() == uploader.getUserId())
                ? MaterialStatus.APPROVED
                : MaterialStatus.PENDING;
    }

    /**
     * Builds a new StudyMaterial object with provided data.
     *
     * @return StudyMaterial object
     */
    private StudyMaterial buildMaterial(User uploader, String name, String description, String fileUrl, String fileType,
                                        byte[] preview, Category category, MaterialStatus status, Set<Tag> tags) {
        StudyMaterial material = new StudyMaterial(
                uploader,
                name,
                description,
                fileUrl,
                preview.length / 1024f,
                fileType,
                LocalDateTime.now(),
                status
        );
        material.setCategory(category);
        material.setPreviewImage(preview);
        material.getTags().addAll(tags);
        return material;
    }

    /**
     * Translates the name and description to supported languages and saves them.
     *
     * @param name        material name
     * @param description material description
     * @param savedMaterial the StudyMaterial to associate translations with
     */
    private void generateAndSaveTranslations(String name, String description, StudyMaterial savedMaterial) {
        try {
            StudyMaterialTranslationRepository translationRepo = new StudyMaterialTranslationRepository();
            TranslationService translationService = new TranslationService();

            String sourceLanguage = LanguageManager.getInstance().getCurrentLanguage();
            if (sourceLanguage == null || sourceLanguage.isEmpty()) {
                sourceLanguage = "en";
            }

            String[] targetLanguages = {"en", "fi", "ru", "zh"};

            Map<String, String> nameTranslations = new HashMap<>();
            Map<String, String> descriptionTranslations = new HashMap<>();

            nameTranslations.put(sourceLanguage, name);
            descriptionTranslations.put(sourceLanguage, description);

            for (String targetLanguage : targetLanguages) {
                if (!targetLanguage.equals(sourceLanguage)) {
                    try {
                        String translatedName = translationService.translate(name, sourceLanguage, targetLanguage);
                        String translatedDescription = (description != null && !description.isEmpty())
                                ? translationService.translate(description, sourceLanguage, targetLanguage)
                                : "";

                        nameTranslations.put(targetLanguage, translatedName);
                        descriptionTranslations.put(targetLanguage, translatedDescription);
                    } catch (Exception e) {
                        logger.error("Failed to translate study material from {} to {}: {}",
                                sourceLanguage, targetLanguage, e.getMessage());
                    }
                }
            }

            translationRepo.saveTranslations(savedMaterial.getMaterialId(), nameTranslations, descriptionTranslations);
            logger.info("Saved translations for material: {}", savedMaterial.getName());
        } catch (Exception e) {
            logger.error("Failed to generate and save translations for study material", e);
        }
    }

    /**
     * Updates a study material if the user has proper permission.
     *
     * @param user the user updating the material
     * @param updatedMaterial updated material info
     * @return the updated StudyMaterial
     */
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

    /**
     * Returns all resources available to the user.
     */
    // READ_RESOURCES
    public List<StudyMaterial> getAllResources(User user) {
        if (!permissionService.hasPermission(user, PermissionType.READ_RESOURCES)) {
            throw new SecurityException("You do not have permission to read study materials.");
        }
        return repository.findAll();
    }

    /**
     * Returns all approved study materials.
     */
    // READ_APPROVED_RESOURCES
    public List<StudyMaterial> getApprovedMaterials(User user) {
        if (!permissionService.hasPermission(user, PermissionType.READ_RESOURCES)) {
            throw new SecurityException("You do not have permission to read study materials.");
        }
        return repository.findByStatus(MaterialStatus.APPROVED);
    }

    /**
     * Returns all pending study materials (for reviewers).
     */
    // READ_PENDING_RESOURCES (For Reviewers Only)
    public List<StudyMaterial> getPendingMaterialsForReview(User user) {
        if (!permissionService.hasPermission(user, PermissionType.REVIEW_PENDING_RESOURCES)) {
            throw new SecurityException("You do not have permission to review pending materials.");
        }
        return repository.findByStatus(MaterialStatus.PENDING);
    }

    /**
     * Asynchronously downloads a file to the given location if the user has permission.
     * This method executes the download operation in a background thread to prevent UI freezing.
     *
     * @param user          User attempting to download the material
     * @param material      StudyMaterial to be downloaded
     * @param saveLocation  File location where the downloaded content will be saved
     * @return Task<Void>   JavaFX Task that can be used to monitor download progress or handle completion
     *                      The task completes with null on success or throws an exception on failure
     * @throws SecurityException if the user lacks permission to download the material
     * @throws Exception for other unexpected errors during download
     */
    public Task<Void> downloadMaterial(User user, StudyMaterial material, File saveLocation) {
        Task<Void> downloadTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                try {
                    boolean canDownload = (material.getStatus() == MaterialStatus.APPROVED &&
                            permissionService.hasPermission(user, PermissionType.READ_RESOURCES))
                            || permissionService.hasPermission(user, PermissionType.REVIEW_PENDING_RESOURCES);

                    if (!canDownload) {
                        throw new SecurityException("You do not have permission to download this material.");
                    }

                    long fileSize = driveService.getFileSize(material.getLink());

                    try (OutputStream out = new FileOutputStream(saveLocation)) {
                        driveService.downloadFile(material.getLink(), out, (bytesCopied) -> {
                            updateProgress(bytesCopied, fileSize);
                        });
                    }

                    Platform.runLater(() -> {
                        logger.info("User {} downloaded material: {}", user.getEmail(), material.getName());
                    });

                    return null;
                } catch (Exception e) {
                    logger.error("Error downloading material: {}", e.getMessage());
                    throw e;
                }
            }
        };

        return downloadTask;
    }

    /**
     * Deletes the specified study material if the user has proper rights.
     */
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

    /**
     * Approves a pending material.
     */
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


    /**
     * Rejects a pending material.
     */
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

    /**
     * Directly updates the material without permission check (used internally).
     */
    public StudyMaterial updateMaterial(StudyMaterial material) {
        return repository.update(material);
    }

    /**
     * Updates the material description if the user has permission.
     */
    public void updateDescription(User user, StudyMaterial sm, String description) {
        if ((user.getUserId() == sm.getUploader().getUserId()
                && permissionService.hasPermission(user, PermissionType.UPDATE_OWN_RESOURCE))
                || user.getRole().getName() == ADMIN) {
            repository.updateMaterialDescription(sm.getMaterialId(), description);
        } else {
            throw new SecurityException("You do not have permission to modify this description.");
        }
    }

    /**
     * Updates the material title if the user has permission.
     */
    public void updateTitle(User user, StudyMaterial sm, String title) {
        if ((user.getUserId() == sm.getUploader().getUserId()
                && permissionService.hasPermission(user, PermissionType.UPDATE_OWN_RESOURCE))
                || user.getRole().getName() == ADMIN) {
            repository.updateMaterialTitle(sm.getMaterialId(), title);
        } else {
            throw new SecurityException("You do not have permission to modify this title.");
        }
    }

    /**
     * Returns all materials uploaded by the user.
     */
    public List<StudyMaterial> findByUser(User user){
        if (!permissionService.hasPermission(user, PermissionType.READ_RESOURCES)) {
            throw new SecurityException("You do not have permission to view these study materials.");
        }
        return repository.findByUser(user);
    }
}
