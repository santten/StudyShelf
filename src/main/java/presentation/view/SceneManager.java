package presentation.view;
import domain.model.StudyMaterial;
import domain.service.RatingService;
import domain.service.Session;
import infrastructure.repository.RatingRepository;
import infrastructure.repository.StudyMaterialRepository;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.TextFlow;
import presentation.components.MaterialCard;
import presentation.components.Stars;
import presentation.components.TextLabels;

import domain.model.Category;
import infrastructure.repository.CategoryRepository;

import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

import static domain.model.RoleType.STUDENT;
import static presentation.view.Screen.*;

import presentation.logger.GUILogger;

public class SceneManager {
    private static SceneManager instance;
    private BorderPane current;
    private GridPane header;
    private HBox footer;
    private boolean logged;
    private ScrollPane scrollpane;
    private Stage primaryStage;

    private SceneManager(){
    }

    public static SceneManager getInstance() {
        if (instance == null){
            instance = new SceneManager();

            try {
                instance.initializeComponents();
            } catch (IOException e) {
                throw new RuntimeException("Failed to load FXML components", e);
            }
        }
        return instance;
    }

    private void initializeComponents() throws IOException {
        instance.current = FXMLLoader.load(Objects.requireNonNull(SceneManager.class.getResource("/fxml/login.fxml")));
        instance.header = FXMLLoader.load(Objects.requireNonNull(SceneManager.class.getResource("/fxml/header.fxml")));
        instance.footer = FXMLLoader.load(Objects.requireNonNull(SceneManager.class.getResource("/fxml/footer.fxml")));
        instance.logged = false;
    }

    public void displayCategory(int id) throws IOException {
        if (!instance.logged){
            setScreen(SCREEN_LOGIN);
        } else {
            CategoryRepository repo = new CategoryRepository();
            Category c = repo.findById(id);
            if (c == null) {
                GUILogger.warn("DNE: Tried to go to category with id " + id);
                displayErrorPage("This category does not exist.", SCREEN_HOME, "Go to home page");
            } else {
                VBox vbox = new VBox();

                vbox.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());
                vbox.setSpacing(12);
                vbox.setPadding(new Insets(20, 20, 20, 20));
                Text title = new Text(c.getCategoryName());
                title.getStyleClass().add("heading3");
                title.getStyleClass().add("secondary");

                Text author = new Text("Course by " + c.getCreator().getFullName());
                VBox header = new VBox();
                header.getChildren().addAll(title, author);

                vbox.getChildren().add(header);

                List<StudyMaterial> creatorMaterials = repo.findMaterialsByUserInCategory(c.getCreator(), c);
                if (!creatorMaterials.isEmpty()) {
                    Text text = new Text("Materials from " + c.getCreator().getFullName());
                    text.getStyleClass().add("heading4");
                    text.getStyleClass().add("secondary");

                    vbox.getChildren().addAll(
                            text,
                            MaterialCard.materialCardScrollHBox(creatorMaterials));
                }

                List<StudyMaterial> otherMaterials = repo.findMaterialsExceptUserInCategory(c.getCreator(), c);
                if (!otherMaterials.isEmpty()) {
                    GUILogger.info(String.valueOf(otherMaterials.size()));
                    Text text = new Text("Materials from others");
                    text.getStyleClass().add("heading4");
                    text.getStyleClass().add("secondary");

                    vbox.getChildren().addAll(
                            text,
                            MaterialCard.materialCardScrollHBox(otherMaterials));
                }

                instance.current.setCenter(vbox);
            }
        }
    }

    public void displayMaterialPage(int id) throws IOException {
        if (!instance.logged){
            setScreen(SCREEN_LOGIN);
            return;
        }

        StudyMaterialRepository repo = new StudyMaterialRepository();
        StudyMaterial s = repo.findById(id);

        if (s == null) {
            GUILogger.warn("DNE: Tried to go to material with id " + id);
            displayErrorPage("This material doesn't exist.", SCREEN_COURSES, "Go to courses page");
            return;
        }

        /* header: preview and file details*/
        VBox base = new VBox();
        base.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());
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
        Text author = new Text("Uploaded by " + s.getUploader().getFullName());
        author.setStyle("-fx-font-size: 1.2em;");

        uploaderLabels.getChildren().addAll(author, new Text("  "), TextLabels.getUserRoleLabel(s.getUploader()), new Text("  "));

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

        left.getChildren().addAll(title, uploaderLabels, fileDetails, downloadBtn,
                                    fileDesc);
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
        instance.current.setCenter(wrapper);
    }

    public void displayErrorPage(String errorText, Screen redirectScreen, String redirectLabel) {
        VBox vbox = new VBox();
        vbox.setSpacing(12);
        vbox.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/css/style.css")).toExternalForm());

        Text title = new Text(":(");
        title.getStyleClass().add("heading3");

        Text label = new Text(errorText);

        Hyperlink link = new Hyperlink(redirectLabel);
        link.setOnAction(event -> {
            try {
                setScreen(redirectScreen);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        vbox.setPadding(new Insets(20, 20, 20, 20));

        vbox.getChildren().addAll(title, label, link);
        instance.current.setCenter(vbox);
    }

    public void setScreen(Screen screen) throws IOException {
        if (!instance.logged){
            instance.current = FXMLLoader.load(Objects.requireNonNull(SceneManager.class.getResource(screen == SCREEN_SIGNUP ? "/fxml/signup.fxml" : "/fxml/login.fxml")));
        } else {
            BorderPane bp = new BorderPane();
            bp.setTop(instance.header);
            bp.setBottom(instance.footer);
            instance.current = bp;

            ScrollPane base = new ScrollPane();
            base.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
            base.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

            String resourcePath = switch (screen) {
                case SCREEN_COURSES -> "/fxml/courses.fxml";
                case SCREEN_PROFILE -> "/fxml/profile.fxml";
                case SCREEN_FIND -> "/fxml/search.fxml";
                case SCREEN_UPLOAD -> "/fxml/upload.fxml";
                default -> "/fxml/home.fxml";
            };

            base.setContent(FXMLLoader.load(Objects.requireNonNull(SceneManager.class.getResource(resourcePath))));
            instance.current.setCenter(base);
        }

        GUILogger.info("Displaying " + screen);

        primaryStage.setTitle("StudyShelf");
        primaryStage.setScene(new Scene(instance.current, 800, 600));
        primaryStage.show();
    }

    public void setPrimaryStage(Stage primaryStage){
        primaryStage.setResizable(false);
        instance.primaryStage = primaryStage;
    }

    public void login() throws IOException {
        if (instance.logged) {
            GUILogger.warn("User is already logged in");
        } else {
            instance.logged = true;
            instance.setScreen(SCREEN_HOME);
        }
    }

    public void logout() throws IOException {
        if (instance.logged){
            instance.logged = false;
            instance.setScreen(SCREEN_LOGIN);
        } else {
            GUILogger.warn("User is already logged out");
        }
    }
}