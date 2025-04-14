package presentation.controller;

import domain.model.*;
import domain.service.*;
import infrastructure.repository.RatingRepository;
import infrastructure.repository.ReviewRepository;
import infrastructure.repository.StudyMaterialRepository;
import infrastructure.repository.StudyMaterialTranslationRepository;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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
import presentation.components.LanguageButton;
import presentation.components.Stars;
import presentation.components.TagButton;
import presentation.components.TextLabels;
import presentation.utility.GUILogger;
import presentation.utility.SVGContents;
import presentation.utility.StyleClasses;
import presentation.view.CurrentUserManager;
import presentation.view.LanguageManager;
import presentation.view.SceneManager;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static domain.model.RoleType.ADMIN;


public class StudyMaterialPageController {
    private final HBox fileContainer;

    private final VBox reviewContainer;
    private final VBox reviewWritingContainer;

    private final VBox pendingStatusVBox;

    private final StudyMaterial material;
    private final StudyMaterialTranslationRepository translationRepository = new StudyMaterialTranslationRepository();

    private List<Review> reviewList;
    private List<Rating> ratingList;

    private int curRatingNum;
    private String curRatingText;
    private double avgRating;

    private final RatingService ratingServ = new RatingService(new RatingRepository(), new PermissionService());
    private final ReviewService reviewSer = new ReviewService(new ReviewRepository(), new PermissionService());
    private final StudyMaterialService materialServ = new StudyMaterialService(new GoogleDriveService(), new StudyMaterialRepository(), new PermissionService());

    private final BooleanProperty isTranslated;

    ResourceBundle rb = LanguageManager.getInstance().getBundle();

    public StudyMaterialPageController(StudyMaterial material) {
        this.material = material;
        this.fileContainer = new HBox();

        this.pendingStatusVBox = new VBox();

        this.reviewWritingContainer = new VBox();
        this.reviewContainer = new VBox();
        this.curRatingNum = 0;
        this.curRatingText = "";

        this.isTranslated = new SimpleBooleanProperty(false);

        refresh();
    }

    public boolean isTranslated() {
        return isTranslated.get();
    }

    public void setTranslated(boolean translated) {
        isTranslated.set(translated);
    }


    private double getAvgRating() {
        return this.avgRating;
    }

    private HBox makeReviewTitleHBox() {
        Text title = new Text(rb.getString("reviews"));
        title.getStyleClass().addAll(StyleClasses.HEADING3, StyleClasses.PRIMARY_LIGHT);

        HBox stars = Stars.getStarRow(getAvgRating(), 1, 4);
        Text starsText = new Text(String.format("(%.1f)", getAvgRating()));

        HBox hbox = new HBox();
        hbox.getChildren().addAll(title, stars, starsText);
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setSpacing(8);

        return hbox;
    }

    private List<Review> getReviewList() {
        return reviewList;
    }

    private List<Rating> getRatings() {
        return ratingList;
    }

    private void refresh() {
        ReviewRepository reviewRepo = new ReviewRepository();
        this.reviewList = reviewRepo.findByStudyMaterial(material);
        RatingRepository ratingRepo = new RatingRepository();
        this.ratingList = ratingRepo.findByMaterial(material);
        this.avgRating = ratingServ.getAverageRating(getMaterial());
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

    /* main method */

    public void displayPage() {
        VBox base = new VBox();
        base.getStylesheets().add(Objects.requireNonNull(CategoryPageController.class.getResource("/css/style.css")).toExternalForm());
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
                Text approvedText = new Text(String.format(rb.getString("approvedMaterial"), sm.getCategory().getCreator().getFullName()));
                approvedText.getStyleClass().add(StyleClasses.SECONDARY_LIGHT);
                base.getChildren().add(approvedText);
                break;
            case REJECTED:
                Text rejectedText = new Text(rb.getString("rejectedMaterial"));
                rejectedText.getStyleClass().add(StyleClasses.ERROR);
                base.getChildren().add(rejectedText);
                break;
            case PENDING:
                User u = CurrentUserManager.get();
                if (u.getUserId() != getMaterial().getCategory().getCreator().getUserId()) {
                    Text pendingText = new Text(rb.getString("pendingMaterial"));
                    pendingText.getStyleClass().add(StyleClasses.PRIMARY_LIGHT);
                    base.getChildren().add(pendingText);
                } else {
                    VBox decisionVBox = new VBox();

                    Text title = new Text(rb.getString("waitingForApproval"));
                    title.getStyleClass().addAll(StyleClasses.ERROR, StyleClasses.HEADING3);

                    Hyperlink courseHyperLink = new Hyperlink(sm.getCategory().getCategoryName());
                    courseHyperLink.setOnAction(e -> {
                        SceneManager sceneMan = SceneManager.getInstance();
                        sceneMan.displayCategory(sm.getCategory().getCategoryId());
                    });

                    TextFlow textFlow = new TextFlow(
                            new Text(rb.getString("waitingForApprovalUnder") + " "),
                            courseHyperLink);

                    HBox buttons = new HBox();

                    buttons.setSpacing(12);
                    buttons.setAlignment(Pos.CENTER_LEFT);

                    Button approvalButton = new Button();
                    approvalButton.setOnAction(e -> {
                        StudyMaterialService smServ = new StudyMaterialService(new GoogleDriveService(), new StudyMaterialRepository(), new PermissionService());
                        smServ.approveMaterial(CurrentUserManager.get(), sm);
                        setUpApprovalStatus();
                    });

                    approvalButton.getStyleClass().add(StyleClasses.APPROVE_REJECT_BUTTON);
                    SVGPath approveSvg = new SVGPath();
                    approveSvg.setContent(SVGContents.APPROVE);
                    approveSvg.getStyleClass().add(StyleClasses.SECONDARY_LIGHT);
                    approveSvg.setFillRule(FillRule.EVEN_ODD);

                    Label approveLabel = new Label(rb.getString("approveMaterial"));
                    approveLabel.getStyleClass().addAll(StyleClasses.LABEL5, StyleClasses.SECONDARY_LIGHT);

                    HBox approvalGraphic = new HBox(approveSvg, approveLabel);
                    approvalGraphic.setSpacing(12);
                    approvalGraphic.setAlignment(Pos.CENTER_LEFT);
                    approvalButton.setGraphic(approvalGraphic);

                    Button rejectButton = new Button();
                    rejectButton.setOnAction(e -> {
                        StudyMaterialService smServ = new StudyMaterialService(new GoogleDriveService(), new StudyMaterialRepository(), new PermissionService());
                        smServ.rejectMaterial(CurrentUserManager.get(), sm);
                        setUpApprovalStatus();
                    });

                    rejectButton.getStyleClass().add(StyleClasses.APPROVE_REJECT_BUTTON);
                    SVGPath rejectSvg = new SVGPath();
                    rejectSvg.setContent(SVGContents.REJECT);
                    rejectSvg.getStyleClass().addAll(StyleClasses.ERROR);
                    rejectSvg.setFillRule(FillRule.EVEN_ODD);

                    Label rejectLabel = new Label(rb.getString("rejectMaterial"));
                    rejectLabel.getStyleClass().addAll(StyleClasses.LABEL5, StyleClasses.ERROR);

                    HBox rejectGraphic = new HBox(rejectSvg, rejectLabel);
                    rejectGraphic.setSpacing(12);
                    rejectGraphic.setAlignment(Pos.CENTER_LEFT);
                    rejectButton.setGraphic(rejectGraphic);

                    buttons.getChildren().addAll(approvalButton, rejectButton);
                    decisionVBox.getChildren().addAll(title, textFlow, buttons);
                    decisionVBox.getStyleClass().add(StyleClasses.DECISION_VBOX);
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


        String currentLanguage = LanguageManager.getInstance().getCurrentLanguage();
        Map<String, String> nameTranslations = translationRepository.getNameTranslations(s.getMaterialId());
        Map<String, String> descTranslations = translationRepository.getDescriptionTranslations(s.getMaterialId());

        String displayName = nameTranslations.containsKey(currentLanguage) && isTranslated() ?
                nameTranslations.get(currentLanguage) : s.getName();
        String displayDescription = descTranslations.containsKey(currentLanguage) && isTranslated() ?
                descTranslations.get(currentLanguage) : s.getDescription();

        VBox base = new VBox();
        base.getStylesheets().add(Objects.requireNonNull(StudyMaterialPageController.class.getResource("/css/style.css")).toExternalForm());
        base.setSpacing(12);
        base.setPadding(new Insets(20, 20, 20, 20));
        VBox left = new VBox();

        /* FILE TITLE */
        TextFlow fileTitleContainer = new TextFlow();
        Label title = new Label(displayName + " ");
        title.getStyleClass().addAll(StyleClasses.LABEL3, StyleClasses.PRIMARY_LIGHT);
        title.setWrapText(true);
        title.setMaxWidth(600);

        if (isEditable) {
            Button editTitle = new Button();
            editTitle.getStyleClass().add(StyleClasses.BUTTON_EMPTY);
            SVGPath svgEdit = new SVGPath();
            svgEdit.setContent(SVGContents.EDIT);
            svgEdit.getStyleClass().add(StyleClasses.PRIMARY_LIGHT);
            SVGContents.setScale(svgEdit, 1.4);
            editTitle.setGraphic(svgEdit);

            fileTitleContainer.getChildren().addAll(title, editTitle);
            editTitle.setOnAction(e -> {
                fileTitleContainer.getChildren().clear();
                TextField titleArea = new TextField(s.getName());
                titleArea.setMinWidth(540);

                Button saveTitle = new Button("");
                saveTitle.getStyleClass().add(StyleClasses.BUTTON_EMPTY);
                SVGPath svgSave = new SVGPath();
                svgSave.setContent(SVGContents.SAVE);
                svgSave.getStyleClass().add(StyleClasses.PRIMARY_LIGHT);
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
        Text author = new Text(rb.getString("uploadedBy") + " ");
        author.setStyle("-fx-font-size: 1.2em;");

        Hyperlink authorLink = new Hyperlink(s.getUploader().getFullName());
        authorLink.setStyle("-fx-font-size: 1.2em; -fx-underline: false;");
        authorLink.setOnAction(e -> SceneManager.getInstance().displayProfile(s.getUploader().getUserId()));

        uploaderLabels.getChildren().addAll(author, authorLink, new Text("  "), TextLabels.getUserRoleLabel(s.getUploader()), new Text("  "));

        if (s.getUploader() == s.getCategory().getCreator()) {
            Label categoryOwnerLabel = new Label(rb.getString("categoryOwner"));
            categoryOwnerLabel.getStyleClass().add(StyleClasses.PRIMARY_TAG_LABEL);
            uploaderLabels.getChildren().add(categoryOwnerLabel);
        }

        Text fileDetails = new Text(Math.round(s.getFileSize()) + " KB " + s.getFileType());

        Button downloadBtn = getDownloadButton(s);

        /* FILE DESC CONTAINER */
        VBox fileDescContainer = new VBox();

        TextFlow fileDesc = new TextFlow();
        fileDesc.getChildren().clear();
        fileDesc.getChildren().add(new Text(displayDescription));
        fileDesc.setMaxWidth(580);

        if (isEditable) {
            Button editDesc = new Button(rb.getString("editDescription"));
            editDesc.getStyleClass().add(StyleClasses.BTN_XS_PRIMARY);
            fileDescContainer.getChildren().addAll(fileDesc, editDesc);
            editDesc.setOnAction(e -> {
                fileDescContainer.getChildren().clear();
                TextArea descArea = new TextArea(s.getDescription());

                descArea.setWrapText(true);
                Button saveDesc = new Button(rb.getString("saveDescription"));
                saveDesc.getStyleClass().add(StyleClasses.BTN_XS_PRIMARY);
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

        TextFlow course = new TextFlow(new Text(rb.getString("uploadedUnderCourse") + " "), courseLink, new Text(" " + String.format(rb.getString("time"), formattedTimestamp)));
        course.getStyleClass().add(StyleClasses.PRIMARY);

        TextFlow tagContainer = new TextFlow();
        Set<Tag> tags = s.getTags();
        tagContainer.setLineSpacing(10);
        tags.forEach(tag -> tagContainer.getChildren().addAll(TagButton.getBtn(tag), new Text("  ")));

        setUpApprovalStatus();
        left.getChildren().addAll(fileTitleContainer, uploaderLabels, tagContainer,
                fileDetails, downloadBtn,
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
            button.getStyleClass().add(StyleClasses.BUTTON_EMPTY);

            SVGPath svgPath = new SVGPath();
            SVGContents.setScale(svgPath,1.5);
            svgPath.setContent(SVGContents.DELETE);
            svgPath.getStyleClass().add(StyleClasses.ERROR);

            button.setGraphic(svgPath);
            button.setOnAction(e -> {
                if (StudyMaterialController.deleteMaterial(s)){
                    SceneManager.getInstance().displayCategory(s.getCategory().getCategoryId());
                }
            });

            HBox managementHBox = new HBox(button);
            managementHBox.setAlignment(Pos.CENTER_RIGHT);
            right.getChildren().add(managementHBox);
        }

        getFileContainer().setSpacing(20);
        getFileContainer().getChildren().addAll(left, right);
    }

    public class AppConfig {
        private AppConfig() {}
        public static final String IMAGE_TRANSLATE_ICON = "/images/google-translate-icon.png";
    }

    private Button getDownloadButton(StudyMaterial s){
        Button downloadBtn = new Button(rb.getString("download"));
        downloadBtn.getStyleClass().add(StyleClasses.BTN_DOWNLOAD);
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

        return downloadBtn;
    }

    private Button createTranslateButton(){
        Button translateButton = new Button(String.format(isTranslated() ? rb.getString("showOriginal") : rb.getString("translateTo")));

        translateButton.setOnAction(e -> {
            setTranslated(!isTranslated());
            translateButton.setText(String.format(isTranslated() ? rb.getString("showOriginal") : rb.getString("translateTo")));
        });

        String imagePath = AppConfig.IMAGE_TRANSLATE_ICON;
        ImageView imageView = new ImageView(new Image(Objects.requireNonNull(LanguageButton.class.getResourceAsStream(imagePath))));
        imageView.setFitWidth(20);
        imageView.setFitHeight(20);

        translateButton.setGraphic(imageView);
        translateButton.getStyleClass().addAll(StyleClasses.BUTTON_EMPTY, StyleClasses.PRIMARY_LIGHT);
        translateButton.setMaxHeight(18);

        return translateButton;
    }

    /* making reviewList and ratings */

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

        Text title = new Text(rb.getString("addYourReview"));
        title.getStyleClass().addAll(StyleClasses.HEADING3, StyleClasses.PRIMARY);

        HBox starContainer = new HBox();
        starContainer.setSpacing(4);
        Button sendReviewButton = new Button(rb.getString("sendReview"));

        for (int i = 0; i < 5; i++) {
            Button btn = getStarButton(i, starContainer, sendReviewButton);
            starContainer.getChildren().add(btn);
        }

        TextField comment = new TextField();
        comment.setPromptText(rb.getString("addTextToReview"));

        comment.textProperty().addListener((observable, oldValue, newValue) -> setCurRatingText(newValue));

        sendReviewButton.setDisable(true);
        sendReviewButton.getStyleClass().add(StyleClasses.BTN_S);
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
        btn.getStyleClass().add(StyleClasses.BUTTON_EMPTY);

        SVGPath graphic = new SVGPath();
        graphic.setContent(SVGContents.STAR);
        graphic.getStyleClass().add(StyleClasses.STAR_EMPTY);

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
                    starGraphic.getStyleClass().add(StyleClasses.STAR_FILLED);
                } else {
                    starGraphic.getStyleClass().clear();
                    starGraphic.getStyleClass().add(StyleClasses.STAR_EMPTY);
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
            getReviewContainer().getChildren().add(new Text(rb.getString("noRatings")));
            return;
        }

        getReviewContainer().getChildren().addAll(makeReviewTitleHBox(), createTranslateButton());


        List<Rating> leftOverRatings = new ArrayList<>(ratings);

        List<Review> reviews = getReviewList();
        Collections.reverse(reviews);
        GUILogger.info(reviews.size() + " reviewList found");

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
        String commentText = (review != null) ? reviewSer.getOriginalReviewText(review) : "";

        VBox base = new VBox();
        base.setSpacing(10);

        Hyperlink userLink = new Hyperlink(rating.getUser().getFullName());
        userLink.setOnAction(e -> {
            SceneManager sm = SceneManager.getInstance();
            sm.displayProfile(rating.getUser().getUserId());
        });
        userLink.getStyleClass().add(StyleClasses.REVIEW_USER_LINK);

        HBox reviewerHBox = new HBox(userLink, TextLabels.getUserRoleLabel(rating.getUser()));
        reviewerHBox.setSpacing(8);

        HBox stars = Stars.getStarRow(rating.getRatingScore(), 1, 3);

        Text comment = new Text(commentText);
        comment.setWrappingWidth(320);

        isTranslated.addListener((observable, oldValue, newValue) -> {
            String updatedText = Boolean.TRUE.equals(newValue) ? reviewSer.getTranslatedReviewText(review) : reviewSer.getOriginalReviewText(review);
            comment.setText(updatedText);
        });

        base.getChildren().addAll(reviewerHBox, stars, comment);
        base.getStyleClass().add(StyleClasses.REVIEW_CARD);
        return base;
    }
}