package me.gerryfletcher.restapi;

import org.glassfish.jersey.logging.LoggingFeature;
import org.glassfish.jersey.server.ResourceConfig;

class CustomConfig extends ResourceConfig {
    CustomConfig() {
        property(LoggingFeature.LOGGING_FEATURE_LOGGER_LEVEL_SERVER, "INFO");
        packages("me.gerryfletcher.restapi");
    }
}
