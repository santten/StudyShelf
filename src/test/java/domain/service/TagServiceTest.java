package domain.service;

import domain.model.Tag;
import domain.model.User;
import domain.model.PermissionType;
import infrastructure.repository.TagRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.List;

class TagServiceTest {
    @Mock
    private TagRepository tagRepository;

    @Mock
    private PermissionService permissionService;

    @InjectMocks
    private TagService tagService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testCreateTag_Success() {
        String tagName = "Java";
        User creator = new User();
        Tag tag = new Tag(tagName, creator);

        when(tagRepository.findByName(tagName)).thenReturn(null);
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);

        Tag result = tagService.createTag(tagName, creator);

        assertNotNull(result);
        assertEquals(tagName, result.getTagName());
    }

    @Test
    void testCreateTag_EmptyTagName() {
        User creator = new User();

        assertThrows(IllegalArgumentException.class, () -> tagService.createTag("", creator));
        assertThrows(IllegalArgumentException.class, () -> tagService.createTag("   ", creator));
        assertThrows(IllegalArgumentException.class, () -> tagService.createTag(null, creator));

        verify(tagRepository, never()).findByName(anyString());
        verify(tagRepository, never()).save(any(Tag.class));
    }

    @Test
    void testGetAllTags() {
        List<Tag> tags = Arrays.asList(new Tag("Java", new User()), new Tag("Spring", new User()));

        when(tagRepository.findAll()).thenReturn(tags);

        List<Tag> result = tagService.getAllTags();

        assertNotNull(result);
        assertEquals(tags.size(), result.size());
        verify(tagRepository).findAll();
    }

    @Test
    void testFindById() {
        int id = 1;
        Tag tag = new Tag("Java", new User());

        when(tagRepository.findById(id)).thenReturn(tag);

        Tag result = tagService.findById(id);

        assertNotNull(result);
        assertEquals(tag, result);
        verify(tagRepository).findById(id);
    }

    @Test
    void testUpdateTag_Success() {
        Tag tag = new Tag("Java", new User());
        String newName = "Java Basics";
        User user = tag.getCreator();

        when(permissionService.hasPermission(user, PermissionType.UPDATE_OWN_TAG)).thenReturn(true);
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);

        Tag result = tagService.updateTag(tag, newName, user);

        assertNotNull(result);
        assertEquals(newName, result.getTagName());
        verify(tagRepository).save(any(Tag.class));
    }

    @Test
    void testUpdateTag_NoPermission() {
        Tag tag = new Tag("Java", new User());
        String newName = "Java Basics";
        User user = new User();

        when(permissionService.hasPermission(user, PermissionType.UPDATE_OWN_TAG)).thenReturn(false);
        when(permissionService.hasPermission(user, PermissionType.UPDATE_COURSE_TAG)).thenReturn(false);
        when(permissionService.hasPermission(user, PermissionType.UPDATE_ANY_TAG)).thenReturn(false);

        assertThrows(SecurityException.class, () -> tagService.updateTag(tag, newName, user));
        verify(tagRepository, never()).save(any(Tag.class));
    }

    @Test
    void testGetTags_WithPermission() {
        User user = new User();
        List<Tag> tags = Arrays.asList(new Tag("Java", user), new Tag("Spring", user));

        when(permissionService.hasPermission(user, PermissionType.READ_TAGS)).thenReturn(true);
        when(tagRepository.findAll()).thenReturn(tags);

        List<Tag> result = tagService.getTags(user);

        assertNotNull(result);
        assertEquals(tags.size(), result.size());
        verify(tagRepository).findAll();
    }

    @Test
    void testGetTags_WithoutPermission() {
        User user = new User();

        when(permissionService.hasPermission(user, PermissionType.READ_TAGS)).thenReturn(false);

        assertThrows(SecurityException.class, () -> tagService.getTags(user));
        verify(tagRepository, never()).findAll();
    }

    @Test
    void testDeleteTag_Success() {
        Tag tag = new Tag("Java", new User());
        User user = new User();

        when(permissionService.hasPermission(user, PermissionType.DELETE_ANY_TAG)).thenReturn(true);

        tagService.deleteTag(tag, user);

        verify(tagRepository).delete(tag);
    }

    @Test
    void testDeleteTag_NoPermission() {
        Tag tag = new Tag("Java", new User());
        User user = new User();

        when(permissionService.hasPermission(user, PermissionType.DELETE_ANY_TAG)).thenReturn(false);
        when(permissionService.hasPermission(user, PermissionType.DELETE_COURSE_TAG)).thenReturn(false);

        assertThrows(SecurityException.class, () -> tagService.deleteTag(tag, user));
        verify(tagRepository, never()).delete(tag);
    }
}
