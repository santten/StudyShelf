package domain.service;

import domain.model.Tag;
import domain.model.User;
import infrastructure.repository.TagRepository;

public class TagService {
    private final TagRepository tagRepository;

    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    public Tag createTag(String name, User creator) {
        Tag existingTag = tagRepository.findByName(name);
        if (existingTag != null) {
            return existingTag;
        }
        Tag newTag = new Tag(name, creator);
        return tagRepository.save(newTag);
    }
}
