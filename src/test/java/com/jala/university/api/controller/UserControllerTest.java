package com.jala.university.api.controller;

import com.jala.university.api.application.dto.UserCredentials;
import com.jala.university.api.application.dto.UserDto;
import com.jala.university.api.application.service.AuthServices;
import com.jala.university.api.application.service.UserService;
import com.jala.university.api.domain.exceptions.authentication.InvalidAuthenticationCredentialsException;
import com.jala.university.api.domain.exceptions.authentication.UserNotValidatedException;
import com.jala.university.api.domain.exceptions.format.InvalidEmailFormatException;
import com.jala.university.api.domain.exceptions.format.InvalidPasswordFormatException;
import com.jala.university.api.domain.exceptions.user.UserAlreadyRegisteredException;
import com.jala.university.api.domain.exceptions.user.UserNotFoundException;
import com.jala.university.api.infrastructure.persistence.security.JwtTokenProvider;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class UserControllerTest {
    @Mock
    private AuthServices authService;
    @Mock
    private UserService userService;
    @Mock
    private JwtTokenProvider jwtTokenProvider;
    @InjectMocks
    private UserController userController;

    private UserDto testUserDto;
    private UserDto invalidUserDto;
    private UserDto createdUser;
    private String testToken;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUserDto = UserDto.builder()
                .id("1")
                .email("testuser@example.com")
                .password("password123")
                .build();

        invalidUserDto = UserDto.builder()
                .id("1")
                .email("invalid-email")
                .password("validpassword123")
                .build();

        createdUser = UserDto.builder()
                .id("1")
                .email("newuser@example.com")
                .build();

        testToken = "Bearer mockJwtToken";
    }

    @Test
    void testLoginUser_Success() throws Exception {
        UserCredentials credentials = UserCredentials.builder()
            .login(testUserDto.getEmail())
            .password(testUserDto.getPassword())
            .build();
        when(authService.login(any(UserCredentials.class))).thenReturn(testUserDto);
        when(jwtTokenProvider.generateToken(anyString())).thenReturn(testToken);

        ResponseEntity<String> response = userController.loginUser(credentials);

        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        assertTrue(response.getBody().contains(testToken));

        verify(authService).login(credentials);
        verify(jwtTokenProvider).generateToken(testUserDto.getId());
    }

    @Test
    void testLoginUser_InvalidCredentials()
            throws InvalidAuthenticationCredentialsException, UserNotValidatedException {
        UserCredentials credentials = UserCredentials.builder()
            .login(testUserDto.getEmail())
            .password(testUserDto.getPassword())
            .build();
        when(authService.login(any(UserCredentials.class)))
            .thenThrow(new InvalidAuthenticationCredentialsException("Invalid credentials"));

        ResponseEntity<String> response = userController.loginUser(credentials);

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatusCodeValue());
        assertEquals("Invalid Credentials: Invalid credentials", response.getBody());

        verify(authService).login(credentials);
        verify(jwtTokenProvider, never()).generateToken(anyString());
    }

    @Test
    void testGetUser_Success() throws UserNotFoundException {
        when(userService.getUserById(testUserDto.getId())).thenReturn(testUserDto);

        ResponseEntity<UserDto> response = userController.getUser(testUserDto.getId());

        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        assertEquals(testUserDto, response.getBody());

        verify(userService).getUserById(testUserDto.getId());
    }

    @Test
    void testGetUser_UserNotFound() throws UserNotFoundException {
        when(userService.getUserById(testUserDto.getId())).thenThrow(new UserNotFoundException());

        ResponseEntity<UserDto> response = userController.getUser(testUserDto.getId());

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
        assertNull(response.getBody());

        verify(userService).getUserById(testUserDto.getId());
    }

    @Test
    void testUpdateUser_InvalidEmailFormat()
            throws UserNotFoundException, InvalidEmailFormatException, InvalidPasswordFormatException, MessagingException {
        when(userService.updateUser(eq(testUserDto.getId()), any(UserDto.class)))
                .thenThrow(new InvalidEmailFormatException("Invalid email format"));

        ResponseEntity<Object> response = userController.updateUser(testUserDto.getId(), testUserDto);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
        assertEquals("Invalid email format", response.getBody());

        verify(userService).updateUser(testUserDto.getId(), testUserDto);
    }

    @Test
    void testUpdateUser_InvalidPasswordFormat()
            throws UserNotFoundException, InvalidEmailFormatException, InvalidPasswordFormatException, MessagingException {
        when(userService.updateUser(eq(testUserDto.getId()), any(UserDto.class)))
                .thenThrow(new InvalidPasswordFormatException("Invalid password format"));

        ResponseEntity<Object> response = userController.updateUser(testUserDto.getId(), testUserDto);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
        assertEquals("Invalid password format", response.getBody());

        verify(userService).updateUser(testUserDto.getId(), testUserDto);
    }

    @Test
    void testDeleteUser_Success() throws Exception {
        when(userService.deleteUser(testUserDto.getId())).thenReturn("User deleted");

        ResponseEntity<String> response = userController.deleteUser(testUserDto.getId());

        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        assertEquals("User deleted", response.getBody());

        verify(userService).deleteUser(testUserDto.getId());
    }

    @Test
    void testDeleteUser_UserNotFound() throws UserNotFoundException {
        when(userService.deleteUser(testUserDto.getId())).thenThrow(new UserNotFoundException("User not found"));

        ResponseEntity<String> response = userController.deleteUser(testUserDto.getId());

        assertEquals(HttpStatus.NOT_FOUND.value(), response.getStatusCodeValue());
        assertNull(response.getBody());

        verify(userService).deleteUser(testUserDto.getId());
    }

    @Test
    void testCreateUser_InvalidEmailFormat()
            throws InvalidEmailFormatException, InvalidPasswordFormatException, UserAlreadyRegisteredException, MessagingException {
        Exception exception = new InvalidEmailFormatException("Invalid email format");

        when(userService.createUser(invalidUserDto))
                .thenThrow(exception);

        ResponseEntity<Object> response = userController.createUser(invalidUserDto);

        assertEquals(HttpStatus.BAD_REQUEST.value(), response.getStatusCodeValue());
        assertEquals("Invalid email format", response.getBody());

        verify(userService).createUser(invalidUserDto);
    }

    @Test
    void testCreateUser_Success()
            throws InvalidEmailFormatException, InvalidPasswordFormatException, UserAlreadyRegisteredException, MessagingException {
        when(userService.createUser(testUserDto)).thenReturn(createdUser);

        ResponseEntity<Object> response = userController.createUser(testUserDto);

        assertEquals(HttpStatus.CREATED.value(), response.getStatusCodeValue());
        assertEquals(createdUser, response.getBody());

        verify(userService).createUser(testUserDto);
    }

    @Test
    void testCreateAlreadyRegisteredUser()
            throws InvalidPasswordFormatException, UserAlreadyRegisteredException, InvalidEmailFormatException, MessagingException {
        Exception exception = new UserAlreadyRegisteredException("User already registered");

        when(userService.createUser(any())).thenThrow(exception);

        ResponseEntity<Object> response = userController.createUser(testUserDto);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("User already registered", response.getBody());
    }

    @Test
    void testUpdateNotFoundUser()
            throws UserNotFoundException, InvalidPasswordFormatException, InvalidEmailFormatException, MessagingException {
        when(userService.updateUser(any(), any())).thenThrow(new UserNotFoundException("User not found"));

        ResponseEntity<Object> response = userController.updateUser(testUserDto.getId(), testUserDto);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User not found", response.getBody());
    }

    @Test
    void testUpdateUserSuccess()
            throws UserNotFoundException, InvalidPasswordFormatException, InvalidEmailFormatException, MessagingException {
        when(userService.updateUser(testUserDto.getId(), testUserDto)).thenReturn(testUserDto);

        ResponseEntity<Object> response = userController.updateUser(testUserDto.getId(), testUserDto);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(testUserDto, response.getBody());
    }

    @Test
    void testValidateUserEmail_Success() {
        when(userService.validateUserEmail(any(UUID.class))).thenReturn(true);

        ResponseEntity<String> response = userController.validateEmail(UUID.randomUUID());

        assertEquals(HttpStatus.OK.value(), response.getStatusCodeValue());
        assertEquals("The user was validated", response.getBody());
    }

    @Test
    void testValidateUserEmail_Fail() {
        when(userService.validateUserEmail(any(UUID.class))).thenReturn(false);

        ResponseEntity<String> response = userController.validateEmail(UUID.randomUUID());

        assertEquals(HttpStatus.FORBIDDEN.value(), response.getStatusCodeValue());
        assertEquals("Invalid or expired token", response.getBody());
    }
}
