package com.anton.tsarenko.payment.core.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

/**
 * Main class of Payment Core API app.
 */
@SpringBootApplication
@ConfigurationPropertiesScan
public class AppLauncher {

    static void main(String[] args) {
        SpringApplication.run(AppLauncher.class, args);
    }
}
