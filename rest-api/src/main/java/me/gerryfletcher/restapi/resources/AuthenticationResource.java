package me.gerryfletcher.restapi.resources;

import com.auth0.jwt.exceptions.JWTCreationException;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import me.gerryfletcher.restapi.authentication.LoginService;
import me.gerryfletcher.restapi.authentication.Role;
import me.gerryfletcher.restapi.authentication.TokenService;
import me.gerryfletcher.restapi.exceptions.InvalidLoginException;
import me.gerryfletcher.restapi.models.AuthTokens;

import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("auth")
public class AuthenticationResource {

    private LoginService loginService;
    private Gson gson;

    public AuthenticationResource() {
        this.loginService = new LoginService(new TokenService());
        this.gson = new Gson();
    }

    @GET
    @Path("login")
    @PermitAll
    public Response login(@QueryParam("username") String username, @QueryParam("password") String password) throws InvalidLoginException, JWTCreationException {
        AuthTokens authTokens = loginService.login(username, password);
        JsonObject result = new JsonObject();
        result.addProperty("access-token", authTokens.getAccessToken());
        result.addProperty("refresh-token", authTokens.getRefreshToken());

        return Response.ok().entity(gson.toJson(result)).build();
    }

//    @GET
//    @Path("refresh")
//    @RolesAllowed(Role.USER)
//    public Response getRefreshToken() {
//
//    }

}
