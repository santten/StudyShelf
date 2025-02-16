package domain.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int userId;

    @Column(name = "FirstName", length = 50)
    private String firstName;

    @Column(name = "LastName", length = 50)
    private String lastName;

    @Column(name = "Email", length = 100, unique = true)
    private String email;

    @Column(name = "Password", length = 255)
    private String password;

    @OneToMany(mappedBy = "user")
    private Set<Rating> ratings = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "user_role",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    // Default constructor
    public User() {}

    // Constructor for new users
    public User(String firstName, String lastName, String email, String password) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    // Constructor for existing users
    public User(int userId, String firstName, String lastName, String email, String password) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    // Returns permissions
    public Set<Permission> getPermissions() {
        Set<Permission> allPermissions = new HashSet<>();
        for (Role role : roles) {
            allPermissions.addAll(role.getPermissions());
        }
        return allPermissions;
    }

    // Checks if user has permission
    public boolean hasPermission(PermissionType permissionType, int resourceOwnerId) {
//        if (roles.isEmpty()) {
//            return false;  // No roles, no permissions
//        }
//
//        if ((permissionType == PermissionType.DELETE_OWN_RESOURCE ||
//                permissionType == PermissionType.UPDATE_OWN_RESOURCE) &&
//                this.userId != resourceOwnerId) {
//            return false;
//        }

        if (permissionType == PermissionType.READ_RESOURCES) {
            return true;
        }

        for (Role role : roles) {
            for (Permission permission : role.getPermissions()) {
                if (permission.getName() == permissionType) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean hasPermissionOnResource(PermissionType permissionType, int resourceOwnerId) {
        if (hasPermission(permissionType, resourceOwnerId) && this.userId == resourceOwnerId) {
            return true;
        }
        return false;
    }

    // Getters and Setters
    public int getUserId() {
        return userId;
    }
    public void setUserId(int userId) {
        this.userId = userId;
    }
    public String getFirstName() {
        return firstName;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return lastName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getFullName() {return firstName + " " + lastName;}
}
