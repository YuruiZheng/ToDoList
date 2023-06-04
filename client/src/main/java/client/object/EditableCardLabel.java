package client.object;

import client.scenes.BoardCtrl;
import commons.Card;
import commons.Tag;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.VPos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.Objects;

public class EditableCardLabel extends GridPane {

    private final int doubleClickTime = 300;
    private TextField text = new TextField();
    private Card card;
    private final BoardCtrl boardCtrl;
    private int clickCount;
    private String cardBackground;
    private String cardFont;
    private Label label;
    private boolean editing;

    public EditableCardLabel(String str, Card card, BoardCtrl boardCtrl, String cardBackground, String cardFont) {
        this.label = new Label(str);
        this.card = card;
        this.boardCtrl = boardCtrl;
        this.cardBackground = cardBackground;
        this.cardFont = cardFont;

        this.getRowConstraints().add(new RowConstraints());
        this.getColumnConstraints().add(new ColumnConstraints());
        this.add(label, 0, 0);

        this.setOnMouseClicked(e -> {
            if (e.getClickCount() >= 2) {
                clickCount = 2;
                boardCtrl.editCard(card);
                clickCount = 0;
            } else if (e.getClickCount() == 1 && this.card != null) {
                clickCount = 1;
                new Thread(() -> {
                    try {
                        Thread.sleep(doubleClickTime);
                        if (clickCount == 1) toTextField();
                    } catch (InterruptedException ignored) {
                    }
                }).start();
            }
        });
        text.focusedProperty().addListener((prop, oldValue, newValue) -> {
            if (!newValue) {
                toLabel();
            }
        });
        text.setOnKeyReleased(e -> {
            if (e.getCode().equals(KeyCode.ENTER) || e.getCode().equals(KeyCode.ESCAPE)) {
                this.getParent().requestFocus();
            }
        });

        this.applyCardStyling();
        this.enableDragging();
    }

    public Card getCard(){ return this.card; }

    public boolean isEditing() {
        return editing;
    }

    public void toTextField() {
        Platform.runLater(() -> {
            editing = true;
            text.setText(label.getText());
            label.setGraphic(text);
            label.setText("");
            text.requestFocus();

            //add key event to the enter button, it makes the text field lose focus.
            text.setOnKeyPressed(event -> {
                if(Objects.equals(event.getCode(),KeyCode.ENTER)){
                    text.getParent().requestFocus();
                }
            });

            clickCount = 0;
        });
    }

    public void toLabel() {
        editing = false;
        label.setText(text.getText());
        if (text.getText() == null || Objects.equals(text.getText(), "")) {
            boardCtrl.refreshBoard();
        } else {
            this.card.setTitle(text.getText());
            if (Objects.isNull(card.getId())) {
                boardCtrl.createCard(card);
            } else {
                boardCtrl.updateCard(card);
            }
        }
        applyCardStyling();
    }

    public void setRows(ProgressBar progressBar, ImageView description, HBox tagBox)   {
        this.getChildren().remove(0, this.getChildren().size());
        this.getRowConstraints().remove(0, this.getRowCount());
        this.getRowConstraints().add(0, new RowConstraints(5));
        this.getRowConstraints().add(1, new RowConstraints(31));
        this.getRowConstraints().add(2, new RowConstraints(14));
        this.getColumnConstraints().remove(0, this.getColumnCount());
        this.getColumnConstraints().add(0, new ColumnConstraints(8));
        this.getColumnConstraints().add(1, new ColumnConstraints(194));
        this.setWidth(212);
        this.add(label, 0, 1);
        if (tagBox != null) {
            this.add(tagBox, 0, 0);
            GridPane.setColumnSpan(tagBox, 2);
            GridPane.setHalignment(tagBox, HPos.CENTER);
            GridPane.setMargin(tagBox, new Insets(3, 0, 0, 0));
        }
        if (description != null) {
            this.add(description, 0, 2);
            GridPane.setHalignment(description, HPos.LEFT);
            GridPane.setValignment(description, VPos.CENTER);
            GridPane.setMargin(description, new Insets(0, 0, 4, -6));
        }
        if (progressBar != null) {
            if (description == null) {
                this.add(progressBar, 0, 2);
                GridPane.setColumnSpan(progressBar, 2);
            } else {
                this.add(progressBar, 1, 2);
            }
            GridPane.setHalignment(progressBar, HPos.CENTER);
            GridPane.setValignment(progressBar, VPos.CENTER);
            GridPane.setMargin(progressBar, new Insets(0, 0, 3, 0));
        }
        GridPane.setHalignment(label, HPos.CENTER);
        GridPane.setColumnSpan(label, 2);

    }

    public void applyCardStyling() {
        this.getStylesheets().add("stylesheet.css");
        this.getStyleClass().add("card");

        this.setBackground(new Background(new BackgroundFill(Color.web(cardBackground), CornerRadii.EMPTY, Insets.EMPTY)));
        label.setTextFill(Color.web(cardFont));

        setRows(calculateProgressBar(), getDescriptionIndicator(), calculateTags());
    }

    public HBox calculateTags() {
        if (card.getTags() == null || card.getTags().size() == 0)
            return null;
        HBox tagBox = new HBox();
        tagBox.setPrefWidth(190);
        tagBox.setPrefHeight(2);
        double size = 0.9 / (double)(card.getTags().size());
        for (Tag tag : card.getTags()) {
            Region region = new Region();
            region.setBackground(new Background(new BackgroundFill(Color.valueOf(tag.getColor()), new CornerRadii(2), Insets.EMPTY)));
            region.setPrefHeight(2);
            region.setMinWidth(2);
            region.setPrefWidth(size * 170);
            region.setMinWidth(size * 170);
            region.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, new CornerRadii(2), new BorderWidths(0.5))));
            tagBox.getChildren().add(region);
        }
        double spacing = 2;

        if (card.getTags().size() == 1)
            spacing = 29.5;
        else
            spacing = 59.0 / (double)(card.getTags().size() - 1);
        tagBox.setSpacing(spacing);
        tagBox.setPadding(new Insets(0, spacing, 0, spacing));
        return tagBox;
    }

    public ImageView getDescriptionIndicator()    {
        if (card.getDescription() != null && !Objects.equals(card.getDescription(), ""))    {
            ImageView imageView = new ImageView("graphics/description.png");
            imageView.setFitHeight(9);
            imageView.setFitWidth(9);
            Tooltip.install(imageView, new Tooltip("Double click to view description"));
            return imageView;
        }
        return null;
    }

    public ProgressBar calculateProgressBar() {
        if (card.getSubtasks() != null && card.getSubtasks().size() != 0) {
            ProgressBar progressBar = new ProgressBar();
            long total = card.getSubtasks().size();
            long done = card.getSubtasks().stream()
                    .filter(subtask -> subtask.getStatus() != null && subtask.getStatus()).count();
            if (total != 0) {
                progressBar.setProgress((float) done / (float) total);
                progressBar.setTooltip(new Tooltip(done + " out of " + total + " subtasks completed"));
                progressBar.setPrefWidth(200);
                progressBar.setPrefHeight(5);
                progressBar.getStylesheets().add("stylesheet.css");
                progressBar.setBlendMode(BlendMode.DARKEN);
                progressBar.setOpacity(1);
            }
            return progressBar;
        }
        return null;
    }

    private void enableDragging() {
        this.setOnDragDetected(event -> {
            Dragboard db = this.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            content.putString(Integer.toString(card.getId()));
            db.setContent(content);
            event.consume();
        });
    }
}