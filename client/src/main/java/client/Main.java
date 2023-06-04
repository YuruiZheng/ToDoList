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
package client;

import client.scenes.*;
import com.google.inject.Injector;
import javafx.application.Application;
import javafx.scene.Parent;
import javafx.stage.Stage;
import javafx.util.Pair;

import static com.google.inject.Guice.createInjector;

public class Main extends Application {

    private static final Injector INJECTOR = createInjector(new MyModule());
    private static final MyFXML FXML = new MyFXML(INJECTOR);

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void start(Stage primaryStage) {
        MainCtrl mainCtrl = INJECTOR.getInstance(MainCtrl.class);

        Pair<BoardCtrl, Parent> board = FXML.load(BoardCtrl.class, "client", "scenes", "Board.fxml");
        Pair<StartCtrl, Parent> start = FXML.load(StartCtrl.class, "client", "scenes", "Start.fxml");
        Pair<CreateTagSceneCtrl, Parent> createTag = FXML
                .load(CreateTagSceneCtrl.class, "client", "scenes", "CreateTagScene.fxml");
        Pair<EditTagSceneCtrl, Parent> editTag = FXML
                .load(EditTagSceneCtrl.class, "client", "scenes", "EditTagScene.fxml");
        Pair<CustomizationCtrl, Parent> customize = FXML
                .load(CustomizationCtrl.class, "client", "scenes", "Customization.fxml");
        Pair<CreateCardCtrl, Parent> createCard = FXML
                .load(CreateCardCtrl.class, "client", "scenes", "CreateCard.fxml");
        Pair<CardDescriptionCtrl, Parent> cardDescription = FXML
                .load(CardDescriptionCtrl.class, "client", "scenes", "CardDescription.fxml");
        Pair<CreateBoardCtrl, Parent> createBoard = FXML
                .load(CreateBoardCtrl.class, "client", "scenes", "CreateBoard.fxml");
        Pair<AdminLoginCtrl, Parent> adminLoginPair = FXML.load(AdminLoginCtrl.class, "client", "scenes", "AdminLoginScene.fxml");
        Pair<AdminCtrl, Parent> adminPair = FXML.load(AdminCtrl.class, "client", "scenes", "AdminScene.fxml");
        Pair<AdminInfoCtrl, Parent> adminInfoPair = FXML.load(AdminInfoCtrl.class, "client", "scenes", "AdminInfoScene.fxml");
        Pair<JoinBoardCtrl, Parent> joinBoard = FXML
                .load(JoinBoardCtrl.class, "client", "scenes", "JoinBoard.fxml");
        Pair<MyBoardsCtrl, Parent> myBoards = FXML
                .load(MyBoardsCtrl.class, "client", "scenes", "MyBoards.fxml");
        Pair<CollapsedBoardsCtrl, Parent> collapsedBoards = FXML
                .load(CollapsedBoardsCtrl.class, "client", "scenes", "CollapsedBoards.fxml");
        Pair<HelpScreenCtrl, Parent> helpScene = FXML
                .load(HelpScreenCtrl.class, "client", "scenes", "HelpScreen.fxml");
        Pair<TagsDisplayCtrl, Parent> tagsDisplayCtrlParentPair = FXML
                .load(TagsDisplayCtrl.class, "client", "scenes", "TagListScene.fxml");
        mainCtrl.initialize(primaryStage, board, start, createCard, cardDescription, createTag, editTag,
                customize, createBoard, adminLoginPair, adminPair, adminInfoPair, joinBoard,
                myBoards, collapsedBoards, helpScene,tagsDisplayCtrlParentPair);


    }

}