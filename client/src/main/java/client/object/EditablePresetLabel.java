package client.object;


import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;


public class EditablePresetLabel extends Label {
    private final TextField textField = new TextField();

    public EditablePresetLabel(String str) {
        super(str);
        initializeField();
        minHeight(USE_COMPUTED_SIZE);
        textField.setMinHeight(USE_COMPUTED_SIZE);
    }

    private void initializeField() {
        this.setOnMouseClicked(event -> {
            this.setGraphic(textField);
            textField.setText(this.getText());
            textField.requestFocus();
        });
        initFieldClicked();
    }

    private void initFieldClicked() {
        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue) {//focused in
                textField.getProperties().put("oldValue", textField.getText());
                this.setText("");
            } else {
                this.setText(textField.getText());
                this.setGraphic(null);
            }
        });
        textField.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                textField.setText((String) textField.getProperties().get("oldValue"));
                this.setGraphic(null);
                this.requestFocus();
            } else if (event.getCode() == KeyCode.ENTER) {
                this.setGraphic(null);
                this.setText(textField.getText());
                this.requestFocus();
            }
        });
    }
}
