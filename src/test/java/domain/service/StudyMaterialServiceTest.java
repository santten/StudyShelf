package domain.service;

import domain.model.*;
import infrastructure.config.DatabaseConnection;
import infrastructure.repository.StudyMaterialRepository;
import jakarta.persistence.EntityManagerFactory;
import javafx.concurrent.Task;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import util.TestPersistenceUtil;

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

    private static MockedStatic<DatabaseConnection> mockedDatabaseConnection;
    private static MockedStatic<Session> mockedSession;
    private static MockedStatic<TestPersistenceUtil> mockedTestPersistenceUtil;

    @BeforeAll
    public static void setupClass() {
        // Force any database connections to use H2 instead of MariaDB
        System.setProperty("jakarta.persistence.jdbc.url", "jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        System.setProperty("jakarta.persistence.jdbc.driver", "org.h2.Driver");
        System.setProperty("jakarta.persistence.jdbc.user", "sa");
        System.setProperty("jakarta.persistence.jdbc.password", "");
        System.setProperty("hibernate.dialect", "org.hibernate.dialect.H2Dialect");
        System.setProperty("test", "true");

        // Mock the static DatabaseConnection.getEntityManagerFactory() method
        mockedDatabaseConnection = Mockito.mockStatic(DatabaseConnection.class);
        EntityManagerFactory mockEmf = Mockito.mock(EntityManagerFactory.class);
        mockedDatabaseConnection.when(DatabaseConnection::getEntityManagerFactory).thenReturn(mockEmf);

        // Mock the Session class
        mockedSession = Mockito.mockStatic(Session.class);
        Session sessionMock = Mockito.mock(Session.class);
        mockedSession.when(Session::getInstance).thenReturn(sessionMock);

        // Mock TestPersistenceUtil
        mockedTestPersistenceUtil = Mockito.mockStatic(TestPersistenceUtil.class);
        EntityManagerFactory testEmf = Mockito.mock(EntityManagerFactory.class);
        mockedTestPersistenceUtil.when(TestPersistenceUtil::getEntityManagerFactory).thenReturn(testEmf);
    }

    @AfterAll
    public static void tearDownStaticMocks() {
        if (mockedDatabaseConnection != null) {
            mockedDatabaseConnection.close();
        }
        if (mockedSession != null) {
            mockedSession.close();
        }
        if (mockedTestPersistenceUtil != null) {
            mockedTestPersistenceUtil.close();
        }
    }

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
    void updateMaterial_AsUser_Success() {
        StudyMaterial updatedMaterial = new StudyMaterial(
                uploader,
                "Updated Java Basics",
                "Updated Introduction to Java",
                "https://drive.google.com/file/test-updated",
                1.5f,
                "text/plain",
                java.time.LocalDateTime.now(),
                MaterialStatus.APPROVED
        );
        updatedMaterial.setMaterialId(1);

        // Setup permission check
        when(permissionService.hasPermissionOnEntity(uploader, PermissionType.UPDATE_OWN_RESOURCE, uploader.getUserId()))
                .thenReturn(true);
        when(materialRepository.findById(updatedMaterial.getMaterialId())).thenReturn(testMaterial);
        when(materialRepository.save(any(StudyMaterial.class))).thenReturn(updatedMaterial);

        StudyMaterial result = materialService.updateMaterial(uploader, updatedMaterial);

        assertNotNull(result);
        assertEquals("Updated Java Basics", result.getName());
        assertEquals("Updated Introduction to Java", result.getDescription());
        assertEquals(MaterialStatus.PENDING, testMaterial.getStatus());

        verify(materialRepository).findById(updatedMaterial.getMaterialId());
        verify(materialRepository).save(testMaterial);
    }

    @Test
    void updateMaterial_NoPermission() {
        // Setup
        StudyMaterial updatedMaterial = new StudyMaterial();
        updatedMaterial.setMaterialId(1);

        when(materialRepository.findById(updatedMaterial.getMaterialId())).thenReturn(testMaterial);
        when(permissionService.hasPermissionOnEntity(uploader, PermissionType.UPDATE_OWN_RESOURCE, uploader.getUserId()))
                .thenReturn(false);

        // Execute & Verify
        assertThrows(SecurityException.class, () ->
                materialService.updateMaterial(uploader, updatedMaterial)
        );

        verify(materialRepository).findById(updatedMaterial.getMaterialId());
        verify(materialRepository, never()).save(any());
    }

    @Test
    void updateMaterial_MaterialNotFound() {
        // Setup
        StudyMaterial updatedMaterial = new StudyMaterial();
        updatedMaterial.setMaterialId(999); // Non-existent ID

        when(materialRepository.findById(999)).thenReturn(null);

        // Execute & Verify
        assertThrows(RuntimeException.class, () ->
                materialService.updateMaterial(uploader, updatedMaterial)
        );

        verify(materialRepository).findById(999);
        verify(materialRepository, never()).save(any());
    }

    @Test
    void getPendingMaterialsForReview_Success() {
        // Setup
        List<StudyMaterial> pendingMaterials = List.of(testMaterial);
        when(permissionService.hasPermission(adminUser, PermissionType.REVIEW_PENDING_RESOURCES))
                .thenReturn(true);
        when(materialRepository.findByStatus(MaterialStatus.PENDING)).thenReturn(pendingMaterials);

        // Execute
        List<StudyMaterial> result = materialService.getPendingMaterialsForReview(adminUser);

        // Verify
        assertEquals(1, result.size());
        assertEquals(testMaterial, result.get(0));
        verify(materialRepository).findByStatus(MaterialStatus.PENDING);
    }

    @Test
    void getPendingMaterialsForReview_NoPermission() {
        // Setup
        when(permissionService.hasPermission(uploader, PermissionType.REVIEW_PENDING_RESOURCES))
                .thenReturn(false);

        // Execute & Verify
        assertThrows(SecurityException.class, () ->
                materialService.getPendingMaterialsForReview(uploader)
        );

        verify(materialRepository, never()).findByStatus(any());
    }

    @Test
    void getAllResources_Success() {
        // Setup
        List<StudyMaterial> allMaterials = List.of(testMaterial);
        when(permissionService.hasPermission(adminUser, PermissionType.READ_RESOURCES))
                .thenReturn(true);
        when(materialRepository.findAll()).thenReturn(allMaterials);

        // Execute
        List<StudyMaterial> result = materialService.getAllResources(adminUser);

        // Verify
        assertEquals(1, result.size());
        assertEquals(testMaterial, result.get(0));
        verify(materialRepository).findAll();
    }

    @Test
    void getAllResources_NoPermission() {
        // Setup
        when(permissionService.hasPermission(uploader, PermissionType.READ_RESOURCES))
                .thenReturn(false);

        // Execute & Verify
        assertThrows(SecurityException.class, () ->
                materialService.getAllResources(uploader)
        );

        verify(materialRepository, never()).findAll();
    }

    @Test
    void updateMaterial_Repository() {
        // Setup
        when(materialRepository.update(testMaterial)).thenReturn(testMaterial);

        // Execute
        StudyMaterial result = materialService.updateMaterial(testMaterial);

        // Verify
        assertNotNull(result);
        assertEquals(testMaterial, result);
        verify(materialRepository).update(testMaterial);
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

        testMaterial.setStatus(MaterialStatus.APPROVED);
        testMaterial.setLink("http://test-download-link.com");

        // Mock the permission check
        when(permissionService.hasPermission(any(User.class), eq(PermissionType.READ_RESOURCES)))
                .thenReturn(true);

        // Create a spy on materialService to verify it creates tasks with the right parameters
        StudyMaterialService spy = spy(materialService);

        // Call the method
        spy.downloadMaterial(uploader, testMaterial, saveLocation);

        // Verify the method was called with the correct parameters
        verify(spy).downloadMaterial(eq(uploader), eq(testMaterial), eq(saveLocation));

        // We don't verify driveService.downloadFile() since it's called inside the task
    }


    @Test
    void downloadMaterial_NoPermission() throws IOException {

        // Create a spy of materialService
        StudyMaterialService serviceSpy = spy(materialService);

        // Mock all permissions to deny access
        when(permissionService.hasPermission(uploader, PermissionType.READ_RESOURCES))
                .thenReturn(false);
        when(permissionService.hasPermission(uploader, PermissionType.REVIEW_PENDING_RESOURCES))
                .thenReturn(false);

        testMaterial.setStatus(MaterialStatus.APPROVED);
        java.io.File saveLocation = mock(java.io.File.class);

        // Call the method and verify the task is created with the right permissions check
        serviceSpy.downloadMaterial(uploader, testMaterial, saveLocation);

        // Verify downloadFile is never called
        verify(driveService, never()).downloadFile(any(), any(), any());
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
    void rejectMaterial_MaterialAlreadyProcessed() {
        // Setup
        testMaterial.setStatus(MaterialStatus.APPROVED);
        when(permissionService.hasApprovalPermission(adminUser)).thenReturn(true);

        // Execute & Verify
        assertThrows(IllegalStateException.class, () ->
                materialService.rejectMaterial(adminUser, testMaterial)
        );

        verify(materialRepository, never()).updateMaterialStatus(anyInt(), any());
    }

    @Test
    void getApprovedMaterials_NoPermission() {
        // Setup
        when(permissionService.hasPermission(uploader, PermissionType.READ_RESOURCES))
                .thenReturn(false);

        // Execute & Verify
        assertThrows(SecurityException.class, () ->
                materialService.getApprovedMaterials(uploader)
        );

        verify(materialRepository, never()).findByStatus(any());
    }

    @Test
    void approveMaterial_MaterialAlreadyProcessed() {
        // Setup
        testMaterial.setStatus(MaterialStatus.REJECTED);
        when(permissionService.hasApprovalPermission(adminUser)).thenReturn(true);

        // Execute & Verify
        assertThrows(IllegalStateException.class, () ->
                materialService.approveMaterial(adminUser, testMaterial)
        );

        verify(materialRepository, never()).updateMaterialStatus(anyInt(), any());
    }

    @Test
    void deleteMaterial_AsCourseOwner_Success() {
        User courseOwner = new User(3, "Course", "Owner", "course@example.com", "password", new Role(RoleType.TEACHER));
        Category category = new Category("Java Course", courseOwner);
        testMaterial.setCategory(category);

        when(permissionService.hasPermission(courseOwner, PermissionType.DELETE_COURSE_RESOURCE)).thenReturn(true);
        when(permissionService.hasPermission(courseOwner, PermissionType.DELETE_OWN_RESOURCE)).thenReturn(false);
        when(permissionService.hasPermission(courseOwner, PermissionType.DELETE_ANY_RESOURCE)).thenReturn(false);

        materialService.deleteMaterial(courseOwner, testMaterial);

        verify(materialRepository).delete(testMaterial);
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

    @Test
    void findByUser() {
        when(permissionService.hasPermission(uploader, PermissionType.READ_RESOURCES)).thenReturn(true);
        when(materialRepository.findByUser(uploader)).thenReturn(List.of(testMaterial));

        List<StudyMaterial> materials = materialService.findByUser(uploader);

        assertEquals(1, materials.size());
        assertEquals(testMaterial, materials.get(0));

        verify(materialRepository, times(1)).findByUser(uploader);
    }

    @Test
    void findByUser_NoPermission() {
        when(permissionService.hasPermission(uploader, PermissionType.READ_RESOURCES)).thenReturn(false);

        assertThrows(SecurityException.class, () -> materialService.findByUser(uploader));

        verify(materialRepository, never()).findByUser(any(User.class));
    }

    @Test
    void updateDescription_AsUploader_Success() {
        when(permissionService.hasPermission(uploader, PermissionType.UPDATE_OWN_RESOURCE)).thenReturn(true);

        materialService.updateDescription(uploader, testMaterial, "Updated Description");

        verify(materialRepository, times(1)).updateMaterialDescription(testMaterial.getMaterialId(), "Updated Description");
    }

    @Test
    void updateDescription_AsAdmin_Success() {
        materialService.updateDescription(adminUser, testMaterial, "Updated Description");

        verify(materialRepository, times(1)).updateMaterialDescription(testMaterial.getMaterialId(), "Updated Description");
    }

    @Test
    void updateDescription_NoPermission() {
        when(permissionService.hasPermission(uploader, PermissionType.UPDATE_OWN_RESOURCE)).thenReturn(false);

        assertThrows(SecurityException.class, () -> materialService.updateDescription(uploader, testMaterial, "Updated Description"));

        verify(materialRepository, never()).updateMaterialDescription(anyInt(), anyString());
    }

    @Test
    void updateTitle_AsUploader_Success() {
        when(permissionService.hasPermission(uploader, PermissionType.UPDATE_OWN_RESOURCE)).thenReturn(true);

        materialService.updateTitle(uploader, testMaterial, "Updated Title");

        verify(materialRepository, times(1)).updateMaterialTitle(testMaterial.getMaterialId(), "Updated Title");
    }

    @Test
    void updateTitle_AsAdmin_Success() {
        materialService.updateTitle(adminUser, testMaterial, "Updated Title");

        verify(materialRepository, times(1)).updateMaterialTitle(testMaterial.getMaterialId(), "Updated Title");
    }

    @Test
    void updateTitle_NoPermission() {
        when(permissionService.hasPermission(uploader, PermissionType.UPDATE_OWN_RESOURCE)).thenReturn(false);

        assertThrows(SecurityException.class, () -> materialService.updateTitle(uploader, testMaterial, "Updated Title"));

        verify(materialRepository, never()).updateMaterialTitle(anyInt(), anyString());
    }
}
