package client.scenes;

import client.Connection.GetFromServer;
import client.utils.SessionData;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;

public class AdminLoginCtrl {
    private final MainCtrl mainCtrl;

    @FXML
    private PasswordField passwordInput;

    @FXML
    private Button submitButton;

    @FXML
    private Button backButton;

    @FXML
    private Label wrongPassword;

    private final GetFromServer getFromServer;
    private final SessionData sessionData;

    /**

     This class represents the controller for the Admin Login scene, responsible for handling user input and
     authentication logic. It is injected with the MainCtrl, GetFromServer, and SessionData classes to allow
     for access to necessary data and methods.
     */
    @Inject
    public AdminLoginCtrl(MainCtrl mainCtrl, GetFromServer getFromServer, SessionData sessionData) {
        this.mainCtrl = mainCtrl;
        this.getFromServer = getFromServer;
        this.sessionData = sessionData;
    }

    /**
     Initializes the Admin Login scene, sets the visibility of the "wrong password" message to false.
     */
    public void initialize(){
        wrongPassword.setVisible(false);
    }

    /**
     Submits the password input for authentication and displays the Admin scene if successful.
     If the password input is incorrect, displays the "wrong password" message.
     */
    public void submit() {

        if(getFromServer.checkRootPassword(passwordInput.getText())){
            wrongPassword.setVisible(false);
            sessionData.setRootPassword(passwordInput.getText());
            mainCtrl.showAdmin();
        }
        else{
            wrongPassword.setVisible(true);
        }
    }

    /**
     Navigates back to the Start scene.
     */
    public void back(){
        mainCtrl.showStart();
    }



}
