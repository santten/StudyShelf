package domain.service;

import domain.model.PermissionType;
import domain.model.RoleType;
import domain.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Set;

/**
 * Service for managing and validating user permissions.
 */
public class PermissionService {
    private static final Logger logger = LoggerFactory.getLogger(PermissionService.class);

    /**
     * Checks if a user has a specific permission.
     *
     * @param user           the user to check
     * @param permissionType the required permission
     * @return true if the user has the permission, false otherwise
     */
    public boolean hasPermission(User user, PermissionType permissionType) {
        if (user == null || user.getRole() == null || user.getRole().getPermissions() == null) {
            logger.warn("Permission check failed: User, role, or permissions are null");
            return false;
        }
        return user.getRole().getPermissions().stream()
                .anyMatch(permission -> permission.getName() == permissionType);
    }

    /**
     * Checks if a user has permission to act on a specific entity (based on ownership or role).
     *
     * @param user           the user making the request
     * @param permissionType the required permission
     * @param entityOwnerId  the ID of the entity owner
     * @return true if permission is granted, false otherwise
     */
    public boolean hasPermissionOnEntity(User user, PermissionType permissionType, int entityOwnerId) {
        if (user == null) {
            logger.warn("Permission check failed: User is null");
            return false;
        }

        if (!hasPermission(user, permissionType)) {
            logger.warn("User {} does NOT have permission: {}",
                    (user.getEmail() != null ? user.getEmail() : "[no email]"),
                    permissionType);
            return false;
        }

        if (hasAdminPermission(user, permissionType)) {
            return true;
        }

        if (hasOwnPermission(user, permissionType, entityOwnerId)) {
            return true;
        }

        if (hasCoursePermission(user, permissionType)) {
            return true;
        }

        logger.warn("User {} does NOT have permission: {}", user.getEmail(), permissionType);
        return false;
    }

    /**
     * Checks if the user has admin-level permissions.
     */
    private boolean hasAdminPermission(User user, PermissionType permissionType) {
        if (!user.isAdmin()) {
            return false;
        }

        Set<PermissionType> adminPermissions = Set.of(
                PermissionType.DELETE_ANY_RESOURCE,
                PermissionType.UPDATE_ANY_TAG,
                PermissionType.DELETE_ANY_TAG,
                PermissionType.UPDATE_ANY_CATEGORY,
                PermissionType.DELETE_ANY_CATEGORY,
                PermissionType.DELETE_ANY_REVIEW,
                PermissionType.DELETE_ANY_RATING,
                PermissionType.READ_ALL_USERS,
                PermissionType.DELETE_ANY_USER,
                PermissionType.CREATE_CATEGORY,
                PermissionType.APPROVE_RESOURCE,
                PermissionType.REJECT_RESOURCE,
                PermissionType.REVIEW_PENDING_RESOURCES
        );

        return adminPermissions.contains(permissionType);
    }

    /**
     * Checks if the user has permissions only on their own resources/entities.
     */
    private boolean hasOwnPermission(User user, PermissionType permissionType, int entityOwnerId) {
        Set<PermissionType> ownPermissions = Set.of(
                PermissionType.CREATE_RESOURCE,
                PermissionType.UPDATE_OWN_RESOURCE,
                PermissionType.READ_RESOURCES,
                PermissionType.DELETE_OWN_RESOURCE,
                PermissionType.CREATE_TAG,
                PermissionType.UPDATE_OWN_TAG,
                PermissionType.READ_TAGS,
                PermissionType.READ_CATEGORIES,
                PermissionType.CREATE_REVIEW,
                PermissionType.UPDATE_OWN_REVIEW,
                PermissionType.DELETE_OWN_REVIEW,
                PermissionType.CREATE_RATING,
                PermissionType.UPDATE_OWN_RATING,
                PermissionType.READ_RATINGS,
                PermissionType.DELETE_OWN_RATING,
                PermissionType.CREATE_USER,
                PermissionType.UPDATE_OWN_USER,
                PermissionType.READ_OWN_USER,
                PermissionType.DELETE_OWN_USER
        );

        if (ownPermissions.contains(permissionType) && user.getUserId() == entityOwnerId) {
            return true;
        }

        logger.warn("User {} is not the owner of the entity and cannot modify it!", user.getEmail());
        return false;
    }

    /**
     * Checks if the user has permissions at the course level (teachers).
     */
    private boolean hasCoursePermission(User user, PermissionType permissionType) {
        if (!user.isTeacher()) {
            return false;
        }

        Set<PermissionType> coursePermissions = Set.of(
                PermissionType.DELETE_COURSE_RESOURCE,
                PermissionType.UPDATE_COURSE_TAG,
                PermissionType.DELETE_COURSE_TAG,
                PermissionType.UPDATE_COURSE_CATEGORY,
                PermissionType.DELETE_COURSE_CATEGORY,
                PermissionType.CREATE_CATEGORY,
                PermissionType.APPROVE_RESOURCE,
                PermissionType.REJECT_RESOURCE,
                PermissionType.REVIEW_PENDING_RESOURCES
        );
        return coursePermissions.contains(permissionType);
    }

    /**
     * Checks if the user has general approval permissions (Admin or Teacher).
     */
    public boolean hasApprovalPermission(User user) {
        RoleType role = user.getRole().getName();
        return role == RoleType.ADMIN || role == RoleType.TEACHER;
    }
}
