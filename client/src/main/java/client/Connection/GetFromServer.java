package client.Connection;

import client.scenes.AdminCtrl;
import client.utils.SessionData;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.Singleton;
import commons.Board;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.GenericType;
import jakarta.ws.rs.core.HttpHeaders;
import jakarta.ws.rs.core.MediaType;
import javafx.application.Platform;
import javafx.scene.control.TableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@Singleton
public class GetFromServer {
    private final Logger log = LoggerFactory.getLogger(GetFromServer.class);
    private final SessionData sessionData;
    private final Provider<AdminCtrl> adminCtrlProvider;

    @Inject
    public GetFromServer(SessionData sessionData, Provider<AdminCtrl> adminCtrlProvider) {
        this.sessionData = sessionData;
        this.adminCtrlProvider = adminCtrlProvider;
    }

    public boolean checkConnection() {
        try {
            URL url = new URL("http://" + sessionData.getServer());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setInstanceFollowRedirects(false);
            connection.connect();
            return connection.getResponseCode() == HttpURLConnection.HTTP_NOT_FOUND;
        } catch (Exception ignored) {

        }
        return false;
    }

    /**
     * long polls for changes. Changes can be adding a board or deleting a board, either way it gets a response.
     */
    public void longPollChanges(TableView<Board> boardTable) {
        Thread thread = new Thread(new Runnable() {
            private volatile boolean running = true;

            // This method runs the long polling process to check for changes.
            @Override
            public void run() {
                try {
                    while (running) {
                        // Connect to the server and get the response code
                        changedBoards(boardTable);
                    }

                } catch (ConnectException e) {
                    // Handle connection lost by waiting and retrying
                    log.warn("Connection lost. Retrying in 5 seconds...");
                    try {
                        Thread.sleep(5000); // Wait 5 seconds before attempting to reconnect
                    } catch (InterruptedException interruptedException) {
                        // Check if the thread was interrupted due to client shutdown
                        if (!running) {
                            log.error("Thread was interrupted due to client shutdown");
                            return;
                        }
                    }
                } catch (IOException e) {
                    // Check if the thread was interrupted due to client shutdown
                    if (!running) {
                        log.warn("Thread was interrupted due to client shutdown");
                        return;
                    }
                    log.error(e.getMessage());
                }
            }

            //This method stops the long polling process.
            public void stopRunning() {
                running = false;
            }
        });
        thread.start();

        // Register a shutdown hook to stop the long-polling thread
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            thread.interrupt();
            try {
                thread.join();
            } catch (InterruptedException e) {
                log.error(e.getMessage());
            }
        }));
    }


    private void changedBoards(TableView<Board> boardTable) throws IOException {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client
                .target("http://" + sessionData.getServer() + "/api/board/long-poll-changes");

        List<Object> changes = webTarget.request(MediaType.APPLICATION_JSON)
                .header("Content-Type", "application/json")
                .header("Transfer-Encoding", "chunked")
                .header("Keep-Alive", "timeout=60, max=100")
                .header("Connection", "keep-alive")
                .header(HttpHeaders.AUTHORIZATION, sessionData.getRootPassword())
                .get(new GenericType<>() {
                });

        if (changes.size() > 0) {
            Platform.runLater(() -> {
                try {
                    adminCtrlProvider.get().refresh(boardTable);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public List<Board> getAllBoards() {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://" + sessionData.getServer() + "/api/board");
        return webTarget.request(MediaType.APPLICATION_JSON)
                .header(HttpHeaders.AUTHORIZATION, sessionData.getRootPassword())
                .get(new GenericType<>() {
                });
    }

    public Board getBoardById(Integer id) {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://" + sessionData.getServer() + "/api/board/" + id);
        return webTarget.request(MediaType.APPLICATION_JSON).get(Board.class);
    }

    public boolean checkRootPassword(String password) {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://" + sessionData.getServer() + "/api/root/" + password);
        try {
            return webTarget.request(MediaType.APPLICATION_JSON).get(Boolean.class);
        } catch (Exception e)   {
            return false;
        }
    }

}
