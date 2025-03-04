package presentation.controller;

import domain.model.Tag;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import presentation.GUILogger;

public class TagPageController {
    @FXML public Text txt_tagTitle;
    @FXML public VBox mainVBoxTagPage;
    @FXML public Text txt_tagCreator;
    @FXML public Text txt_tagDescription;
    @FXML public Text txt_relatedMaterials;

    private Tag tag;

    @FXML
    private void initialize() {
        if (this.tag == null) {
            GUILogger.warn("TagPageController.initialize: tag is null");
        } else {
            GUILogger.info(tag.getTagName());
            txt_tagTitle.setText(tag.getTagName());

            if (tag.getCreator() != null) {
                txt_tagCreator.setText("Created by: " + tag.getCreator().getFullName());
            } else {
                txt_tagCreator.setText("Unknown Creator");
            }

            if (tag.getMaterials() != null && !tag.getMaterials().isEmpty()) {
                txt_relatedMaterials.setText("Related Materials: " + tag.getMaterials().size());
            } else {
                txt_relatedMaterials.setText("No related materials.");
            }
        }
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }
}
