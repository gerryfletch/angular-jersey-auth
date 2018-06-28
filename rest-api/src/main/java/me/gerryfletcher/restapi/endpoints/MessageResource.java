package me.gerryfletcher.restapi.endpoints;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.gerryfletcher.restapi.authentication.Role;
import me.gerryfletcher.restapi.config.Secured;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("message")
public class MessageResource {

//    private static String publicMessage = "Hello, world.";
    private static String publicMessage = "Hello, world.";
    private static String privateMessage = "You're a logged in user, accessing a private message.";

    private Gson gson;

    public MessageResource() {
        this.gson = new Gson();
    }

    /**
     * @return The public message to anyone who wants it.
     */
    @GET
    @Path("public")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPublicMessage() {
        return formMessageResponse(publicMessage);
    }

    /**
     * @return The private message for logged in users only.
     */
    @GET
    @Secured({Role.USER})
    @Path("private")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPrivateMessage() {
        return formMessageResponse(privateMessage);
    }


    private Response formMessageResponse(String message) {
        JsonObject response = new JsonObject();
        response.addProperty("message", message);
        return Response.ok().entity(gson.toJson(response)).build();
    }
}
