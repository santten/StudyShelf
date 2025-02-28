package domain.model;

import jakarta.persistence.*;
import javafx.scene.Node;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "study_materials")

public class StudyMaterial {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int materialId;

    @ManyToOne
    @JoinColumn(name = "UploaderId")
    private User uploader;

    @ManyToOne
    @JoinColumn(name = "CategoryId")
    private Category category;

    @Column(name = "MaterialName", length = 255)
    private String name;

    @Column(name = "MaterialDescription", columnDefinition = "TEXT")
    private String description;

    @Column(name = "MaterialLink", length = 255)
    private String link;

    @Column(name = "FileSize")
    private Float fileSize;

    @Column(name = "FileType", length = 150)
    private String fileType;

    @Column(name = "TimeStamp")
    private LocalDateTime timestamp = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "Status")
    private MaterialStatus status;
    @ManyToMany
    @JoinTable(
            name = "MaterialTag",
            joinColumns = @JoinColumn(name = "MaterialId"),
            inverseJoinColumns = @JoinColumn(name = "TagId")
    )
    private Set<Tag> tags = new HashSet<>();
    @OneToMany(mappedBy = "studyMaterial")
    private Set<Rating> ratings = new HashSet<>();
    @OneToMany(mappedBy = "studyMaterial")
    private Set<Review> reviews = new HashSet<>();
    @Lob
    @Column(name = "PreviewImage", columnDefinition = "LONGBLOB")
    private byte[] previewImage;

    // Default constructor
    public StudyMaterial() {}

    // Constructor for new study materials
    public StudyMaterial(User uploader, String name, String description, String link,
                         float fileSize, String fileType, LocalDateTime timestamp, MaterialStatus status) {
        this.uploader = uploader;
        this.name = name;
        this.description = description;
        this.link = link;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.timestamp = timestamp;
        this.status = status;
    }

    // Constructor for existing study materials
    public StudyMaterial(int materialId, User uploader, String name, String description,
                         String link, float fileSize, String fileType,
                         LocalDateTime timestamp, MaterialStatus status) {
        this.materialId = materialId;
        this.uploader = uploader;
        this.name = name;
        this.description = description;
        this.link = link;
        this.fileSize = fileSize;
        this.fileType = fileType;
        this.timestamp = timestamp;
        this.status = status;
    }
    // Getters and setters
    public int getMaterialId() {
        return materialId;
    }
    public void setMaterialId(int materialId) {
        this.materialId = materialId;
    }
    public User getUploader() {
        return uploader;
    }
    public void setUploader(User uploader) {
        this.uploader = uploader;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getLink() {
        return link;
    }
    public void setLink(String link) {
        this.link = link;
    }
    public Float getFileSize() {
        return fileSize;
    }
    public void setFileSize(Float fileSize) {
        this.fileSize = fileSize;
    }
    public String getFileType() {
        return fileType;
    }
    public void setFileType(String fileType) {
        this.fileType = fileType;
    }
    public LocalDateTime getTimestamp() {
        return timestamp;
    }
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
    public MaterialStatus getStatus() {
        return status;
    }
    public void setStatus(MaterialStatus status) {
        this.status = status;
    }
    public Category getCategory() {
        return category;
    }
    public void setCategory(Category category) {
        this.category = category;
    }
    public Set<Tag> getTags() {
        return tags;
    }
    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }
    public Set<Rating> getRatings() {
        return ratings;
    }
    public void setRatings(Set<Rating> ratings) {
        this.ratings = ratings;
    }
    public byte[] getPreviewImage() {
        return previewImage;
    }
    public void setPreviewImage(byte[] previewImage) {
        this.previewImage = previewImage;
    }
    public String getFileExtension() {
        switch (fileType) {
            case "application/pdf": return "pdf";
            case "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet": return "xlsx";
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document": return "docx";
            case "image/jpeg": return "jpg";
            case "image/png": return "png";
            case "application/zip": return "zip";
            case "application/x-rar-compressed": return "rar";
            case "application/x-7z-compressed": return "7z";
            case "application/x-tar": return "tar";
            case "application/gzip": return "gz";
            case "application/x-bzip2": return "bz2";
            case "text/plain": return "txt";
            case "text/csv": return "csv";
            case "application/vnd.ms-powerpoint": return "ppt";
            case "application/vnd.openxmlformats-officedocument.presentationml.presentation": return "pptx";
            case "application/vnd.ms-excel": return "xls";
            case "image/gif": return "gif";
            case "video/mp4": return "mp4";
            case "video/x-msvideo": return "avi";
            case "video/quicktime": return "mov";
            case "audio/mpeg": return "mp3";
            case "application/java-archive": return "jar";
            default: return fileType.split("/")[1];
        }
    }


}
