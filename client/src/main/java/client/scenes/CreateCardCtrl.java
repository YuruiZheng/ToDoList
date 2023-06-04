package client.scenes;

import client.websocket.WebSocketClient;
import com.google.inject.Inject;
import com.google.inject.Provider;
import commons.Card;
import commons.DataType;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class CreateCardCtrl implements Initializable {

    private final MainCtrl mainCtrl;
    private final WebSocketClient webSocketClient;

    @FXML
    private TextField enterTitle;
    @FXML
    private TextField enterDescription;
    private VBox listBody;
    private Integer listId;
    private Integer boardId;

    @FXML
    private Label invalidTITLEMessage;

    @FXML
    private Label invalidDescriptionMessage;

    @Inject
    public CreateCardCtrl(MainCtrl mainCtrl, Provider<WebSocketClient> boardClientProvider) {
        this.mainCtrl = mainCtrl;
        this.webSocketClient = boardClientProvider.get();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        invalidTITLEMessage.setVisible(false);
        invalidDescriptionMessage.setVisible(false);
        Platform.runLater(this::keyEvent);
    }

    /**
     * This method receives the VBox, listId and boardId values from the previous scene.
     *
     * @param listBody The VBox where the new card will be added
     * @param listId   The id of the list where the new card will be added
     * @param boardId  The id of the board containing the list
     */
    public void passListAndBoardID(VBox listBody, Integer listId, Integer boardId) {
        this.listBody = listBody;
        this.listId = listId;
        this.boardId = boardId;
    }

    /**
     * This method is called when the user clicks the "Back" button. It clears the fields and goes back to the main board.
     */
    public void goBack() {
        clearFields();
        mainCtrl.showBoard();
    }


    public void done() {
        boolean isTitleInvalid = false;
        boolean isDescriptionInvalid = false;

        // Check if title contains special characters or is too long
        if (enterTitle.getText().matches(".*[^a-zA-Z0-9 ].*") || enterTitle.getText().length() > 29 || enterTitle.getText().length() == 0) {
            invalidTITLEMessage.setVisible(true);
            isTitleInvalid = true;
        }

        // Check if description contains special characters or is too long
        if (enterDescription.getText().matches(".*[^a-zA-Z0-9 ].*") || enterDescription.getText().length() > 29) {
            invalidDescriptionMessage.setVisible(true);
            isDescriptionInvalid = true;
        }

        if (isTitleInvalid || isDescriptionInvalid) {

        } else {
            invalidTITLEMessage.setVisible(false);
            invalidDescriptionMessage.setVisible(false);
            // If no errors, create the new card and add it to the list
            Card card = new Card();
            card.setTitle(enterTitle.getText());
            card.setDescription(enterDescription.getText());
            card.setListId(listId);
            webSocketClient.create(card, DataType.CARD);
            goBack();
        }
    }

    /*
    add keyEvents to the esc button in this scene.
     */
    private void keyEvent() {
        Scene scene = enterTitle.getScene();
        scene.setOnKeyPressed(event -> {
            Node current = scene.getFocusOwner();
            //if you are not typing in either title or description, and you press esc, you go back to the board.
            if (!Objects.equals(current, enterTitle) && !Objects.equals(current, enterDescription)
                    && event.getCode() == KeyCode.ESCAPE) {
                goBack();
            }
            //if you are typing in title, and you press esc, you leave the text-field
            else if(Objects.equals(current, enterTitle) && Objects.equals(event.getCode(), KeyCode.ESCAPE)){
                enterTitle.getParent().requestFocus();
            }
            //if you are typing in description, and you press esc, you leave the text-field
            else if(Objects.equals(current, enterDescription) && Objects.equals(event.getCode(), KeyCode.ESCAPE)){
                enterTitle.getParent().requestFocus();
            }
        });
    }


    /**
     * This method clears the fields.
     */
    private void clearFields() {
        invalidTITLEMessage.setVisible(false);
        invalidDescriptionMessage.setVisible(false);
        enterTitle.clear();
        enterDescription.clear();
    }


}