package domain.model;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
@Entity
@Table(name = "tags")
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int tagId;

    @Column(name = "TagName", length = 100, unique = true)
    private String tagName;

    @ManyToOne
    @JoinColumn(name = "CreatorId")
    private User creator;

    @ManyToMany(mappedBy = "tags")
    private Set<StudyMaterial> materials = new HashSet<>();

    // Constructors
    public Tag() {}

    public Tag(String tagName, User creator) {
        this.tagName = tagName;
        this.creator = creator;
    }

    // Getters and setters
    public int getTagId() {
        return tagId;
    }

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public Set<StudyMaterial> getMaterials() {
        return materials;
    }

    public void setMaterials(Set<StudyMaterial> materials) {
        this.materials = materials;
    }
}


