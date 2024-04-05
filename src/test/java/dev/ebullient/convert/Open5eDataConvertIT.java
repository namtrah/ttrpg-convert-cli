package dev.ebullient.convert;

import org.junit.jupiter.api.BeforeAll;

import io.quarkus.test.junit.main.QuarkusMainIntegrationTest;

@QuarkusMainIntegrationTest
public class Open5eDataConvertIT extends Open5eDataConvertTest {
    @BeforeAll
    public static void setupDir() {
        setupDir("Open5eDataConvertIT");
    }
}
