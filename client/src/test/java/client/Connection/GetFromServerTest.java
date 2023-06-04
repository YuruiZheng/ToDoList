package client.Connection;

import client.scenes.AdminCtrl;
import client.utils.SessionData;
import com.google.inject.Provider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class GetFromServerTest {

    private SessionData sessionData = new SessionData();
    @Mock
    private Provider<AdminCtrl> adminCtrlProvider;
    @Mock
    private AdminCtrl adminCtrl;
    @Mock
    private URL mockedURL;
    @Mock
    private HttpURLConnection mockConnection;
    private GetFromServer getFromServer;

    @BeforeEach
    void init() {
        openMocks(this);
        when(adminCtrlProvider.get()).thenReturn(adminCtrl);
        getFromServer = new GetFromServer(sessionData, adminCtrlProvider);
    }

    @Test
    void checkConnection() throws Exception {
        try (MockedConstruction<URL> mockedUrl = Mockito.mockConstruction(URL.class,
                (mock, context) -> when(mock.openConnection()).thenReturn(mockConnection))) {
            when(mockConnection.getResponseCode()).thenReturn(404);
            when(mockedURL.openConnection()).thenReturn(mockConnection);
            assertTrue(getFromServer.checkConnection());
            verify(mockConnection).connect();
        }
    }

    @Test
    void checkConnectionFalse() throws Exception {
        try (MockedConstruction<URL> mockedUrl = Mockito.mockConstruction(URL.class,
                (mock, context) -> when(mock.openConnection()).thenReturn(mockConnection))) {
            doThrow(IOException.class).when(mockConnection).connect();
            when(mockedURL.openConnection()).thenReturn(mockConnection);
            assertFalse(getFromServer.checkConnection());
            verify(mockConnection).connect();
        }
    }
}