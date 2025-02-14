package domain.model;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, unique = true, updatable = false)
    private RoleType name;

    @OneToMany(mappedBy = "role")
    private Set<User> users = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "role_permissions",
            joinColumns = @JoinColumn(name = "role_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();

    public Role() {}
    public Role(RoleType name) { this.name = name; }

    public Long getId() {
        return id;
    }
    public RoleType getName() { return name; }
    public Set<Permission> getPermissions() {return permissions;}


}
