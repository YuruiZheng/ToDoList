package client.scenes;

import client.Connection.CreateUpdateEntityServer;
import client.object.EditablePresetLabel;
import client.websocket.WebSocketClient;
import com.google.inject.Inject;
import com.google.inject.Provider;
import commons.Board;
import commons.ColorPreset;
import commons.DataType;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class CustomizationCtrl {

    private final Logger log = LoggerFactory.getLogger(CustomizationCtrl.class);
    @FXML
    private Button resetColour;

    @FXML
    private Button back;

    @FXML
    private ColorPicker boardBGColour;
    @FXML
    private ColorPicker boardFontColour;
    @FXML
    private ColorPicker listBGColour;
    @FXML
    private ColorPicker listFontColour;

    @FXML
    private Button set;

    @FXML
    private Label title;

    @FXML
    private Label titleColor;

    @FXML
    private VBox presetArea;

    private final MainCtrl mainCtrl;
    private final WebSocketClient webSocketClient;
    private final CreateUpdateEntityServer createUpdateEntityServer;

    private Color colorHEX;
    private Board board = new Board();
    //list of color presets to be deleted
    private List<Integer> deleted = new ArrayList<>();

    /**
     * Constructor for the CustomizationCtrl class, which handles the customization scene for the application.
     *
     * @param mainCtrl The main controller for the application.
     * @param webSocketClientProvider A provider for the WebSocketClient used by the application.
     * @param createUpdateEntityServer The server used to update entity information.
     */
    @Inject
    public CustomizationCtrl(MainCtrl mainCtrl, Provider<WebSocketClient> webSocketClientProvider,
                             CreateUpdateEntityServer createUpdateEntityServer) {
        this.mainCtrl = mainCtrl;
        this.webSocketClient = webSocketClientProvider.get();
        this.createUpdateEntityServer = createUpdateEntityServer;
    }

    /**
     * Extracts the color string from a given color picker.
     *
     * @param cp The color picker to extract the color string from.
     * @return The color string for the given color picker.
     */
    private String extractColorString(ColorPicker cp) {
        return String.format( "%02X%02X%02X",
                (int)( cp.getValue().getRed() * 255 ),
                (int)( cp.getValue().getGreen() * 255 ),
                (int)( cp.getValue().getBlue() * 255 ));
    }

    /**
     * Initializes the scene, setting the board object and clearing the scene.
     *
     * @param board The board object to set.
     */
    public void initializeScene(Board board) {
        this.board = board;
        clearScene();
        //get a list of all color presets
        if(board.getColorPresets() != null)
            board.getColorPresets().forEach(this::addPresetToList);
        boardFontColour.setValue(Color.web(board.getFont()));
        boardBGColour.setValue(Color.web(board.getBackground()));
        listFontColour.setValue(Color.web(board.getListF()));
        listBGColour.setValue(Color.web(board.getListBG()));
    }

    /**
     Initializes the ColorPreset UI components.
     Adds an ImageView to the presetArea and sets up its functionality.
     Initializes the color containers for the ColorPreset list and sets up their functionality.
     */
    public void initialize() {
        ImageView addPresetImg = new ImageView("graphics/MoreBtn_0.png");
        addPresetImg.setPreserveRatio(true);
        addPresetImg.setFitWidth(50);
        addPresetImg.setPickOnBounds(true);
        presetArea.getChildren().add(addPresetImg);
        addPresetImg.setOnMouseClicked(event -> {
            ColorPreset c = new ColorPreset();
            c.setBackground("000000");
            c.setFont("000000");
            addPresetToList(c);
        });
        presetArea.setBackground(Background.EMPTY);
        presetArea.setSpacing(20);
        presetArea.setPadding(new Insets(20, 0, 0, 0));
    }

    /**
     Adds a ColorPreset to the list of presets.
     Creates a container for the preset with the appropriate properties and UI elements.
     @param colorPreset the ColorPreset to be added to the list
     */
    private void addPresetToList(ColorPreset colorPreset) {
        VBox container = new VBox();
        container.setMaxWidth(350);
        container.setPadding(new Insets(10, 0, 0, 0));
        container.getProperties().put("presetObject", colorPreset);
        container.setMinHeight(120);
        container.setSpacing(20);
        container.setBorder(new Border(new BorderStroke(Color.web("000000"), BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(3))));
        container.setBackground(new Background(new BackgroundFill(Color.web("323232"), CornerRadii.EMPTY, Insets.EMPTY)));

        HBox colorContainer = initColorContainer(container, colorPreset.getFont(), colorPreset.getBackground());
        HBox labelContainer = initLabelContainer(colorPreset, container);

        container.getChildren().addAll(colorContainer, labelContainer);

        presetArea.getChildren().add(presetArea.getChildren().size()-1, container);
    }

    /**
     Initializes the color containers for a ColorPreset list.
     Sets up the color pickers and labels for the font and background colors.
     @param container the container for the ColorPreset
     @param font the font color for the ColorPreset
     @param background the background color for the ColorPreset
     @return an HBox containing the font and background color pickers and labels
     */
    private HBox initColorContainer(VBox container, String font, String background) {
        HBox colorContainer = new HBox();
        colorContainer.setAlignment(Pos.CENTER);
        colorContainer.setSpacing(15);

        VBox fontContainer = new VBox();
        ColorPicker fontPicker = new ColorPicker(Color.web(
                font != null ? font : "ffffff"));
        fontPicker.setOnAction(event -> {
            ((ColorPreset)container.getProperties().get("presetObject")).setFont(extractColorString(fontPicker));
        });
        Label fontLabel = new Label("Font");
        fontLabel.setTextFill(Color.web("ffffff"));
        fontLabel.getStyleClass().add("lulo");
        fontContainer.setAlignment(Pos.CENTER);
        fontContainer.getChildren().addAll(fontLabel, fontPicker);

        VBox bgContainer = new VBox();
        ColorPicker bgPicker = new ColorPicker(Color.web(
                background != null ? background : "ffffff"));
        bgPicker.setOnAction(event -> {
            ((ColorPreset)container.getProperties().get("presetObject")).setBackground(extractColorString(bgPicker));
        });
        Label bgLabel = new Label("Background");
        bgLabel.setTextFill(Color.web("ffffff"));
        bgLabel.getStyleClass().add("lulo");
        bgContainer.setAlignment(Pos.CENTER);
        bgContainer.getChildren().addAll(bgLabel, bgPicker);

        colorContainer.getChildren().addAll(fontContainer, bgContainer);
        return colorContainer;
    }

    /**
     Initializes the label container for a ColorPreset, which includes the preset name, status indicator, and remove button.
     @param colorPreset the ColorPreset object to use for initializing the label container
     @param container the VBox container to add the label container to
     @return an HBox containing the preset name, status indicator, and remove button
     */
    private HBox initLabelContainer(ColorPreset colorPreset, VBox container) {
        HBox labelContainer = new HBox();
        labelContainer.setAlignment(Pos.CENTER);
        labelContainer.setSpacing(20);
        Label status = initStatus(container, colorPreset);

        EditablePresetLabel presetName = new EditablePresetLabel(
                colorPreset.getName() != null ? colorPreset.getName() : "My Preset");
        container.getProperties().put("presetNameLabel", presetName);
        presetName.getStyleClass().add("lulo");
        presetName.setTextFill(Color.WHITE);
        Label removeBtn = initRemove(container);


        labelContainer.getChildren().addAll(status, presetName, removeBtn);
        return labelContainer;
    }

    /**
     Initializes the remove button label for a ColorPreset.
     @param parent the VBox parent container of the ColorPreset
     @return a Label representing the remove button for the ColorPreset
     */
    private Label initRemove(VBox parent) {
        ColorPreset colorPreset = (ColorPreset) parent.getProperties().get("presetObject");
        Integer id = colorPreset.getId();
        Label removeBtn = new Label("x");
        removeBtn.setTextFill(Color.RED);
        removeBtn.setOnMouseEntered(event -> {removeBtn.setTextFill(Color.DARKRED);});
        removeBtn.setOnMouseExited(event -> {removeBtn.setTextFill(Color.RED);});
        removeBtn.setOnMouseClicked(event -> {
            if(id != null) this.deleted.add(id);
            presetArea.getChildren().removeIf(x -> Objects.equals(x, parent));
        });
        removeBtn.getStyleClass().add("lulo");
        return removeBtn;
    }

    /**
     Initializes the status indicator label for a ColorPreset.
     @param parent the VBox parent container of the ColorPreset
     @param colorPreset the ColorPreset object to use for initializing the status indicator label
     @return a Label representing the status indicator for the ColorPreset
     */
    private Label initStatus(VBox parent, ColorPreset colorPreset) {
        Label status = new Label("o");
        boolean isDefault = false;
        Color color = Color.web("ffffff"); //defaults to white

        if(board.getDefaultPreset() != null &&
                Objects.equals(colorPreset.getId(), board.getDefaultPreset())) {
            color = Color.web("ffa800"); //turns to gold if default
            isDefault = true;
        }
        status.getProperties().put("isDefault", isDefault);
        parent.getProperties().put("statusLabel", status);

        status.setOnMouseEntered(event -> {
            if((boolean)status.getProperties().get("isDefault"))
                status.setTextFill(Color.web("cc8600"));
            else
                status.setTextFill(Color.GRAY);
        });
        status.setOnMouseExited(event -> {
            if((boolean)status.getProperties().get("isDefault"))
                status.setTextFill(Color.web("ffa800"));
            else
                status.setTextFill(Color.WHITE);
        });
        status.setOnMouseClicked(event -> {
            presetArea.getChildren().forEach(x -> {
                Label statusLabel = (Label) x.getProperties().get("statusLabel");
                if(statusLabel == null) return;
                Boolean xIsDefault = (Boolean) statusLabel.getProperties().get("isDefault");
                if(Objects.equals(xIsDefault, true) && !Objects.equals(statusLabel, status)) {
                    statusLabel.getProperties().put("isDefault", false);
                    statusLabel.setTextFill(Color.WHITE);
                }
            });
            status.getProperties().put("isDefault",
                    !((Boolean)status.getProperties().get("isDefault")));
        });

        status.setTextFill(color);
        status.getStyleClass().add("lulo");

        return status;
    }

    /**
     This method clears the current scene by resetting the colour, creating a new ArrayList for deleted presets,
     and removing all VBox children from presetArea.
     */
    public void clearScene() {
        resetColour();
        deleted = new ArrayList<>();
        presetArea.getChildren().removeIf(x -> x.getClass() == VBox.class);
    }

    /**
     This method sets the board for the controller.
     @param board The Board object to set for the controller.
     */
    public void setBoard(Board board)   {
        this.board = board;
    }

    /**
     This method changes the background color of the board by getting the color value from the boardBGColour color picker.
     */
    @FXML
    public void changeColor(){
        try {
            colorHEX = boardBGColour.getValue();
        } catch (IllegalArgumentException ignored)    {

        }
    }

    /**
     This method navigates back to the main board view by calling the showBoard() method of the main controller.
     */
    @FXML
    void back() {
        mainCtrl.showBoard();
    }

    /**
     Sets the selected color presets to the board and updates them to the server.
     Also updates the background color, font color, list background color and list font color of the board.
     Finally, it updates the board itself to the server and goes back to the main view.
     */
    @FXML
    void set() {
        ArrayList<ColorPreset> toUpdate = new ArrayList<>();
        board.setDefaultPreset(-1);
        for(int i = 0; i < presetArea.getChildren().size()-1; i++) {
            Node container = presetArea.getChildren().get(i);
            EditablePresetLabel label = (EditablePresetLabel) container.getProperties().get("presetNameLabel");
            Label status = (Label) container.getProperties().get("statusLabel");
            Boolean isDefault = (Boolean) status.getProperties().get("isDefault");
            ColorPreset cp = (ColorPreset)(container.getProperties().get("presetObject"));
            cp.setName(label.getText());
            if(cp.getId() == null) {
                cp.setBoardId(board.getId());
                try {
                    cp = createUpdateEntityServer.createColorPreset(cp);
                } catch (Exception ignored) {}
            } else {
                toUpdate.add(cp);
            }

            if(isDefault) {
                board.setDefaultPreset(cp.getId());
            }
        }

        if (toUpdate.size()!=0){
            webSocketClient.updateMultipleColorPresets(toUpdate, DataType.COLORPRESET, board.getId());
        }
        if (deleted!=null && deleted.size()!=0){
            webSocketClient.deleteMultiple(deleted, DataType.COLORPRESET, board.getId());
        }

        board.setBackground(extractColorString(boardBGColour));
        board.setFont(extractColorString(boardFontColour));
        board.setListBG(extractColorString(listBGColour));
        board.setListF(extractColorString(listFontColour));

        webSocketClient.update(board, DataType.BOARD);
        back();
    }

    /**
     Resets the color of the board to a default color.
     Default background color: #343434
     Default font color: WHITE
     Default list background color: #ffa800
     Default list font color: WHITE
     */
    public void resetColour(){
        Color defaultBGColour = Color.web("#343434");
        Color defaultFontColour = Color.WHITE;
        Color defaultListBGColour = Color.web("ffa800");
        boardBGColour.setValue(defaultBGColour);
        boardFontColour.setValue(defaultFontColour);
        listFontColour.setValue(defaultFontColour);
        listBGColour.setValue(defaultListBGColour);
    }
}
