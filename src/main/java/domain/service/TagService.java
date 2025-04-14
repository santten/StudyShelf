package domain.service;

import domain.model.Tag;
import domain.model.User;
import domain.model.PermissionType;

import infrastructure.repository.TagRepository;

import java.util.List;

/**
 * Service class for managing tags.
 * Handles creation, retrieval, updating, and deletion of tags with permission checks.
 */
public class TagService {
    private final TagRepository tagRepository;
    private final PermissionService permissionService;

    /**
     * Constructor for TagService.
     *
     * @param tagRepository      repository for tag persistence
     * @param permissionService  service for checking user permissions
     */
    public TagService(TagRepository tagRepository, PermissionService permissionService) {
        this.tagRepository = tagRepository;
        this.permissionService = permissionService;

    }

    /**
     * Creates a new tag if it doesn't already exist.
     *
     * @param tagName name of the tag
     * @param creator user who creates the tag
     * @return existing or newly created Tag
     */
    public Tag createTag(String tagName, User creator) {
        if (tagName == null || tagName.trim().isEmpty()) {
            throw new IllegalArgumentException("Tag name cannot be empty");
        }
        String normalizedTagName = tagName.trim().toLowerCase();

        Tag existingTag = tagRepository.findByName(normalizedTagName);
        if (existingTag != null) {
            return existingTag;
        }

        Tag tag = new Tag(normalizedTagName, creator);
        return tagRepository.save(tag);
    }

    /**
     * Retrieves all tags from the repository.
     *
     * @return list of all tags
     */
    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    /**
     * Finds a tag by its ID.
     *
     * @param id tag ID
     * @return the Tag if found, else null
     */
    public Tag findById(int id) {
        return tagRepository.findById(id);
    }

    /**
     * Updates a tag's name, if the user has appropriate permissions.
     *
     * @param tag      tag to be updated
     * @param newName  new name for the tag
     * @param user     user attempting the update
     * @return updated Tag
     */
    public Tag updateTag(Tag tag, String newName, User user) {
        // UPDATE_OWN_TAG
        boolean isOwner = tag.getCreator().equals(user);
        boolean canUpdateOwn = isOwner && permissionService.hasPermission(user, PermissionType.UPDATE_OWN_TAG);
        // UPDATE_COURSE_TAG
        boolean canUpdateCourse = permissionService.hasPermission(user, PermissionType.UPDATE_COURSE_TAG);
        // UPDATE_ANY_TAG
        boolean canUpdateAny = permissionService.hasPermission(user, PermissionType.UPDATE_ANY_TAG);

        if (!(canUpdateOwn || canUpdateCourse || canUpdateAny)) {
            throw new SecurityException("You do not have permission to update this tag.");
        }

        tag.setTagName(newName);
        return tagRepository.save(tag);
    }

    /**
     * Retrieves all tags for a user with permission check.
     *
     * @param user the user making the request
     * @return list of all tags
     */
    // READ_TAGS
    public List<Tag> getTags(User user) {
        if (!permissionService.hasPermission(user, PermissionType.READ_TAGS)) {
            throw new SecurityException("You do not have permission to read tags.");
        }
        return tagRepository.findAll();
    }

    /**
     * Deletes a tag if the user has the proper permission.
     *
     * @param tag  the tag to be deleted
     * @param user the user requesting deletion
     */
    public void deleteTag(Tag tag, User user) {
        // DELETE_ANY_TAG
        boolean canDeleteAny = permissionService.hasPermission(user, PermissionType.DELETE_ANY_TAG);
        // DELETE_COURSE_TAG
        boolean canDeleteCourse = permissionService.hasPermission(user, PermissionType.DELETE_COURSE_TAG) &&
                tag.getCreator().equals(user);

        if (!(canDeleteAny || canDeleteCourse)) {
            throw new SecurityException("You do not have permission to delete this tag.");
        }
        tagRepository.delete(tag);
    }
}
