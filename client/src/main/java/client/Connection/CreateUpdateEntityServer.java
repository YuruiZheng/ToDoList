package client.Connection;

import client.utils.SessionData;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import commons.Board;
import commons.ColorPreset;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Singleton
public class CreateUpdateEntityServer {

    private final SessionData sessionData;

    @Inject
    public CreateUpdateEntityServer(SessionData sessionData) {
        this.sessionData = sessionData;
    }

    public ColorPreset createColorPreset(ColorPreset colorPreset) {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://" + sessionData.getServer() + "/api/colorpreset");
        Response response = webTarget.request(MediaType.APPLICATION_JSON)
                .post(Entity.json(colorPreset));
        return response.readEntity(ColorPreset.class);
    }

    public Board createBoard(Board board)   {
        Client client = ClientBuilder.newClient();
        WebTarget webTarget = client.target("http://" + sessionData.getServer() + "/api/board");
        Response response = webTarget.request(MediaType.APPLICATION_JSON)
                .post(Entity.json(board));
        return response.readEntity(Board.class);
    }
}
