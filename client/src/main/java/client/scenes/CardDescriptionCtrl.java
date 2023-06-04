package client.scenes;

import client.object.SubtaskItem;
import client.websocket.CardWebSocketClient;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import commons.*;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Singleton
public class CardDescriptionCtrl {
    private final Logger log = LoggerFactory.getLogger(CardDescriptionCtrl.class);
    private final MainCtrl mainCtrl;
    private final CardWebSocketClient webSocketClient;
    @FXML
    private TextField title;

    @FXML
    private TextArea description;

    @FXML
    private ScrollPane tasks;

    @FXML
    private VBox taskBody;

    private Card card;
    private final BoardCtrl boardCtrl;
    private final ColorPreset defaultPresetChoice = new ColorPreset();

    @FXML
    private Label invalidTITLEMessage;

    @FXML
    private Label invalidDescriptionMessage;

    @FXML
    private Label invalidSUBTASKMessage;

    @FXML
    private Label tooManyTagsMessage;

    @FXML
    private Button deleteButton;

    @FXML
    private Button doneButton;

    @FXML
    private Button addTaskButton;

    @FXML
    private GridPane mainGrid;

    @FXML
    private VBox tagList;


    @Inject
    public CardDescriptionCtrl(MainCtrl mainCtrl, BoardCtrl boardCtrl, Provider<CardWebSocketClient> webSocketClientProvider) {
        this.mainCtrl = mainCtrl;
        this.webSocketClient = webSocketClientProvider.get();
        this.boardCtrl = boardCtrl;
    }
    /**
     * Gets called once at the creation of the Scene
     */
    public void initialize() {
        Font luloFont = Font.loadFont("fonts/Lulo.ttf", 14);
        deleteButton.setFont(luloFont);
        deleteButton.setOnMouseEntered(event -> {deleteButton.setStyle("-fx-background-color: '8b0000'");});
        deleteButton.setOnMouseExited(event -> {deleteButton.setStyle("-fx-background-color: red");});
        doneButton.setFont(luloFont);
        addTaskButton.setFont(luloFont);
        defaultPresetChoice.setBackground("-1");
        defaultPresetChoice.setFont("-1");
        defaultPresetChoice.setName("DEFAULT");
        defaultPresetChoice.setId(-1);
    }


    /**
     * Clears the scene's fields and resets all error messages
     */
    private void clearScene() {
        this.title.setText("");
        this.description.setText("");
        this.taskBody.getChildren().remove(0, taskBody.getChildren().size());
        resetErrorMessages();
    }

    /**
     * Loads a card into the scene.
     * Called whenever the websocket receives a new Card.
     *
     * @param card The card being loaded
     */
    public void displayUpdatedCard(Card card) {
        this.card = card;
        Platform.runLater(() -> {
            clearScene();
            if (card.getId() == -1)
                done();
            this.title.setText(card.getTitle());
            this.title.getProperties().put("cardObject", card);
            this.description.setText(card.getDescription());
            this.description.getProperties().put("cardObject", card);
            initPresetDropDown();
            if (card.getSubtasks() != null)
                card.getSubtasks().forEach(this::addToList);
            dragDrop();
        });
    }

    /**
     * Initializes the scene with the card details and its subtasks.
     *
     * @param card a Card object that represents the card to be displayed.
     */
    public void initializeScene(Card card, int boardId) {
        log.info("Getting card with id: '" + card.getId() + "'");
        Platform.runLater(() -> {
            try {
                Board board = boardCtrl.getBoard();
                clearScene();

                this.card = card;

                initializeTitle(card);
                initializeDescription(card);
                webSocketClient.subscribeToCard(boardId, card.getId());
                initializeTags(board);

                if (card.getSubtasks() != null){
                    card.getSubtasks().forEach(this::addToList);
                }
                initPresetDropDown();
                dragDrop();
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        });

        setKeyPressEvent();
    }

    /*
        add a lister to the scene. It checks for escape presses and checks if title and description are not in focus.
        if this is the case you get send back to the board scene.
        if escape gets pressed when title is in focus, then title will lose focus
        and same goes for description.
    */
    private void setKeyPressEvent() {
        Scene scene = title.getScene();
        scene.setOnKeyPressed(event -> {
            Node current = scene.getFocusOwner();
            //if we aren't currently typing in the title, and not in the description.
            //if then esc gets pressed we go back to the board scene.
            if (!Objects.equals(current, title) && !Objects.equals(current, description)
                    && event.getCode() == KeyCode.ESCAPE) {
                done();
            }

            //if we are currently typing in title, and either esc or enter gets pressed.
            //then the text box will lose focus.
            else if (Objects.equals(current, title)
                    && (event.getCode() == KeyCode.ESCAPE || event.getCode() == KeyCode.ENTER)) {
                title.getParent().requestFocus();
            }

            //if we are currently typing in description, and either esc or enter gets pressed.
            //then the text box will lose focus.
            else if (Objects.equals(current, description)
                    && (event.getCode() == KeyCode.ESCAPE || event.getCode() == KeyCode.ENTER)) {
                description.getParent().requestFocus();
            }
        });
    }

    /**
     This method initializes the tags of a board.
     If the board doesn't have any tags, an empty ArrayList is created.
     Then, for each tag, a label is created with the tag's title and color.
     If the label is already assigned to the card, it will be displayed with a different color (in this case, #FFA800).
     When a tag is clicked, if it is already assigned to the card, it will be removed and deleted from the server.
     If it is not assigned to the card and the card has less than 15 tags, it will be added and saved to the server.
     If the card already has 15 tags, the tooManyTagsMessage is displayed.
     @param board The board to initialize the tags for
     */
    public void initializeTags(Board board){
        if (board.getTags()==null){board.setTags(new ArrayList<>());}
        List<Tag> listOfTags = board.getTags();
        tagList.getChildren().removeAll();
        for(Tag t: listOfTags) {

            Label tag = new Label(t.getTitle());

            tag.getStylesheets().add("stylesheet.css");
            tag.getStyleClass().add("CardTag");
            String border = "-fx-border-color:#" +  t.getColor() +";";
            if (containsTag(card,t)){
                tag.setStyle(border + " -fx-text-fill: #FFA800");
            }
            else{
                tag.setStyle(border + " -fx-text-fill: black");
            }
            System.out.println(border);
            tag.setOnMouseClicked(e -> {
                if (containsTag(card,t)){
                    card.getTags().remove(t);
                    CardTag cardTag = new  CardTag(card.getId(),t.getId());
                    webSocketClient.deleteTag(cardTag,DataType.TAG);
                    tag.setStyle(border+"-fx-text-fill: black");
                }
                else{
                    if (card.getTags().size()==15){
                        tooManyTagsMessage.setVisible(true);

                    } else{
                        card.getTags().add(t);
                        CardTag cardTag = new CardTag(card.getId(), t.getId());
                        webSocketClient.addTag(cardTag, DataType.TAG);
                        tag.setStyle(border + "-fx-text-fill: #FFA800");
                    }
                }
            });
            tagList.getChildren().add(tag);
        }
    }

    /**
     This method checks if a tag is already assigned to a card
     @param card The card to check if it contains the tag
     @param tag The tag to check if it is in the card's list of tags
     @return true if the card already contains the tag, false otherwise
     */
    public boolean containsTag(Card card, Tag tag){
        for (Tag t: card.getTags()){
            if (t.getId()==tag.getId()){
                return true;
            }
        }
        return false;
    }


    /** Generate a new drop down to house presets
     */
    private void initPresetDropDown() {
        ChoiceBox<ColorPreset> presetChoiceBox = new ChoiceBox<>();
        presetChoiceBox.setPrefWidth(150);
        mainGrid.add(presetChoiceBox, 4, 0);

        //Create an ObservableList for choicebox options
        ObservableList<ColorPreset> options = FXCollections.observableArrayList();
        options.add(defaultPresetChoice); //add the default preset choice

        //defaults to the 'DEFAULT' preset if no preset is found linked to the card
        ColorPreset setPreset = defaultPresetChoice;
        for (ColorPreset preset : boardCtrl.getBoard().getColorPresets()) {
            options.add(preset);
            if(Objects.equals(preset.getId(), card.getPreset())) {
                setPreset = preset;
            }
        };
        presetChoiceBox.setItems(options); //Set the generated options to the choicebox
        presetChoiceBox.setValue(setPreset); //Set the preset dropdown's value

        presetChoiceBox.setOnAction(event -> { //Event listener to update card if preset value changes
            ColorPreset cp = (ColorPreset) presetChoiceBox.getValue();
            log.info(cp.getId().toString());
            card.setPreset(cp.getId());
            webSocketClient.update(card, DataType.CARD);
        });
    }

    /**
     * Initializes the title field using a given card's title
     *
     * @param card The card whose title is to be displayed
     */
    private void initializeTitle(Card card) {
        this.title.setText(card.getTitle());
        this.title.getProperties().put("cardObject", card);

        title.setOnKeyReleased(e -> {
            if (e.getCode().equals(KeyCode.ENTER) || e.getCode().equals(KeyCode.ESCAPE)) {
                title.getParent().requestFocus();
            }
        });

        title.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { //focus out
                if (!Objects.equals(title.getProperties().get("oldTitle"), title.getText()) && validateFields()) {
                    Card c = (Card) title.getProperties().get("cardObject");
                    c.setTitle(title.getText());
                    webSocketClient.update(c, DataType.CARD);
                }
            } else {
                title.getProperties().put("oldTitle", title.getText());
            }
        });
    }

    /**
     * Initializes the description field using a given card's description
     *
     * @param card The card whose description is to be displayed
     */
    private void initializeDescription(Card card) {
        this.description.setText((card.getDescription()));
        this.description.getProperties().put("cardObject", card);

        description.setOnKeyReleased(e -> {
            if (e.getCode().equals(KeyCode.ENTER) || e.getCode().equals(KeyCode.ESCAPE)) {
                description.getParent().requestFocus();
            }
        });

        description.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { //focus out
                if (!Objects.equals(description.getProperties().get("oldContent"), description.getText()) && validateFields()) {
                    Card c = (Card) description.getProperties().get("cardObject");
                    c.setDescription(description.getText());
                    webSocketClient.update(c, DataType.CARD);
                }
            } else {
                description.getProperties().put("oldContent", description.getText());
            }
        });
    }


    /**
     Allows the user to drag and drop subtasks within a card. When a subtask is dragged and dropped, this method removes
     the dragged subtask from its previous position and inserts it into its new position within the list of subtasks
     for the current card.
     */
    private void dragDrop() {
        taskBody.setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.MOVE);
            event.consume();
        });
        taskBody.setOnDragDropped(event -> {
            log.info("Drag dropped in the card with id: " + card.getId());

            taskBody.getChildren().remove((Node) event.getGestureSource());
            int childrenSize = taskBody.getChildren().size();
            int index = 0;

            if (childrenSize > 0) {
                for (int i = 0; i < childrenSize; i++) {
                    Node currentLabel = taskBody.getChildren().get(i);
                    Bounds boundsInScene = currentLabel.localToScene(currentLabel.getBoundsInLocal());
                    double midOfCurrentLabel = 0.5d * (boundsInScene.getMaxY() + boundsInScene.getMinY());

                    if (event.getSceneY() > midOfCurrentLabel) {
                        index += 1;
                    }
                }
            }
            taskBody.getChildren().add(index, (Node) event.getGestureSource());
            updateDBAfterDrag(event, index);
        });
    }


    /**
     Updates the database after a subtask has been dragged and dropped. The method determines the new priority of the
     subtask and updates it in the database.
     @param event The DragEvent containing information about the drag and drop operation.
     @param index The index of the subtask in the list of subtasks for the current card.
     */
    private void updateDBAfterDrag(DragEvent event, int index) {
        Integer subtaskId = Integer.parseInt(event.getDragboard().getString());
        Subtask subtask = null;
        for (Subtask current : card.getSubtasks()) {
            if (current.getId().equals(subtaskId)) {
                subtask = current;
                break;
            }
        }
        int priority;
        if (card.getSubtasks().get(index).getPriority() > subtask.getPriority()) {
            ++index;
        }
        if (card.getSubtasks().size() == 0 || Objects.isNull(card.getSubtasks())) {
            priority = 1;
        } else if (index >= card.getSubtasks().size()) {
            priority = card.getSubtasks().get(card.getSubtasks().size() - 1).getPriority() + 1;
        } else {
            priority = card.getSubtasks().get(index).getPriority();
        }
        subtask.setPriority(priority);

        updateSubtask(subtask);
    }

    /**
     Adds a new SubtaskItem to the list of subtasks displayed on the task body pane.
     @param subtask The subtask to be added to the list as a SubtaskItem.
     */
    private void addToList(Subtask subtask) {
        SubtaskItem newTask = new SubtaskItem(subtask, this);
        newTask.maxWidth(Double.MAX_VALUE);
        int listSize = taskBody.getChildren().size() + 1;
        taskBody.getChildren().add(listSize > 1 ? listSize - 1 : 0, newTask);

        newTask.setOnKeyReleased(e -> {
            if (e.getCode().equals(KeyCode.ENTER) || e.getCode().equals(KeyCode.ESCAPE)) {
                newTask.getParent().requestFocus();
            }
        });

        newTask.getTextField().focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { //Check if focused out
                //Check if title had changes and those changes are valid
                if (!Objects.equals(newTask.getProperties().get("oldTitle"), newTask.getText()) && validateFields()) {
                    Subtask st = newTask.getSubtask();
                    st.setTitle(newTask.getText());
                    updateSubtask(subtask);
                }
            } else {
                newTask.getProperties().put("oldTitle", newTask.getText());
            }
        });

        newTask.getTextField().setOnDragDetected(event -> {
            Dragboard db = newTask.startDragAndDrop(TransferMode.ANY);
            ClipboardContent content = new ClipboardContent();
            content.putString(Integer.toString(subtask.getId()));
            db.setContent(content);
            event.consume();
        });
    }

    /**
     Sends an update message to the server to update the given subtask.
     @param subtask The subtask to be updated.
     */
    public void updateSubtask(Subtask subtask)  {
        webSocketClient.update(subtask, DataType.SUBTASK);
    }

    /**
     * Updates the card and its subtasks when done button is clicked.
     * Send requests to the server using the WebSocket client.
     * Displays error messages if there are special characters in the title, description, or any subtask title.
     * Sets the maximum length of the  fields to 29 characters.
     */
    public void done() {
        if (validateFields()) { //no need to update since all updates are immediately synchronized
            webSocketClient.unsubscribe();
            clearScene();
            mainCtrl.showBoard();
            tagList.getChildren().remove(0, tagList.getChildren().size());
        }
    }

    /**
     * Validates title, description, and subtasks fields.
     * WILL show error messages in the UI when called, if they are found.
     *
     * @return false if fields are invalid, true otherwise
     */
    private boolean validateFields() {

        resetErrorMessages();

        boolean isTitleInvalid = false;
        boolean isDescriptionInvalid = false;
        boolean isSubtaskInvalid = false;


        // Check if title contains special characters or is too long
        if (title.getText() != null && (title.getText().matches(".*[^a-zA-Z0-9 ].*") || title.getText().length() > 29)) {
            invalidTITLEMessage.setVisible(true);
            isTitleInvalid = true;
        }


        // Check if description contains special characters or is too long
        if (description.getText() != null && (description.getText().matches(".*[^a-zA-Z0-9 ].*") || description.getText().length() > 29)) {
            invalidDescriptionMessage.setVisible(true);
            isDescriptionInvalid = true;
        }

        // Check if any subtask title contains special characters or is too long
        for (Node node : taskBody.getChildren()) {
            SubtaskItem textField = (SubtaskItem) node;
            String subtaskTitle = textField.getText();
            if (subtaskTitle == null) continue; //skip if null
            if (subtaskTitle.matches(".*[^a-zA-Z0-9 ].*") || subtaskTitle.length() > 29) {
                invalidSUBTASKMessage.setVisible(true);
                isSubtaskInvalid = true;
                break;
            }
        }

        return !isDescriptionInvalid && !isSubtaskInvalid && !isTitleInvalid;
    }

    /**
     * Hide all error messages
     */
    private void resetErrorMessages() {
        //reset messages
        invalidDescriptionMessage.setVisible(false);
        invalidTITLEMessage.setVisible(false);
        invalidSUBTASKMessage.setVisible(false);
        tooManyTagsMessage.setVisible(false);
    }


    /**
     * Adds a new subtask to the card and sends a request to the server using the WebSocket client.
     */
    public void addTask() {
        Subtask subtask = new Subtask();
        subtask.setTitle("New Task");
        subtask.setCardId(card.getId());
        webSocketClient.create(subtask, DataType.SUBTASK);
    }

    /**
     * Deletes the card and navigates to the board view.
     * Sends a request to the server using the WebSocket client.
     */
    public void deleteCard() {
        resetErrorMessages();
        webSocketClient.delete(card.getId(), DataType.CARD);
        done();
        tagList.getChildren().remove(0, tagList.getChildren().size());
    }

    /**
     Sends a delete message to the server to delete the given subtask.
     @param id The subtask id to be deleted.
     */
    public void deleteSubtask(int id) {
        webSocketClient.delete(id, DataType.SUBTASK);
    }

}
