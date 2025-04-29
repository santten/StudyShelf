package presentation.components;

import javafx.scene.control.Hyperlink;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import presentation.controller.PageController;
import presentation.view.SceneManager;
import presentation.view.ScreenHistory;

import java.util.ArrayList;

public class BreadCrumb {
    SceneManager sm = SceneManager.getInstance();
    ScreenHistory history = sm.getHistory();
    int size;

    public BreadCrumb(){
        this.size = 5;
    }

    public HBox makeBreadCrumb(){
        HBox hb = new HBox();
        ArrayList<PageController> list = history.getHistory();

        for (int i = Math.max(list.size() - 1 - this.size, 0); i < list.size() - 1; i++){
            int index = i;
            PageController item = list.get(index);
            Hyperlink link = new Hyperlink(item.getPageName());
            link.setOnAction(e -> history.jumpTo(index));

            hb.getChildren().add(link);

            hb.getChildren().add(new Text(" » "));
        }

        return hb;
    }
}
