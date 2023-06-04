package client.websocket;

import client.scenes.BoardCtrl;
import client.scenes.CardDescriptionCtrl;
import com.google.inject.Provider;
import commons.Board;
import commons.Card;
import commons.ColorPreset;
import commons.DataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.simp.stomp.*;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class WebSocketSessionHandler implements StompSessionHandler {
    private final Logger log = LoggerFactory.getLogger(WebSocketSessionHandler.class);

    private final AtomicReference<Throwable> failure;
    private final Provider ctrlProvider;
    private final Integer subscribedId;
    private StompSession stompSession;
    private StompSession.Subscription subscription;


    public WebSocketSessionHandler(AtomicReference<Throwable> failure, Integer subscribedId,
                                   Provider ctrlProvider) {
        this.subscribedId = subscribedId;
        this.failure = failure;
        this.ctrlProvider = ctrlProvider;
    }

    @Override
    public Type getPayloadType(StompHeaders headers) {
        return Board.class;
    }


    /**
     * Subscribes to updates for a particular board through the client STOMP session.
     * When an update is received, it either displays the updated board or exits the board if it was deleted.
     *
     * @param session          the client STOMP session
     * @param connectedHeaders the STOMP CONNECTED frame headers
     **/
    @Override
    public void afterConnected(final StompSession session, StompHeaders connectedHeaders) {
        String type = "invalid";

        //we check what sort of payload we are getting, and change the url accordingly.
        if (ctrlProvider.get() instanceof BoardCtrl) type = "board";
        else if (ctrlProvider.get() instanceof CardDescriptionCtrl) type = "card";
        else {
            log.error("Websocket Session Handler: Provided scene is of an unknown type");
            return;
        }


        subscription = session.subscribe("/updates/" + type + "/" + subscribedId, new StompFrameHandler() {

            /*
             * Returns the type of payload to be handled by this frame handler
             * In this case it is either a Card, or a board.
             */
            @Override
            public Type getPayloadType(StompHeaders headers) {
                if (ctrlProvider.get() instanceof CardDescriptionCtrl) return Card.class;
                return Board.class;
            }

            /**
             * either displays the updated board, exits the board if it was deleted or Updates a card.
             * if a board gets deleted, the server sends a board with id -1,
             * this is why we check the board id to be -1. If it is -1 the board we are currently
             * subscribed to is deleted, and we need to leave.
             * In case it is a card we receive, we update it.
             *
             * @param headers the STOMP frame headers
             * @param payload the payload of the STOMP frame
             */
            @Override
            public void handleFrame(StompHeaders headers, Object payload) {
                //check if we have received a board, or card
                if (ctrlProvider.get() instanceof BoardCtrl) {
                    Board board = (Board) payload;

                    if (board.getId() == -1) {
                        // If the board was deleted, exit the board
                        log.info("Received deleted board from server");
                        ((BoardCtrl) ctrlProvider.get()).exitBoard();
                        ((BoardCtrl) ctrlProvider.get()).deleteFromTree();
                        subscription.unsubscribe();

                    } else {
                        // Otherwise, display the updated board
                        log.info("Received updated board with ID " + board.getId());

                        ((BoardCtrl) ctrlProvider.get()).displayUpdatedBoard((Board) payload);

                    }
                }
                //Check if we have received a card
                else if (ctrlProvider.get() instanceof CardDescriptionCtrl) {
                    //if we did, we update the card
                    log.info("Received a new card");
                    ((CardDescriptionCtrl) ctrlProvider.get()).displayUpdatedCard((Card) payload);
                }
            }
        });
        this.stompSession = session;
    }

    @Override
    public void handleFrame(StompHeaders headers, Object payload) {
        this.failure.set(new Exception(headers.toString()));
    }

    @Override
    public void handleException(StompSession s, StompCommand c, StompHeaders h, byte[] p, Throwable ex) {
        this.failure.set(ex);
    }

    @Override
    public void handleTransportError(StompSession session, Throwable ex) {
        this.failure.set(ex);
    }

    public boolean sendCreate(Object object, DataType type) {
        try {
            stompSession.send("/create" + type.getWebSocketPath() + subscribedId, object);
            return true;
        } catch (Throwable t) {
            failure.set(t);
            return false;
        }
    }

    public boolean sendCreate(Object object, DataType type, int boardId) {
        try {
            stompSession.send("/create" + type.getWebSocketPath() + boardId, object);
            return true;
        } catch (Throwable t) {
            failure.set(t);
            return false;
        }
    }

    public boolean sendUpdate(Object object, DataType type) {
        try {
            stompSession.send("/update" + type.getWebSocketPath() + subscribedId, object);
            return true;
        } catch (Throwable t) {
            failure.set(t);
            return false;
        }
    }

    public boolean sendUpdate(Object object, DataType type, int boardId) {
        try {
            stompSession.send("/update" + type.getWebSocketPath() + boardId, object);
            return true;
        } catch (Throwable t) {
            failure.set(t);
            return false;
        }
    }

    public boolean delete(Integer id, DataType type) {
        try {
            stompSession.send("/delete" + type.getWebSocketPath() + subscribedId, id);
            return true;
        } catch (Throwable t) {
            failure.set(t);
            return false;
        }
    }

    public boolean delete(Integer id, DataType type, int boardId) {
        try {
            stompSession.send("/delete" + type.getWebSocketPath() + boardId, id);
            return true;
        } catch (Throwable t) {
            failure.set(t);
            return false;
        }
    }

    public boolean sendMultipleUpdatesColorPreset(List<ColorPreset> colorPresets, DataType type, int boardId) {
        try {
            stompSession.send("/update-multiple" + type.getWebSocketPath() + boardId, colorPresets);
            return true;
        } catch (Throwable t) {
            failure.set(t);
            return false;
        }
    }

    public boolean sendMultipleDeletes(List<Integer> ids, DataType type, int boardId) {
        try {
            stompSession.send("/delete-multiple" + type.getWebSocketPath() + boardId, ids);
            return true;
        } catch (Throwable t) {
            failure.set(t);
            return false;
        }
    }

    public boolean addTag(Object object, int boardId) {
        try {
            stompSession.send("/addTag/card/" + boardId, object);
            return true;
        } catch (Throwable t) {
            failure.set(t);
            return false;
        }
    }

    public boolean deleteTag(Object object, int boardId) {
        try {
            stompSession.send("/deleteTag/card/" + boardId, object);
            return true;
        } catch (Throwable t) {
            failure.set(t);
            return false;
        }
    }

    public void unsubscribe()   {
        subscription.unsubscribe();
    }

}
