package client.websocket;

import client.scenes.BoardCtrl;
import client.scenes.CardDescriptionCtrl;
import client.utils.SessionData;
import com.google.inject.Provider;
import commons.Card;
import commons.DataType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class CardWebSocketClientTest {
    @Mock
    private Provider<CardDescriptionCtrl> cardDescriptionCtrlProvider;

    private SessionData sessionData = new SessionData();
    @Captor
    private ArgumentCaptor<WebSocketSessionHandler> handlerArgumentCaptor;
    private WebSocketSessionHandler handler;
    private CardWebSocketClient webSocketClient;


    @BeforeEach
    void init() {
        openMocks(this);
    }

    @Test
    void subscribeAndUnsubscribe() {
        try (MockedConstruction<WebSocketStompClient> mocked = mockConstruction(WebSocketStompClient.class)) {
            webSocketClient = spy(new CardWebSocketClient(cardDescriptionCtrlProvider, sessionData));
            try (MockedConstruction<WebSocketSessionHandler> mockedHandler = mockConstruction(WebSocketSessionHandler.class)) {
                Assertions.assertDoesNotThrow(() -> webSocketClient.subscribeToCard(1, 1));
                verify(mocked.constructed().get(0)).connect(anyString(), any(WebSocketHttpHeaders.class), handlerArgumentCaptor.capture());
                handler = handlerArgumentCaptor.getValue();
                Assertions.assertDoesNotThrow(() -> webSocketClient.unsubscribe());
            }
        }
    }

    @Test
    void subscribeTwice()   {
        try (MockedConstruction<WebSocketStompClient> mocked = mockConstruction(WebSocketStompClient.class)) {
            webSocketClient = spy(new CardWebSocketClient(cardDescriptionCtrlProvider, sessionData));
            try (MockedConstruction<WebSocketSessionHandler> mockedHandler = mockConstruction(WebSocketSessionHandler.class)) {
                Assertions.assertDoesNotThrow(() -> webSocketClient.subscribeToCard(1, 1));
                Assertions.assertDoesNotThrow(() -> webSocketClient.subscribeToCard(1, 1));
                verify(mocked.constructed().get(0), times(2)).connect(anyString(),
                        any(WebSocketHttpHeaders.class), handlerArgumentCaptor.capture());
            }
        }
    }

    @Test
    void create() {
        try (MockedConstruction<WebSocketStompClient> mocked = mockConstruction(WebSocketStompClient.class)) {
            webSocketClient = spy(new CardWebSocketClient(cardDescriptionCtrlProvider, sessionData));
            try (MockedConstruction<WebSocketSessionHandler> mockedHandler = mockConstruction(WebSocketSessionHandler.class)) {
                Assertions.assertDoesNotThrow(() -> webSocketClient.subscribeToCard(1, 1));
                verify(mocked.constructed().get(0)).connect(anyString(), any(WebSocketHttpHeaders.class), handlerArgumentCaptor.capture());
                handler = handlerArgumentCaptor.getValue();
                Card card = new Card();
                webSocketClient.create(card, DataType.CARD);
                verify(handler).sendCreate(card, DataType.CARD, 1);
                webSocketClient.unsubscribe();

                assertFalse(webSocketClient.create(card, DataType.CARD));
                verify(handler).sendCreate(card, DataType.CARD, 1);

            }
        }
    }

    @Test
    void update() {
        try (MockedConstruction<WebSocketStompClient> mocked = mockConstruction(WebSocketStompClient.class)) {
            webSocketClient = spy(new CardWebSocketClient(cardDescriptionCtrlProvider, sessionData));
            try (MockedConstruction<WebSocketSessionHandler> mockedHandler = mockConstruction(WebSocketSessionHandler.class)) {
                Assertions.assertDoesNotThrow(() -> webSocketClient.subscribeToCard(1, 1));
                verify(mocked.constructed().get(0)).connect(anyString(), any(WebSocketHttpHeaders.class), handlerArgumentCaptor.capture());
                handler = handlerArgumentCaptor.getValue();
                Card card = new Card();
                webSocketClient.update(card, DataType.CARD);
                verify(handler).sendUpdate(card, DataType.CARD, 1);
                webSocketClient.unsubscribe();

                assertFalse(webSocketClient.update(card, DataType.CARD));
                verify(handler).sendUpdate(card, DataType.CARD, 1);

            }
        }
    }

    @Test
    void delete() {
        try (MockedConstruction<WebSocketStompClient> mocked = mockConstruction(WebSocketStompClient.class)) {
            webSocketClient = spy(new CardWebSocketClient(cardDescriptionCtrlProvider, sessionData));
            try (MockedConstruction<WebSocketSessionHandler> mockedHandler = mockConstruction(WebSocketSessionHandler.class)) {
                Assertions.assertDoesNotThrow(() -> webSocketClient.subscribeToCard(1, 1));
                verify(mocked.constructed().get(0)).connect(anyString(), any(WebSocketHttpHeaders.class), handlerArgumentCaptor.capture());
                handler = handlerArgumentCaptor.getValue();
                webSocketClient.delete(1, DataType.CARD);
                verify(handler).delete(1, DataType.CARD, 1);
                webSocketClient.unsubscribe();

                assertFalse(webSocketClient.delete(1, DataType.CARD));
                verify(handler).delete(1, DataType.CARD, 1);
            }
        }
    }
}