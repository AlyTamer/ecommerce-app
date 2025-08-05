package com.aly.ecomapp.testing;

import com.aly.ecomapp.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@Tag(name = "Test Controller",
        description = "Testing for swagger")
@RequestMapping("/api/test")
public class TestController {
    private final TestUserService testUserService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    @Autowired
    public TestController(TestUserService testUserService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.testUserService = testUserService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }
    @GetMapping("/")
    @Operation(
            summary = "Test Endpoint",
            description = "A simple endpoint to test the application and Swagger integration."
    )
    public String test() {
        return "Hello, this is a test endpoint!";
    }

    @PostMapping("/user")
    @Operation(
            summary="Create User test",
            description = "This endpoint is used to create a new user for testing purposes. " +
                    "It accepts a JSON object with username, email, and password fields. " +
                    "The password is hashed before saving the user."
    )
    public ResponseEntity<?> createUser(@RequestBody TestUserDTO inpUser) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .header(HttpHeaders.AUTHORIZATION, "Bearer " + testUserService.createUser(inpUser))
                .body(inpUser);

    }
    @PostMapping("/admin")
    public ResponseEntity<TestUserDTO> createAdmin(TestUserDTO inpUser) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .header(HttpHeaders.AUTHORIZATION,"Bearer "+ testUserService.createAdmin(inpUser))
                .body(inpUser);
    }
    @GetMapping("/testAdmin")
    @Operation(
            summary = "Admin Test Endpoint",
            description = "This endpoint is accessible only to users with admin privileges. " +
                    "It returns a simple message indicating that the admin test endpoint is working.",
            security = @SecurityRequirement(name = "bearerAuth")

    )
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String testAdmin() {
        return "This is an admin test endpoint";
    }

    @PostMapping("/changePassword")
    public ResponseEntity<?> changePassword(@RequestBody(required = true) String newPassword,
                                                  @RequestBody(required = true) String oldPassword,
                                                  @RequestHeader(required = true) Long id) {
        testUserService.changePassword(newPassword, oldPassword, id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body("Password changed successfully for user with ID: " + id);
    }
}
