package client.scenes;

import com.google.inject.Inject;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;

public class CollapsedBoardsCtrl {
    @FXML
    private Button showBoards;
    @FXML
    private ScrollPane scrollPane;
    private final MainCtrl mainCtrl;

    /**
     Controller class for the collapsed boards section of the UI. This class is responsible for managing the collapsed
     boards view, such as showing the collapsed boards sidebar and styling the UI components.
     */
    @Inject
    public CollapsedBoardsCtrl(MainCtrl mainCtrl) {
        this.mainCtrl = mainCtrl;
    }

    public void initialize() {
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        showBoards.setOnMouseEntered(event -> {
            showBoards.setStyle("-fx-background-color: rgba(35, 35, 35, 1)");
            scrollPane.setStyle("-fx-background: rgba(35, 35, 35, 1); -fx-border-color: rgba(35, 35, 35, 1); " +
                    "-fx-border-width: 1;-fx-border-color: rgba(35, 35, 35, 1) rgba(35, 35, 35, 1) rgba(35, 35, 35, 1) #ffa800;" +
                    "-fx-border-style: solid;");

        });
        showBoards.setOnMouseExited(event -> {
            showBoards.setStyle("-fx-background-color: rgba(52, 52, 52, 1)");
            scrollPane.setStyle("-fx-background: rgba(52, 52, 52, 1); -fx-border-color: rgba(52, 52, 52, 1); " +
                    "-fx-border-width: 1;-fx-border-color: rgba(52, 52, 52, 1) rgba(52, 52, 52, 1) rgba(52, 52, 52, 1) #ffa800;" +
                    "-fx-border-style: solid;");
        });
    }

    /**
     Initializes the UI components of the collapsed boards section, such as the scroll pane and the show boards button,
     and sets their styling.
     */
    public void showBoards() {
        mainCtrl.showBoardsSidebar();
    }
}
