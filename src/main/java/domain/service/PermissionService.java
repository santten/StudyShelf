package domain.service;

import domain.model.PermissionType;
import domain.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PermissionService {
    private static final Logger logger = LoggerFactory.getLogger(PermissionService.class);

    public boolean hasPermission(User user, PermissionType permissionType) {
        if (user == null || user.getRole() == null) {
            logger.warn("Permission check failed: User or role is null");
            return false;
        }
        return user.getRole().getPermissions().stream()
                .anyMatch(permission -> permission.getName().equals(permissionType));
    }


    public boolean hasPermissionOnResource(User user, String permissionName, int resourceOwnerId) {
        if (user == null) {
            logger.warn("Permission check failed: User is null");
            return false;
        }

        PermissionType permissionType;
        try {
            permissionType = PermissionType.valueOf(permissionName.toUpperCase());
        } catch (IllegalArgumentException e) {
            logger.warn("Invalid permission name: {}", permissionName);
            return false;
        }

        if (permissionType == PermissionType.DELETE_OWN_RESOURCE && user.getUserId() != resourceOwnerId) {
            logger.warn("User {} is not the owner of the resource and cannot delete it!", user.getEmail());
            return false;
        }

        boolean result = hasPermission(user, permissionType);

        if (!result) {
            logger.warn("User {} does NOT have permission: {}", user.getEmail(), permissionName);
        } else {
            logger.info("User {} has permission: {}", user.getEmail(), permissionName);
        }

        return result;
    }
}
