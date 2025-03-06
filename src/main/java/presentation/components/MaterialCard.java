package presentation.components;

import domain.model.StudyMaterial;
import domain.model.User;
import domain.service.PermissionService;
import domain.service.RatingService;
import infrastructure.repository.RatingRepository;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import presentation.GUILogger;
import presentation.view.SceneManager;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static javafx.scene.shape.FillRule.EVEN_ODD;
import static presentation.view.Screen.SCREEN_HOME;

public class MaterialCard {
    public static Button materialCard(StudyMaterial s) {
        Button materialCard = new Button();
        HBox wrapper = new HBox();
        wrapper.setSpacing(12);
        VBox contentBox = new VBox();
        User uploader = s.getUploader();
        wrapper.setPadding(new Insets(8));

        if (s.getPreviewImage() != null) {
            ImageView preview = new ImageView(new Image(new ByteArrayInputStream(s.getPreviewImage())));
            preview.setFitWidth(60);
            preview.setFitHeight(80);
            preview.setPreserveRatio(false);
            wrapper.getChildren().add(preview);
        }

        TextFlow titleArea = new TextFlow();

        SVGPath svgPath = new SVGPath();

        // there will be more options to reflect different file types in the future
        switch (s.getFileType()) {
            default:
                svgPath.setContent(SVGContents.file());
        }


        svgPath.getStyleClass().add("iconPrimaryM");
        svgPath.setFillRule(EVEN_ODD);
        titleArea.getChildren().add(svgPath);

        Label title = new Label();
        title.setText("   " + s.getName());
        title.setWrapText(true);
        title.setTextOverrun(OverrunStyle.ELLIPSIS);

        title.setMaxWidth(110);
        title.setMaxHeight(20);

        title.getStyleClass().add("label4");
        title.getStyleClass().add("primary-light");
        titleArea.getChildren().add(title);
        contentBox.getChildren().add(titleArea);

        Text uploaderLabel = new Text(uploader.getFirstName() + " " + uploader.getLastName());
        contentBox.getChildren().add(uploaderLabel);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
        String formattedTimestamp = s.getTimestamp().format(formatter);

        Text timestamp = new Text(formattedTimestamp);
        timestamp.getStyleClass().add("materialCardSubtitle");
        contentBox.getChildren().add(timestamp);

        HBox ratingBox = new HBox();
        ratingBox.setPrefWidth(100);
        ratingBox.setMaxWidth(100);
        ratingBox.setMinWidth(100);
        RatingService ratingService = new RatingService(new RatingRepository(), new PermissionService()
        );
        double avgRating = ratingService.getAverageRating(s);

        if (avgRating > 0.0) {
            HBox starContainer = Stars.StarRow(avgRating, 0.7, 5, null);
            ratingBox.getChildren().add(starContainer);
        }

        contentBox.getChildren().add(ratingBox);
        wrapper.getChildren().add(contentBox);

        materialCard.setTooltip(new Tooltip("\"" + s.getName() + "\", uploaded by " + uploader.getFirstName() + " " + uploader.getLastName()));
        materialCard.setGraphic(wrapper);
        materialCard.getStyleClass().add("materialCardM");
        materialCard.setOnAction(e -> {
            GUILogger.info("MaterialCard " + s.getName() + " clicked.");
            SceneManager.getInstance().displayMaterial(s.getMaterialId());
        });
        return materialCard;
    }

    public static ScrollPane materialCardScrollHBox(List<StudyMaterial> list) {
        HBox materialCardHBox = new HBox();
        for (int i = list.size() - 1; i >= 0; i--) {
            materialCardHBox.getChildren().add(materialCard(list.get(i)));
        }

        materialCardHBox.setSpacing(10);

        ScrollPane pane = new ScrollPane();

        pane.setContent(materialCardHBox);
        pane.setMinViewportWidth(720);
        pane.setPrefViewportWidth(720);
        pane.setMinViewportHeight(120);
        pane.setPrefViewportHeight(120);
        pane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        pane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        return pane;
    }
}