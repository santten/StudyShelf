package domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class GoogleDriveServiceTest {
    private GoogleDriveService driveService;

    @BeforeEach
    void setUp() {
        driveService = new GoogleDriveService();
    }

    @Test
    void uploadFile() throws IOException {
        byte[] content = "Test content".getBytes();
        String filename = "test.txt";
        String contentType = "text/plain";

        String fileUrl = driveService.uploadFile(content, filename, contentType);
        assertNotNull(fileUrl);
        assertTrue(fileUrl.contains("drive.google.com"));
    }

    @Test
    void serviceInitialization() {
        assertNotNull(driveService);
    }
}