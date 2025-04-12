package presentation.controller;

import domain.model.PermissionType;
import domain.model.StudyMaterial;
import domain.service.GoogleDriveService;
import domain.service.PermissionService;
import domain.service.StudyMaterialService;
import infrastructure.repository.StudyMaterialRepository;
import presentation.utility.CustomAlert;
import presentation.view.CurrentUserManager;
import presentation.view.LanguageManager;
import presentation.view.SceneManager;

import java.util.ResourceBundle;

import static javafx.scene.control.Alert.AlertType.WARNING;

public class StudyMaterialController {
    public static final ResourceBundle rb = LanguageManager.getInstance().getBundle();

    private StudyMaterialController(){}

    public static boolean deleteMaterial(StudyMaterial s) {
        if (CustomAlert.confirm(rb.getString("alertDeletingMaterial"), String.format(rb.getString("alertConfirmDeleteMaterial"), s.getName()), rb.getString("alertNoUndo"), true)) {
            if (!(CurrentUserManager.get().getUserId() == s.getUploader().getUserId() || CurrentUserManager.get().hasPermission(PermissionType.DELETE_ANY_RESOURCE))) {
                CustomAlert.show(WARNING, rb.getString("alertPermissionDenied"), rb.getString("alertPermissionDeniedMaterial"));
            }
            new StudyMaterialService(new GoogleDriveService(), new StudyMaterialRepository(), new PermissionService()).deleteMaterial(CurrentUserManager.get(), s);
            return true;
        } else {
            return false;
        }
    }
}
