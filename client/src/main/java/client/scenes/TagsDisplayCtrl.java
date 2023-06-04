package client.scenes;

import com.google.inject.Inject;
import commons.Board;
import commons.Tag;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class TagsDisplayCtrl {
    private MainCtrl mainCtrl;
    @FXML
    private Button goBackButton;
    @FXML
    private Button createTagButton;
    @FXML
    private VBox tagList;

    /**
     * Constructs a TagsDisplayCtrl object with a reference to the MainCtrl object.
     *
     * @param mainCtrl the MainCtrl object to reference
     */
    @Inject
    public TagsDisplayCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    public void initialize() {
        goBackButton.setOnMouseEntered(event -> {
            goBackButton.setStyle("-fx-background-color: 'ac730b'");
        });
        goBackButton.setOnMouseExited(event -> {
            goBackButton.setStyle("-fx-background-color: 'ffa800'");
        });
        createTagButton.setOnMouseEntered(event -> {
            createTagButton.setStyle("-fx-background-color: 'ac730b'");
        });
        createTagButton.setOnMouseExited(event -> {
            createTagButton.setStyle("-fx-background-color: 'ffa800'");
        });
    }
    /**
     * Returns to the board scene and clears the tag list.
     */
    public void goBack(){
        mainCtrl.showBoard();
        tagList.getChildren().remove(0, tagList.getChildren().size());
    }
    /**
     * Initializes the scene with a list of tags for a board.
     *
     * @param board the Board object containing the list of tags
     * @throws IOException if there is an error reading the stylesheet
     */
    public void initializeScene(Board board) throws IOException {
        List<Tag> listOfTags = board.getTags();
        tagList.getChildren().remove(0, tagList.getChildren().size());
        if (listOfTags==null){board.setTags(new ArrayList<>());}
        for(Tag t: listOfTags) {

            Label tag = new Label(t.getTitle());

            tag.getStylesheets().add("stylesheet.css");
            tag.getStyleClass().add("tag");
            tag.setOnMouseClicked(e -> {
                tagList.getChildren().remove(0, tagList.getChildren().size());
                mainCtrl.showEditTag(t);
            });
            tagList.getChildren().add(tag);
        }
    }
    /**
     * Shows the create tag scene and clears the tag list.
     */
    public void showCreateTagScene(){
        tagList.getChildren().remove(0, tagList.getChildren().size());
        mainCtrl.showCreateTagScene();
    }

}
