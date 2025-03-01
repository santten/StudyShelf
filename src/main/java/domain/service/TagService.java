package domain.service;

import domain.model.Tag;
import domain.model.User;
import infrastructure.repository.TagRepository;

import java.util.List;

public class TagService {
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
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
}
