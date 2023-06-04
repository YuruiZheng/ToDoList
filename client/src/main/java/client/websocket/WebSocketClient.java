package client.websocket;

import client.scenes.BoardCtrl;
import client.utils.SessionData;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import commons.ColorPreset;
import commons.DataType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import javax.inject.Inject;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Singleton
public class WebSocketClient {

    private final Logger log = LoggerFactory.getLogger(WebSocketClient.class);

    private final WebSocketStompClient stompClient;
    private final Provider<BoardCtrl> boardCtrlProvider;
    private WebSocketSessionHandler handler;
    private final SessionData sessionData;

    @Inject
    public WebSocketClient(Provider<BoardCtrl> boardCtrlProvider, SessionData sessionData) {
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
        this.boardCtrlProvider = boardCtrlProvider;
        this.sessionData = sessionData;
    }

    public void subscribeToBoard(Integer id) {
        if (!Objects.isNull(sessionData.getSubscribedBoardId()))
            this.stompClient.stop();
        final AtomicReference<Throwable> failure = new AtomicReference<>();
        handler = new WebSocketSessionHandler(failure, id, boardCtrlProvider);
        stompClient.connect("ws://" + sessionData.getServer() + "/api", new WebSocketHttpHeaders(), handler);
        sessionData.setSubscribedBoardId(id);
    }

    public void unsubscribe() {
        if (sessionData.getSubscribedBoardId() != null) {
            stompClient.stop();
            handler.unsubscribe();
            sessionData.setSubscribedBoardId(null);
        }
    }

    public boolean create(Object object, DataType type) {
        if (Objects.isNull(sessionData.getSubscribedBoardId())) {
            log.error("Disconnected - Cannot create " + type);
            return false;
        }
        return handler.sendCreate(object, type);
    }

    public boolean update(Object object, DataType type) {
        if (Objects.isNull(sessionData.getSubscribedBoardId())) {
            log.error("Disconnected - Cannot update " + type);
            return false;
        }
        return handler.sendUpdate(object, type);
    }

    public boolean delete(Integer id, DataType type) {
        if (Objects.isNull(sessionData.getSubscribedBoardId())) {
            log.error("Disconnected - Cannot delete " + type);
            return false;
        }
        return handler.delete(id, type);
    }

    public boolean deleteMultiple(List<Integer> ids, DataType type, int boardId) {
        if (Objects.isNull(sessionData.getSubscribedBoardId())) {
            log.error("Disconnected - Cannot delete " + type);
            return false;
        }
        return handler.sendMultipleDeletes(ids, type, boardId);
    }

    public boolean updateMultipleColorPresets(List<ColorPreset> colorPresets, DataType type, int boardId) {
        if (Objects.isNull(sessionData.getSubscribedBoardId())) {
            log.error("Disconnected - Cannot delete " + type);
            return false;
        }
        return handler.sendMultipleUpdatesColorPreset(colorPresets, type, boardId);
    }
}
