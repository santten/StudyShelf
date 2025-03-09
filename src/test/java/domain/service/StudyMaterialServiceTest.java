package domain.service;

import domain.model.*;
import infrastructure.repository.StudyMaterialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class StudyMaterialServiceTest {
    private StudyMaterialService materialService;

    @Mock
    private GoogleDriveService driveService;

    @Mock
    private StudyMaterialRepository materialRepository;

    @Mock(strictness = Mock.Strictness.LENIENT)
    private PermissionService permissionService;

    private User uploader;
    private User adminUser;
    private StudyMaterial testMaterial;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        materialService = new StudyMaterialService(driveService, materialRepository, permissionService);

        Role adminRole = new Role(RoleType.ADMIN);
        adminUser = new User(2, "Admin", "User", "admin@example.com", "password", adminRole);

        Role testRole = new Role(RoleType.STUDENT);
        uploader = new User(1, "Armas", "Nevolainen", "armas@example.com", "password", testRole);

        Category category = new Category("Java Programming", uploader);

        testMaterial = new StudyMaterial(
                uploader,
                "Java Basics",
                "Introduction to Java",
                "https://drive.google.com/file/test",
                1.2f,
                "text/plain",
                java.time.LocalDateTime.now(),
                MaterialStatus.PENDING
        );

        testMaterial.setMaterialId(1);
        testMaterial.setCategory(category);
    }

    @Test
    void uploadMaterial_Success() throws IOException {
        byte[] content = "Test content".getBytes();
        String filename = "test.txt";
        String expectedDriveUrl = "https://drive.google.com/file/test";
        Category category = new Category("Java Programming", adminUser);

        when(permissionService.hasPermission(uploader, PermissionType.CREATE_RESOURCE)).thenReturn(true);
        when(driveService.uploadFile(content, filename, "text/plain")).thenReturn(expectedDriveUrl);
        when(materialRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        Set<Tag> tags = new HashSet<>();

        StudyMaterial result = materialService.uploadMaterial(content, filename, uploader, "Java Basics", "Intro to Java", category, tags);

        assertNotNull(result);
        assertEquals("Java Basics", result.getName());
        assertEquals("Intro to Java", result.getDescription());
        assertEquals(expectedDriveUrl, result.getLink());
        assertEquals(MaterialStatus.PENDING, result.getStatus());

        verify(materialRepository, times(1)).save(any(StudyMaterial.class));
    }

    @Test
    void uploadMaterial_NoPermission() {
        byte[] content = "Test content".getBytes();
        String filename = "test.txt";
        Category category = new Category("Java Programming", uploader);

        when(permissionService.hasPermission(uploader, PermissionType.CREATE_RESOURCE)).thenReturn(false);

        assertThrows(SecurityException.class, () ->
                materialService.uploadMaterial(content, filename, uploader, "Java Basics", "Intro to Java", category, null)
        );

        verify(materialRepository, never()).save(any());
    }

    @Test
    void updateMaterialStatus_Success() {
        when(permissionService.hasApprovalPermission(adminUser)).thenReturn(true);

        materialService.approveMaterial(adminUser, testMaterial);

        assertEquals(MaterialStatus.APPROVED, testMaterial.getStatus());
        verify(materialRepository, times(1)).updateMaterialStatus(testMaterial.getMaterialId(), MaterialStatus.APPROVED);
    }



    @Test
    void findMaterialsByStatus() {
        when(permissionService.hasPermission(uploader, PermissionType.READ_RESOURCES)).thenReturn(true);
        when(materialRepository.findByStatus(MaterialStatus.APPROVED)).thenReturn(List.of(testMaterial));

        List<StudyMaterial> materials = materialService.getApprovedMaterials(uploader);

        assertEquals(1, materials.size());
        assertEquals(testMaterial, materials.get(0));

        verify(materialRepository, times(1)).findByStatus(MaterialStatus.APPROVED);
    }

    @Test
    void deleteMaterial_AsUploader_Success() {
        when(permissionService.hasPermission(uploader, PermissionType.DELETE_OWN_RESOURCE)).thenReturn(true);
        assertEquals(uploader.getUserId(), testMaterial.getUploader().getUserId());

        materialService.deleteMaterial(uploader, testMaterial);

        verify(materialRepository, times(1)).delete(testMaterial);
    }

    @Test
    void deleteMaterial_AsAdmin_Success() {
        when(permissionService.hasPermission(adminUser, PermissionType.DELETE_ANY_RESOURCE)).thenReturn(true);

        materialService.deleteMaterial(adminUser, testMaterial);

        verify(materialRepository, times(1)).delete(testMaterial);
    }

    @Test
    void deleteMaterial_NoPermission() {
        when(permissionService.hasPermissionOnEntity(uploader, PermissionType.DELETE_OWN_RESOURCE, uploader.getUserId())).thenReturn(false);
        when(permissionService.hasPermission(uploader, PermissionType.DELETE_ANY_RESOURCE)).thenReturn(false);

        assertThrows(SecurityException.class, () -> materialService.deleteMaterial(uploader, testMaterial));

        verify(materialRepository, never()).delete(any(StudyMaterial.class));
    }

    @Test
    void downloadMaterial_Success() throws IOException {
        java.io.File saveLocation = File.createTempFile("testFile", ".txt");

        byte[] fileData = "Test File Data".getBytes();

        testMaterial.setStatus(MaterialStatus.APPROVED);
        testMaterial.setLink("http://test-download-link.com");

        assertNotNull(uploader, "Uploader cannot be null");
        assertNotNull(permissionService, "PermissionService cannot be null");
        assertNotNull(testMaterial.getLink(), "StudyMaterial cannot be null");

        when(permissionService.hasPermission(any(User.class), eq(PermissionType.READ_RESOURCES)))
                .thenReturn(true);

        when(driveService.downloadFile(testMaterial.getLink())).thenReturn(fileData);

        materialService.downloadMaterial(uploader, testMaterial, saveLocation);
        verify(driveService, times(1)).downloadFile(testMaterial.getLink());
    }


    @Test
    void downloadMaterial_NoPermission() throws IOException {
        java.io.File saveLocation = mock(java.io.File.class);

        when(permissionService.hasPermission(uploader, PermissionType.READ_RESOURCES)).thenReturn(false);

        assertThrows(SecurityException.class, () ->
                materialService.downloadMaterial(uploader, testMaterial, saveLocation)
        );

        verify(driveService, never()).downloadFile(anyString());
    }

    @Test
    void approveMaterial_AsAdmin_Success() {
        when(permissionService.hasApprovalPermission(adminUser)).thenReturn(true);

        materialService.approveMaterial(adminUser, testMaterial);

        assertEquals(MaterialStatus.APPROVED, testMaterial.getStatus());
        verify(materialRepository, times(1)).updateMaterialStatus(testMaterial.getMaterialId(), MaterialStatus.APPROVED);
    }

    @Test
    void rejectMaterial_AsAdmin_Success() {
        when(permissionService.hasApprovalPermission(adminUser)).thenReturn(true);

        materialService.rejectMaterial(adminUser, testMaterial);

        assertEquals(MaterialStatus.REJECTED, testMaterial.getStatus());
        verify(materialRepository, times(1)).updateMaterialStatus(testMaterial.getMaterialId(), MaterialStatus.REJECTED);
    }

    @Test
    void approveMaterial_NoPermission() {
        when(permissionService.hasApprovalPermission(uploader)).thenReturn(false);

        assertThrows(SecurityException.class, () -> materialService.approveMaterial(uploader, testMaterial));

        verify(materialRepository, never()).save(any(StudyMaterial.class));
    }

    @Test
    void autoApprove_courseOwnerMaterial() throws IOException {
        byte[] content = "Test content".getBytes();
        String filename = "test.txt";
        String expectedDriveUrl = "https://drive.google.com/file/test";
        Category category = new Category("Java Programming", uploader);

        when(permissionService.hasPermission(uploader, PermissionType.CREATE_RESOURCE)).thenReturn(true);
        when(driveService.uploadFile(content, filename, "text/plain")).thenReturn(expectedDriveUrl);
        when(materialRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);
        Set<Tag> tags = new HashSet<>();

        StudyMaterial result = materialService.uploadMaterial(content, filename, uploader, "Java Basics", "Intro to Java", category, tags);

        assertNotNull(result);
        assertEquals("Java Basics", result.getName());
        assertEquals("Intro to Java", result.getDescription());
        assertEquals(expectedDriveUrl, result.getLink());
        assertEquals(MaterialStatus.APPROVED, result.getStatus());

        verify(materialRepository, times(1)).save(any(StudyMaterial.class));
    }
}
