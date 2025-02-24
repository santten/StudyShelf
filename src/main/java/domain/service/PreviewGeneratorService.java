package domain.service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import net.coobird.thumbnailator.Thumbnails;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import java.awt.Graphics2D;
import java.awt.Color;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;
import java.io.IOException;

public class PreviewGeneratorService {
    public byte[] generatePreview(byte[] content, String fileType) throws IOException {
        if (fileType == null || fileType.isEmpty()) {
            return generateDefaultPreview();
        }
        switch(fileType) {
            case "application/pdf":
                return generatePDFPreview(content);
            case "image/jpeg":
            case "image/png":
            case "image/gif":
                return generateImagePreview(content);
            case "application/msword":
            case "application/vnd.openxmlformats-officedocument.wordprocessingml.document":
                return generateWordPreview(content);
            default:
                return generateDefaultPreview();
        }
    }

    private byte[] generatePDFPreview(byte[] content) throws IOException {
        PDDocument document = PDDocument.load(content);
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        BufferedImage image = pdfRenderer.renderImageWithDPI(0, 50);
        BufferedImage resized = Thumbnails.of(image)
                .size(200, 200)
                .asBufferedImage();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resized, "PNG", baos);
        document.close();
        return baos.toByteArray();
    }

    private byte[] generateImagePreview(byte[] content) throws IOException {
        BufferedImage originalImage = ImageIO.read(new ByteArrayInputStream(content));
        BufferedImage thumbnail = Thumbnails.of(originalImage)
            .size(200, 200)
            .asBufferedImage();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(thumbnail, "PNG", baos);
        return baos.toByteArray();
    }

    private byte[] generateWordPreview(byte[] content) throws IOException {
        XWPFDocument document = new XWPFDocument(new ByteArrayInputStream(content));
        BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        // Draw first page preview
        XWPFParagraph firstParagraph = document.getParagraphs().get(0);
        g2d.drawString(firstParagraph.getText(), 10, 20);
        g2d.dispose();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);
        return baos.toByteArray();
    }

    private byte[] generateDefaultPreview() throws IOException {
        BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, 200, 200);
        g2d.setColor(Color.BLACK);
        g2d.drawString("No Preview Available", 40, 100);
        g2d.dispose();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "PNG", baos);
        return baos.toByteArray();
    }
}
