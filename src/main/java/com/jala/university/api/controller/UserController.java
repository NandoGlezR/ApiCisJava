package com.jala.university.api.controller;

import com.jala.university.api.application.dto.UserCredentials;
import com.jala.university.api.application.dto.UserDto;
import com.jala.university.api.application.service.AuthServices;
import com.jala.university.api.application.service.UserService;
import com.jala.university.api.domain.exceptions.authentication.InvalidAuthenticationCredentialsException;
import com.jala.university.api.domain.exceptions.format.InvalidEmailFormatException;
import com.jala.university.api.domain.exceptions.format.InvalidPasswordFormatException;
import com.jala.university.api.domain.exceptions.user.UserAlreadyRegisteredException;
import com.jala.university.api.domain.exceptions.user.UserNotFoundException;
import com.jala.university.api.domain.exceptions.authentication.UserNotValidatedException;
import com.jala.university.api.infrastructure.persistence.security.JwtTokenProvider;
import com.jala.university.api.infrastructure.persistence.utils.CreateGroup;
import com.jala.university.api.infrastructure.persistence.utils.UpdateGroup;
import jakarta.mail.MessagingException;
import java.util.UUID;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Controller for user-related operations such as login, fetching, updating, and deleting
 * authenticated user data.
 */
@RestController
@RequestMapping("/users")
public final class UserController {

    private final AuthServices authService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserService userService;

    @Autowired
    public UserController(AuthServices authService, JwtTokenProvider jwtTokenProvider,
            UserService userService) {
        this.authService = authService;
        this.jwtTokenProvider = jwtTokenProvider;
        this.userService = userService;
    }

    @Operation(summary = "Create a new user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created",
              content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
        @ApiResponse(responseCode = "400", description = "Invalid format or empty email or password",
              content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "409", description = "User already registered",
              content = @Content(mediaType = "application/json")),
        @ApiResponse(responseCode = "500", description = "Error at sending email",
              content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/register")
    public ResponseEntity<Object> createUser(@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "User details", required = true)
                                         @Validated(CreateGroup.class) @RequestBody UserDto userDto) {
        try {
            UserDto user = userService.createUser(userDto);

            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (InvalidEmailFormatException | InvalidPasswordFormatException e) {
          return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (UserAlreadyRegisteredException ex) {
          return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
        } catch (MessagingException me) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @Operation(summary = "Get user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User found",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json"))
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable("id") String userId) {
        try {
            UserDto user = userService.getUserById(userId);
            return ResponseEntity.ok(user);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @Operation(summary = "Update user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User updated",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "Invalid email or password format",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "500", description = "Error at sending email",
                    content = @Content(mediaType = "application/json")),
    })

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateUser(@PathVariable("id") String userId,
        @Validated(UpdateGroup.class) @RequestBody UserDto userDto) {
        try {
            UserDto updatedUser = userService.updateUser(userId, userDto);

            return ResponseEntity.ok(updatedUser);
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (InvalidEmailFormatException | InvalidPasswordFormatException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        } catch (MessagingException e) {
            return  ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @Operation(summary = "Delete user by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "User deleted",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json"))
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable("id") String userId) {
        try {
            userService.deleteUser(userId);
            return ResponseEntity.ok("User deleted");
        } catch (UserNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @Operation(summary = "Login user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login successful",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Invalid Credentials",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "User not validated",
                    content = @Content(mediaType = "application/json"))
    })
    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@RequestBody UserCredentials credentials) {

        try {
            UserDto user = authService.login(credentials);
            String token = jwtTokenProvider.generateToken(user.getId());

            return ResponseEntity.status(HttpStatus.OK)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("{\"token\": \"" + token + "\"}");
        } catch (InvalidAuthenticationCredentialsException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .contentType(MediaType.APPLICATION_JSON)
                    .body("Invalid Credentials: " + e.getMessage());
        } catch (UserNotValidatedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage());
        }
    }

    @Operation(summary = "Validate user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Validation successful",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "403", description = "Invalid or expired token",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User not found",
                    content = @Content(mediaType = "application/json"))
    })
    @PatchMapping("/email-validation")
    public ResponseEntity<String> validateEmail(@RequestParam UUID token) {
        if (userService.validateUserEmail(token)) {
            return ResponseEntity.ok("The user was validated");
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Invalid or expired token");
    }
}
