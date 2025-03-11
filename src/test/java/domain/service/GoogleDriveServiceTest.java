package domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GoogleDriveServiceTest {

    @Mock
    private GoogleDriveService driveService;

    @BeforeEach
    void setUp() {

    }

    @Test
    void uploadFile() throws IOException {
        when(driveService.uploadFile(any(byte[].class), anyString(), anyString()))
                .thenReturn("https://drive.google.com/file/d/mock-file-id/view");

        byte[] content = "Test content".getBytes();
        String filename = "test1.txt";
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