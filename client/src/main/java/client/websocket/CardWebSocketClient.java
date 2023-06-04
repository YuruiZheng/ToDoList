package client.websocket;

import client.scenes.CardDescriptionCtrl;
import client.utils.SessionData;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import commons.DataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import javax.inject.Inject;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Singleton
public class CardWebSocketClient {

    private final Logger log = LoggerFactory.getLogger(CardWebSocketClient.class);

    private final WebSocketStompClient stompClient;
    private final Provider<CardDescriptionCtrl> cardCtrlProvider;
    private final SessionData sessionData;
    private WebSocketSessionHandler handler;
    private Integer subscribedCardId;
    private Integer boardId;

    @Inject
    public CardWebSocketClient(Provider<CardDescriptionCtrl> cardCtrlProvider, SessionData sessionData) {
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        this.cardCtrlProvider = cardCtrlProvider;
        this.sessionData = sessionData;
    }

    public void subscribeToCard(Integer boardId, Integer cardId) {
        if (!Objects.isNull(subscribedCardId))
            this.stompClient.stop();
        final AtomicReference<Throwable> failure = new AtomicReference<>();
        handler = new WebSocketSessionHandler(failure, cardId, cardCtrlProvider);
        stompClient.connect("ws://" + sessionData.getServer() + "/api", new WebSocketHttpHeaders(), handler);
        this.subscribedCardId = cardId;
        this.boardId = boardId;
    }

    public void unsubscribe() {
        stompClient.stop();
        subscribedCardId = null;
        boardId = null;
    }

    public boolean create(Object object, DataType type) {
        if (Objects.isNull(subscribedCardId)) {
            log.error("Disconnected - Cannot create " + type);
            return false;
        }
        return handler.sendCreate(object, type, boardId);
    }

    public boolean update(Object object, DataType type) {
        if (Objects.isNull(subscribedCardId)) {
            log.error("Disconnected - Cannot update " + type);
            return false;
        }
        return handler.sendUpdate(object, type, boardId);
    }

    public boolean delete(Integer id, DataType type) {
        if (Objects.isNull(subscribedCardId)) {
            log.error("Disconnected - Cannot delete " + type);
            return false;
        }
        return handler.delete(id, type, boardId);
    }

    public boolean addTag(Object object,DataType type) {
        if (Objects.isNull(subscribedCardId)) {
            log.error("Disconnected - Cannot add " + type);
            return false;
        }
        return handler.addTag(object, boardId);
    }

    public boolean deleteTag(Object object,DataType type) {
        if (Objects.isNull(subscribedCardId)) {
            log.error("Disconnected - Cannot delete " + type);
            return false;
        }
        return handler.deleteTag(object, boardId);
    }


}
