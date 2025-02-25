package presentation.components;

import domain.model.StudyMaterial;
import domain.service.RatingService;
import infrastructure.repository.RatingRepository;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import presentation.GUILogger;
import presentation.view.SceneManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static presentation.view.Screen.SCREEN_HOME;

public class MaterialPage {
    public static void setPage(StudyMaterial s){
        /* header: preview and file details*/
        VBox base = new VBox();
        base.getStylesheets().add(Objects.requireNonNull(MaterialPage.class.getResource("/css/style.css")).toExternalForm());
        base.setSpacing(12);
        base.setPadding(new Insets(20, 20, 20, 20));

        HBox main = new HBox();

        VBox left = new VBox();
        Label title = new Label(s.getName());
        title.getStyleClass().add("label3");
        title.getStyleClass().add("primary-light");
        title.setWrapText(true);
        title.setMaxWidth(600);

        TextFlow uploaderLabels = new TextFlow();
        Text author = new Text("Uploaded by ");
        author.setStyle("-fx-font-size: 1.2em;");

        Hyperlink authorLink = new Hyperlink(s.getUploader().getFullName());
        authorLink.setStyle("-fx-font-size: 1.2em; -fx-underline: false;");
        authorLink.setOnAction(e -> {
            try {
                SceneManager.getInstance().displayProfile(s.getUploader().getUserId());
            } catch (IOException ex) {
                SceneManager.getInstance().displayErrorPage("User not found.", SCREEN_HOME, "Go to Home");
            }
        });

        uploaderLabels.getChildren().addAll(author, authorLink, new Text("  "), TextLabels.getUserRoleLabel(s.getUploader()), new Text("  "));

        if (s.getUploader() == s.getCategory().getCreator()) {
            Label categoryOwnerLabel = new Label("Course Owner");
            categoryOwnerLabel.getStyleClass().add("primaryTagLabel");
            uploaderLabels.getChildren().add(categoryOwnerLabel);
        }

        Text fileDetails = new Text(Math.round(s.getFileSize()) + " KB " + s.getFileType());

        Button downloadBtn = new Button("Download");
        downloadBtn.getStyleClass().add("btnDownload");
        downloadBtn.setOnAction(event -> GUILogger.info("Pressed button to download " + s.getName()));

        TextFlow fileDesc = new TextFlow();
        fileDesc.getChildren().add(new Text(s.getDescription()));
        fileDesc.setMaxWidth(580);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
        String formattedTimestamp = s.getTimestamp().format(formatter);

        Text course = new Text("Uploaded under course " + s.getCategory().getCategoryName() + " on " + formattedTimestamp);
        course.getStyleClass().add("primary");

        left.getChildren().addAll(title, uploaderLabels, fileDetails, downloadBtn,
                course, fileDesc);
        left.setMinWidth(580);
        left.setMaxWidth(580);
        left.setSpacing(8);

        VBox right = new VBox();
        ImageView preview = new ImageView(new Image(new ByteArrayInputStream(s.getPreviewImage())));
        preview.setFitWidth(141);
        preview.setFitHeight(188);
        preview.setPreserveRatio(false);
        right.getChildren().add(preview);

        main.setSpacing(20);
        main.getChildren().addAll(left, right);

        /* under header: review section*/
        VBox reviews = new VBox();
        HBox reviewHeading = new HBox();

        Label reviewTitle = new Label("Ratings");
        reviewTitle.getStyleClass().add("label3");
        reviewTitle.getStyleClass().add("error");

        double avgRating = new RatingService(new RatingRepository()).getAverageRating(s);
        Text avgRatingText = new Text(avgRating > 0 ? String.format("(%.1f)", avgRating) : "(No ratings yet)");

        avgRatingText.setStyle("-fx-font-size: 1.2em;");
        reviewHeading.getChildren().addAll(reviewTitle, Stars.StarRow(avgRating, 1.2, 5), avgRatingText);
        reviewHeading.setSpacing(10);
        reviewHeading.setAlignment(Pos.CENTER_LEFT);

        reviews.getChildren().addAll(reviewHeading);

        base.getChildren().addAll(main, reviews);
        ScrollPane wrapper = new ScrollPane(base);
        wrapper.setFitToHeight(true);
        wrapper.setFitToWidth(true);

        SceneManager.getInstance().setCenter(wrapper);
    }
}
