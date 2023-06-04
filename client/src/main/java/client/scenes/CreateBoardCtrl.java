package client.scenes;

import client.Connection.CreateUpdateEntityServer;
import client.websocket.WebSocketClient;
import com.google.inject.Inject;
import com.google.inject.Provider;
import commons.Board;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;

public class CreateBoardCtrl {

    private final WebSocketClient webSocketClient;
    @FXML
    private Button createButton;

    @FXML
    private Button exitButton;

    @FXML
    private TextField boardName;

    @FXML
    private TextField boardPassword;

    @FXML
    private ColorPicker boardBg;

    private MainCtrl mainCtrl;

    @FXML
    private Label invalidBoardTitleMessage;

    @FXML
    private Label invalidPasswordMessage;

    private final CreateUpdateEntityServer createUpdateEntityServer;


    /**
     The CreateBoardCtrl class is responsible for controlling the "create board" scene,
     including validating user input and creating a new board.
     The class is injected with the main controller, web socket client provider,
     and create/update entity server, which are used to handle web socket communication and database interactions.
     */
    @Inject
    public CreateBoardCtrl(MainCtrl mainCtrl, Provider<WebSocketClient> webSocketClientProvider,
                           CreateUpdateEntityServer createUpdateEntityServer) {
        this.mainCtrl = mainCtrl;
        this.webSocketClient = webSocketClientProvider.get();
        this.createUpdateEntityServer = createUpdateEntityServer;
    }

    /**
     Initializes the create board scene and sets up event handlers for buttons and mouse interactions.
     */
    public void initialize() {
        invalidBoardTitleMessage.setVisible(false);
        invalidPasswordMessage.setVisible(false);
        exitButton.setOnMouseEntered(event -> exitButton.setStyle("-fx-background-color: 'ac730b'"));
        exitButton.setOnMouseExited(event -> exitButton.setStyle("-fx-background-color: 'ffa800'"));
        createButton.setOnMouseEntered(event -> createButton.setStyle("-fx-background-color: 'ac730b'"));
        createButton.setOnMouseExited(event -> createButton.setStyle("-fx-background-color: 'ffa800'"));

        //Set the default colour of the colour selector to a dark gray.
        Color defaultColour = Color.web("#343434");
        boardBg.setValue(defaultColour);
    }

    /**
     Validates the input board name and password and creates a new board with the given properties if valid.
     If any of the input fields are invalid, an error message is displayed to the user and the board is not created.
     */
    public void submit() {
        boolean isBoardNameInvalid = false;
        boolean isBoardPasswordInvalid = false;

        // Check if boardName contains special characters or has length greater than 25
        if (boardName.getText().matches(".*[^a-zA-Z0-9 ].*") || boardName.getText().length() > 25) {
            invalidBoardTitleMessage.setVisible(true);
            isBoardNameInvalid = true;
        }

        // Check if boardPassword contains special characters or has length greater than 25
        if (boardPassword.getText().matches(".*[^a-zA-Z0-9 ].*") || boardPassword.getText().length() > 25) {
            invalidPasswordMessage.setVisible(true);
            isBoardPasswordInvalid = true;
        }

        if (isBoardNameInvalid || isBoardPasswordInvalid) {

        } else {
            invalidBoardTitleMessage.setVisible(false);
            invalidPasswordMessage.setVisible(false);
            Board board = new Board();
            board.setName(boardName.getText());
            board.setPassword(boardPassword.getText());
            board.setBackground(String.format("%02X%02X%02X",
                    (int) (boardBg.getValue().getRed() * 255),
                    (int) (boardBg.getValue().getGreen() * 255),
                    (int) (boardBg.getValue().getBlue() * 255)));
            board.setFont("ffffff");
            board.setListBG("ffa800");
            board.setListF("ffffff");

            //Send a create request to the server. createBoard returns the newly-created board
            Board createdBoard = createUpdateEntityServer.createBoard(board);
            clearScene();
            mainCtrl.showBoard(createdBoard.getId(), boardPassword.getText());

        }
    }


    /**
     Goes back to the start screen.
     */
    public void exit() {
        clearScene();
        mainCtrl.showStart();
    }

    /**
     Clears the scene by hiding error messages and resetting text fields and color selectors to their default values.
     */
    private void clearScene() {
        invalidBoardTitleMessage.setVisible(false);
        invalidPasswordMessage.setVisible(false);
        boardName.setText("");
        boardPassword.setText("");
        boardBg.setValue(Color.WHITE);
    }
}
