package com.smartparkinglot;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

class AppTest {

    @Test
    void shouldRunDemoScenariosWithoutThrowing() {
        App app = new App();
        assertDoesNotThrow(() -> App.main(new String[0]));
        assertDoesNotThrow(() -> app.toString());
    }
}
