package com.anton.tsarenko.payment.core.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.modulith.core.ApplicationModules;

/**
 * Tests for {@link AppLauncher}.
 */
class AppLauncherTest {

    @Test
    void shouldHaveSpringBootApplicationAnnotation() {
        // Given
        Class<AppLauncher> appLauncherClass = AppLauncher.class;

        // When
        SpringBootApplication springBootApplication = appLauncherClass.getAnnotation(SpringBootApplication.class);
        ConfigurationPropertiesScan configurationPropertiesScan =
                appLauncherClass.getAnnotation(ConfigurationPropertiesScan.class);

        // Then
        assertThat(springBootApplication).isNotNull();
        assertThat(configurationPropertiesScan).isNotNull();
    }

    @Test
    void shouldRunSpringApplication() {
        // Given
        String[] args = {"--server.port=0"};

        try (MockedStatic<SpringApplication> springApplication = mockStatic(SpringApplication.class)) {
            ConfigurableApplicationContext applicationContext = mock(ConfigurableApplicationContext.class);
            springApplication.when(() -> SpringApplication.run(AppLauncher.class, args))
                    .thenReturn(applicationContext);

            // When
            AppLauncher.main(args);

            // Then
            springApplication.verify(() -> SpringApplication.run(AppLauncher.class, args));
        }
    }

    @Test
    void verifyModules() {
        ApplicationModules.of(AppLauncher.class)
                .forEach(System.out::println);

        ApplicationModules.of(AppLauncher.class)
                .verify();
    }
}
