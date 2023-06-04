package client.scenes;

import client.Connection.GetFromServer;
import client.object.EditableCardLabel;
import client.object.ListBox;
import client.websocket.WebSocketClient;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import commons.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Singleton
public class BoardCtrl {
    private final Logger log = LoggerFactory.getLogger(BoardCtrl.class);
    private final MainCtrl mainCtrl;
    private final WebSocketClient webSocketClient;
    boolean isInTag = false;
    boolean isInColour = false;
    boolean isInExit = false;
    private Board board = new Board();
    private ColorPreset defaultPreset = null;
    private final EditableCardLabel[] movingCard = {null};
    @FXML
    private TextField boardTitle;
    @FXML
    private VBox tagArea;
    @FXML
    private VBox colourArea;
    @FXML
    private VBox exitArea;
    @FXML
    private VBox idArea;
    @FXML
    private ImageView addListImg;
    @FXML
    private ImageView tagImg;
    @FXML
    private Label tagLabel;
    @FXML
    private ImageView colourImg;
    @FXML
    private Label colourLabel;
    @FXML
    private ImageView exitImg;
    @FXML
    private Label exitLabel;
    @FXML
    private GridPane listGrid;
    @FXML
    private GridPane grid;
    @FXML
    private GridPane background;
    @FXML
    private ScrollPane listGridBackground;
    @FXML
    private Label iDLabel;
    @FXML
    private Button deleteBoardButton;

    @FXML
    private VBox helpArea;
    @FXML
    private Label helpIcon;
    @FXML
    private Label helpLabel;
    private CreateTagSceneCtrl createTagSceneCtrl;
    private final GetFromServer getFromServer;


    /***
     * Constructor.
     * @param mainCtrl
     * @param webSocketClientProvider provider for the webSocketClient. A provider was needed because of circular dependencies.
     */
    @Inject
    public BoardCtrl(MainCtrl mainCtrl, Provider<WebSocketClient> webSocketClientProvider,
                     CreateTagSceneCtrl createTagSceneCtrl, GetFromServer getFromServer) {
        this.mainCtrl = mainCtrl;
        this.webSocketClient = webSocketClientProvider.get();
        this.createTagSceneCtrl= createTagSceneCtrl;
        this.getFromServer = getFromServer;
    }

    /**
     Returns the MainCtrl instance associated with this controller.
     @return The MainCtrl instance associated with this controller.
     */
    public MainCtrl getMainCtrl() { return mainCtrl; }

    /**
     Sets the priority of a Card object within a given ToDoList based on the index it was dropped at.
     @param toDoList The ToDoList object that the Card was dropped in.
     @param index The index that the Card was dropped at.
     @param card The Card object that needs to have its priority updated.
     */
    private void setPriorityOfCard(ToDoList toDoList, int index, Card card) {
        int priority;

        if (Objects.equals(card.getListId(), toDoList.getId()) && toDoList.getCards().get(index).getPriority() > card.getPriority()) {
            ++index;
        }
        if (toDoList.getCards().size() == 0 || Objects.isNull(toDoList.getCards())) {
            priority = 1;
        } else if (index == toDoList.getCards().size()) {
            priority = toDoList.getCards().get(index - 1).getPriority() + 1;
        } else {
            priority = toDoList.getCards().get(index).getPriority();
        }
        card.setListId(toDoList.getId()); //updates the card's listId with the toDoList it was dropped in
        card.setPriority(priority);
    }

    /**

     Returns the current moving card label.
     @return The current moving card label.
     */
    public EditableCardLabel getMovingCard(){
        return movingCard[0];
    }

    /**
     Sets the current moving card label.
     @param newLabel The new EditableCardLabel object to set as the moving card label.
     */
    public void setMovingCard(EditableCardLabel newLabel){
        movingCard[0] = newLabel;
    }


    /***
     * This method takes a Board and displays its title, lists and cards.
     * @param board board to be displayed
     */
    public void displayUpdatedBoard(Board board) {
        this.board = board;
        if (board.getDefaultPreset() == null) this.defaultPreset = null;
        else
            this.defaultPreset = board.getColorPresets().stream()
                    .filter(cp -> Objects.equals(cp.getId(), board.getDefaultPreset()))
                    .findAny().orElse(null);
        refreshBoard();
        createTagSceneCtrl.setBoard(board);
    }

    /**
     Refreshes the current board display by updating its title, id, and to-do lists, and resetting its background and font styles.
     Additionally, sets listeners for the deleteBoardButton hover effect.
     Uses Platform.runLater() to execute this method on the JavaFX application thread.
     */
    public void refreshBoard() {
        boardTitle.getProperties().put("oldValue", board.getName());
        Platform.runLater(() -> {
            clearScene();
            this.boardTitle.setText(board.getName());
            this.iDLabel.setText("ID: " + board.getId());
            boardTitle.getProperties().put("oldValue", board.getName());
            board.getToDoLists().forEach(this::displayList);
            deleteBoardButton.setOnMouseEntered(event -> {
                deleteBoardButton.setStyle("-fx-background-color: darkred;");
            });
            deleteBoardButton.setOnMouseExited(event -> {
                deleteBoardButton.setStyle("-fx-background-color: red;");
            });
            updateBackground(board.getBackground());
            updateFont(board.getFont());
        });
    }

    /***
     * Displays a list along with all its cards.
     * @param toDoList list to be displayed
     */
    public void displayList(ToDoList toDoList) {
        String font;
        String bg;
        if (board.getListF() == null) {
            font = "-fx-text-fill: 'white';";
        } else {
            font = "-fx-text-fill: #" + board.getListF();
        }
        if (board.getListBG() == null) {
            bg = "-fx-background-color: 'FFA800';";

        } else {
            bg = "-fx-background-color: #" + board.getListBG();
        }

        ListBox listBody = new ListBox(this, toDoList, font, bg, board.getColorPresets(), defaultPreset, webSocketClient);
        listBody.addToGrid(listGrid, addListImg);
        listBody.addRemoveBtn(listGrid);
        listBody.setFocusTraversable(false); // Initially set focusTraversable to false
        listBody.addArrowFunctionality(listBody);
    }

    /**
     Updates the given ToDoList object in the webSocketClient with the DataType LIST.
     @param list the ToDoList object to be updated in the webSocketClient.
     */
    public void updateList(ToDoList list)   {
        webSocketClient.update(list, DataType.LIST);
    }

    /**
     Updates the background of the board with the given hex color. Sets the style of the background, listGrid, and
     listGridBackground.
     @param hexColor the hex color to be set as the background color.
     */
    public void updateBackground(String hexColor) {
        if (hexColor != null) {
            background.setStyle("-fx-border-color: black; -fx-border-width: 9;"
                    + " -fx-border-style: solid solid solid none; -fx-background-color: #" + hexColor);
            listGrid.setStyle("-fx-background-color: #" + hexColor);
            listGridBackground.setStyle("-fx-background: #" + hexColor + "; -fx-background-color: #" + hexColor);
        }
    }

    /**
     Updates the font color of the boardTitle with the given hex color.
     @param hexColor the hex color to be set as the font color of the boardTitle.
     */
    public void updateFont(String hexColor) {
        if (hexColor == null) {
            this.boardTitle.setStyle("-fx-font-size: 48; -fx-background-color: transparent; -fx-text-fill: white;");
        } else {
            this.boardTitle.setStyle("-fx-font-size: 48; -fx-background-color: transparent; -fx-text-fill: #" + hexColor);
        }
    }

    /**
     Sets the ID and password of the board.
     @param boardId the ID of the board to be set.
     @param password the password of the board to be set.
     */
    public void setBoardId(int boardId, String password) {
        this.board.setId(boardId);
        this.board.setPassword(password);
        clearScene(); // Clear the scene if a new board is to be joined
    }

    /**
     Initializes the add list image and sidebar.
     */
    public void initialize() {
        initAddListImg();
        initSidebar();

        boardTitle.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                Object oldValueObj = boardTitle.getProperties().get("oldValue");
                String oldName = oldValueObj != null ? oldValueObj.toString() : "";
                String newName = boardTitle.getText();

                if (!oldName.equals(newName)) {
                    log.info("List title changed to :" + newName);
                    board.setName(newName);

                    webSocketClient.update(board, DataType.BOARD);
                }
            }
        });
    }

    /*
    This method adds evens on key presses for "c" and "t"
    When c is pressed, it will take you to the customization scene
    and when t gets pressed it will take you to the add tag scene.
     */
    public void onShown() {
        //get the scene and activate a setOnKeyPressed event on it.
        Scene scene = boardTitle.getScene();
        scene.setOnKeyPressed(event -> {
            Node current = scene.getFocusOwner();

            //check if the key that was pressed was a "c", and if we are not typing in the board title currently.
            if (Objects.equals(event.getCode(), KeyCode.C) && !Objects.equals(current, boardTitle)) {
                log.info("Displaying the colours menu via shortcut");
                mainCtrl.showChooseBackGroundScene();
            }
            //check if the key that was pressed was a "t", and if we are not typing the board title currently.
            if (Objects.equals(event.getCode(), KeyCode.T) && !Objects.equals(current, boardTitle)) {
                log.info("Displaying the add tag menu via shortcut");
                mainCtrl.showCreateTagScene();
            }
        });

    }

    /**
     Initializes the scene by getting the board with the current ID from the server, adding it to the sidebar,
     and subscribing to the board's WebSocket. Displays the updated board and sets up event listeners for adding
     lists and hovering over sidebar icons.
     */
    public void initializeScene() {
        log.info("Getting board with id: '" + this.board.getId() + "'");
        try {
            webSocketClient.unsubscribe();
            Board board = getFromServer.getBoardById(this.board.getId());
            mainCtrl.addBoardToSidebar(board);
            this.board = board;
            log.info("Board name: " + board.getName());
            displayUpdatedBoard(board);
            Platform.runLater(this::onShown);
            webSocketClient.subscribeToBoard(this.board.getId());
        } catch (Exception e) {
            log.error(e.getMessage());
            exitBoard();
        }
    }

    /**
     Initializes the "Add List" button image and sets up event listeners for when the mouse enters and exits the button.
     */
    private void initAddListImg() {
        addListImg.setImage(new Image("graphics/AddListBtn_0.png"));
        addListImg.setOnMouseEntered(event -> addListImg.setImage(new Image("graphics/AddListBtn_1.png")));
        addListImg.setOnMouseExited(event -> addListImg.setImage(new Image("graphics/AddListBtn_0.png")));
    }

    /**
     Sets up event listeners for when the mouse enters the sidebar icons, changing the color of the icons and labels.
     */
    private void initSidebarHovered() {
        tagArea.setOnMouseEntered(event -> {
            tagImg.setImage(new Image("graphics/side/Tag_1.png"));
            tagLabel.setTextFill(Color.WHITE);
            isInTag = true;
        });
        colourArea.setOnMouseEntered(event -> {
            colourImg.setImage(new Image("graphics/side/Colour_1.png"));
            colourLabel.setTextFill(Color.WHITE);
            isInColour = true;
        });
        exitArea.setOnMouseEntered(event -> {
            exitImg.setImage(new Image("graphics/side/Exit_1.png"));
            exitLabel.setTextFill(Color.WHITE);
            isInExit = true;
        });
        helpArea.setOnMouseEntered(event -> {
            helpIcon.setTextFill(Color.WHITE);
            helpLabel.setTextFill(Color.WHITE);
        });
    }

    /**

     Initializes the sidebar exited event for the tag, colour, exit, and help areas. Resets the corresponding images and labels
     and sets the corresponding booleans to false.
     */
    private void initSidebarExited() {
        tagArea.setOnMouseExited(event -> {
            tagImg.setImage(new Image("graphics/side/Tag_0.png"));
            tagLabel.setTextFill(Color.BLACK);
            isInTag = false;
        });
        colourArea.setOnMouseExited(event -> {
            colourImg.setImage(new Image("graphics/side/Colour_0.png"));
            colourLabel.setTextFill(Color.BLACK);
            isInColour = false;
        });
        exitArea.setOnMouseExited(event -> {
            exitImg.setImage(new Image("graphics/side/Exit_0.png"));
            exitLabel.setTextFill(Color.BLACK);
            isInExit = false;
        });
        helpArea.setOnMouseExited(event -> {
            helpIcon.setTextFill(Color.BLACK);
            helpLabel.setTextFill(Color.BLACK);
        });
    }

    /**
     Initializes the sidebar clicked event for the tag, colour, exit, and help areas. Sets the corresponding images and labels
     to their clicked state and changes their text color. Resets them if they are not clicked.
     */
    private void initSidebarClicked() {
        tagArea.setOnMousePressed(event -> {
            tagImg.setImage(new Image("graphics/side/Tag_2.png"));
            tagLabel.setTextFill(Color.GRAY);
        });
        tagArea.setOnMouseReleased(event -> {
            if (!isInTag) return;
            tagImg.setImage(new Image("graphics/side/Tag_1.png"));
            tagLabel.setTextFill(Color.WHITE);
        });
        colourArea.setOnMousePressed(event -> {
            colourImg.setImage(new Image("graphics/side/Colour_2.png"));
            colourLabel.setTextFill(Color.GRAY);
        });
        colourArea.setOnMouseReleased(event -> {
            if (!isInColour) return;
            colourImg.setImage(new Image("graphics/side/Colour_1.png"));
            colourLabel.setTextFill(Color.WHITE);
        });
        exitArea.setOnMousePressed(event -> {
            exitImg.setImage(new Image("graphics/side/Exit_2.png"));
            exitLabel.setTextFill(Color.GRAY);
        });
        exitArea.setOnMouseReleased(event -> {
            if (!isInExit) return;
            exitImg.setImage(new Image("graphics/side/Exit_1.png"));
            exitLabel.setTextFill(Color.WHITE);
        });
        helpArea.setOnMousePressed(event -> {
            helpIcon.setTextFill(Color.GRAY);
            helpLabel.setTextFill(Color.GRAY);
        });
        helpArea.setOnMouseReleased(event -> {
            helpIcon.setTextFill(Color.WHITE);
            helpLabel.setTextFill(Color.WHITE);
        });
    }

    /**
     Initializes the sidebar by setting up the default images for the tag, color, and exit buttons and
     adding listeners for when the buttons are hovered over, clicked, and released.
     */
    private void initSidebar() {
        tagImg.setImage(new Image("graphics/side/Tag_0.png"));
        colourImg.setImage(new Image("graphics/side/Colour_0.png"));
        exitImg.setImage(new Image("graphics/side/Exit_0.png"));

        initSidebarHovered();
        initSidebarExited();
        initSidebarClicked();
    }

    /**
     Displays a list of tags for the current board.
     @throws IOException if an error occurs while showing the tag list
     */
    public void showTagList() throws IOException {
        log.info("showing list of tags!");
        mainCtrl.showTagList();
    }

    /**
     Displays the color menu for the current board.
     */
    public void showColourMenu() {
        log.info("Displaying the colours menu");
        mainCtrl.showChooseBackGroundScene();

    }

    /**
     Exits the current board and returns to the start scene.
     */
    public void exitBoard() {
        log.info("Exiting board...");
        Platform.runLater(mainCtrl::showStart);
        webSocketClient.unsubscribe();
    }

    /**
     Displays the help scene.
     */
    public void showHelpScene() {
        log.info("Displaying help scene");
        mainCtrl.showHelpScene();
    }

    /**
     * Makes necessary updates to database
     * after the motion of dragging and dropping is done.
     */
    public void updateDatabaseAfterDrag(ToDoList toDoList, DragEvent event, int index) {

        Integer cardId = Integer.parseInt(event.getDragboard().getString());
        List<ToDoList> lists = board.getToDoLists();

        //finds the card object that was dragged and the list it's in
        for (ToDoList todo : lists) {
            List<Card> cards = todo.getCards();
            for (Card current : cards) {
                if (current.getId().equals(cardId)) {
                    setPriorityOfCard(toDoList, index, current);
                    webSocketClient.update(current, DataType.CARD);
                    return;
                }
            }
        }
    }

    /**
     * After creating the list the board will be updated
     * so no need to handle anything for the UI here anymore
     */
    public void addList() {
        if (Objects.isNull(this.board.getToDoLists())) {
            this.board.setToDoLists(new ArrayList<>());
        }
        ToDoList list = new ToDoList();
        list.setTitle("My New List");
        list.setBoardId(board.getId());
        webSocketClient.create(list, DataType.LIST);
    }

    /**
     Adds a new empty card to the specified list body using the default color preset.
     @param listBody the list box to add the new card to
     */
    public void quickCreateCard(ListBox listBody) {
        listBody.addCardToList(new Card(), true, board.getColorPresets(), defaultPreset);
    }

    /**
     Displays the card creation dialog to allow the user to create a new card.
     The card will be added to the list with the specified ID in the current board.
     @param listBody the list body where the new card will be added to
     @param listId the ID of the list where the new card will be added to
     */
    public void createCard(VBox listBody, Integer listId) {
        Platform.runLater(() -> mainCtrl.createCard(listBody, listId, board.getId()));
    }

    /**
     * Sends an update for the board title if Enter was pressed within the title textbox.
     *
     * @param keyEvent contains information about the pressed key.
     */
    public void editBoardTitle(KeyEvent keyEvent) {
        if (Objects.equals("Enter", keyEvent.getCode().getName()) && !boardTitle.getText().equals(boardTitle.getProperties().get("oldValue"))) {
            log.info("new boardTitle: " + boardTitle.getText());
            board.setName(boardTitle.getText());
            boardTitle.getProperties().put("oldValue", boardTitle.getText());
            webSocketClient.update(board, DataType.BOARD);
        }
        if (Objects.equals("Enter", keyEvent.getCode().getName())) {
            log.info("removed focus from boardTitle");
            boardTitle.getParent().requestFocus();
        }
    }

    /**
     Clears the current scene by resetting the board title to "My Board", removing all previous lists except for the
     "Add List" button, and applying positioning to the "Add List" button.
     */
    private void clearScene() {
        boardTitle.setText("My Board"); //Reset title to default in case of errors in getting the new board name
        //Remove the previous board's lists but preserve the 'Add List' button
        listGrid.getChildren().removeIf(x -> x.getClass() != ImageView.class || GridPane.getRowIndex(x) == 2);
        //Apply positioning to the 'Add List' button
        GridPane.setColumnIndex(addListImg, 0);
        GridPane.setRowIndex(addListImg, 0);
        GridPane.setRowSpan(addListImg, GridPane.REMAINING);
    }

    /**
     Opens the card description view for the specified card.
     @param card the card to edit
     */
    public void editCard(Card card) {
        mainCtrl.cardDescription(card, board.getId());
    }

    /**
     Updates the specified card on the server.
     @param card the card to update
     */
    public void updateCard(Card card) {
        webSocketClient.update(card, DataType.CARD);
    }

    /**
     Creates the specified card on the server.
     @param card the card to create
     */
    public void createCard(Card card) {
        webSocketClient.create(card, DataType.CARD);
    }

    /**
     Deletes the list with the specified ID from the server.
     @param id the ID of the list to delete
     */
    public void deleteList(Integer id) {
        webSocketClient.delete(id, DataType.LIST);
    }

    /**
     Returns the current board.
     @return the current board
     */
    public Board getBoard() {
        return board;
    }

    /**
     * Deletes a board from the database, and exits the board.
     **/
    public void deleteBoard() {
        webSocketClient.delete(board.getId(), DataType.BOARD);
    }

    /**
     Copies the board ID to the clipboard.
     The board ID is retrieved from the current board and added to the clipboard.
     */
    public void copyID() {
        String boardID = board.getId().toString();

        log.info("putting the id: " + boardID + "in clipboard.");
        Clipboard clipboard = Clipboard.getSystemClipboard();
        ClipboardContent clipboardContent = new ClipboardContent();
        clipboardContent.putString(boardID);
        clipboard.setContent(clipboardContent);
    }

    /**
     Deletes the current board from the tree view in the 'My Boards' section.
     The board is retrieved from the current context and its corresponding tree item is deleted from the tree view.
     */
    public void deleteFromTree() {
        Platform.runLater(() -> {
            MyBoardsCtrl boardsCtrl = mainCtrl.getBoardsCtrl();
            TreeItem<String> deletion = boardsCtrl.findTreeItemForBoard(boardsCtrl.getTreeView(), getBoard());
            boardsCtrl.deleteBoardFromTreeView(deletion);
        });
    }

    /**
     Deletes a card with the given ID.
     The ID is used to retrieve the corresponding card from the current board and then send a delete request to the server.
     @param id the ID of the card to be deleted.
     */
    public void deleteCard(int id) {
        log.info("deleting card with id: " + id);
        webSocketClient.delete(id, DataType.CARD);
    }
}
