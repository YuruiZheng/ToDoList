package server.api;

import commons.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandler;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.socket.WebSocketHttpHeaders;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import server.service.*;

import java.lang.reflect.Type;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class WebSocketsTest {

    @Value(value="${local.server.port}")
    private int port;

    private WebSocketStompClient stompClient;
    private final Board board = new Board(1, "myBoardName", "myPassword", "000000", "000000", "000000", "000000", null);
    private final Card card = new Card(1, 1, "title1", "desc1","000000", null);
    private final Subtask subtask = new Subtask(1, 1, "title1", false);
    private final Tag tag = new Tag(1, 1, "myTagTitle", "000000");
    private final ToDoList list = new ToDoList(1, board.getId(),"list", List.of(card));
    private final ColorPreset cp = new ColorPreset(1, board.getId(), "preset1", "ffffff", "000000");

    @MockBean
    private CardService cardService;
    @MockBean
    private BoardService boardService;
    @MockBean
    private SubtaskService subtaskService;
    @MockBean
    private TagService tagService;
    @MockBean
    private ToDoListService toDoListService;


    @Autowired
    private WebApplicationContext webApplicationContext;
    private static MockMvc mockMvc;

    @BeforeEach
    void init() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        stompClient = new WebSocketStompClient(new StandardWebSocketClient());
        stompClient.setMessageConverter(new MappingJackson2MessageConverter());
    }

    @ParameterizedTest
    @CsvSource({"update,board,1,0", "delete,board,-1,0",
            "create,card,1,0", "update,card,1,1", "delete,card,1,-1",
            "create,subtask,1,1", "update,subtask,1,1", "delete,subtask,1,1",
            "create,tag,1,0", "update,tag,1,0", "delete,tag,1,0",
            "create,list,1,0", "update,list,1,0", "delete,list,1,0",
            "delete-multiple,colorpreset,0,0", "update-multiple,colorpreset,0,0"})
    void updateCardWebSocket(String operation, String type, String boardAns, String cardAns) throws Exception {
        Mockito.when(boardService.updateBoard(board)).thenReturn(board);
        Mockito.when(cardService.updateCard(card)).thenReturn(card);
        Mockito.when(cardService.createCard(card)).thenReturn(card);
        Mockito.when(boardService.getById(board.getId())).thenReturn(board);
        Mockito.when(cardService.getById(card.getId())).thenReturn(card);
        Mockito.when(subtaskService.getById(subtask.getId())).thenReturn(subtask);
        Object object = switch (type) {
            case "card" -> card;
            case "subtask" -> subtask;
            case "tag" -> tag;
            case "list" -> list;
            case "board" -> board;
            case "colorpreset" -> cp;
            default -> card;
        };

        final Object sentPayload = operation.equals("delete") ? 1 : object;

        final CountDownLatch latch = new CountDownLatch(1);
        final AtomicReference<Throwable> failure = new AtomicReference<>();
        final Board receivedBoard = new Board();
        final Card receivedCard = new Card();
        final StompSessionHandler handler = new TestSessionHandler(failure) {
            @Override
            public void afterConnected(final StompSession session, StompHeaders connectedHeaders) {
                session.subscribe("/updates/board/1", new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return Board.class;
                    }
                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        receivedBoard.setId(((Board)payload).getId());
                    }
                });

                session.subscribe("/updates/card/" + card.getId(), new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(StompHeaders headers) {
                        return Card.class;
                    }
                    @Override
                    public void handleFrame(StompHeaders headers, Object payload) {
                        receivedCard.setId(((Card)payload).getId());
                    }
                });
                try {
                    session.send("/" + operation + "/" + type + "/1", sentPayload);
                } catch (Throwable t) {
                    failure.set(t);
                    latch.countDown();
                }
            }
        };

        this.stompClient.connect("ws://localhost:{port}/api", new WebSocketHttpHeaders(),
                handler, port);

        latch.await(3, TimeUnit.SECONDS);
        if (boardAns.equals("1"))
            assertEquals(board.getId(), receivedBoard.getId());
        else if (boardAns.equals("-1"))
            assertEquals(-1, receivedBoard.getId());
        if (cardAns.equals("1"))
            assertEquals(card.getId(), receivedCard.getId());
        else if (cardAns.equals("-1"))
            assertEquals(-1, receivedCard.getId());
        else assertNull(receivedCard.getId());
    }

}
