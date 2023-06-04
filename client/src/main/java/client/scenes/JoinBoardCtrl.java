package client.scenes;

import client.Connection.GetFromServer;
import client.websocket.WebSocketClient;
import com.google.inject.Inject;
import com.google.inject.Provider;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class JoinBoardCtrl {
    private final WebSocketClient webSocketClient;

    @FXML
    private TextField boardId;
    @FXML
    private PasswordField boardPassword;

    @FXML
    private Button joinButton;

    @FXML
    private Button exitButton;
    @FXML
    private Label invalidPasswordMessage;

    private final MainCtrl mainCtrl;
    private final GetFromServer getFromServer;

    /**
     JoinBoardCtrl is the controller responsible for the Join Board Scene. It handles user interaction with the Join Board
     Scene and communicates with the backend through the GetFromServer and WebSocketClient classes. It also injects dependencies
     from other controllers and providers.
     @param mainCtrl the main controller of the application
     @param webSocketClientProvider a provider for creating instances of WebSocketClient
     @param getFromServer the object responsible for getting data from the server
     */
    @Inject
    public JoinBoardCtrl(MainCtrl mainCtrl, Provider<WebSocketClient> webSocketClientProvider, GetFromServer getFromServer) {
        this.mainCtrl = mainCtrl;
        this.webSocketClient = webSocketClientProvider.get();
        this.getFromServer = getFromServer;
    }

    /**
     Initializes the Join Board Scene by hiding the invalid password message and setting up the styling for the exit and join buttons.
     */
    public void initialize()    {
        this.invalidPasswordMessage.setVisible(false);
        exitButton.setOnMouseEntered(event -> {exitButton.setStyle("-fx-background-color: 'ac730b'");});
        exitButton.setOnMouseExited(event -> {exitButton.setStyle("-fx-background-color: 'ffa800'");});
        joinButton.setOnMouseEntered(event -> {joinButton.setStyle("-fx-background-color: 'ac730b'");});
        joinButton.setOnMouseExited(event -> {joinButton.setStyle("-fx-background-color: 'ffa800'");});
    }

    /**
     Attempts to join the board with the provided ID and password by calling the getBoardById method from GetFromServer and then showing the board.
     If the join is unsuccessful, displays an error message to the user.
     */
    public void join()   {
        try {
            getFromServer.getBoardById(Integer.parseInt(boardId.getText()));
            mainCtrl.showBoard(Integer.parseInt(boardId.getText()), boardPassword.getText());
            invalidPasswordMessage.setVisible(false);
        } catch (Exception e) {
            invalidPasswordMessage.setVisible(true);
        }
    }

    /**
     Clears the Join Board Scene and shows the Start Page.
     */
    public void exit() {
        clearScene();
        mainCtrl.showStart();
    }

    /**
     Clears the text fields and error message on the Join Board Scene.
     */
    private void clearScene() {
        invalidPasswordMessage.setVisible(false);
        boardPassword.setText("");
        boardId.setText("");
    }
}
