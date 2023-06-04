package client.object;

import client.scenes.CardDescriptionCtrl;
import commons.Subtask;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class SubtaskItem extends StackPane {
    private TextField text = new TextField();
    private CheckBox checkBox = new CheckBox();
    private ImageView deleteButton = new ImageView();
    private CardDescriptionCtrl cardDescriptionCtrl;
    private Subtask subtask;

    public SubtaskItem(Subtask subtask, CardDescriptionCtrl cardDescriptionCtrl) {
        super();
        this.cardDescriptionCtrl = cardDescriptionCtrl;
        this.subtask = subtask;
        text.setText(subtask.getTitle());
        checkBox.setSelected(subtask.getStatus() != null && subtask.getStatus());
        this.getChildren().add(text);
        this.getChildren().add(deleteButton);
        this.getChildren().add(checkBox);
        this.applyStyling();
        this.addEvents();
        setAlignment(checkBox, Pos.CENTER_RIGHT);
        setAlignment(text, Pos.CENTER_LEFT);
        setAlignment(deleteButton, Pos.CENTER_RIGHT);
        setMargin(checkBox, new Insets(0, 30, 0, 0));
    }

    public String getText() {
        return text.getText();
    }

    public TextField getTextField() {
        return text;
    }

    public Subtask getSubtask() {
        return subtask;
    }

    public void applyStyling() {
        this.getStylesheets().add("stylesheet.css");
        this.getStyleClass().add("subtask");
        this.deleteButton.getStyleClass().add("delete");
        this.deleteButton.setFitHeight(25);
        this.deleteButton.setFitWidth(17);
        this.text.setStyle("-fx-text-box-border: transparent; -fx-focus-color: transparent;");
    }

    public void addEvents() {
        checkBox.selectedProperty().addListener((event, oldValue, newValue) -> {
            if (newValue != subtask.getStatus())
                subtask.setStatus(newValue);
            cardDescriptionCtrl.updateSubtask(subtask);
        });
        deleteButton.setOnMouseClicked(event -> cardDescriptionCtrl.deleteSubtask(subtask.getId()));
    }
}
