package com.jala.university.api.application.service.impl;

import com.jala.university.api.application.dto.IdentityValidationTokenDto;
import com.jala.university.api.application.dto.UserDto;
import com.jala.university.api.application.mapper.impl.IdentityValidationTokenMapper;
import com.jala.university.api.application.mapper.impl.UserMapper;
import com.jala.university.api.application.service.EmailService;
import com.jala.university.api.application.service.TokenService;
import com.jala.university.api.application.service.UserService;
import com.jala.university.api.application.service.ValidationService;
import com.jala.university.api.domain.entity.User;
import com.jala.university.api.domain.exceptions.format.InvalidEmailFormatException;
import com.jala.university.api.domain.exceptions.format.InvalidPasswordFormatException;
import com.jala.university.api.domain.exceptions.user.UserAlreadyRegisteredException;
import com.jala.university.api.domain.exceptions.user.UserNotFoundException;
import com.jala.university.api.domain.repository.UserRepository;
import jakarta.mail.MessagingException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final IdentityValidationTokenMapper tokenMapper;
    private final PasswordEncoder passwordEncoder;
    private final ValidationService validationService;
    private final TokenService tokenService;
    private final EmailService emailService;

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        ValidationService validationService,
        TokenService tokenService,
        EmailService emailService) {
        this.userRepository = userRepository;
        this.userMapper = new UserMapper();
        this.tokenMapper = new IdentityValidationTokenMapper();
        this.passwordEncoder = passwordEncoder;
        this.validationService = validationService;
        this.tokenService = tokenService;
        this.emailService = emailService;
    }

    /**
     * Retrieves a user by their UUID and maps them to a UserDto.
     *
     * @param id the UUID of the user to retrieve.
     * @return the UserDto representing the user with the given UUID.
     * @throws UserNotFoundException if the user is not found.
     */
    @Override
    public UserDto getUserById(String id) throws UserNotFoundException {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            throw new UserNotFoundException();
        }
        return userMapper.mapTo(user.get());
    }

    /**
     * Creates a new user based on the provided UserDto.
     *
     * @param user the UserDto containing the details of the user to create.
     * @return the created UserDto representing the new user.
     * @throws InvalidPasswordFormatException if the password format is invalid.
     * @throws InvalidEmailFormatException    if the email format is invalid.
     * @throws UserAlreadyRegisteredException if the user is already registered.
     * @throws MessagingException             if there is an issue sending the validation token email.
     */
    @Override
    public UserDto createUser(UserDto user)
        throws InvalidPasswordFormatException, InvalidEmailFormatException, UserAlreadyRegisteredException, MessagingException {
        validationService.isValidEmailAndPassword(user.getEmail(), user.getPassword());

        if (userRepository.findByLogin(user.getEmail()) != null) {
            throw new UserAlreadyRegisteredException("The user is already registered");
        }

        User userToBeCreated = userMapper.mapFrom(user);
        userToBeCreated.setId(UUID.randomUUID().toString());
        userToBeCreated.setPassword(passwordEncoder.encode(user.getPassword()));
        userToBeCreated.setValidated(false);
        User newUser = userRepository.save(userToBeCreated);
        sendTokenEmailToUser(newUser);

        return userMapper.mapTo(newUser);
    }

    /**
     * Sends a validation token email to the user.
     *
     * @param user the user containing the user information to send the email to.
     * @throws MessagingException if there is an issue sending the email.
     */
    private void sendTokenEmailToUser(User user) throws MessagingException {

        try {
            IdentityValidationTokenDto token = tokenService
                .createToken(LocalDateTime.now().plusHours(1), userMapper.mapTo(user));

            emailService.sendEmail(user.getLogin(), "Verify your identity",
                token.getToken());
        } catch (UserNotFoundException ignored) {

        }
    }

    /**
     * Updates an existing user with the details provided in the UserDto.
     *
     * @param id      the UUID of the user to update.
     * @param userDto the UserDto containing the updated user details.
     * @return the updated UserDto representing the user.
     * @throws UserNotFoundException          if the user is not found.
     * @throws InvalidEmailFormatException    if the email is invalid.
     * @throws InvalidPasswordFormatException if the password is invalid.
     * @throws MessagingException             if there is an issue sending the validation token email.
     */
    @Override
    public UserDto updateUser(String id, UserDto userDto)
        throws UserNotFoundException, InvalidEmailFormatException, InvalidPasswordFormatException, MessagingException {
        Optional<User> optionalUser = userRepository.findById(id);

        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException();
        }

        if (userDto.getEmail() != null && !validationService.isValidEmail(userDto.getEmail())) {
            throw new InvalidEmailFormatException();
        }

        if (userDto.getPassword() != null && !validationService.isValidPassword(userDto.getPassword())) {
            throw  new InvalidPasswordFormatException();
        }

        if (userDto.getEmail() != null && !Objects.equals(userDto.getEmail(), optionalUser.get().getLogin())) {
            userDto.setId(optionalUser.get().getId());

          User user = optionalUser.get();
          user.setValidated(false);

          userRepository.save(user);
            sendTokenEmailToUser(userMapper.mapFrom(userDto));
        }

        return userMapper.mapTo(userRepository.save(mergeDtoIntoEntity(userDto, optionalUser.get())));
    }

    /**
     * Merge the data in userDto into user.
     * <p>
     * If a field on userDto is not null, it will be putted into user.
     *
     * @param userDto data to be merged.
     * @param user user to be updated.
     * @return user with the merged data.
     */
    private User mergeDtoIntoEntity(UserDto userDto, User user) {
        if (userDto.getEmail() != null) {
            user.setLogin(userDto.getEmail());
        }

        if (userDto.getPassword() != null) {
            user.setPassword(encryptPassword(userDto.getPassword()));
        }

        if (userDto.getId() != null) {
            user.setId(userDto.getId());
        }

        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }

        return user;
    }

    /**
     * Validates a user's email based on a provided token.
     *
     * @param token the UUID token used to validate the user's email.
     * @return true if the user email is successfully validated, false otherwise.
     */
    @Override
    public boolean validateUserEmail(UUID token) {
        if (tokenService.verifyToken(token)) {
            UserDto userDto = tokenService.getUserWithToken(token).get();
            User user = userRepository.findById(userDto.getId()).get();
            user.setValidated(true);
            userRepository.save(user);

            return true;
        }
        return false;
    }

    /**
     * This method is responsible for encrypting the given password.
     *
     * @param password The plain-text password to be encrypted.
     * @return A string representing the encrypted password.
     */
    private String encryptPassword(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * Deletes a user and their associated external information by their ID.
     *
     * @param id the UUID of the user to delete.
     * @return a string message indicating the user was deleted.
     * @throws UserNotFoundException if the user is not found.
     */
    @Override
    public String deleteUser(String id) throws UserNotFoundException {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException("User with ID " + id + " not found");
        }

        userRepository.deleteById(id);
        return "User deleted";
    }

}
