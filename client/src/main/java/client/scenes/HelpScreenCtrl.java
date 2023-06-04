package client.scenes;

import client.utils.SessionData;
import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.text.Text;

public class HelpScreenCtrl {
    private final MainCtrl mainCtrl;
    private final SessionData sessionData;

    @FXML
    private Text longText;

    /**
     * Constructs a new HelpScreenCtrl instance.
     * @param mainCtrl the MainCtrl instance
     * @param sessionData the SessionData instance
     */
    @Inject
    public HelpScreenCtrl(MainCtrl mainCtrl, SessionData sessionData) {
        this.mainCtrl = mainCtrl;
        this.sessionData = sessionData;
    }

    /**
     * Goes back to the previous scene.
     * If the user was subscribed to a board, it goes back to the board scene, otherwise it goes back to the start page.
     */
    public void goBack(){
        if (sessionData.getSubscribedBoardId() != null)
            mainCtrl.showBoard();
        else mainCtrl.showStart();
    }
}
