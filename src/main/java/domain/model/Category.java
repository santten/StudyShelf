package domain.model;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int categoryId;
    @Column(name = "CategoryName", length = 50)
    private String name;
    @ManyToOne
    @JoinColumn(name = "CreatorId")
    private User creator;

    @OneToMany(mappedBy = "category")
    private List<StudyMaterial> materials = new ArrayList<>();

    // Default constructor
    public Category() {}

    // Constructor for new category
    public Category(String name, User creator) {
        this.name = name;
        this.creator = creator;
    }

    // Constructor for existing category
    public Category(int categoryId, String name, User creator) {
        this.categoryId = categoryId;
        this.name = name;
        this.creator = creator;
    }

    // Getters and setters
    public int getCategoryId() {
        return categoryId;
    }

    public String getCategoryName() {
        return name;
    }

    public void setCategoryName(String categoryName) {
        this.name = categoryName;
    }

    public User getCreator() {
        return creator;
    }

    public void setCreator(User creator) {
        this.creator = creator;
    }

    public List<StudyMaterial> getMaterials() {
        return materials;
    }

    public void setMaterials(List<StudyMaterial> materials) {
        this.materials = materials;
    }
}
