package presentation.controller;

import domain.model.PermissionType;
import domain.model.StudyMaterial;
import domain.service.GoogleDriveService;
import domain.service.PermissionService;
import domain.service.StudyMaterialService;
import infrastructure.repository.StudyMaterialRepository;
import presentation.view.CurrentUserManager;
import presentation.utility.CustomAlert;

import static javafx.scene.control.Alert.AlertType.WARNING;

public class StudyMaterialController {
    public static boolean deleteMaterial(StudyMaterial s) {
        if (CustomAlert.confirm("Deleting Material", "Are you sure you want to delete material \"" + s.getName() + "\"?", "This can not be undone.", true)) {
            if (!(CurrentUserManager.get().getUserId() == s.getUploader().getUserId() || CurrentUserManager.get().hasPermission(PermissionType.DELETE_ANY_RESOURCE))) {
                CustomAlert.show(WARNING, "Permission Denied", "You do not have permission to delete this material.");
            }
            new StudyMaterialService(new GoogleDriveService(), new StudyMaterialRepository(), new PermissionService()).deleteMaterial(CurrentUserManager.get(), s);
            return true;
        } else {
            return false;
        }
    }
}
