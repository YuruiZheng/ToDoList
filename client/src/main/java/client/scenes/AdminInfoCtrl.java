package client.scenes;

import client.Connection.DeleteEntityServer;
import commons.Board;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import com.google.inject.Inject;
import java.io.IOException;

public class AdminInfoCtrl {
    private final MainCtrl mainCtrl;
    private final DeleteEntityServer deleteEntityServer;

    @FXML
    private Button backButton;

    @FXML
    private Button deleteButton;

    @FXML
    private Button joinButton;

    @FXML
    private Label boardId;
    @FXML
    private Label boardName;

    private Board currentBoard;

    /**
     The AdminInfoCtrl class is responsible for controlling the admin info view.
     It displays the information of a particular board and allows the admin to delete the board or join it.
     */
    @Inject
    public AdminInfoCtrl(MainCtrl mainCtrl, DeleteEntityServer deleteEntityServer) {
        this.mainCtrl = mainCtrl;
        this.deleteEntityServer = deleteEntityServer;
    }

    /**
     Initializes the board information on the view.
     @param currentBoard the board for which the information will be displayed
     */
    public void init(Board currentBoard){
        this.currentBoard = currentBoard;
        boardId.setText(String.format("ID: %d", currentBoard.getId()));
        boardName.setText(String.format("Name: %s", currentBoard.getName()));
    }

    /**
     Returns to the admin view.
     */
    @FXML
    void back() {
        mainCtrl.showAdmin();
    }

    /**
     Deletes the current board and returns to the admin view.
     @throws IOException if an error occurs while deleting the board
     */
    @FXML
    void delete() throws IOException {
        deleteEntityServer.delete(currentBoard.getId(), "board");
        mainCtrl.showAdmin();
    }

    /**
     Joins the current board and shows it on the main view.
     */
    @FXML
    void join() {
        mainCtrl.showBoard(currentBoard.getId(), currentBoard.getPassword());
    }

}

