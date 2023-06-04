package client.scenes;

import client.Connection.GetFromServer;
import client.utils.SessionData;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StartCtrl {
    private final Logger log = LoggerFactory.getLogger(StartCtrl.class);
    private final MainCtrl mainCtrl;

    @FXML
    private TextField server;
    @FXML
    private Button joinButton;
    @FXML
    private Button createButton;
    @FXML
    private Label invalidServerMessage;

    private final SessionData sessionData;
    private final GetFromServer getFromServer;
    /**
     * Constructs a new instance of the StartCtrl class.
     *
     * @param mainCtrl      The MainCtrl object that represents the main controller of the application.
     * @param sessionData   The SessionData object that contains data about the current session.
     * @param getFromServer The GetFromServer object that is responsible for checking the connection to the server.
     */
    @Inject
    public StartCtrl(MainCtrl mainCtrl, SessionData sessionData, GetFromServer getFromServer) {
        this.mainCtrl = mainCtrl;
        this.sessionData = sessionData;
        this.getFromServer = getFromServer;
    }

    /**
     * Initializes the start scene by setting the mouse events for the join and create buttons.
     */
    public void initialize() {
        invalidServerMessage.setVisible(false);
        joinButton.setOnMouseEntered(event -> {
            joinButton.setStyle("-fx-background-color: 'ac730b'");
        });
        joinButton.setOnMouseExited(event -> {
            joinButton.setStyle("-fx-background-color: 'ffa800'");
        });
        createButton.setOnMouseEntered(event -> {
            createButton.setStyle("-fx-background-color: 'ac730b'");
        });
        createButton.setOnMouseExited(event -> {
            createButton.setStyle("-fx-background-color: 'ffa800'");
        });
    }

    /**
     * Method that is called when the admin button is pressed. Sets the server address
     * in the SessionData object and calls the showAdminLogin() method of the MainCtrl
     * object if the server address is valid.
     */
    @FXML
    void admin() {
        sessionData.setServer(server.getText());
        if(checkServer()){
            mainCtrl.showAdminLogin();
        }
    }

    /**
     Sets the server for the user's session data and shows the board login screen if the server is valid.
     */
    public void joinBoard() {
        sessionData.setServer(server.getText());
        if (checkServer()) {
            mainCtrl.joinBoard();
        }
    }

    /**
     Sets the server for the user's session data and shows the create board screen if the server is valid.
     */
    public void createBoard() {
        sessionData.setServer(server.getText());
        if (checkServer()) {
            mainCtrl.showCreateBoard();
        }
    }

    /**
     Checks the connection status of the server and displays an error message if the connection is invalid.
     @return boolean value indicating whether the server is valid or not
     */
    public boolean checkServer() {
        if (getFromServer.checkConnection()) {
            invalidServerMessage.setVisible(false);
            return true;
        } else {
            invalidServerMessage.setVisible(true);
            return false;
        }
    }
}
