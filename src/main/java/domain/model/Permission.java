package domain.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "permissions")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, updatable = false)
    private PermissionType name;

    @ManyToMany(mappedBy = "permissions")
    private Set<Role> roles = new HashSet<>();

    public Permission() { }
    public Permission(PermissionType name) {
        this.name = name;
        this.roles = new HashSet<>();
    }

    public Long getId() { return id; }
    public PermissionType getName() { return name; }
    public Set<Role> getRoles() { return roles; }

    public void setRoles(Set<Role> roles) {
        this.roles.clear();
        this.roles.addAll(roles);
        for (Role role : roles) {
            role.getPermissions().add(this);
        }

    }
}