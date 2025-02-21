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

import java.io.ByteArrayInputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MaterialCard {
    public static Button materialCard(StudyMaterial s) {
        Button materialCard = new Button();
        HBox container = new HBox(10);
        VBox contentBox = new VBox(5);
        VBox previewBox = new VBox();
        User uploader = s.getUploader();
        contentBox.setPadding(new Insets(5, 5, 5, 5));


        TextFlow titleArea = new TextFlow();

        SVGPath svgPath = new SVGPath();

        // there will be more options to reflect different file types in the future
        switch (s.getFileType()){
            default: svgPath.setContent("M4.5 3h7A1.5 1.5 0 0 1 13 4.5v7a1.5 1.5 0 0 1-1.5 1.5h-7A1.5 1.5 0 0 1 3 11.5v-7A1.5 1.5 0 0 1 4.5 3m-3 1.5a3 3 0 0 1 3-3h7a3 3 0 0 1 3 3v7a3 3 0 0 1-3 3h-7a3 3 0 0 1-3-3zm3.75 5.498a.75.75 0 0 0 0 1.5h5.502a.75.75 0 0 0 0-1.5zM4.5 8a.75.75 0 0 1 .75-.75h5.502a.75.75 0 0 1 0 1.5H5.25A.75.75 0 0 1 4.5 8m.75-3.498a.75.75 0 0 0 0 1.5h5.502a.75.75 0 0 0 0-1.5z");
        }

        svgPath.getStyleClass().add("materialCardIcon");
        svgPath.setFillRule(FillRule.EVEN_ODD);
        titleArea.getChildren().add(svgPath);

        Label title = new Label();
        title.setText("   " + s.getName());
        title.setWrapText(true);
        title.setTextOverrun(OverrunStyle.ELLIPSIS);

        title.setMaxWidth(160);
        title.setMaxHeight(20);

        title.getStyleClass().add("label4");
        titleArea.getChildren().add(title);
        contentBox.getChildren().add(titleArea);

        Text uploaderLabel = new Text(uploader.getFirstName() + " " + uploader.getLastName());
        contentBox.getChildren().add(uploaderLabel);

        Text fileLabel = new Text(s.getFileType());
        fileLabel.getStyleClass().add("materialCardSubtitle");
        contentBox.getChildren().add(fileLabel);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yy HH:mm");
        String formattedTimestamp = s.getTimestamp().format(formatter);

        Text timestamp = new Text(formattedTimestamp);
        contentBox.getChildren().add(timestamp);

        materialCard.setTooltip(new Tooltip("\"" + s.getName() + "\", uploaded by " + uploader.getFirstName() + " " + uploader.getLastName()));

        if (s.getPreviewImage() != null) {
            ImageView preview = new ImageView(new Image(new ByteArrayInputStream(s.getPreviewImage())));
            preview.setFitWidth(60);
            preview.setFitHeight(80);
            preview.setPreserveRatio(false);

            HBox ratingBox = new HBox();
            ratingBox.setPrefWidth(100);
            ratingBox.setMaxWidth(100);
            ratingBox.setMinWidth(100);
            RatingService ratingService = new RatingService(new RatingRepository());
            double avgRating = ratingService.getAverageRating(s);

            for (int i = 1; i <= 5; i++) {
                StackPane starContainer = new StackPane();


                SVGPath emptyStar = new SVGPath();
                emptyStar.setContent("M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z");
                emptyStar.setScaleX(0.7);
                emptyStar.setScaleY(0.7);
                emptyStar.getStyleClass().add("star-empty");


                SVGPath filledStar = new SVGPath();
                filledStar.setContent("M12 17.27L18.18 21l-1.64-7.03L22 9.24l-7.19-.61L12 2 9.19 8.63 2 9.24l5.46 4.73L5.82 21z");
                filledStar.setScaleX(0.7);
                filledStar.setScaleY(0.7);
                filledStar.getStyleClass().add("star-filled");


                double fillPercentage = Math.max(0, Math.min(1, avgRating - (i - 1)));
                Rectangle clip = new Rectangle();
                clip.setWidth(filledStar.getBoundsInLocal().getWidth() * fillPercentage);
                clip.setHeight(filledStar.getBoundsInLocal().getHeight());
                filledStar.setClip(clip);

                starContainer.getChildren().addAll(emptyStar, filledStar);
                ratingBox.getChildren().add(starContainer);
            }

            previewBox.getChildren().addAll(preview, ratingBox);
            previewBox.setAlignment(Pos.CENTER);
            previewBox.setPrefWidth(100);
        }

        container.getChildren().addAll(contentBox, previewBox);
        materialCard.setGraphic(container);
        materialCard.getStyleClass().add("materialCardM");

        return materialCard;
    }

    public static ScrollPane materialCardScrollHBox(List<StudyMaterial> list) {
        HBox materialCardHBox = new HBox();
        for (int i = list.size() - 1; i > 0; i--) {
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