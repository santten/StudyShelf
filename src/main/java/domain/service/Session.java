package domain.service;

import domain.model.User;

/**
 * Singleton class to manage session state, including the current logged-in user
 * and access to permission-related checks.
 */
public class Session {
    private static Session instance;
    private User currentUser;
    private final PermissionService permissionService;

    /**
     * Private constructor to enforce singleton pattern.
     */
    private Session() {
        this.permissionService = new PermissionService();
    }

    /**
     * Gets the singleton instance of Session.
     *
     * @return Session instance
     */
    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    /**
     * Gets the current logged-in user.
     *
     * @return current User object or null if not logged in
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Sets the currently logged-in user.
     *
     * @param user the User to set as current
     */
    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    /**
     * Provides access to the PermissionService.
     *
     * @return PermissionService instance
     */
    public PermissionService getPermissionService() {
        return permissionService;
    }

    /**
     * Logs out the current user by clearing the session.
     */
    public void logout() {
        this.currentUser = null;
    }
}
