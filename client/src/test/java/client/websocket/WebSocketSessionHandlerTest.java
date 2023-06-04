package client.websocket;

import client.scenes.BoardCtrl;
import client.scenes.CardDescriptionCtrl;
import com.google.inject.Provider;
import commons.Board;
import commons.Card;
import commons.ColorPreset;
import commons.DataType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.springframework.messaging.MessageDeliveryException;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.openMocks;

class WebSocketSessionHandlerTest {
    private WebSocketSessionHandler handler;
    private StompHeaders stompHeaders = new StompHeaders();
    @Mock
    private BoardCtrl boardCtrl;
    @Mock
    private CardDescriptionCtrl cardDescriptionCtrl;

    private Provider<BoardCtrl> boardCtrlProvider;
    private Provider<CardDescriptionCtrl> cardDescriptionCtrlProvider;
    @Mock
    private StompSession stompSession;

    @Captor
    private ArgumentCaptor<StompFrameHandler> stompFrameHandlerArgumentCaptor;

    private StompSession.Subscription subscription = new StompSession.Subscription() {
        public String getSubscriptionId() {return "1";}

        public StompHeaders getSubscriptionHeaders() {return null;}

        public void unsubscribe() {}

        public void unsubscribe(StompHeaders headers) {}

        public String getReceiptId() {return null;}

        public void addReceiptTask(Runnable runnable) {}

        public void addReceiptLostTask(Runnable runnable) {}
    };

    @BeforeEach
    void init() {
        openMocks(this);
        boardCtrlProvider = () -> boardCtrl;
        cardDescriptionCtrlProvider = () -> cardDescriptionCtrl;
    }

    @Test
    void initBoard()    {
        handler = new WebSocketSessionHandler(new AtomicReference<>(), 1, boardCtrlProvider);
        assertEquals(Board.class, handler.getPayloadType(stompHeaders));
    }

    @Test
    void connect()  {
        handler = new WebSocketSessionHandler(new AtomicReference<>(), 1, boardCtrlProvider);
        assertDoesNotThrow(() -> handler.afterConnected(stompSession, stompHeaders));
    }

    @Test
    void connectInvalid()  {
        handler = new WebSocketSessionHandler(new AtomicReference<>(), 1, () -> null);
        assertDoesNotThrow(() -> handler.afterConnected(stompSession, stompHeaders));
    }

    @Test
    void handle()   {
        handler = new WebSocketSessionHandler(new AtomicReference<>(), 1, () -> null);
        assertDoesNotThrow(() -> handler.handleException(stompSession, StompCommand.ERROR,
                stompHeaders, new byte[1], new Throwable()));
        assertDoesNotThrow(() -> handler.handleFrame(stompHeaders, new Object()));
        assertDoesNotThrow(() -> handler.handleTransportError(stompSession, new Throwable()));
    }

    @Test
    void testBoardSubscribe()   {
        handler = new WebSocketSessionHandler(new AtomicReference<>(), 1, boardCtrlProvider);
        when(stompSession.subscribe(anyString(), stompFrameHandlerArgumentCaptor.capture()))
                .thenReturn(subscription);
        assertDoesNotThrow(() -> handler.afterConnected(stompSession, stompHeaders));
        StompFrameHandler stompFrameHandler = stompFrameHandlerArgumentCaptor.getValue();
        assertEquals(Board.class, stompFrameHandler.getPayloadType(stompHeaders));

        Board board = new Board();
        board.setId(1); // updated board
        stompFrameHandler.handleFrame(stompHeaders, board);
        verify(boardCtrl).displayUpdatedBoard(board);
        board.setId(-1); // deleted board
        stompFrameHandler.handleFrame(stompHeaders, board);
        verify(boardCtrl).exitBoard();
        verify(boardCtrl).deleteFromTree();
    }

    @Test
    void testCardSubscribe()   {
        handler = new WebSocketSessionHandler(new AtomicReference<>(), 1, cardDescriptionCtrlProvider);
        assertDoesNotThrow(() -> handler.afterConnected(stompSession, stompHeaders));
        verify(stompSession).subscribe(anyString(), stompFrameHandlerArgumentCaptor.capture());
        StompFrameHandler stompFrameHandler = stompFrameHandlerArgumentCaptor.getValue();
        assertEquals(Card.class, stompFrameHandler.getPayloadType(stompHeaders));

        Card card = new Card();
        card.setId(1);
        stompFrameHandler.handleFrame(stompHeaders, card);
        verify(cardDescriptionCtrl).displayUpdatedCard(card);
    }

    @Test
    void sendFails()   {
        handler = new WebSocketSessionHandler(new AtomicReference<>(), 1, boardCtrlProvider);
        assertDoesNotThrow(() -> handler.afterConnected(stompSession, stompHeaders));
        verify(stompSession).subscribe(anyString(), stompFrameHandlerArgumentCaptor.capture());
        Card card = new Card();
        List<Integer> ints = new ArrayList<>();
        List<ColorPreset> colorPresets = new ArrayList<>();

        when(stompSession.send("/create/card/1", card)).thenThrow(new MessageDeliveryException("failed"));
        when(stompSession.send("/delete/card/1", 1)).thenThrow(new MessageDeliveryException("failed"));
        when(stompSession.send("/update/card/1", card)).thenThrow(new MessageDeliveryException("failed"));
        when(stompSession.send("/delete-multiple/card/1", ints)).thenThrow(new MessageDeliveryException("failed"));
        when(stompSession.send("/update-multiple/colorpreset/1", colorPresets)).thenThrow(new MessageDeliveryException("failed"));

        assertFalse(handler.sendCreate(card, DataType.CARD, 1));
        assertFalse(handler.sendUpdate(card, DataType.CARD, 1));
        assertFalse(handler.delete(1, DataType.CARD, 1));

        assertFalse(handler.sendCreate(card, DataType.CARD));
        assertFalse(handler.sendUpdate(card, DataType.CARD));
        assertFalse(handler.delete(1, DataType.CARD));

        assertFalse(handler.sendMultipleDeletes(ints, DataType.CARD, 1));
        assertFalse(handler.sendMultipleUpdatesColorPreset(colorPresets, DataType.COLORPRESET, 1));
    }

    @Test
    void sendSuccessful()   {
        handler = new WebSocketSessionHandler(new AtomicReference<>(), 1, boardCtrlProvider);
        assertDoesNotThrow(() -> handler.afterConnected(stompSession, stompHeaders));
        verify(stompSession).subscribe(anyString(), stompFrameHandlerArgumentCaptor.capture());
        Card card = new Card();
        assertTrue(handler.sendCreate(card, DataType.CARD, 1));
        assertTrue(handler.sendCreate(card, DataType.CARD));
        assertTrue(handler.sendUpdate(card, DataType.CARD));
        assertTrue(handler.sendUpdate(card, DataType.CARD, 1));
        assertTrue(handler.delete(1, DataType.CARD, 1));
        assertTrue(handler.delete(1, DataType.CARD));
        List<Integer> ints = new ArrayList<>();
        assertTrue(handler.sendMultipleDeletes(ints, DataType.COLORPRESET, 1));
        List<ColorPreset> colorPresets = new ArrayList<>();
        assertTrue(handler.sendMultipleUpdatesColorPreset(colorPresets, DataType.COLORPRESET, 1));
    }

}