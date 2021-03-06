package me.gerryfletcher.restapi.endpoints;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.gerryfletcher.restapi.authentication.AuthenticationService;
import me.gerryfletcher.restapi.authentication.Role;
import me.gerryfletcher.restapi.authentication.UserSecurityContext;
import me.gerryfletcher.restapi.config.Secured;
import me.gerryfletcher.restapi.exceptions.InvalidLoginException;
import me.gerryfletcher.restapi.models.AuthTokens;
import me.gerryfletcher.restapi.permissions.UserPermission;
import me.gerryfletcher.restapi.permissions.PermissionAction;
import me.gerryfletcher.restapi.permissions.PermissionService;

import javax.validation.constraints.NotNull;
import javax.ws.rs.*;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("auth")
public class AuthenticationResource {

    private AuthenticationService authService;
    private PermissionService permissionService;
    private Gson gson;

    public AuthenticationResource() {
        this.authService = new AuthenticationService();
        this.permissionService = new PermissionService();
        this.gson = new Gson();
    }

    /**
     * @return A pair of JWTs on successful login.
     * @throws InvalidLoginException For incorrect credentials.
     */
    @POST
    @Path("login")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public Response login(@NotNull @FormParam("username") String username, @NotNull @FormParam("password") String password) {
        AuthTokens authTokens = authService.login(username, password);
        return Response.ok().entity(gson.toJson(formResponse(authTokens))).build();
    }

    /**
     * If the refresh token is valid, create and return an access token.
     * @param cont The user requesting a refresh.
     * @return A fresh access token.
     */
    @GET
    @Path("refresh")
    @Secured({Role.REFRESH})
    @Produces(MediaType.APPLICATION_JSON)
    public Response refreshAccessToken(@Context ContainerRequestContext cont) {
        UserSecurityContext context = (UserSecurityContext) cont.getSecurityContext();

        String accessToken = authService.refreshAccessToken(context.getToken());
        String accessTokenResponse = gson.toJson(formAccessTokenResponse(accessToken));

        return Response.ok().entity(accessTokenResponse).build();
    }

    private JsonObject formResponse(AuthTokens authTokens) {
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

    /**
     * Revoke a users account.
     */
    @Path("revoke/{user}")
    @Secured({Role.ADMIN})
    @POST
    public void revokeUser(@PathParam("user") String user) {
        UserPermission userPermission = new UserPermission(user, PermissionAction.REVOKE, "Example desc. User was disorderly.");
        this.permissionService.addUserPermission(userPermission);
    }

    /**
     * Re-enable a users account.
     */
    @Path("clear/{user}")
    @Secured({Role.ADMIN})
    @POST
    public void clearUser(@PathParam("user") String user) {
        this.addUserPermission(user, PermissionAction.CLEAR);
    }

    /**
     * Log a user out, invalidating their refresh tokens.
     */
    @Path("logout/{user}")
    @Secured({Role.ADMIN})
    @POST
    public void logoutUser(@PathParam("user") String user) {
        this.addUserPermission(user, PermissionAction.LOGOUT);
    }

    /**
     * Promote a user to the next tier.
     */
    @Path("promote/{user}")
    @Secured({Role.ADMIN})
    @POST
    public void promoteUser(@PathParam("user") String user) {
        this.addUserPermission(user, PermissionAction.PROMOTE);
    }

    /**
     * Demote a user to the previous tier.
     */
    @Path("demote/{user}")
    @Secured({Role.ADMIN})
    @POST
    public void demoteUser(@PathParam("user") String user) {
        this.addUserPermission(user, PermissionAction.DEMOTE);
    }

    private void addUserPermission(String username, PermissionAction action) {
        this.permissionService.addUserPermission(new UserPermission(username, action));
    }

}
