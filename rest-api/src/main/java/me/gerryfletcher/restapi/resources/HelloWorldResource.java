package me.gerryfletcher.restapi.resources;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

@Path("hello")
public class HelloWorldResource {

    @GET
    @Path("world")
    public Response helloWorld() {
        return Response.ok().entity("Hello, world!").build();
    }

}
