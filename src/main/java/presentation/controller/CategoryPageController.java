package presentation.controller;

import domain.model.Category;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import presentation.utility.GUILogger;

public class CategoryPageController {
    @FXML public Text txt_courseTitle;
    @FXML public VBox mainVBoxCoursePage;
    @FXML public Text txt_courseAuthor;
    @FXML public Text txt_fromOthers;
    @FXML public Text txt_courseDescription;
    @FXML public Text txt_fromAuthor;
    private Category c;

    @FXML
    private void initialize() {
        if (this.c == null) {
            GUILogger.warn("CategoryPageController.initialize: c is null");
        } else {
            GUILogger.info(c.getCategoryName());
            txt_courseTitle.setText(c.getCategoryName());
        }
    }

    public void setCategory(Category c) {
        this.c = c;
    }
}
