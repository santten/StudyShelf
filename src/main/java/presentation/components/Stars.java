package presentation.components;

import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;

import static javafx.scene.shape.FillRule.EVEN_ODD;

public class Stars {
    public static HBox StarRow(double fillAmount, double scale){
        return StarRow(fillAmount, scale, 0);
    }

    public static HBox StarRow(double fillAmount, double scale, int spacing){
        HBox base = new HBox();
        for (int i = 1; i <= 5; i++) {
            StackPane starContainer = new StackPane();
            SVGPath emptyStar = new SVGPath();
            emptyStar.setContent("M6.886.773C7.29-.231 8.71-.231 9.114.773l1.472 3.667l3.943.268c1.08.073 1.518 1.424.688 2.118L12.185 9.36l.964 3.832c.264 1.05-.886 1.884-1.802 1.31L8 12.4l-3.347 2.101c-.916.575-2.066-.26-1.802-1.309l.964-3.832L.783 6.826c-.83-.694-.391-2.045.688-2.118l3.943-.268z");
            emptyStar.setScaleX(scale);
            emptyStar.setScaleY(scale);
            emptyStar.setFillRule(EVEN_ODD);
            emptyStar.getStyleClass().add("star-empty");

            SVGPath filledStar = new SVGPath();
            filledStar.setContent("M6.886.773C7.29-.231 8.71-.231 9.114.773l1.472 3.667l3.943.268c1.08.073 1.518 1.424.688 2.118L12.185 9.36l.964 3.832c.264 1.05-.886 1.884-1.802 1.31L8 12.4l-3.347 2.101c-.916.575-2.066-.26-1.802-1.309l.964-3.832L.783 6.826c-.83-.694-.391-2.045.688-2.118l3.943-.268z");
            filledStar.setScaleX(scale);
            filledStar.setScaleY(scale);
            filledStar.setFillRule(EVEN_ODD);
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
