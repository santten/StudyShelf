package domain.service;

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.ByteArrayContent;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;
import com.google.api.services.drive.model.File;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

/**
 * Service class responsible for interacting with Google Drive API.
 * Supports file upload and download operations using user credentials.
 */
public class GoogleDriveService {
    private final Drive driveService;
    // add file credentials.json to this path
    private static final String CREDENTIALS_PATH = "src/main/resources/credentials/credentials.json";
    private static final JsonFactory JSON_FACTORY = GsonFactory.getDefaultInstance();
    private static final List<String> SCOPES = Collections.singletonList(DriveScopes.DRIVE_FILE);

    /**
     * Initializes the Google Drive client using OAuth 2.0 credentials.
     * Requires a valid credentials.json file under src/main/resources/credentials/.
     */
    public GoogleDriveService() {
        try {
            NetHttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
            GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(JSON_FACTORY,
                    new FileReader(CREDENTIALS_PATH));

            GoogleAuthorizationCodeFlow flow = new GoogleAuthorizationCodeFlow.Builder(
                    httpTransport, JSON_FACTORY, clientSecrets, SCOPES)
                    .setDataStoreFactory(new FileDataStoreFactory(new java.io.File("tokens")))
                    .setAccessType("offline")
                    .build();

            LocalServerReceiver receiver = new LocalServerReceiver.Builder().setPort(8888).build();
            Credential credential = new AuthorizationCodeInstalledApp(flow, receiver).authorize("user");

            driveService = new Drive.Builder(httpTransport, JSON_FACTORY, credential)
                    .setApplicationName("StudyShelf")
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to initialize Google Drive service", e);
        }
    }

    /**
     * Uploads a file to Google Drive.
     *
     * @param content     byte array of file content
     * @param filename    name of the file to be stored
     * @param contentType MIME type of the file
     * @return a public web view link to the uploaded file
     * @throws IOException if an upload error occurs
     */
    public String uploadFile(byte[] content, String filename, String contentType) throws IOException {
        File fileMetadata = new File();
        fileMetadata.setName(filename);

        ByteArrayContent mediaContent = new ByteArrayContent(contentType, content);

        File uploadedFile = driveService.files().create(fileMetadata, mediaContent)
                .setFields("id, webViewLink")
                .execute();

        return uploadedFile.getWebViewLink();
    }

    /**
     * Downloads a file from Google Drive based on its shared webViewLink.
     *
     * @param fileUrl the web view URL of the Google Drive file
     * @return the file contents as a byte array
     * @throws IOException if download fails
     */
    public void downloadFile(String fileUrl, OutputStream outputStream, Consumer<Long> progressCallback) throws IOException {
        String fileId = extractFileIdFromUrl(fileUrl);
        InputStream in = driveService.files().get(fileId).executeMediaAsInputStream();
        byte[] buffer = new byte[8192];
        long totalRead = 0;
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
            totalRead += bytesRead;
            progressCallback.accept(totalRead);
        }
        in.close();
    }

    /**
     * Extracts the file ID from a Google Drive web view URL.
     * Expected format: https://drive.google.com/file/d/{fileId}/view
     *
     * @param url the shared Google Drive URL
     * @return the extracted file ID
     */
    private String extractFileIdFromUrl(String url) {
        String[] parts = url.split("/");
        return parts[5];
    }

    public long getFileSize(String fileUrl) throws IOException {
        String fileId = extractFileIdFromUrl(fileUrl);
        File file = driveService.files().get(fileId).setFields("size").execute();
        return file.getSize();
    }




}