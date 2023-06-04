package client.scenes;

import client.Connection.GetFromServer;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import commons.Board;
import javafx.application.Platform;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@Singleton
public class AdminCtrl {
    private final MainCtrl mainCtrl;
    private final GetFromServer getFromServer;
    private final Logger log = LoggerFactory.getLogger(AdminCtrl.class);

    @FXML
    private Button backButton;

    @FXML
    private TableView<Board> boardTable;

    @FXML
    private TableColumn<Board, String> boardName;

    @FXML
    private TableColumn<Board, Integer> boardID;
    @FXML
    private TableColumn<Board, String> boardPassword;

    /**
     Constructor for AdminCtrl class, which injects MainCtrl and GetFromServer objects
     to manage navigation and retrieve data from the server, respectively.
     @param mainCtrl an instance of MainCtrl class
     @param getFromServer an instance of GetFromServer class
     */
    @Inject
    public AdminCtrl(MainCtrl mainCtrl, GetFromServer getFromServer) {
        this.mainCtrl = mainCtrl;
        this.getFromServer = getFromServer;
    }

    /**
     Initializes the Admin page view and sets the table columns to display board data
     */
    public void init()  {
        try {
            refresh();
        } catch (InterruptedException e)   {
            log.error("Could not refresh boards");
        }
        getFromServer.longPollChanges(boardTable);
        boardName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getName()));
        boardPassword.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPassword()));
        boardID.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
    }

    /**
     Initializes the Admin page view and sets the table columns to display board data
     as well as adds a listener to the table view to display board info when a row is selected.
     */
    public void initialize() {
        boardTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                info(newSelection);
                Platform.runLater(() -> boardTable.getSelectionModel().clearSelection());
            }
        });
    }

    /**
     Navigates back to the Start page
     */
    @FXML
    void back() {
        mainCtrl.showStart();
    }

    /**
     Refreshes the table view with the latest board data from the server
     @throws InterruptedException if the thread is interrupted
     */
    @FXML
    public void refresh() throws InterruptedException {
        Thread.sleep(50); //fixes a bug where sometimes the changed name isnt in boardList
        List<Board> boardList = getFromServer.getAllBoards();
        boardTable.setItems(FXCollections.observableArrayList(boardList));
    }

    /**
     Refreshes the table view with the latest board data from the server and updates
     the given table view with the new data
     @param boardTable the TableView to be updated with new data
     @throws IOException if an I/O error occurs
     @throws InterruptedException if the thread is interrupted
     */
    @FXML
    public void refresh(TableView<Board> boardTable) throws IOException, InterruptedException {
        Thread.sleep(50); //fixes a bug where sometimes the changed name isnt in boardList
        List<Board> boardList = getFromServer.getAllBoards();
        this.boardTable.setItems(FXCollections.observableArrayList(boardList));
    }


    /**
     * the method to add to a Label in the Vbox so it goes to the right info scene for that board
     * @param currentBoard
     */
    public void info(Board currentBoard){
        mainCtrl.showAdminInfo(currentBoard);
    }
}
