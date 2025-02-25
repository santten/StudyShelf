package presentation.components;

import domain.model.StudyMaterial;
import domain.model.User;
import domain.service.RatingService;
import infrastructure.repository.RatingRepository;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import presentation.logger.GUILogger;
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
        switch (s.getFileType()){
            default: svgPath.setContent("M4.5 3h7A1.5 1.5 0 0 1 13 4.5v7a1.5 1.5 0 0 1-1.5 1.5h-7A1.5 1.5 0 0 1 3 11.5v-7A1.5 1.5 0 0 1 4.5 3m-3 1.5a3 3 0 0 1 3-3h7a3 3 0 0 1 3 3v7a3 3 0 0 1-3 3h-7a3 3 0 0 1-3-3zm3.75 5.498a.75.75 0 0 0 0 1.5h5.502a.75.75 0 0 0 0-1.5zM4.5 8a.75.75 0 0 1 .75-.75h5.502a.75.75 0 0 1 0 1.5H5.25A.75.75 0 0 1 4.5 8m.75-3.498a.75.75 0 0 0 0 1.5h5.502a.75.75 0 0 0 0-1.5z");
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
        RatingService ratingService = new RatingService(new RatingRepository());
        double avgRating = ratingService.getAverageRating(s);

        if (avgRating > 0.0) {
            HBox starContainer = Stars.StarRow(avgRating, 0.7);
            ratingBox.getChildren().add(starContainer);
        }

        contentBox.getChildren().add(ratingBox);
        wrapper.getChildren().add(contentBox);

        materialCard.setTooltip(new Tooltip("\"" + s.getName() + "\", uploaded by " + uploader.getFirstName() + " " + uploader.getLastName()));
        materialCard.setGraphic(wrapper);
        materialCard.getStyleClass().add("materialCardM");
        materialCard.setOnAction(e -> {
            GUILogger.info("MaterialCard " + s.getName() + " clicked.");
            try {
                SceneManager.getInstance().displayMaterialPage(s.getMaterialId());
            } catch (IOException ex) {
                SceneManager.getInstance().displayErrorPage("Can't display this material!", SCREEN_HOME, "Go to Home Page");
            }
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
        pane.setMinViewportHeight(130);
        pane.setMinViewportWidth(740);
        pane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        pane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        return pane;
    }
}