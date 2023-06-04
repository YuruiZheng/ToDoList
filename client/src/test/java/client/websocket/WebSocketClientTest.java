package client.websocket;

import client.scenes.BoardCtrl;
import client.utils.SessionData;
import com.google.inject.Provider;
import commons.Card;
import commons.ColorPreset;
import commons.DataType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class WebSocketClientTest {
    @Mock
    private Provider<BoardCtrl> boardCtrlProvider;

    private SessionData sessionData = new SessionData();
    @Captor
    private ArgumentCaptor<WebSocketSessionHandler> handlerArgumentCaptor;
    private WebSocketSessionHandler handler;
    private WebSocketClient webSocketClient;


    @BeforeEach
    void init() {
        openMocks(this);
    }

    @Test
    void subscribeAndUnsubscribe() {
        try (MockedConstruction<WebSocketStompClient> mocked = mockConstruction(WebSocketStompClient.class)) {
            webSocketClient = spy(new WebSocketClient(boardCtrlProvider, sessionData));
            try (MockedConstruction<WebSocketSessionHandler> mockedHandler = mockConstruction(WebSocketSessionHandler.class)) {
                Assertions.assertDoesNotThrow(() -> webSocketClient.subscribeToBoard(1));
                verify(mocked.constructed().get(0)).connect(anyString(), any(WebSocketHttpHeaders.class), handlerArgumentCaptor.capture());
                handler = handlerArgumentCaptor.getValue();
                Assertions.assertDoesNotThrow(() -> webSocketClient.unsubscribe());
            }
        }
    }

    @Test
    void subscribeTwice()   {
        try (MockedConstruction<WebSocketStompClient> mocked = mockConstruction(WebSocketStompClient.class)) {
            webSocketClient = spy(new WebSocketClient(boardCtrlProvider, sessionData));
            try (MockedConstruction<WebSocketSessionHandler> mockedHandler = mockConstruction(WebSocketSessionHandler.class)) {
                Assertions.assertDoesNotThrow(() -> webSocketClient.subscribeToBoard(1));
                Assertions.assertDoesNotThrow(() -> webSocketClient.subscribeToBoard(1));
                verify(mocked.constructed().get(0), times(2)).connect(anyString(),
                        any(WebSocketHttpHeaders.class), handlerArgumentCaptor.capture());
            }
        }
    }

    @Test
    void create() {
        try (MockedConstruction<WebSocketStompClient> mocked = mockConstruction(WebSocketStompClient.class)) {
            webSocketClient = spy(new WebSocketClient(boardCtrlProvider, sessionData));
            try (MockedConstruction<WebSocketSessionHandler> mockedHandler = mockConstruction(WebSocketSessionHandler.class)) {
                Assertions.assertDoesNotThrow(() -> webSocketClient.subscribeToBoard(1));
                verify(mocked.constructed().get(0)).connect(anyString(), any(WebSocketHttpHeaders.class), handlerArgumentCaptor.capture());
                handler = handlerArgumentCaptor.getValue();
                Card card = new Card();
                webSocketClient.create(card, DataType.CARD);
                verify(handler).sendCreate(card, DataType.CARD);
                webSocketClient.unsubscribe();

                assertFalse(webSocketClient.create(card, DataType.CARD));
                verify(handler).sendCreate(card, DataType.CARD);

            }
        }
    }

    @Test
    void update() {
        try (MockedConstruction<WebSocketStompClient> mocked = mockConstruction(WebSocketStompClient.class)) {
            webSocketClient = spy(new WebSocketClient(boardCtrlProvider, sessionData));
            try (MockedConstruction<WebSocketSessionHandler> mockedHandler = mockConstruction(WebSocketSessionHandler.class)) {
                Assertions.assertDoesNotThrow(() -> webSocketClient.subscribeToBoard(1));
                verify(mocked.constructed().get(0)).connect(anyString(), any(WebSocketHttpHeaders.class), handlerArgumentCaptor.capture());
                handler = handlerArgumentCaptor.getValue();
                Card card = new Card();
                webSocketClient.update(card, DataType.CARD);
                verify(handler).sendUpdate(card, DataType.CARD);
                webSocketClient.unsubscribe();

                assertFalse(webSocketClient.update(card, DataType.CARD));
                verify(handler).sendUpdate(card, DataType.CARD);

            }
        }
    }

    @Test
    void delete() {
        try (MockedConstruction<WebSocketStompClient> mocked = mockConstruction(WebSocketStompClient.class)) {
            webSocketClient = spy(new WebSocketClient(boardCtrlProvider, sessionData));
            try (MockedConstruction<WebSocketSessionHandler> mockedHandler = mockConstruction(WebSocketSessionHandler.class)) {
                Assertions.assertDoesNotThrow(() -> webSocketClient.subscribeToBoard(1));
                verify(mocked.constructed().get(0)).connect(anyString(), any(WebSocketHttpHeaders.class), handlerArgumentCaptor.capture());
                handler = handlerArgumentCaptor.getValue();
                webSocketClient.delete(1, DataType.CARD);
                verify(handler).delete(1, DataType.CARD);
                webSocketClient.unsubscribe();

                assertFalse(webSocketClient.delete(1, DataType.CARD));
                verify(handler).delete(1, DataType.CARD);
            }
        }
    }

    @Test
    void deleteMultiple() {
        try (MockedConstruction<WebSocketStompClient> mocked = mockConstruction(WebSocketStompClient.class)) {
            webSocketClient = spy(new WebSocketClient(boardCtrlProvider, sessionData));
            try (MockedConstruction<WebSocketSessionHandler> mockedHandler = mockConstruction(WebSocketSessionHandler.class)) {
                Assertions.assertDoesNotThrow(() -> webSocketClient.subscribeToBoard(1));
                verify(mocked.constructed().get(0)).connect(anyString(), any(WebSocketHttpHeaders.class), handlerArgumentCaptor.capture());
                handler = handlerArgumentCaptor.getValue();

                List<Integer> ints = new ArrayList<>();
                webSocketClient.deleteMultiple(ints, DataType.COLORPRESET, 1);
                verify(handler).sendMultipleDeletes(ints, DataType.COLORPRESET, 1);
                webSocketClient.unsubscribe();

                assertFalse(webSocketClient.deleteMultiple(ints, DataType.COLORPRESET, 1));
                verify(handler).sendMultipleDeletes(ints, DataType.COLORPRESET, 1);

            }
        }
    }

    @Test
    void updateMultipleColorPresets() {
        try (MockedConstruction<WebSocketStompClient> mocked = mockConstruction(WebSocketStompClient.class)) {
            webSocketClient = spy(new WebSocketClient(boardCtrlProvider, sessionData));
            try (MockedConstruction<WebSocketSessionHandler> mockedHandler = mockConstruction(WebSocketSessionHandler.class)) {
                Assertions.assertDoesNotThrow(() -> webSocketClient.subscribeToBoard(1));
                verify(mocked.constructed().get(0)).connect(anyString(), any(WebSocketHttpHeaders.class), handlerArgumentCaptor.capture());
                handler = handlerArgumentCaptor.getValue();

                List<ColorPreset> colorPresets = new ArrayList<>();
                webSocketClient.updateMultipleColorPresets(colorPresets, DataType.COLORPRESET, 1);
                verify(handler).sendMultipleUpdatesColorPreset(colorPresets, DataType.COLORPRESET, 1);
                webSocketClient.unsubscribe();

                assertFalse(webSocketClient.updateMultipleColorPresets(colorPresets, DataType.COLORPRESET, 1));
                verify(handler).sendMultipleUpdatesColorPreset(colorPresets, DataType.COLORPRESET, 1);
            }
        }
    }
}