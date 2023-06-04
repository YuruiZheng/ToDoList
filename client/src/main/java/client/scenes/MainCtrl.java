/*
 * Copyright 2021 Delft University of Technology
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package client.scenes;

import client.utils.SessionData;
import com.google.inject.Inject;
import commons.Board;
import commons.Card;
import commons.Tag;
import javafx.application.Platform;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

public class MainCtrl {

    private final Logger log = LoggerFactory.getLogger(MainCtrl.class);
    private Stage primaryStage;
    private BorderPane borderPane;
    private Scene mainScene;

    private BoardCtrl boardCtrl;
    private Scene board;

    private StartCtrl startCtrl;
    private Scene start;

    private CreateCardCtrl createCardCtrl;
    private Scene createCard;

    private CardDescriptionCtrl cardDescriptionCtrl;
    private Scene cardDescription;

    private CreateTagSceneCtrl createTagSceneCtrl;
    private Scene createTagScene;

    private EditTagSceneCtrl editTagSceneCtrl;
    private Scene editTagScene;

    private CustomizationCtrl customizationCtrl;
    private Scene chooseBackGroundColorScene;

    private CreateBoardCtrl createBoardCtrl;
    private Scene createBoardScene;

    private JoinBoardCtrl joinBoardCtrl;
    private Scene joinBoard;

    private MyBoardsCtrl myBoardsCtrl;
    private Scene myBoards;

    private CollapsedBoardsCtrl collapsedBoardsCtrl;
    private Scene collapsedBoards;


    private AdminLoginCtrl adminLoginCtrl;
    private Scene adminLoginScene;

    private AdminCtrl adminCtrl;
    private Scene adminScene;

    private AdminInfoCtrl adminInfoCtrl;
    private Scene adminInfoScene;

    private HelpScreenCtrl helpCtrl;
    private Scene helpScene;

    private TagsDisplayCtrl tagsDisplayCtrl;
    private Scene tagListScene;

    private final SessionData sessionData;

    /**
     Constructs a new instance of the MainCtrl class, initializes the different scenes
     and their respective controllers. The initialize() method sets up listeners for keyboard events
     and adds the different controllers to their respective scenes.
     @param sessionData SessionData instance for user session tracking.
     */
    @Inject
    public MainCtrl(SessionData sessionData) {
        this.sessionData = sessionData;
    }


    @SuppressWarnings({"ParameterNumber"})
    public void initialize(Stage primaryStage,
                           Pair<BoardCtrl, Parent> board,
                           Pair<StartCtrl, Parent> start,
                           Pair<CreateCardCtrl, Parent> createCard,
                           Pair<CardDescriptionCtrl, Parent> cardDescription,
                           Pair<CreateTagSceneCtrl, Parent> createTagScenePair,
                           Pair<EditTagSceneCtrl, Parent> editTagScenePair,
                           Pair<CustomizationCtrl, Parent> customize,
                           Pair<CreateBoardCtrl, Parent> createBoard,
                           Pair<AdminLoginCtrl, Parent> adminLoginPair,
                           Pair<AdminCtrl, Parent> adminPair,
                           Pair<AdminInfoCtrl, Parent> adminInfoPair,
                           Pair<JoinBoardCtrl, Parent> joinBoard,
                           Pair<MyBoardsCtrl, Parent> myBoards,
                           Pair<CollapsedBoardsCtrl, Parent> collapsedBoards,
                           Pair<HelpScreenCtrl, Parent> helpScenePair,
                           Pair<TagsDisplayCtrl, Parent> tagsDisplayCtrlParentPair){
        this.primaryStage = primaryStage;

        this.boardCtrl = board.getKey(); //Initialize the board scene's controller
        this.board = new Scene(board.getValue()); //Initialize the board's parent

        this.startCtrl = start.getKey();
        this.start = new Scene(start.getValue());

        this.tagsDisplayCtrl = tagsDisplayCtrlParentPair.getKey();
        this.tagListScene = new Scene(tagsDisplayCtrlParentPair.getValue());

        this.helpCtrl = helpScenePair.getKey();
        this.helpScene = new Scene(helpScenePair.getValue());

        this.createCardCtrl = createCard.getKey();
        this.createCard = new Scene(createCard.getValue());

        this.cardDescriptionCtrl = cardDescription.getKey();
        this.cardDescription = new Scene(cardDescription.getValue());

        this.createTagSceneCtrl = createTagScenePair.getKey();
        this.createTagScene = new Scene(createTagScenePair.getValue());

        this.editTagSceneCtrl = editTagScenePair.getKey();
        this.editTagScene = new Scene(editTagScenePair.getValue());

        this.customizationCtrl = customize.getKey();
        this.chooseBackGroundColorScene = new Scene(customize.getValue());

        this.createBoardCtrl = createBoard.getKey();
        this.createBoardScene = new Scene(createBoard.getValue());

        this.adminLoginCtrl = adminLoginPair.getKey();
        this.adminLoginScene = new Scene(adminLoginPair.getValue());

        this.adminCtrl = adminPair.getKey();
        this.adminScene = new Scene(adminPair.getValue());

        this.adminInfoCtrl = adminInfoPair.getKey();
        this.adminInfoScene = new Scene(adminInfoPair.getValue());

        this.joinBoardCtrl = joinBoard.getKey();
        this.joinBoard = new Scene(joinBoard.getValue());

        this.myBoardsCtrl = myBoards.getKey();
        this.myBoards = new Scene(myBoards.getValue());

        this.collapsedBoardsCtrl = collapsedBoards.getKey();
        this.collapsedBoards = new Scene(collapsedBoards.getValue());

        this.borderPane = new BorderPane();
        this.borderPane.setRight(collapsedBoards.getValue());
        this.mainScene = new Scene(borderPane);

        Platform.runLater(this::addLister);
        showStart();
        primaryStage.show();
    }

    /*
    Sets a setOnKeyPressed event for all scenes.
    and checks if the "/" gets pressed, since this is the same button the question mark is on.
    and if so it will take you to the help screen.
     */
    private void addLister() {
        Scene scene = primaryStage.getScene();
        scene.setOnKeyPressed(event -> {
            if(Objects.equals(event.getCode(), KeyCode.SLASH)){
                log.info("Displaying the help menu via shortcut");
                showHelpScene();
            }
        });
    }

    /**
     * Switches the Scene in the primary stage and gets rid of fullscreen or maximization
     */
    private void changeScene(Scene scene) {
        boolean fullscreen = primaryStage.isFullScreen();
        boolean maximized = primaryStage.isMaximized();

        if (fullscreen || maximized) {
            primaryStage.setMaximized(false);
            primaryStage.setFullScreen(false);
        } else {
            primaryStage.hide();
        }

        primaryStage.setScene(scene);
        primaryStage.centerOnScreen();

        primaryStage.show();
    }

    /**
     * Adds the switch board sidebar and changes the scene
     */
    private void changeSceneWithSidebar(Scene scene) {
        borderPane.setCenter(scene.getRoot());
        myBoardsCtrl.resetSize();
        changeScene(mainScene);
        myBoardsCtrl.setSize(borderPane.getHeight());
    }

    /**
     * Displays the board with the given ID and password, sets the title of the stage to "My Board",
     * initializes the board scene, and switches the scene to the board scene with the sidebar.
     *
     * @param boardId  the ID of the board to show
     * @param password the password of the board to show
     */
    public void showBoard(int boardId, String password) {
        boardCtrl.setBoardId(boardId, password);
        boardCtrl.initializeScene();
        primaryStage.setTitle("My Board");
        changeSceneWithSidebar(board);
    }

    /**
     * Displays the tag list for the board, sets the title of the stage to "My Tags",
     * initializes the tag list scene, and switches the scene to the tag list scene with the sidebar.
     *
     * @throws IOException if an I/O error occurs
     */
    public void showTagList() throws IOException {
        System.out.println(boardCtrl.getBoard().getTags());
        tagsDisplayCtrl.initializeScene(boardCtrl.getBoard());
        primaryStage.setTitle("My Tags");
        changeSceneWithSidebar(tagListScene);
    }

    /**
     * Switches the scene to the board scene with the sidebar, sets the title of the stage to "My Board",
     * and sets the style of the currently moving card to "-fx-border-style: solid" if it is not null.
     */
    public void showBoard() {
        primaryStage.setTitle("My Board");
        changeSceneWithSidebar(board);
        if(boardCtrl.getMovingCard() != null){
            boardCtrl.getMovingCard().setStyle("-fx-border-style: solid");
        }
    }

    /**
     * Displays the help screen, sets the title of the stage to "Help",
     * and switches the scene to the help screen.
     */
    public void showHelpScene() {
        primaryStage.setTitle("Help");
        changeScene(helpScene);
    }

    /**
     * Switches the scene to the create card scene, sets the title of the stage to "Creating Card",
     * and passes the list body, list ID, and board ID to the create card controller.
     *
     * @param listBody the VBox of the list where the card will be created
     * @param listId   the ID of the list where the card will be created
     * @param boardId  the ID of the board where the card will be created
     */
    public void createCard(VBox listBody, Integer listId, Integer boardId) {
        primaryStage.setTitle("Creating Card");
        changeScene(createCard);
        createCardCtrl.passListAndBoardID(listBody, listId, boardId);
    }

    /**
     Shows the start page scene.
     */
    public void showStart() {
        primaryStage.setTitle("Start Page");
        changeSceneWithSidebar(start);
    }

    /**
     Shows the card description scene.
     @param card The card to be edited.
     @param boardId The ID of the board the card is on.
     */
    public void cardDescription(Card card, int boardId) {
        primaryStage.setTitle("Edit Card");
        cardDescriptionCtrl.initializeScene(card, boardId);
        changeScene(cardDescription);
    }

    /**
     Shows the edit tag scene.
     @param tag The tag to be edited.
     */
    public void showEditTag(Tag tag) {
        primaryStage.setTitle("Edit Tag");
        editTagSceneCtrl.initializeScene(boardCtrl.getBoard(),tag);
        changeScene(editTagScene);
    }

    /**
     Shows the create tag scene.
     */
    public void showCreateTagScene() {
        primaryStage.setTitle("Create Tag");
        createTagSceneCtrl.initializeScene(boardCtrl.getBoard());
        changeScene(createTagScene);
    }

    /**
     Shows the customize scene.
     */
    public void showChooseBackGroundScene() {
        primaryStage.setTitle("Customize");
        customizationCtrl.initializeScene(boardCtrl.getBoard());
        changeScene(chooseBackGroundColorScene);
    }

    /**
     Shows the create board scene.
     */
    public void showCreateBoard() {
        primaryStage.setTitle("Create Board");
        changeSceneWithSidebar(createBoardScene);
    }

    /**
     Shows the join board scene.
     */
    public void joinBoard() {
        primaryStage.setTitle("Join Board");
        changeSceneWithSidebar(joinBoard);
    }

    /**
     Shows the boards sidebar.
     */
    public void showBoardsSidebar() {
        borderPane.setRight(myBoards.getRoot());
        myBoardsCtrl.setSize(borderPane.getHeight());
    }

    /**
     Hides the boards sidebar.
     */
    public void hideBoardsSidebar() {
        borderPane.setRight(collapsedBoards.getRoot());
    }

    /**
     Adds a board to the boards sidebar.
     @param board The board to be added.
     */
    public void addBoardToSidebar(Board board) {
        myBoardsCtrl.addBoard(board, sessionData.getServer());
    }

    /**
     Shows the admin login scene.
     */
    public void showAdminLogin(){
        primaryStage.setTitle("Login");
        primaryStage.setScene(adminLoginScene);
    }

    /**
     Shows the admin view scene.
     */
    public void showAdmin() {
        primaryStage.setTitle("Admin");
        adminCtrl.init();
        primaryStage.setScene(adminScene);
    }

    /**
     Shows the admin info scene.
     @param currentBoard The board for which the admin info is being displayed.
     */
    public void showAdminInfo(Board currentBoard){
        primaryStage.setTitle("Admin Info");
        primaryStage.setScene(adminInfoScene);
        adminInfoCtrl.init(currentBoard);
    }

    /**
     Gets the controller for the boards sidebar.
     @return The MyBoardsCtrl object.
     */
    public MyBoardsCtrl getBoardsCtrl(){
        return myBoardsCtrl;
    }

}