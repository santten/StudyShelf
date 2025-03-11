package domain.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class PreviewGeneratorServiceTest {

    private PreviewGeneratorService previewGenerator;

    @BeforeEach
    void setUp() {
        previewGenerator = new PreviewGeneratorService();
    }

    @Test
    void testGeneratePreview_PDF() throws IOException {
        // Create a simple PDF document
        byte[] pdfData = createSamplePDF();

        // Generate preview
        byte[] preview = previewGenerator.generatePreview(pdfData, "application/pdf");

        // Verify
        assertNotNull(preview);
        assertTrue(preview.length > 0);
        assertValidImageData(preview);
    }

    @Test
    void testGeneratePreview_Image() throws IOException {
        // Create a sample image
        byte[] imageData = createSampleImage();

        // Test JPEG
        byte[] jpegPreview = previewGenerator.generatePreview(imageData, "image/jpeg");
        assertNotNull(jpegPreview);
        assertTrue(jpegPreview.length > 0);
        assertValidImageData(jpegPreview);

        // Test PNG
        byte[] pngPreview = previewGenerator.generatePreview(imageData, "image/png");
        assertNotNull(pngPreview);
        assertTrue(pngPreview.length > 0);
        assertValidImageData(pngPreview);

        // Test GIF
        byte[] gifPreview = previewGenerator.generatePreview(imageData, "image/gif");
        assertNotNull(gifPreview);
        assertTrue(gifPreview.length > 0);
        assertValidImageData(gifPreview);
    }

    @Test
    void testGeneratePreview_Word() throws IOException {
        // Create a sample Word document
        byte[] wordData = createSampleWordDocument();

        // Test DOCX
        byte[] docxPreview = previewGenerator.generatePreview(wordData,
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document");
        assertNotNull(docxPreview);
        assertTrue(docxPreview.length > 0);
        assertValidImageData(docxPreview);

        // Test DOC
        byte[] docPreview = previewGenerator.generatePreview(wordData, "application/msword");
        assertNotNull(docPreview);
        assertTrue(docPreview.length > 0);
        assertValidImageData(docPreview);
    }

    @Test
    void testGeneratePreview_DefaultForUnknownType() throws IOException {
        // Test unknown file type
        byte[] data = "Some random data".getBytes();
        byte[] preview = previewGenerator.generatePreview(data, "application/unknown");

        assertNotNull(preview);
        assertTrue(preview.length > 0);
        assertValidImageData(preview);
    }

    @Test
    void testGeneratePreview_NullOrEmptyFileType() throws IOException {
        byte[] data = "Some random data".getBytes();

        // Test null file type
        byte[] preview1 = previewGenerator.generatePreview(data, null);
        assertNotNull(preview1);
        assertTrue(preview1.length > 0);
        assertValidImageData(preview1);

        // Test empty file type
        byte[] preview2 = previewGenerator.generatePreview(data, "");
        assertNotNull(preview2);
        assertTrue(preview2.length > 0);
        assertValidImageData(preview2);
    }



    private byte[] createSamplePDF() throws IOException {
        PDDocument document = new PDDocument();
        document.addPage(new PDPage());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        document.save(baos);
        document.close();
        return baos.toByteArray();
    }

    private byte[] createSampleImage() throws IOException {
        BufferedImage image = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);
        return baos.toByteArray();
    }

    private byte[] createSampleWordDocument() throws IOException {
        XWPFDocument document = new XWPFDocument();
        XWPFParagraph paragraph = document.createParagraph();
        XWPFRun run = paragraph.createRun();
        run.setText("Sample Word document for testing");

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        document.write(baos);
        document.close();
        return baos.toByteArray();
    }

    private void assertValidImageData(byte[] data) {
        try {
            BufferedImage image = ImageIO.read(new ByteArrayInputStream(data));
            assertNotNull(image, "Data should be valid image data");
        } catch (IOException e) {
            fail("Could not read image data: " + e.getMessage());
        }
    }
}
