package presentation.components;

import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import presentation.utility.SVGContents;

public class Stars {
    public static HBox StarRow(double fillAmount, double scale, int spacing){
        HBox base = new HBox();

        for (int i = 1; i <= 5; i++) {
            StackPane starContainer = new StackPane();
            SVGPath emptyStar = new SVGPath();
            emptyStar.setContent(SVGContents.star());
            SVGContents.setScale(emptyStar, scale);
            emptyStar.getStyleClass().add("star-empty");

            SVGPath filledStar = new SVGPath();
            filledStar.setContent(SVGContents.star());
            SVGContents.setScale(filledStar, scale);
            filledStar.getStyleClass().add("star-filled");

            double fillPercentage = Math.max(0, Math.min(1, fillAmount - (i - 1)));
            Rectangle clip = new Rectangle();
            clip.setWidth(filledStar.getBoundsInLocal().getWidth() * fillPercentage);
            clip.setHeight(filledStar.getBoundsInLocal().getHeight());
            filledStar.setClip(clip);

            starContainer.getChildren().addAll(emptyStar, filledStar);
            base.getChildren().add(starContainer);
        }
        base.setSpacing(spacing);
        return base;
    }
}
