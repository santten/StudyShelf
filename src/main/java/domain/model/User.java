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

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
//    @JoinColumn(name = "role_id")
    private Role role;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Rating> ratings = new HashSet<>();


    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Review> reviews = new HashSet<>();


    // Default constructor
    public User() {}

    // Constructor for new users
    public User(String firstName, String lastName, String email, String password, Role role) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    // Constructor for existing users
    public User(int userId, String firstName, String lastName, String email, String password , Role role) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.password = password;
        this.role = role;
    }

    public boolean hasPermission(PermissionType permissionType) {
        if (role == null || role.getPermissions() == null) {
            return false;
        }
        return role.getPermissions().stream()
                .anyMatch(permission -> permission.getName().equals(permissionType));
    }

    public boolean isAdmin() {
        return role != null && role.getName() == RoleType.ADMIN;
    }

    public boolean isTeacher() {
        return role != null && role.getName() == RoleType.TEACHER;
    }

    public boolean isStudent() {
        return role != null && role.getName() == RoleType.STUDENT;
    }


    // Getters and Setters
    public int getUserId() { return userId; }
    public void setUserId(int i) { this.userId = i; };

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

    public String getPassword() { return password; }
    public void setPassword(String password) {
        this.password = password;
    }

     public Role getRole() { return role; }
     public void setRole(Role role) { this.role = role; }

     public String getFullName() {return firstName + " " + lastName;}
}
