package client.scenes;

import client.Connection.GetFromServer;
import client.utils.SessionData;
import commons.Board;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MyBoardsCtrl {

    private final Logger log = LoggerFactory.getLogger(MyBoardsCtrl.class);
    private final MainCtrl mainCtrl;
    private final Map<String, Map<Integer, Board>> boards = new HashMap<>();
    @FXML
    private TreeView<String> treeView;
    @FXML
    private Button hideBoards;
    @FXML
    private ScrollPane treeContainer;

    private final SessionData sessionData;
    private final GetFromServer getFromServer;

    /**
     * The MyBoardsCtrl class is responsible for controlling the view of the "My Boards" section of the application.
     * It handles adding, updating, and deleting boards from the view, as well as saving and restoring the state of joined boards.
     * @param mainCtrl      The MainCtrl object responsible for controlling the main view of the application.
     * @param sessionData   The SessionData object responsible for handling session data and server information.
     * @param getFromServer The GetFromServer object responsible for handling HTTP requests to the server.
     */
    @Inject
    public MyBoardsCtrl(MainCtrl mainCtrl, SessionData sessionData, GetFromServer getFromServer) {
        this.mainCtrl = mainCtrl;
        this.sessionData = sessionData;
        this.getFromServer = getFromServer;
    }

    /**
     * Resets the size of the tree view and container to their default size.
     */
    public void resetSize() {
        treeView.setPrefHeight(-1);
        treeContainer.setPrefHeight(-1);
    }

    /**
     * Sets the size of the tree view and container to the specified height.
     * @param height The height to set the tree view and container to.
     */
    public void setSize(double height) {
        height -= 40;
        treeContainer.setPrefHeight(height);
        treeView.setPrefHeight(height);
        treeContainer.setVvalue(0);
        treeContainer.setHvalue(0);
    }

    /**
     * Initializes the view of the "My Boards" section of the application.
     * Sets the scroll bar policies of the tree container, styles the hide boards button, and sets the on click listener for the tree view.
     */
    public void initialize() {
        treeContainer.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        treeContainer.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        hideBoards.setOnMouseEntered(event -> hideBoards.setStyle("-fx-background-color: rgba(35, 35, 35, 1)"));
        hideBoards.setOnMouseExited(event -> hideBoards.setStyle("-fx-background-color: rgba(52, 52, 52, 1)"));
        restoreState();

        treeView.setOnMouseClicked(event -> {
            try {
                Text text = (Text) event.getTarget();
                TreeItem<String> selectedItem = treeView.getSelectionModel().getSelectedItem();
                if (Objects.equals(text.getText(), "X")) {
                    deleteBoardFromTreeView(selectedItem);
                } else {
                    joinBoardFromTreeView(selectedItem);
                }
            } catch (ClassCastException ignored) {

            }
        });
    }


    /**
     * Adds a board to the view of the "My Boards" section of the application.
     * @param board  The board to add to the view.
     * @param server The server the board belongs to.
     */
    public void addBoard(Board board, String server) {
        if (!boards.containsKey(server)) {
            boards.put(server, new HashMap<>());
            TreeItem<String> treeItem = new TreeItem<>(server);
            treeView.getRoot().getChildren().add(treeItem);
        }
        if (!boards.get(server).containsKey(board.getId())) {
            addBoardToTreeView(board, server);
        } else if (!Objects.equals(boards.get(server).get(board.getId()).getName(), board.getName())) {
            updateBoardNameInTreeView(board, server);
        }
    }

    /**
     * Hides the "My Boards" sidebar.
     */
    public void hideBoards() {
        mainCtrl.hideBoardsSidebar();
    }

    /**
     * Saves the state of joined boards to a file.
     */
    public void saveState() {
        try {
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(new FileOutputStream("saved"));
            objectOutputStream.writeObject(boards);
        } catch (IOException e) {
            log.error("Could not save state of joined boards");
        }
    }

    /**
     * Restores the state of joined boards from a file.
     */
    public void restoreState() {
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(new FileInputStream("saved"));
            Map<String, Map<Integer, Board>> boards = (Map<String, Map<Integer, Board>>) objectInputStream.readObject();
            for (Map.Entry<String, Map<Integer, Board>> boardEntry : boards.entrySet()) {
                boardEntry.getValue().forEach((key, value) -> addBoard(value, boardEntry.getKey()));
            }
        } catch (Exception e) {
            log.error("Could not restore state of joined boards");
        }
    }

    /**
     * Gets the ID of a board from its tree item.
     * @param selectedItem The tree item representing the board.
     * @return The ID of the board.
     */
    private int getIdFromTreeItem(TreeItem<String> selectedItem) {
        String[] splitName = selectedItem.getValue().split("#");
        return Integer.parseInt(splitName[splitName.length - 1]);
    }

    /**
     * Updates the name of a board in the view of the "My Boards" section of the application.
     * @param board  The board to update the name of.
     * @param server The server the board belongs to.
     */
    private void updateBoardNameInTreeView(Board board, String server) {
        boards.get(server).put(board.getId(), board);
        for (Object object : treeView.getRoot().getChildren()) {
            TreeItem<String> treeItem = (TreeItem) object;
            if (Objects.equals(treeItem.getValue(), server)) {
                for (Object item : treeItem.getChildren()) {
                    TreeItem<String> boardItem = (TreeItem) item;
                    if (Objects.equals(board.getId(), getIdFromTreeItem(boardItem))) {
                        boardItem.setValue(board.getName() + "#" + board.getId());
                        saveState();
                        return;
                    }
                }
            }
        }
    }

    /**
     * Adds a board to the view of the "My Boards" section of the application.
     *
     * @param board  The board to add to the view.
     * @param server The server the board belongs to.
     */
    private void addBoardToTreeView(Board board, String server) {
        boards.get(server).put(board.getId(), board);
        for (Object object : treeView.getRoot().getChildren()) {
            TreeItem<String> treeItem = (TreeItem) object;
            if (Objects.equals(treeItem.getValue(), server)) {
                TreeItem<String> boardItem = new TreeItem<>(board.getName() + "#" + board.getId());
                boardItem.setGraphic(new Label("X"));
                treeItem.getChildren().add(boardItem);
                saveState();
                return;
            }
        }
    }

    /**
     * Joins a board from its tree item in the view of the "My Boards" section of the application.
     *
     * @param selectedItem The tree item representing the board to join.
     */
    void joinBoardFromTreeView(TreeItem<String> selectedItem) {
        if (!Objects.isNull(selectedItem) && selectedItem.isLeaf() && !Objects.isNull(selectedItem.getParent())
                && !Objects.isNull(selectedItem.getValue()) && selectedItem.getValue().contains("#")) {
            String server = selectedItem.getParent().getValue();
            sessionData.setServer(server);
            int id = getIdFromTreeItem(selectedItem);
            Platform.runLater(treeView.getSelectionModel()::clearSelection);
            if (getFromServer.checkConnection()) {
                try {
                    getFromServer.getBoardById(id);
                    mainCtrl.showBoard(id, boards.get(server).get(id).getPassword());
                } catch (Exception e)   {
                    deleteBoardFromTreeView(selectedItem);
                }
            } else {
                selectedItem.getParent().setExpanded(false);
            }
        }
    }

    /**
     * Deletes a board from the view of the "My Boards" section of the application.
     *
     * @param selectedItem The tree item representing the board to delete.
     */
    public void deleteBoardFromTreeView(TreeItem<String> selectedItem) {
        if (selectedItem == null) return;
        boards.get(selectedItem.getParent().getValue()).remove(getIdFromTreeItem(selectedItem));
        selectedItem.getParent().getChildren().remove(selectedItem);
        saveState();
    }

    /**
     Finds the TreeItem corresponding to the given Board object in the provided TreeView.
     @param treeView the TreeView in which to search for the TreeItem
     @param board the Board object to search for in the TreeView
     @return the TreeItem corresponding to the given Board object, or null if it cannot be found
     */
    public TreeItem<String> findTreeItemForBoard(TreeView<String> treeView, Board board) {

        for (Object child : treeView.getRoot().getChildren()) {
            TreeItem<String> treeItem = (TreeItem) child;
            for (TreeItem<String> tItem : treeItem.getChildren()) {
                if (Objects.equals(tItem.getValue(), board.getName() + "#" + board.getId())) {
                    return tItem;
                }
            }
        }
        return null;
    }

    /**
     Returns the TreeView associated with this object.
     @return the TreeView associated with this object
     */
    public TreeView<String> getTreeView() {
        return treeView;
    }
}
