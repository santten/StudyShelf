package domain.service;

import domain.model.Tag;
import domain.model.User;
import domain.model.PermissionType;

import infrastructure.repository.TagRepository;

import java.util.List;

public class TagService {
    private final TagRepository tagRepository;
    private final PermissionService permissionService;


    public TagService(TagRepository tagRepository, PermissionService permissionService) {
        this.tagRepository = tagRepository;
        this.permissionService = permissionService;

    }
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

    public List<Tag> getAllTags() {
        return tagRepository.findAll();
    }

    public Tag findById(int id) {
        return tagRepository.findById(id);
    }

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

    // READ_TAGS
    public List<Tag> getTags(User user) {
        if (!permissionService.hasPermission(user, PermissionType.READ_TAGS)) {
            throw new SecurityException("You do not have permission to read tags.");
        }
        return tagRepository.findAll();
    }


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
