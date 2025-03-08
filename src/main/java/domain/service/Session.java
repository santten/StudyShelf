package domain.service;

import domain.model.User;

public class Session {
    private static Session instance;
    private User currentUser;
    private final PermissionService permissionService;

    private Session() {
        this.permissionService = new PermissionService();
    }
    public static Session getInstance() {
        if (instance == null) {
            instance = new Session();
        }
        return instance;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public PermissionService getPermissionService() { return permissionService; }

    public void logout() { this.currentUser = null; }

}
