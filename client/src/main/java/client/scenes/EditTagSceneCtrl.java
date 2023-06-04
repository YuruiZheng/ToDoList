package client.scenes;

import client.websocket.WebSocketClient;
import com.google.inject.Inject;
import com.google.inject.Provider;
import commons.Board;
import commons.DataType;
import commons.Tag;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class EditTagSceneCtrl {

    private final Logger log = LoggerFactory.getLogger(EditTagSceneCtrl.class);

    @FXML
    private Button back;

    @FXML
    private Button delete;

    @FXML
    private Button set;

    @FXML
    private TextField tagName;

    @FXML
    private Label title;

    @FXML
    private Label titleTagColor;

    @FXML
    private Label titleTagName;

    @FXML
    private Circle prevCircle;

    private Color colorHEX;

    private final MainCtrl mainCtrl;

    private final WebSocketClient webSocketClient;

    @FXML
    private Label invalidTagNameMessage;

    @FXML
    private ColorPicker tagColor;

    private Tag tag;

    private Board board;

    /**
     * Creates a new instance of EditTagSceneCtrl
     *
     * @param mainCtrl         the MainCtrl object that controls the main scene of the application
     * @param boardClientProvider a Provider object that provides the WebSocketClient object that sends data to the server
     */
    @Inject
    public EditTagSceneCtrl(MainCtrl mainCtrl, Provider<WebSocketClient> boardClientProvider) {
        this.mainCtrl = mainCtrl;
        this.webSocketClient=boardClientProvider.get();
    }

    /**
     * Initializes the scene when it is loaded
     */
    public void initialize(){
        invalidTagNameMessage.setVisible(false);
    }

    /**
     * Initializes the scene with the given board and tag
     *
     * @param board  the board that the tag belongs to
     * @param tag    the tag that will be edited
     */
    public void initializeScene(Board board,Tag tag) {
        this.tag=tag;
        this.board= board;
        tagName.setText(tag.getTitle());
        tagColor.setValue( Color.rgb(
                Integer.valueOf(tag.getColor().substring(0, 2), 16),
                Integer.valueOf(tag.getColor().substring(2, 4), 16),
                Integer.valueOf(tag.getColor().substring(4, 6), 16)
        ));


    }

    /**
     * Goes back to the Tag List scene
     *
     * @throws IOException if an I/O error occurs
     */
    @FXML
    void back() throws IOException {
        invalidTagNameMessage.setVisible(false);
        mainCtrl.showTagList();
    }

    /**
     * Deletes the tag from the board
     *
     * @throws IOException if an I/O error occurs
     */
    @FXML
    void delete() throws IOException {
        board.getTags().remove(tag);
        mainCtrl.showTagList();
        webSocketClient.delete(tag.getId(), DataType.TAG);

        invalidTagNameMessage.setVisible(false);
        log.info("DELETE");
    }

    /**
     * Sets the changes to the tag and updates the board
     *
     * @throws IOException if an I/O error occurs
     */
    @FXML
    void set() throws IOException {
        if (tagName.getText().matches(".*[^a-zA-Z0-9 ].*") || tagName.getText().length() > 10) {
            invalidTagNameMessage.setVisible(true);
        } else {
            board.getTags().remove(tag);
            tag.setTitle(tagName.getText());
            tag.setColor(String.format("%02X%02X%02X",
                    (int) (tagColor.getValue().getRed() * 255),
                    (int) (tagColor.getValue().getGreen() * 255),
                    (int) (tagColor.getValue().getBlue() * 255)));
            invalidTagNameMessage.setVisible(false);
            colorHEX = tagColor.getValue();
            board.getTags().add(tag);
            webSocketClient.update(tag,DataType.TAG);
            mainCtrl.showTagList();
            log.info("CREATE TAG WITH name=" + tagName + " and color=" + String.format("%02X%02X%02X",
                    (int) (tagColor.getValue().getRed() * 255),
                    (int) (tagColor.getValue().getGreen() * 255),
                    (int) (tagColor.getValue().getBlue() * 255)));
        }
    }


}
