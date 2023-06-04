package client.object;

import client.scenes.BoardCtrl;
import client.websocket.WebSocketClient;
import commons.Card;
import commons.ColorPreset;
import commons.DataType;
import commons.ToDoList;
import javafx.event.EventHandler;
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;

public class ListBox extends VBox {
    private final Logger log = LoggerFactory.getLogger(ListBox.class);
    private final BoardCtrl boardCtrl;
    private ToDoList list;
    private TextField listTitle;
    private final WebSocketClient webSocketClient;
    private final boolean[] hover = {false};
    private final boolean[] shift = {false};

    //this boolean fixes a weird bug,
    // when you hover over a lot of different cards and then press enter it took a few seconds before it opened the next scene
    private boolean onetime = false;
    private EditableCardLabel highlightedLabel;

    public ListBox(BoardCtrl boardCtrl, ToDoList list, String font, String bg,
                   List<ColorPreset> colorPresets, ColorPreset defaultPreset,
                   WebSocketClient webSocketClient) {
        this.boardCtrl = boardCtrl;
        this.list = list;
        this.listTitle = new TextField(list.getTitle());
        this.list.getCards().forEach(card -> addCardToList(card, false, colorPresets, defaultPreset));
        this.listTitle.setStyle(font);
        this.setStyle(bg);
        this.addListTitleEvents();
        this.addBtnToList();
        this.enableDragAndDropCards();
        this.applyListStyling();
        this.webSocketClient = webSocketClient;
    }

    private void addListTitleEvents()    {
        listTitle.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) {
                String newTitle = listTitle.getText();
                if (!Objects.equals(list.getTitle(), newTitle)) {
                    log.info("List title changed to :" + newTitle);
                    list.setTitle(newTitle);
                    boardCtrl.updateList(list);
                }
            }
        });

        listTitle.setOnAction(event -> {
            log.info("Pressed Enter");
            listTitle.getParent().requestFocus();
        });
    }

    private void addBtnToList() {
        ImageView moreImg = new ImageView("graphics/MoreBtn_0.png");
        moreImg.setPreserveRatio(true);
        moreImg.setFitWidth(50);
        moreImg.setPickOnBounds(true);

        moreImg.setUserData(0); //number of consecutive clicks
        moreImg.setOnMouseClicked(event -> {
            if (event.getClickCount() >= 2) {
                moreImg.setUserData(2);
                boardCtrl.quickCreateCard(this);
                moreImg.setUserData(0);
            } else if (event.getClickCount() == 1) {
                moreImg.setUserData(1);
                new Thread(() -> {
                    try {
                        Thread.sleep(300);
                        if (Objects.equals(moreImg.getUserData(), 1))
                            boardCtrl.createCard(this, list.getId());
                    } catch (InterruptedException ignored) {
                    }
                }).start();
            }
            this.applyListStyling();
        });
        moreImg.setOnMouseEntered(event -> moreImg.setImage(new Image("graphics/MoreBtn_1.png")));
        moreImg.setOnMouseExited(event -> moreImg.setImage(new Image("graphics/MoreBtn_0.png")));
        this.getChildren().add(moreImg);
        this.setAlignment(Pos.CENTER);
    }

    private void applyListStyling() {
        //apply styling to the newly-generated objects
        listTitle.getStylesheets().add("stylesheet.css");
        this.getStylesheets().add("stylesheet.css");
        listTitle.getStyleClass().add("listTitle");
        this.getStyleClass().add("listBody");

        listTitle.setMinWidth(249);
        listTitle.setMaxWidth(249);
        listTitle.setMaxHeight(Double.MAX_VALUE);
        listTitle.setAlignment(Pos.CENTER);
        listTitle.setTranslateY(2);
        int bodyWidth = 250 - (2 * 9)-1;
        this.setMinWidth(bodyWidth);
        this.setMaxWidth(bodyWidth);
    }

    /**
     * Sets the drag and drop behaviour for new ToDoList
     */
    public void enableDragAndDropCards() {
        this.setOnDragOver(event -> {
            event.acceptTransferModes(TransferMode.MOVE);
            event.consume();
        });
        this.setOnDragDropped(event -> {
            log.info("Drag dropped in the list with id: " + list.getId());
            this.getChildren().remove((Node) event.getGestureSource());
            int childrenSize = this.getChildren().size();
            int index = 0;

            if (childrenSize > 1) {
                for (int i = 0; i < childrenSize - 1; i++) {
                    Node currentLabel = this.getChildren().get(i);
                    Bounds boundsInScene = currentLabel.localToScene(currentLabel.getBoundsInLocal());
                    double midOfCurrentLabel = 0.5d * (boundsInScene.getMaxY() + boundsInScene.getMinY());

                    if (event.getSceneY() > midOfCurrentLabel) {
                        index += 1;
                    }
                }
            }
            this.getChildren().add(index, (Node) event.getGestureSource());

            boardCtrl.updateDatabaseAfterDrag(list, event, index);
        });
    }

    private EditableCardLabel applyCardColours(Card card, List<ColorPreset> colorPresets, ColorPreset defaultPreset) {
        ColorPreset cardPreset = defaultPreset;
        if(defaultPreset == null) {
            cardPreset = new ColorPreset();
            cardPreset.setId(-1);
            cardPreset.setName("DEFAULT");
            cardPreset.setFont("000000");
            cardPreset.setBackground("ffffff");
        }

        cardPreset = colorPresets.stream().filter(
                        cp -> Objects.equals(cp.getId(), card.getPreset()))
                .findFirst().orElse(null) != null ?
                colorPresets.stream().filter(
                                cp -> Objects.equals(cp.getId(), card.getPreset()))
                        .findFirst().orElse(null)
                : cardPreset;

        EditableCardLabel newCard = new EditableCardLabel(card.getTitle(), card, boardCtrl, cardPreset.getBackground(), cardPreset.getFont());
        newCard.getProperties().put("cardPresetObject", cardPreset);
        return newCard;
    }

    public void addCardToList(Card card, boolean unsavedCard, List<ColorPreset> colorPresets, ColorPreset defaultPreset) {
        card.setListId(list.getId());
        EditableCardLabel newCard = applyCardColours(card, colorPresets, defaultPreset);
        int listSize = this.getChildren().size() + 1;
        if (unsavedCard) {
            this.getChildren().add(listSize - 2, newCard);
        } else {
            this.getChildren().add(listSize > 1 ? listSize - 1 : 0, newCard);
        }

        if (unsavedCard)    {
            newCard.toTextField();
        }

        addHoverProperty(newCard);
    }


    public EditableCardLabel getHighlightedLabel(){
        return highlightedLabel;
    }
    public void setHighlightedLabel(EditableCardLabel label){
        onetime = true;
        EditableCardLabel old = highlightedLabel;
        highlightedLabel = label;
        if(highlightedLabel != null){
            highlight(highlightedLabel);
        }
        if(old != null && old != highlightedLabel){
            deHighlight(old);
        }
    }




    public void highlight(EditableCardLabel newCard){
        newCard.requestFocus();
        newCard.setBackground(new Background(new BackgroundFill(Color.color(154/255.0,153/255.0,153/255.0, 0.66), CornerRadii.EMPTY, Insets.EMPTY)));

        // Add the event filter for the: enter, delete and backspace keys.
        EventHandler<KeyEvent> keyListener = e -> {

            //if the key press is an enter, then we enter the card details.
            if (e.getCode() == KeyCode.ENTER && onetime && !newCard.isEditing()) {
                log.info("entered card details using shortcut");
                boardCtrl.editCard(newCard.getCard());
                onetime = false;

            }
            //if they key press is a back-space or delete. then we delete the card.
            else if(!newCard.isEditing() && (Objects.equals(e.getCode(), KeyCode.BACK_SPACE) ||
                    Objects.equals(e.getCode(), KeyCode.DELETE)) && onetime){
                log.info("delete card via shortcut");
                boardCtrl.deleteCard(newCard.getCard().getId());
                onetime = false;
            }
        };

        //add those key events to newcard, but only if it isn't just created with quick add.
        if(newCard.getCard().getTitle() != null) {
            newCard.addEventFilter(KeyEvent.KEY_PRESSED, keyListener);
        }


        //add an event filter to newcard, when e is pressed when a cord is hovered we edit the title.
        newCard.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            //check if the keypress is 'e'
            if (e.getCode() == KeyCode.E && !newCard.isEditing()) {
                log.info("edit card title with shortcut");
                newCard.toTextField();

                //Remove the event filter for the "other" keys, since we don't want them
                //interfering when we are editing the title.
                newCard.removeEventFilter(KeyEvent.KEY_PRESSED, keyListener);
            }
        });
    }


    private void deHighlight(EditableCardLabel newCard){
        ColorPreset cardPreset = (ColorPreset) newCard.getProperties().get("cardPresetObject");
        Background oldBackground = new Background(new BackgroundFill(Color.web(cardPreset.getBackground()),
                CornerRadii.EMPTY, Insets.EMPTY));
        newCard.setBackground(oldBackground);
        newCard.setOnKeyPressed(null);
    }


    public void addHoverProperty(EditableCardLabel newCard) {
        //add a listener on the hover from the EditableCardLabel
        newCard.hoverProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue && !shift[0]) { //do not apply highlight if the card is being moved
                setHighlightedLabel(newCard);
            }
        });
    }



    public int addToGrid(GridPane grid, ImageView addListImg) {
        //get the current position of the "Add list" button for shifting purposes
        int column = GridPane.getColumnIndex(addListImg);

        //shift the "Add list" button
        GridPane.setColumnIndex(addListImg, column + 1);

        //ScrollPane container in case list body's contents overflow
        ScrollPane scroll = new ScrollPane();
        scroll.setFitToWidth(true);
        scroll.setMaxHeight(Double.MAX_VALUE);
        scroll.setMaxWidth(249);//Prevent list body from going over this
        scroll.setStyle("-fx-background-insets: 0; -fx-padding: 0; -fx-background: '000000'; -fx-border-width: 9; -fx-border-color: black;");
        scroll.setContent(this);
        GridPane.setFillHeight(scroll, true);
        GridPane.setFillWidth(scroll, true);
        scroll.getStylesheets().add("stylesheet.css");
        scroll.getStyleClass().add("scroll-bar");

        scroll.setFitToHeight(true);
        scroll.setFitToWidth(true);

        //Add the newly-created list to the grid
        grid.add(listTitle, column, 0);
        grid.add(scroll, column, 1);

        return column;
    }

    public void addRemoveBtn(GridPane listGrid) {
        ImageView removeImg = new ImageView("graphics/RemoveBtn_0.png");
        removeImg.setPreserveRatio(true);
        removeImg.setFitWidth(50);
        removeImg.setPickOnBounds(true);
        removeImg.setOnMouseClicked(event -> {
            int column = GridPane.getColumnIndex(listTitle);
            boardCtrl.deleteList(list.getId());
            //Remove children if same column as button
            listGrid.getChildren().removeIf(x -> GridPane.getColumnIndex(x) == column);
            //Shift children when applicable
            for (Node x : listGrid.getChildren()) {
                int col = GridPane.getColumnIndex(x);
                if (col > column) GridPane.setColumnIndex(x, col - 1);
            }
        });
        removeImg.setOnMouseEntered(event -> removeImg.setImage(new Image("graphics/RemoveBtn_1.png")));
        removeImg.setOnMouseExited(event -> removeImg.setImage(new Image("graphics/RemoveBtn_0.png")));
        listGrid.add(removeImg, GridPane.getColumnIndex(listTitle), 2);
        GridPane.setMargin(removeImg, new Insets(10, 0, 0, 0));
        GridPane.setHalignment(removeImg, HPos.CENTER);
    }

    public ToDoList getList() {
        return list;
    }

    public TextField getListTitle() {
        return listTitle;
    }


    /**
     * adds the functionallity to change the selected card with the arrow keys
     * @param listBody
     */
    public void addArrowFunctionality(ListBox listBody){

        addArrowFunctionalityHelper1(listBody); //otherwise the cyclomatic complexity was too high

        listBody.setOnKeyPressed(event -> {
            if(listBody.getChildren().size() > 1 && hover[0]){
                if(event.getCode() == KeyCode.SHIFT){
                    boardCtrl.setMovingCard(listBody.getHighlightedLabel());
                    boardCtrl.getMovingCard().setStyle("-fx-border-style: dotted");
                    shift[0] = true;
                    event.consume();
                }

                if(event.getCode() == KeyCode.UP && shift[0]){
                    int indexCurrent = listBody.getChildren().indexOf(boardCtrl.getMovingCard());
                    if(indexCurrent-1 >= 0){
                        swap(listBody, indexCurrent, indexCurrent-1, true);
                        //update highlighted label
                        setHighlightedLabel((EditableCardLabel) listBody.getChildren().get(indexCurrent-1));
                    }
                    event.consume();
                }

                if(event.getCode() == KeyCode.DOWN && shift[0]){
                    int indexCurrent = listBody.getChildren().indexOf(boardCtrl.getMovingCard());
                    if(indexCurrent+1 <= listBody.getChildren().size() -2){
                        swap(listBody, indexCurrent, indexCurrent+1, false);
                        //udpate highlighted label
                        setHighlightedLabel((EditableCardLabel) listBody.getChildren().get(indexCurrent+1));
                    }
                    event.consume();
                }
                addArrowFunctionalityHelper2(listBody, event);
            }
        });
    }


    private void addArrowFunctionalityHelper1(ListBox listBody){
        listBody.setOnMouseEntered(event -> {
            listBody.requestFocus();
            hover[0] = true;
        });

        listBody.setOnMouseExited(event -> {
            listBody.setHighlightedLabel(null);
            hover[0] = false;
        });

        listBody.setOnKeyReleased(event -> {
            //Check if shift has been released
            if(event.getCode() == KeyCode.SHIFT){
                shift[0] = false;

                for(int i=0; i<listBody.getChildren().size()-1; i++){
                    //Find the moved card in the listbody. Required as
                    //priority of neighbours are important to know
                    if(Objects.equals(listBody.getChildren().get(i),
                            boardCtrl.getMovingCard())) {
                        EditableCardLabel current = (EditableCardLabel) listBody.getChildren().get(i);
                        current.getCard().setPriority(
                                // Get priority of the card after this one
                                // (once setPriority is called, everything with the same priority and above is incremented by 1)
                                ((EditableCardLabel) listBody.getChildren().get(
                                        //If this is the last card (the last child of listBody is the addMore button),
                                        //get the priority of the card before this
                                        i == listBody.getChildren().size()-2 ? i-1 : i+1
                                )).getCard().getPriority()
                                //If the priority of the penultimate card was retrieved,
                                //increment that priority by 1 to ensure this card has the highest priority
                                + (i == listBody.getChildren().size()-2 ? 1 : 0)
                        );
                        //update the card afterwards
                        webSocketClient.update(current.getCard(), DataType.CARD);
                    }
                }
                //Get rid of the dotted border style
                boardCtrl.getMovingCard().setStyle("-fx-border-style: solid");
                event.consume(); //make sure the event doesn't further propagate and cause unwanted behaviour
            }
        });
    }


    private void addArrowFunctionalityHelper2(ListBox listBody, KeyEvent event){
        EditableCardLabel current = listBody.getHighlightedLabel();
        int index = listBody.getChildren().indexOf(current);

        if(event.getCode() == KeyCode.UP && !shift[0]){
            if(index != 0){
                index--;
            }
            listBody.setHighlightedLabel((EditableCardLabel) listBody.getChildren().get(index));
            event.consume();
        }

        if(event.getCode() == KeyCode.DOWN && !shift[0]){
            if(index != listBody.getChildren().size() - 2){
                index++;
            }
            listBody.setHighlightedLabel((EditableCardLabel) listBody.getChildren().get(index));
            event.consume();
        }
    }




    private void swap(ListBox listBody, int indexCurrent, int indexNewPlace, boolean up){
        EditableCardLabel current = (EditableCardLabel) listBody.getChildren().get(indexCurrent);
        EditableCardLabel nodeOnNewPlace = (EditableCardLabel) listBody.getChildren().get(indexNewPlace);

        if(up){
            listBody.getChildren().remove(indexCurrent);
            listBody.getChildren().set(indexNewPlace, current);
            listBody.getChildren().add(indexCurrent, nodeOnNewPlace);
        } else {
            listBody.getChildren().remove(indexNewPlace);
            listBody.getChildren().set(indexCurrent, nodeOnNewPlace);
            listBody.getChildren().add(indexNewPlace, current);
        }
    }

}
