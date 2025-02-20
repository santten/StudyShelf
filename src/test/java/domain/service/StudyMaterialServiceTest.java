package domain.service;

import domain.model.*;
import infrastructure.repository.StudyMaterialRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.*;
@ExtendWith(MockitoExtension.class)
class StudyMaterialServiceTest {
    private StudyMaterialService materialService;

    @Mock
    private GoogleDriveService driveService;

    @Mock
    private StudyMaterialRepository materialRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        materialService = new StudyMaterialService(driveService, materialRepository);
    }

    @Test
    void uploadMaterial() throws IOException {
        // Setup
        byte[] content = "Test content".getBytes();
        String filename = "test.txt";
        Role testRole = new Role(RoleType.STUDENT);
        User uploader = new User("Armas", "Nevolainen", "armas" + System.currentTimeMillis() + "@gmail.com", "password",testRole);
        String name = "Test Material";
        String description = "Test Description";
        String expectedDriveUrl = "https://drive.google.com/file/test";

        when(driveService.uploadFile(content, filename, "text/plain")).thenReturn(expectedDriveUrl);
        when(materialRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        // Execute
        StudyMaterial result = materialService.uploadMaterial(content, filename, uploader, name, description);

        // Verify
        assertNotNull(result);
        assertEquals(name, result.getName());
        assertEquals(description, result.getDescription());
        assertEquals(expectedDriveUrl, result.getLink());
        assertEquals(uploader, result.getUploader());
        assertEquals(MaterialStatus.APPROVED, result.getStatus());
    }
}
