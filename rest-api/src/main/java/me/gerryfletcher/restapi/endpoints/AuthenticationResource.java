package me.gerryfletcher.restapi.endpoints;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import me.gerryfletcher.restapi.authentication.AuthenticationService;
import me.gerryfletcher.restapi.authentication.Role;
import me.gerryfletcher.restapi.exceptions.InvalidLoginException;
import me.gerryfletcher.restapi.models.AuthTokens;
import me.gerryfletcher.restapi.models.User;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;

@Path("auth")
public class AuthenticationResource {

    private AuthenticationService authService;
    private Gson gson;

    public AuthenticationResource() {
        this.authService = new AuthenticationService();
        this.gson = new Gson();
    }

    @POST
    @Path("login")
    @PermitAll
    @Produces(MediaType.APPLICATION_JSON)
    public Response login(@NotNull @FormParam("username") String username, @NotNull @FormParam("password") String password) throws InvalidLoginException, JWTCreationException {
        AuthTokens authTokens = authService.login(username, password);
        return Response.ok().entity(gson.toJson(formResponse(authTokens))).build();
    }

    @GET
    @Path("refresh")
    @RolesAllowed(Role.REFRESH)
    @Produces(MediaType.APPLICATION_JSON)
    public Response refreshAccessToken(@Context SecurityContext context) throws JWTCreationException {
        User user = (User) context.getUserPrincipal();

        String accessToken = authService.refreshAccessToken(user.getName(), user.getRole());
        String accessTokenResponse = gson.toJson(formAccessTokenResponse(accessToken));

        return Response.ok().entity(accessTokenResponse).build();
    }

    private JsonObject formResponse(AuthTokens authTokens) throws JWTCreationException {
        JsonObject result = new JsonObject();
        result.addProperty("access_token", authTokens.getAccessToken());
        result.addProperty("refresh_token", authTokens.getRefreshToken());
        return result;
    }

    private JsonObject formAccessTokenResponse(String accessToken) {
        JsonObject result = new JsonObject();
        result.addProperty("access_token", accessToken);
        return result;
    }

    @Path("revoke/{user}")
    @RolesAllowed(Role.ADMIN)
    @POST
    public Response revokeUserPriveleges(@PathParam("user") String user) {
        return Response.ok().build();
    }

}
