package client.scenes;

import client.websocket.WebSocketClient;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
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
@Singleton
public class CreateTagSceneCtrl {

    private final Logger log = LoggerFactory.getLogger(CreateTagSceneCtrl.class);

    @FXML
    private Button back;

    @FXML
    private Button create;

    @FXML
    private ColorPicker tagColor;

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

    private final MainCtrl mainCtrl;

    private Color colorHEX;

    @FXML
    private Label invalidTagNameMessage;

    private final WebSocketClient webSocketClient;

    private Board board;

    /**
     Controller class for creating a new tag in the application.
     */
    @Inject
    public CreateTagSceneCtrl(MainCtrl mainCtrl, Provider<WebSocketClient> boardClientProvider) {
        this.mainCtrl = mainCtrl;
        this.webSocketClient=boardClientProvider.get();
    }

    /**
     Sets the board for which the tag is being created.
     @param board The board object for which the tag is being created.
     */
    public void setBoard(Board board) {
        this.board = board;
    }

    /**
     Initializes the scene by subscribing to the board.
     @param board The board object for which the tag is being created.
     */
    public void initializeScene(Board board) {
        this.board = board;
        webSocketClient.subscribeToBoard(this.board.getId());
    }

    /**
     Initializes the scene.
     */
    public void initialize(){
        invalidTagNameMessage.setVisible(false);
    }

    /**
     Changes the tag color based on the user's selection.
     */
    public void changeColor(){
        colorHEX = tagColor.getValue();
    }

    /**
     Takes the user back to the tag list view.
     @throws IOException If there is an error navigating to the tag list view.
     */
    public void back() throws IOException {
        invalidTagNameMessage.setVisible(false);
        mainCtrl.showTagList();
    }

    /**

     Creates a new tag with the given name and color and adds it to the board.
     @throws IOException If there is an error navigating to the tag list view.
     */
    public void create() throws IOException {
        String name = tagName.getText();
        if (name.matches(".*[^a-zA-Z0-9 ].*") || name.length() > 10) {
            invalidTagNameMessage.setVisible(true);
        } else {
            Tag tag= new Tag();
            tag.setTitle(tagName.getText());
            tag.setColor(String.format("%02X%02X%02X",
                    (int) (tagColor.getValue().getRed() * 255),
                    (int) (tagColor.getValue().getGreen() * 255),
                    (int) (tagColor.getValue().getBlue() * 255)));
            invalidTagNameMessage.setVisible(false);
            colorHEX = tagColor.getValue();
            tag.setBoardId(board.getId());
            board.getTags().add(tag);
            webSocketClient.create(tag, DataType.TAG);
            log.info("CREATE TAG WITH name=" + name + " and color=" + String.format("%02X%02X%02X",
                    (int) (tagColor.getValue().getRed() * 255),
                    (int) (tagColor.getValue().getGreen() * 255),
                    (int) (tagColor.getValue().getBlue() * 255)));
            clear();
            mainCtrl.showTagList();
        }
    }

    /**

     Clears the scene
     DEFAULT tagName: ""
     DEFAULT tagColor" WHITE
     */
    public void clear(){
        tagName.setText("");
        tagColor.setValue(Color.WHITE);
    }


}
