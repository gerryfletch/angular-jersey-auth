package me.gerryfletcher.restapi.resources;

import me.gerryfletcher.restapi.authentication.Role;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("message")
public class MessageResource {

    private static String publicMessage = "Hello, world.";
    private static String privateMessage = "You're a logged in user!";

    /**
     * @return The public message to anyone who wants it.
     */
    @GET
    @PermitAll
    @Path("public")
    public String getPublicMessage() {
        return publicMessage;
    }

    /**
     * @return The private message for logged in users only.
     */
    @GET
    @RolesAllowed(Role.USER)
    @Path("private")
    public String getPrivateMessage() {
        return privateMessage;
    }

    /**
     * Update the public message. Only admins can do this.
     *
     * @param message The new message.
     * @return 200 OK for a non-empty message, 400 bad request otherwise.
     */
    @PUT
    @RolesAllowed(Role.ADMIN)
    @Path("public")
    public Response setPublicMessage(@QueryParam("message") String message) {
        if (message == null || message.isEmpty()) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("You must specify a non-empty message.")
                    .build();
        }

        publicMessage = message;
        return Response.status(Response.Status.NO_CONTENT).build();
    }
}
