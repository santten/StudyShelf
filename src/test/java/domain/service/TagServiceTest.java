package domain.service;

import domain.model.Tag;
import domain.model.User;
import infrastructure.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TagServiceTest {
    private TagService tagService;
    private TagRepository tagRepository;

    @BeforeEach
    void setUp() {
        tagRepository = Mockito.mock(TagRepository.class);
        tagService = new TagService(tagRepository);
    }

    @Test
    void testCreateTag_NewTag() {
        String tagName = "Java";
        User creator = new User();

        when(tagRepository.findByName(tagName)).thenReturn(null);
        when(tagRepository.save(any(Tag.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Tag createdTag = tagService.createTag(tagName, creator);

        assertNotNull(createdTag);
        assertEquals(tagName, createdTag.getTagName());
        assertEquals(creator, createdTag.getCreator());

        verify(tagRepository, times(1)).save(any(Tag.class));
    }

    @Test
    void testCreateTag_ExistingTag() {
        String tagName = "Python";
        User creator = new User();
        Tag existingTag = new Tag(tagName, creator);

        when(tagRepository.findByName(tagName)).thenReturn(existingTag);

        Tag resultTag = tagService.createTag(tagName, creator);

        assertNotNull(resultTag);
        assertEquals(existingTag, resultTag);

        verify(tagRepository, never()).save(any(Tag.class));
    }
}
