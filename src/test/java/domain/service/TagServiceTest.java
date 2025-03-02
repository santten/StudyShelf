package domain.service;

import domain.model.Tag;
import domain.model.User;
import domain.model.PermissionType;
import infrastructure.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TagServiceTest {
    private TagService tagService;
    private TagRepository tagRepository;
    private PermissionService permissionService;

    @BeforeEach
    void setUp() {
        tagRepository = Mockito.mock(TagRepository.class);
        permissionService = Mockito.mock(PermissionService.class);
        tagService = new TagService(tagRepository, permissionService);
    }

    @Test
    void testCreateTag_NewTag() {
        String tagName = "Java";
        User creator = new User();

        when(permissionService.hasPermission(creator, PermissionType.CREATE_TAG)).thenReturn(true);
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

        when(permissionService.hasPermission(creator, PermissionType.CREATE_TAG)).thenReturn(true);
        when(tagRepository.findByName(tagName)).thenReturn(existingTag);

        Tag resultTag = tagService.createTag(tagName, creator);

        assertNotNull(resultTag);
        assertEquals(existingTag, resultTag);

        verify(tagRepository, never()).save(any(Tag.class));
    }

    @Test
    void testCreateTag_NoPermission() {
        String tagName = "C++";
        User creator = new User();

        when(permissionService.hasPermission(creator, PermissionType.CREATE_TAG)).thenReturn(false);

        assertThrows(SecurityException.class, () -> tagService.createTag(tagName, creator));

        verify(tagRepository, never()).save(any(Tag.class));
    }
}
