package com.aly.ecomapp.deletelater;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Test Controller",
        description = "Testing for swagger")
public class TestController {
    @GetMapping("/")
    @Operation(
            summary = "Test Endpoint",
            description = "A simple endpoint to test the application and Swagger integration."
    )
    public String test() {
        return "Hello, this is a test endpoint!";
    }
}
