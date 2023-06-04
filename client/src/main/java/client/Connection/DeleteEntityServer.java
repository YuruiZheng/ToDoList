package client.Connection;

import client.utils.SessionData;
import com.google.inject.Inject;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class DeleteEntityServer {
    private final Logger log = LoggerFactory.getLogger(DeleteEntityServer.class);

    private final SessionData sessionData;

    @Inject
    public DeleteEntityServer(SessionData sessionData) {
        this.sessionData = sessionData;
    }

    public void delete(Integer id, String entity)   {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://" + sessionData.getServer() + "/api/" + entity + "/" + id);
        Invocation.Builder invocationBuilder
                = webTarget.request(MediaType.APPLICATION_JSON);
        invocationBuilder.delete();
    }

}
