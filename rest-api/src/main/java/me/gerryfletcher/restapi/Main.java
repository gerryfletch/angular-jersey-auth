package me.gerryfletcher.restapi;

import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;

import java.io.IOException;
import java.net.URI;

public class Main {

    private static final String BASE_URI = "http://localhost:8080/api";

    public static void main(String[] args) throws IOException {
        final HttpServer server = getServer();

        System.in.read();

        server.shutdown();
    }

    private static HttpServer getServer() {
        return GrizzlyHttpServerFactory.createHttpServer(URI.create(BASE_URI), new CustomConfig());
    }
}
