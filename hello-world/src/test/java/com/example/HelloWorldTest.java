package com.example;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

class HelloWorldTest {

    @Test
    void greetReturnsHelloWorld() {
        assertEquals("Hello, World!", HelloWorld.greet());
    }
}
