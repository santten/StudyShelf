package domain.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "permissions")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, updatable = false)
    private PermissionType name;

    @ManyToMany(mappedBy = "permissions")
    private Set<Role> roles = new HashSet<>();

//    @ManyToMany
//    @JoinTable(
//            name = "role_permissions",
//            joinColumns = @JoinColumn(name = "permission_id"),
//            inverseJoinColumns = @JoinColumn(name = "role_id")
//    )
//    private Set<Role> roles = new HashSet<>();


    public Permission() { }
    public Permission(PermissionType name) {
        this.name = name;
    }
    public int getId() { return id; }
    public PermissionType getName() { return name; }
    public void setName(PermissionType name) {
        this.name = name;
    }
    public Set<Role> getRoles() { return roles; }
}