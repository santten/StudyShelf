package presentation.components;

import domain.model.StudyMaterial;
import domain.model.Tag;
import domain.service.*;
import infrastructure.repository.StudyMaterialRepository;
import infrastructure.repository.RatingRepository;
import infrastructure.repository.ReviewRepository;
import domain.model.Review;
import javafx.scene.control.TextArea;
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
import javafx.stage.FileChooser;
import presentation.GUILogger;
import presentation.view.SceneManager;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.Set;

import static presentation.view.Screen.SCREEN_HOME;

public class MaterialPage {
    private static HBox reviewHeading;

    public static void setPage(StudyMaterial s){
        /* header: preview and file details*/
        reviewHeading = new HBox();
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
        downloadBtn.setOnAction(event -> {
            try {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setInitialFileName(s.getName() + "." + s.getFileExtension());
                File saveLocation = fileChooser.showSaveDialog(null);

                if (saveLocation != null) {
                    StudyMaterialService materialService = new StudyMaterialService(
                            new GoogleDriveService(),
                            new StudyMaterialRepository(),
                            new PermissionService()
                    );
                    materialService.downloadMaterial(Session.getInstance().getCurrentUser(), s, saveLocation);
                    GUILogger.info("Successfully downloaded " + s.getName());
                }
            } catch (IOException e) {
                GUILogger.warn("Failed to download file: " + e.getMessage());
            }
        });

        TextFlow fileDesc = new TextFlow();
        fileDesc.getChildren().add(new Text(s.getDescription()));
        fileDesc.setMaxWidth(580);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
        String formattedTimestamp = s.getTimestamp().format(formatter);

        Text course = new Text("Uploaded under course " + s.getCategory().getCategoryName() + " on " + formattedTimestamp);
        course.getStyleClass().add("primary");

        TextFlow tagContainer = new TextFlow();
        Set<Tag> tags = s.getTags();
        tagContainer.setLineSpacing(10);
        tags.forEach(tag -> tagContainer.getChildren().addAll(TagButton.getBtn(tag), new Text("  ")));

        left.getChildren().addAll(title, uploaderLabels, fileDetails, downloadBtn,
                course, fileDesc, tagContainer);
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

        reviews.getChildren().add(reviewHeading);

        base.getChildren().addAll(main, reviews);
        ScrollPane wrapper = new ScrollPane(base);
        wrapper.setFitToHeight(true);
        wrapper.setFitToWidth(true);

        SceneManager.getInstance().setCenter(wrapper);
        updateRatingDisplay(s);
        VBox reviewSection = new VBox();
        displayReviews(s, reviewSection);
        reviews.getChildren().add(reviewSection);
    }

    private static void updateRatingDisplay(StudyMaterial s) {
        double avgRating = new RatingService(new RatingRepository(), new PermissionService()).getAverageRating(s);
        reviewHeading.getChildren().clear();

        Label reviewTitle = new Label("Ratings");
        reviewTitle.getStyleClass().add("label3");
        reviewTitle.getStyleClass().add("error");

        Text avgRatingText = new Text(avgRating > 0 ? String.format("(%.1f)", avgRating) : "(No ratings yet)");
        avgRatingText.setStyle("-fx-font-size: 1.2em;");

        reviewHeading.getChildren().addAll(
                reviewTitle,
                Stars.StarRow(avgRating, 1.2, 5, rating -> {
                    RatingService ratingService = new RatingService(new RatingRepository(), new PermissionService());
                    ratingService.rateMaterial(rating, s, Session.getInstance().getCurrentUser());
                    updateRatingDisplay(s);
                }),
                avgRatingText
        );
        reviewHeading.setSpacing(10);
        reviewHeading.setAlignment(Pos.CENTER_LEFT);
    }


    private static void displayReviews(StudyMaterial material, VBox container) {
        ReviewService reviewService = new ReviewService(new ReviewRepository(), new PermissionService());
        List<Review> reviews = reviewService.getReviewsForMaterial(material);

        container.getChildren().clear();
        container.setSpacing(15);

        Label reviewSectionTitle = new Label("Reviews");
        reviewSectionTitle.getStyleClass().add("label3");

        TextArea newReviewInput = new TextArea();
        newReviewInput.setPromptText("Write your review...");
        newReviewInput.setPrefRowCount(3);
        newReviewInput.setWrapText(true);

        Button submitReview = new Button("Submit Review");
        submitReview.getStyleClass().add("btnPrimary");
        submitReview.setOnAction(e -> {
            String reviewText = newReviewInput.getText().trim();
            if (!reviewText.isEmpty()) {
                reviewService.addReview(
                        Session.getInstance().getCurrentUser(),
                        material,
                        reviewText
                );
                newReviewInput.clear();
                displayReviews(material, container);
            }
        });

        container.getChildren().addAll(reviewSectionTitle, newReviewInput, submitReview);

        for (Review review : reviews) {
            VBox reviewBox = new VBox(5);
            reviewBox.getStyleClass().add("reviewBox");

            Hyperlink reviewerName = new Hyperlink(review.getUser().getFullName());
            reviewerName.setOnAction(e -> {
                try {
                    SceneManager.getInstance().displayProfile(review.getUser().getUserId());
                } catch (IOException ex) {
                    SceneManager.getInstance().displayErrorPage("User not found.", SCREEN_HOME, "Go to Home");
                }
            });

            Text reviewText = new Text(review.getReviewText());
            reviewText.setWrappingWidth(500);

            reviewBox.getChildren().addAll(reviewerName, reviewText);
            container.getChildren().add(reviewBox);
        }
    }
}
