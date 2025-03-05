package presentation.controller;

import domain.model.MaterialStatus;
import domain.model.PermissionType;
import domain.model.StudyMaterial;
import domain.model.User;
import domain.service.GoogleDriveService;
import domain.service.PermissionService;
import domain.service.Session;
import domain.service.StudyMaterialService;
import infrastructure.repository.StudyMaterialRepository;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class StudyMaterialController extends BaseController {
    private final StudyMaterialService studyMaterialService =
            new StudyMaterialService(new GoogleDriveService(), new StudyMaterialRepository(), new PermissionService());

    public List<StudyMaterial> getAllMaterials() {
        User user = Session.getInstance().getCurrentUser();
        if (!hasPermission(PermissionType.READ_RESOURCES)) {
            showAlert("Permission Denied", "You do not have permission to read study materials.");
            return List.of();
        }
        return studyMaterialService.getAllResources(user);
    }


//    public void uploadMaterial(File file, String title, String description, User uploader, String category) {
//        if (!hasPermission(PermissionType.CREATE_RESOURCE)) {
//            showAlert("Permission Denied", "You do not have permission to upload study materials.");
//            return;
//        }
//
//        try {
//            byte[] content = java.nio.file.Files.readAllBytes(file.toPath());
//            StudyMaterial material = studyMaterialService.uploadMaterial(content, file.getName(), uploader, title, description, null);
//            showAlert("Success", "Study material uploaded successfully.");
//        } catch (IOException e) {
//            showAlert("Upload Failed", "Failed to upload study material.");
//        }
//    }


    public void updateMaterial(StudyMaterial updatedMaterial) {
        User user = Session.getInstance().getCurrentUser();
        if (!hasPermission(PermissionType.UPDATE_OWN_RESOURCE)) {
            showAlert("Permission Denied", "You do not have permission to update this material.");
            return;
        }

        studyMaterialService.updateMaterial(user, updatedMaterial);
        showAlert("Success", "Study material updated.");
    }

    public void deleteMaterial(StudyMaterial material) {
        User user = Session.getInstance().getCurrentUser();
        if (!hasPermission(PermissionType.DELETE_OWN_RESOURCE) && !hasPermission(PermissionType.DELETE_ANY_RESOURCE)) {
            showAlert("Permission Denied", "You do not have permission to delete this material.");
            return;
        }

        studyMaterialService.deleteMaterial(user, material);
        showAlert("Success", "Study material deleted.");
    }

    public void downloadMaterial(StudyMaterial material) {
        User user = Session.getInstance().getCurrentUser();
        if (!hasPermission(PermissionType.READ_RESOURCES)) {
            showAlert("Permission Denied", "You do not have permission to download this material.");
            return;
        }

        FileChooser fileChooser = new FileChooser();
        fileChooser.setInitialFileName(material.getName() + "." + material.getFileExtension());
        File saveLocation = fileChooser.showSaveDialog(null);

        if (saveLocation != null) {
            try {
                studyMaterialService.downloadMaterial(user, material, saveLocation);
                showAlert("Success", "Material downloaded successfully.");
            } catch (IOException e) {
                showAlert("Download Failed", "Failed to download material.");
            }
        }
    }

    public void approveMaterial(StudyMaterial material) {
        User user = Session.getInstance().getCurrentUser();
        if (!hasPermission(PermissionType.APPROVE_RESOURCE)) {
            showAlert("Permission Denied", "You do not have permission to approve study materials.");
            return;
        }

        studyMaterialService.approveMaterial(user, material);
        showAlert("Success", "Study material approved.");
    }

    public void rejectMaterial(StudyMaterial material) {
        User user = Session.getInstance().getCurrentUser();
        if (!hasPermission(PermissionType.REJECT_RESOURCE)) {
            showAlert("Permission Denied", "You do not have permission to reject study materials.");
            return;
        }

        studyMaterialService.rejectMaterial(user, material);
        showAlert("Success", "Study material rejected.");
    }


    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
