package me.gerryfletcher.restapi.config;

import me.gerryfletcher.restapi.authentication.AuthenticationFilter;
import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;

public class CustomConfig extends ResourceConfig {
    public CustomConfig() {
        property(LoggingFeature.LOGGING_FEATURE_LOGGER_LEVEL_SERVER, "INFO");
        packages("me.gerryfletcher.restapi");
        register(AuthenticationFilter.class);
    }
}