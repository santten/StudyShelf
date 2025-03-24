package presentation.controller;

import domain.model.*;
import domain.service.*;
import infrastructure.repository.RatingRepository;
import infrastructure.repository.ReviewRepository;
import infrastructure.repository.StudyMaterialRepository;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.stage.FileChooser;
import presentation.components.CategoryPage;
import presentation.components.Stars;
import presentation.components.TagButton;
import presentation.components.TextLabels;
import presentation.view.CurrentUserManager;
import presentation.utility.GUILogger;
import presentation.utility.SVGContents;
import presentation.view.SceneManager;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static domain.model.MaterialStatus.APPROVED;
import static domain.model.MaterialStatus.REJECTED;
import static domain.model.RoleType.ADMIN;


public class StudyMaterialPageController {
    private final HBox fileContainer;

    private final VBox reviewContainer;
    private final VBox reviewWritingContainer;

    private final VBox pendingStatusVBox;
    private MaterialStatus pendingStatus;

    private final StudyMaterial material;

    private List<Review> reviews;
    private List<Rating> ratings;

    private int curRatingNum;
    private String curRatingText;
    private double avgRating;

    private final RatingService ratingServ = new RatingService(new RatingRepository(), new PermissionService());
    private final ReviewService reviewSer = new ReviewService(new ReviewRepository(), new PermissionService());
    private final StudyMaterialService materialServ = new StudyMaterialService(new GoogleDriveService(), new StudyMaterialRepository(), new PermissionService());

    public StudyMaterialPageController(StudyMaterial material) {
        this.material = material;
        this.fileContainer = new HBox();

        this.pendingStatusVBox = new VBox();

        this.reviewWritingContainer = new VBox();
        this.reviewContainer = new VBox();
        this.curRatingNum = 0;
        this.curRatingText = "";

        refresh();
    }

    private double getAvgRating() {
        return this.avgRating;
    }

    private HBox makeReviewTitleHBox() {
        Text title = new Text("Reviews");
        title.getStyleClass().addAll("heading3", "primary-light");

        HBox stars = Stars.StarRow(getAvgRating(), 1, 4);
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
        this.avgRating = ratingServ.getAverageRating(getMaterial());
        ;
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

    private VBox getPendingStatusVBox() {
        return pendingStatusVBox;
    }

    private void setPendingStatus(MaterialStatus status) {
        this.pendingStatus = status;
    }

    /* main method */

    public void displayPage() {
        VBox base = new VBox();
        base.getStylesheets().add(Objects.requireNonNull(CategoryPage.class.getResource("/css/style.css")).toExternalForm());
        base.setSpacing(12);
        base.setPadding(new Insets(20, 20, 20, 20));

        boolean isOwned = CurrentUserManager.get().getUserId() == getMaterial().getUploader().getUserId();
        boolean isAdmin = (CurrentUserManager.get().getRole().getName() == ADMIN);

        setUpFileContainer(isOwned || isAdmin);
        base.getChildren().add(getFileContainer());

        if (!isOwned && !ratingServ.hasUserRatedMaterial(CurrentUserManager.get(), getMaterial())) {
            setUpReviewWriting();
            base.getChildren().add(getReviewWritingContainer());
        }

        setUpReviewDisplay();
        base.getChildren().add(getReviewContainer());

        ScrollPane wrapper = new ScrollPane(base);
        SceneManager.getInstance().setCenter(wrapper);
    }

    private void setUpApprovalStatus() {
        VBox base = getPendingStatusVBox();
        base.getChildren().clear();

        StudyMaterial sm = getMaterial();

        switch (sm.getStatus()) {
            case APPROVED:
                Text approvedText = new Text("This material has been approved by the course's owner " + sm.getCategory().getCreator().getFullName());
                approvedText.getStyleClass().add("secondary-light");
                base.getChildren().add(approvedText);
                break;
            case REJECTED:
                Text rejectedText = new Text("Please note: This material has been rejected for the course it was submitted to.");
                rejectedText.getStyleClass().add("error");
                base.getChildren().add(rejectedText);
                break;
            case PENDING:
                User u = CurrentUserManager.get();
                if (u.getUserId() != getMaterial().getCategory().getCreator().getUserId()) {
                    Text pendingText = new Text("Please note: This material is still pending approval from the course owner.");
                    pendingText.getStyleClass().add("primary-light");
                    base.getChildren().add(pendingText);
                } else {
                    VBox decisionVBox = new VBox();

                    Text title = new Text("Waiting for approval!");
                    title.getStyleClass().addAll("error", "heading3");

                    Hyperlink courseHyperLink = new Hyperlink(sm.getCategory().getCategoryName());
                    courseHyperLink.setOnAction(e -> {
                        SceneManager sceneMan = SceneManager.getInstance();
                        sceneMan.displayCategory(sm.getCategory().getCategoryId());
                    });

                    TextFlow textFlow = new TextFlow(
                            new Text("This material is waiting for your approval under your course "),
                            courseHyperLink);

                    HBox buttons = new HBox();

                    buttons.setSpacing(12);
                    buttons.setAlignment(Pos.CENTER_LEFT);

                    Button approvalButton = new Button();
                    approvalButton.setOnAction(e -> {
                        StudyMaterialService smServ = new StudyMaterialService(new GoogleDriveService(), new StudyMaterialRepository(), new PermissionService());
                        smServ.approveMaterial(CurrentUserManager.get(), sm);
                        setPendingStatus(APPROVED);
                        setUpApprovalStatus();
                    });

                    approvalButton.getStyleClass().add("approveRejectButton");
                    SVGPath approveSvg = new SVGPath();
                    approveSvg.setContent(SVGContents.approve());
                    approveSvg.getStyleClass().addAll("btnHover", "secondary-light");
                    approveSvg.setFillRule(FillRule.EVEN_ODD);

                    Label approveLabel = new Label("Approve Material");
                    approveLabel.getStyleClass().addAll("label5", "secondary-light");

                    HBox approvalGraphic = new HBox(approveSvg, approveLabel);
                    approvalGraphic.setSpacing(12);
                    approvalGraphic.setAlignment(Pos.CENTER_LEFT);
                    approvalButton.setGraphic(approvalGraphic);

                    Button rejectButton = new Button();
                    rejectButton.setOnAction(e -> {
                        StudyMaterialService smServ = new StudyMaterialService(new GoogleDriveService(), new StudyMaterialRepository(), new PermissionService());
                        smServ.rejectMaterial(CurrentUserManager.get(), sm);
                        setPendingStatus(REJECTED);
                        setUpApprovalStatus();
                    });

                    rejectButton.getStyleClass().add("approveRejectButton");
                    SVGPath rejectSvg = new SVGPath();
                    rejectSvg.setContent(SVGContents.reject());
                    rejectSvg.getStyleClass().addAll("btnHover", "error");
                    rejectSvg.setFillRule(FillRule.EVEN_ODD);

                    Label rejectLabel = new Label("Reject Material");
                    rejectLabel.getStyleClass().addAll("label5", "error");

                    HBox rejectGraphic = new HBox(rejectSvg, rejectLabel);
                    rejectGraphic.setSpacing(12);
                    rejectGraphic.setAlignment(Pos.CENTER_LEFT);
                    rejectButton.setGraphic(rejectGraphic);

                    buttons.getChildren().addAll(approvalButton, rejectButton);
                    decisionVBox.getChildren().addAll(title, textFlow, buttons);
                    decisionVBox.getStyleClass().add("decisionVBox");
                    decisionVBox.setSpacing(10);
                    base.getChildren().addAll(decisionVBox);
                }
                break;
        }
    }

    /* file display */

    private void setUpFileContainer(boolean isEditable) {
        fileContainer.getChildren().clear();
        StudyMaterial s = getMaterial();

        VBox base = new VBox();
        base.getStylesheets().add(Objects.requireNonNull(StudyMaterialPageController.class.getResource("/css/style.css")).toExternalForm());
        base.setSpacing(12);
        base.setPadding(new Insets(20, 20, 20, 20));
        VBox left = new VBox();

        /* FILE TITLE */
        TextFlow fileTitleContainer = new TextFlow();
        Label title = new Label(s.getName() + " ");
        title.getStyleClass().add("label3");
        title.getStyleClass().add("primary-light");
        title.setWrapText(true);
        title.setMaxWidth(600);

        if (isEditable) {
            Button editTitle = new Button();
            editTitle.getStyleClass().add("buttonEmpty");
            SVGPath svgEdit = new SVGPath();
            svgEdit.setContent(SVGContents.edit());
            svgEdit.getStyleClass().add("primary-light");
            SVGContents.setScale(svgEdit, 1.4);
            editTitle.setGraphic(svgEdit);

            fileTitleContainer.getChildren().addAll(title, editTitle);
            editTitle.setOnAction(e -> {
                fileTitleContainer.getChildren().clear();
                TextField titleArea = new TextField(s.getName());
                titleArea.setMinWidth(540);

                Button saveTitle = new Button("");
                saveTitle.getStyleClass().add("buttonEmpty");
                SVGPath svgSave = new SVGPath();
                svgSave.setContent(SVGContents.save());
                svgSave.getStyleClass().add("primary-light");
                SVGContents.setScale(svgSave, 1.4);
                saveTitle.setGraphic(svgSave);

                saveTitle.setOnAction(ev -> {
                    materialServ.updateTitle(CurrentUserManager.get(), s, titleArea.getText());

                    title.setText(titleArea.getText());

                    fileTitleContainer.getChildren().clear();
                    fileTitleContainer.getChildren().addAll(title, editTitle);
                });
                fileTitleContainer.getChildren().clear();
                fileTitleContainer.getChildren().addAll(titleArea, saveTitle);
            });
        } else {
            fileTitleContainer.getChildren().add(title);
        }

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
                    materialService.downloadMaterial(CurrentUserManager.get(), s, saveLocation);
                    GUILogger.info("Successfully downloaded " + s.getName());
                }
            } catch (IOException e) {
                GUILogger.warn("Failed to download file: " + e.getMessage());
            }
        });

        /* FILE DESC CONTAINER */
        VBox fileDescContainer = new VBox();

        TextFlow fileDesc = new TextFlow();
        fileDesc.getChildren().clear();
        fileDesc.getChildren().add(new Text(s.getDescription()));
        fileDesc.setMaxWidth(580);

        if (isEditable) {
            Button editDesc = new Button("Edit Description");
            editDesc.getStyleClass().add("btnXSPrimary");
            fileDescContainer.getChildren().addAll(fileDesc, editDesc);
            editDesc.setOnAction(e -> {
                fileDescContainer.getChildren().clear();
                TextArea descArea = new TextArea(s.getDescription());

                descArea.setWrapText(true);
                Button saveDesc = new Button("Save Description");
                saveDesc.getStyleClass().add("btnXSPrimary");
                saveDesc.setOnAction(ev -> {
                    materialServ.updateDescription(CurrentUserManager.get(), s, descArea.getText());
                    fileDescContainer.getChildren().clear();

                    fileDesc.getChildren().clear();
                    fileDesc.getChildren().add(new Text(descArea.getText()));

                    fileDescContainer.getChildren().addAll(fileDesc, editDesc);
                });
                fileDescContainer.getChildren().addAll(descArea, saveDesc);
            });
            fileDescContainer.setSpacing(12);
        } else {
            fileDescContainer.getChildren().add(fileDesc);
        }


        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
        String formattedTimestamp = s.getTimestamp().format(formatter);

        Hyperlink courseLink = new Hyperlink(s.getCategory().getCategoryName());
        courseLink.setOnAction(e -> {
            SceneManager sm = SceneManager.getInstance();
            sm.displayCategory(s.getCategory().getCategoryId());
        });

        TextFlow course = new TextFlow(new Text("Uploaded under course "), courseLink, new Text(" on " + formattedTimestamp));
        course.getStyleClass().add("primary");

        TextFlow tagContainer = new TextFlow();
        Set<Tag> tags = s.getTags();
        tagContainer.setLineSpacing(10);
        tags.forEach(tag -> tagContainer.getChildren().addAll(TagButton.getBtn(tag), new Text("  ")));

        setUpApprovalStatus();
        left.getChildren().addAll(fileTitleContainer, uploaderLabels, tagContainer, fileDetails, downloadBtn,
                course, fileDescContainer, getPendingStatusVBox());
        left.setMinWidth(580);
        left.setMaxWidth(580);
        left.setSpacing(8);

        VBox right = new VBox();
        ImageView preview = new ImageView(new Image(new ByteArrayInputStream(s.getPreviewImage())));
        preview.setFitWidth(141);
        preview.setFitHeight(188);
        preview.setPreserveRatio(false);

        right.getChildren().add(preview);
        right.setSpacing(8);

        if (isEditable) {
            Button button = new Button();
            button.getStyleClass().add("buttonEmpty");

            SVGPath svgPath = new SVGPath();
            SVGContents.setScale(svgPath,1.5);
            svgPath.setContent(SVGContents.delete());
            svgPath.getStyleClass().add("error");

            button.setGraphic(svgPath);
            button.setOnAction(e -> {
                StudyMaterialController.deleteMaterial(s);
                SceneManager.getInstance().displayCategory(s.getCategory().getCategoryId());
            });

            HBox managementHBox = new HBox(button);
            managementHBox.setAlignment(Pos.CENTER_RIGHT);
            right.getChildren().add(managementHBox);
        }

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

        for (int i = 0; i < 5; i++) {
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
            if (!Objects.equals(getCurRatingText(), "") && (getCurRatingText() != null)) {
                String reviewText = getCurRatingText().trim();
                if (!reviewText.isEmpty()) {
                    reviewSer.addReview(
                            CurrentUserManager.get(),
                            material,
                            reviewText
                    );
                }
            }

            int rating = getCurRatingNum();
            if (rating > 0) {
                ratingServ.rateMaterial(rating, getMaterial(), CurrentUserManager.get());
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

        if (ratings.isEmpty()) {
            getReviewContainer().getChildren().add(new Text("No ratings left yet!"));
            return;
        }

        getReviewContainer().getChildren().add(makeReviewTitleHBox());

        List<Rating> leftOverRatings = new ArrayList<>(ratings);

        List<Review> reviews = getReviews();
        Collections.reverse(reviews);
        GUILogger.info(reviews.size() + " reviews found");

        FlowPane fp = new FlowPane();
        fp.setMaxWidth(700);

        if (!reviews.isEmpty()) {
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
        }

        VBox leftOverVBox = new VBox();
        leftOverVBox.setSpacing(8);
        leftOverRatings.forEach(r -> fp.getChildren().add(reviewCard(r)));

        getReviewContainer().getChildren().add(fp);
        getReviewContainer().setSpacing(8);
    }

    private Node reviewCard(Rating rating) {
        return reviewCard(rating, null);
    }

    private Node reviewCard(Rating rating, Review review) {
        String commentText = (review != null) ? review.getReviewText() : "";

        VBox base = new VBox();
        base.setSpacing(10);

        Hyperlink userLink = new Hyperlink(rating.getUser().getFullName());
        userLink.setOnAction(e -> {
            SceneManager sm = SceneManager.getInstance();
            sm.displayProfile(rating.getUser().getUserId());
        });
        userLink.getStyleClass().add("reviewUserLink");

        HBox reviewerHBox = new HBox(userLink, TextLabels.getUserRoleLabel(rating.getUser()));
        reviewerHBox.setSpacing(8);

        HBox stars = Stars.StarRow(rating.getRatingScore(), 1, 3);

        Text comment = new Text(commentText);
        comment.setWrappingWidth(320);

        base.getChildren().addAll(reviewerHBox, stars, comment);
        base.getStyleClass().add("reviewCard");
        return base;
    }
}