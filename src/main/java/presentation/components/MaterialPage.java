package presentation.components;

import domain.model.Rating;
import domain.model.StudyMaterial;
import domain.model.Tag;
import domain.service.*;
import infrastructure.repository.StudyMaterialRepository;
import infrastructure.repository.RatingRepository;
import infrastructure.repository.ReviewRepository;
import domain.model.Review;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import presentation.GUILogger;
import presentation.view.SceneManager;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class MaterialPage {
    private final HBox fileContainer;
    private final VBox reviewContainer;
    private final VBox reviewWritingContainer;
    private final HBox reviewTitleHBox;

    private final StudyMaterial material;

    private List<Review> reviews;
    private List<Rating> ratings;

    private int curRatingNum;
    private String curRatingText;
    private double avgRating;

    private final RatingService ratingServ = new RatingService(new RatingRepository(), new PermissionService());
    private final ReviewService reviewSer = new ReviewService(new ReviewRepository(), new PermissionService());

    public MaterialPage(StudyMaterial material) {
        this.material = material;
        this.fileContainer = new HBox();
        this.reviewWritingContainer = new VBox();
        this.reviewContainer = new VBox();
        this.reviewTitleHBox = getReviewTitleHBox();

        this.curRatingNum = 0;
        this.curRatingText = "";

        refresh();
    }

    private double getAvgRating() {
        return this.avgRating;
    }

    private HBox getReviewTitleHBox() {
        Text title = new Text("Reviews");
        title.getStyleClass().addAll("heading3", "primary-light");

        HBox stars = Stars.StarRow(getAvgRating(), 1, 4, null);
        Text starsText = new Text(String.format("(%.1f)", getAvgRating()));

        HBox hbox = new HBox();
        hbox.getChildren().addAll(title, stars, starsText);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setSpacing(8);

        return hbox;
    }

    private List<Review> getReviews() {
        return reviews;
    }

    private List<Rating> getRatings() {
        return ratings;
    }

    private void refresh() {
        ReviewRepository reviewRepo = new ReviewRepository();
        this.reviews = reviewRepo.findByStudyMaterial(material);
        RatingRepository ratingRepo = new RatingRepository();
        this.ratings = ratingRepo.findByMaterial(material);
        this.avgRating = ratingServ.getAverageRating(getMaterial());;
    }

    private StudyMaterial getMaterial() {
        return material;
    }

    private HBox getFileContainer() {
        return fileContainer;
    }

    private VBox getReviewWritingContainer() {
        return reviewWritingContainer;
    }

    private VBox getReviewContainer() {
        return reviewContainer;
    }

    /* main method */

    public void displayPage(){
        VBox base = new VBox();
        base.getStylesheets().add(Objects.requireNonNull(CategoryPage.class.getResource("/css/style.css")).toExternalForm());
        base.setSpacing(12);
        base.setPadding(new Insets(20, 20, 20, 20));

        setUpFileContainer();
        base.getChildren().add(getFileContainer());


        if (Session.getInstance().getCurrentUser().getUserId() != getMaterial().getUploader().getUserId() &&
            !ratingServ.hasUserRatedMaterial(Session.getInstance().getCurrentUser(), getMaterial())) {
            setUpReviewWriting();
            base.getChildren().add(getReviewWritingContainer());
        }

        setUpReviewDisplay();
        base.getChildren().add(getReviewContainer());

        ScrollPane wrapper = new ScrollPane(base);
        SceneManager.getInstance().setCenter(wrapper);
    }

    /* file display */

    private void setUpFileContainer() {
        fileContainer.getChildren().clear();
        StudyMaterial s = getMaterial();

        VBox base = new VBox();
        base.getStylesheets().add(Objects.requireNonNull(MaterialPage.class.getResource("/css/style.css")).toExternalForm());
        base.setSpacing(12);
        base.setPadding(new Insets(20, 20, 20, 20));

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
            SceneManager.getInstance().displayProfile(s.getUploader().getUserId());
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

        getFileContainer().setSpacing(20);
        getFileContainer().getChildren().addAll(left, right);
    }

    /* making reviews and ratings */

    public int getCurRatingNum() {
        return curRatingNum;
    }

    public void setCurRatingNum(int curRatingNum) {
        this.curRatingNum = curRatingNum;
        GUILogger.info("Current users rating " + curRatingNum + " as var, but in object:" + getCurRatingNum());
    }

    public String getCurRatingText() {
        return curRatingText;
    }

    public void setCurRatingText(String curRatingText) {
        this.curRatingText = curRatingText;
    }

    private void setUpReviewWriting() {
        getReviewWritingContainer().getChildren().clear();
        VBox base = new VBox();

        Text title = new Text("Add Your Review");
        title.getStyleClass().addAll("heading3", "primary");

        HBox starContainer = new HBox();
        starContainer.setSpacing(4);
        Button sendReviewButton = new Button("Send Review");

        for (int i = 0; i < 5; i++){
            Button btn = getStarButton(i, starContainer, sendReviewButton);
            starContainer.getChildren().add(btn);
        }

        TextField comment = new TextField();
        comment.setPromptText("Add text to your review");

        comment.textProperty().addListener((observable, oldValue, newValue) -> {
            setCurRatingText(newValue);
        });

        sendReviewButton.setDisable(true);
        sendReviewButton.getStyleClass().add("btnS");
        sendReviewButton.setOnAction(e -> {
            if (!Objects.equals(getCurRatingText(), "") && (getCurRatingText() != null)){
                String reviewText = getCurRatingText().trim();
                if (!reviewText.isEmpty()) {
                    reviewSer.addReview(
                         Session.getInstance().getCurrentUser(),
                         material,
                         reviewText
                    );
                }
            }

            int rating = getCurRatingNum();
            if (rating > 0){
                ratingServ.rateMaterial(rating, getMaterial(), Session.getInstance().getCurrentUser());
            }

            getReviewWritingContainer().getChildren().clear();
            refresh();
            refresh();
            GUILogger.info("Setting up review display again...");
            setUpReviewDisplay();
        });

        HBox header = new HBox(title, starContainer);
        header.setAlignment(Pos.BASELINE_LEFT);
        header.setSpacing(10);

        base.getChildren().addAll(
                header,
                comment,
                sendReviewButton
        );

        base.setSpacing(10);
        getReviewWritingContainer().getChildren().add(base);
    }

    private Button getStarButton(int i, HBox starContainer, Button sendReviewButton) {
        Button btn = new Button();
        btn.getStyleClass().add("buttonEmpty");

        SVGPath graphic = new SVGPath();
        graphic.setContent(SVGContents.star());
        graphic.getStyleClass().add("star-empty");

        btn.setStyle("-fx-padding: 0; -fx-border: none;");

        graphic.setScaleX(1.2);
        graphic.setScaleY(1.2);
        btn.setGraphic(graphic);

        btn.setOnAction(e -> {
            setCurRatingNum(i + 1);
            for (int j = 0; j < starContainer.getChildren().size(); j++) {
                Node star = starContainer.getChildren().get(j);
                SVGPath starGraphic = (SVGPath) ((Button) star).getGraphic();
                if (j < getCurRatingNum()) {
                    starGraphic.getStyleClass().clear();
                    starGraphic.getStyleClass().add("star-filled");
                } else {
                    starGraphic.getStyleClass().clear();
                    starGraphic.getStyleClass().add("star-empty");
                }
            }

            sendReviewButton.setDisable(false);
        });

        return btn;
    }

    private void setUpReviewDisplay() {
        getReviewContainer().getChildren().clear();

        List<Rating> ratings = getRatings();
        Collections.reverse(ratings);
        GUILogger.info(ratings.size() + " ratings found");

        if (ratings.isEmpty()){
            getReviewContainer().getChildren().add(new Text("No ratings left yet!"));
            return;
        }

        getReviewContainer().getChildren().add(getReviewTitleHBox());

        List<Rating> leftOverRatings = new ArrayList<>(ratings);

        List<Review> reviews = getReviews();
        Collections.reverse(reviews);
        GUILogger.info(reviews.size() + " reviews found");

        if (!reviews.isEmpty()){
            FlowPane fp = new FlowPane();
            reviews.forEach(r -> {
                Rating correspondingRating = ratings.stream()
                        .filter(rating -> rating.getUser().getUserId() == r.getUser().getUserId())
                        .findFirst()
                        .orElse(null);

                leftOverRatings.remove(correspondingRating);

                if (correspondingRating != null) {
                    fp.getChildren().add(reviewCard(correspondingRating, r));
                }
            });

            fp.setMaxWidth(700);
            getReviewContainer().getChildren().add(fp);
        }

        VBox leftOverVBox = new VBox();
        leftOverVBox.setSpacing(8);
        leftOverRatings.forEach(r -> leftOverVBox.getChildren().add(ratingOnlyCard(r)));

        getReviewContainer().setSpacing(8);
        getReviewContainer().getChildren().add(leftOverVBox);
    }

    private Node reviewCard(Rating rating, Review review) {
        VBox base = new VBox();
        base.setSpacing(10);

        Hyperlink userLink = new Hyperlink(review.getUser().getFullName());
        userLink.setOnAction(e -> {
            SceneManager sm = SceneManager.getInstance();
            sm.displayProfile(review.getUser().getUserId());
        });
        userLink.getStyleClass().add("reviewUserLink");

        HBox reviewerHBox = new HBox(userLink, TextLabels.getUserRoleLabel(review.getUser()));
        reviewerHBox.setSpacing(8);

        HBox stars = Stars.StarRow(rating.getRatingScore(), 1, 3, null);

        Text comment = new Text(review.getReviewText());
        comment.setWrappingWidth(320);

        base.getChildren().addAll(reviewerHBox, stars, comment);
        base.getStyleClass().add("reviewCard");
        return base;
    }

    private HBox ratingOnlyCard(Rating r) {
        HBox stars = Stars.StarRow(r.getRatingScore(), 1, 3, null);
        Hyperlink userLink = new Hyperlink(r.getUser().getFullName());
        userLink.setOnAction(e -> {
            SceneManager sm = SceneManager.getInstance();
            sm.displayProfile(r.getUser().getUserId());
        });
        userLink.getStyleClass().add("reviewUserLink");

        HBox userLabel = new HBox(userLink, TextLabels.getUserRoleLabel(r.getUser()));
        userLabel.setSpacing(8);

        HBox base = new HBox(stars, userLabel);
        base.setSpacing(12);
        base.setAlignment(Pos.CENTER_LEFT);
        return base;
    }
}