package com.jala.university.api.application.service;

import com.jala.university.api.application.dto.UserDto;
import com.jala.university.api.application.dto.IdentityValidationTokenDto;
import com.jala.university.api.application.service.impl.UserServiceImpl;
import com.jala.university.api.domain.entity.User;
import com.jala.university.api.domain.exceptions.format.InvalidEmailFormatException;
import com.jala.university.api.domain.exceptions.format.InvalidPasswordFormatException;
import com.jala.university.api.domain.exceptions.user.UserAlreadyRegisteredException;
import com.jala.university.api.domain.exceptions.user.UserNotFoundException;
import com.jala.university.api.domain.repository.UserRepository;
import jakarta.mail.MessagingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private ValidationService validationService;

    @Mock
    private TokenService tokenService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private UserServiceImpl userService;

    private User user;
    private UserDto userDto;
    private IdentityValidationTokenDto tokenDto;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        String id = UUID.randomUUID().toString();
        user = User.builder().id(id).name("Kratos").login("sparta@gmail.com").password("A7@d4mB2").build();
        userDto = UserDto.builder().name("Kratos").email("sparta@gmail.com").password("A7@d4mB2").build();
        tokenDto = IdentityValidationTokenDto.builder().token(String.valueOf(UUID.randomUUID())).build();
    }

    @Test
    void testCreateUserSuccess() throws InvalidPasswordFormatException, InvalidEmailFormatException,
        UserAlreadyRegisteredException, MessagingException, UserNotFoundException {
        when(passwordEncoder.encode(anyString())).thenReturn("A7@d4mB2");
        when(userRepository.save(any(User.class))).thenReturn(user);
        when(tokenService.createToken(any(), any())).thenReturn(tokenDto);
        doNothing().when(emailService).sendEmail(anyString(), anyString(), anyString());

        UserDto result = userService.createUser(userDto);

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getLogin(), result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
        verify(emailService, times(1)).sendEmail(anyString(), anyString(), anyString());
    }

    @Test
    void userWithSameEmailTest() {
        when(userRepository.findByLogin(any())).thenReturn(user);

        assertThrows(UserAlreadyRegisteredException.class, () -> userService.createUser(userDto));
    }

    @Test
    void testGetUserByIdSuccess() throws UserNotFoundException {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        UserDto result = userService.getUserById(user.getId());

        assertNotNull(result);
        assertEquals(user.getName(), result.getName());
        assertEquals(user.getLogin(), result.getEmail());
    }

    @Test
    void testGetUserByIdFail() {
        when(userRepository.findById(any(String.class))).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(UUID.randomUUID().toString()));
    }

    @Test
    void testDeleteUserSuccess() throws UserNotFoundException {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));

        String result = userService.deleteUser(user.getId());

        assertEquals("User deleted", result);
        verify(userRepository, times(1)).deleteById(user.getId());
    }

    @Test
    void testDeleteUserFail() {
        when(userRepository.findById(any(String.class))).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.deleteUser(UUID.randomUUID().toString()));
    }

    @Test
    void testUpdateUser() throws UserNotFoundException, InvalidPasswordFormatException, InvalidEmailFormatException, MessagingException {
        when(userRepository.findById(user.getId())).thenReturn(Optional.of(user));
        when(validationService.isValidEmail(anyString())).thenReturn(true);
        when(validationService.isValidPassword(anyString())).thenReturn(true);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserDto result = userService.updateUser(user.getId(), userDto);

        assertNotNull(result);
        assertEquals(userDto.getName(), result.getName());
        assertEquals(user.getLogin(), result.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void testUpdateUserFail() {
        when(userRepository.findById(any(String.class))).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.updateUser(UUID.randomUUID().toString(), userDto));
    }

    @Test
    void testValidateUserEmailSuccess() {
        when(tokenService.verifyToken(any(UUID.class))).thenReturn(true);
        userDto.setId(UUID.randomUUID().toString());
        when(tokenService.getUserWithToken(any(UUID.class))).thenReturn(Optional.of(userDto));
        user.setId(userDto.getId());
        when(userRepository.findById(userDto.getId())).thenReturn(Optional.of(user));

        boolean result = userService.validateUserEmail(UUID.randomUUID());

        assertTrue(result);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void testValidateUserEmailFail() {
        when(tokenService.verifyToken(any(UUID.class))).thenReturn(false);

        boolean result = userService.validateUserEmail(UUID.randomUUID());

        assertFalse(result);
        verify(userRepository, times(0)).save(user);
    }
}

