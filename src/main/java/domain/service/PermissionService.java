package domain.service;

import domain.model.PermissionType;
import domain.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PermissionService {
    private static final Logger logger = LoggerFactory.getLogger(PermissionService.class);

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

        boolean result = user.hasPermission(permissionType, resourceOwnerId);

        if (!result) {
            logger.warn("User {} does NOT have permission: {}", user.getEmail(), permissionName);
        } else {
            logger.info("User {} has permission: {}", user.getEmail(), permissionName);
        }

        return result;
    }
}