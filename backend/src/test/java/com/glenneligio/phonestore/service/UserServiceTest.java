package com.glenneligio.phonestore.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import com.glenneligio.phonestore.dtos.XUserDetails;
import com.glenneligio.phonestore.entity.UserEntity;
import com.glenneligio.phonestore.enums.UserType;
import com.glenneligio.phonestore.exception.ApiException;
import com.glenneligio.phonestore.repository.PhoneRepository;
import com.glenneligio.phonestore.repository.UserRepository;
import io.swagger.v3.oas.models.media.XML;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.Optional;

@SpringBootTest
@ExtendWith(SpringExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    private UserEntity user1, user2;
    private List<UserEntity> users;

    @BeforeEach
    void setupEach() {
        user1 = UserEntity.builder()
                .username("Username1")
                .fullName("Fullname1")
                .isActive(false)
                .password("Password1")
                .userType(UserType.CUSTOMER)
                .email("Email1@gmail.com")
                .build();
        user2 = UserEntity.builder()
                .username("Username2")
                .fullName("Fullname2")
                .isActive(true)
                .password("Password2")
                .userType(UserType.CUSTOMER)
                .email("Email2@gmail.com")
                .build();
        users = List.of(user1, user2);
    }

    @Test
    @DisplayName("Get all users returns users")
    void getAllUsers_returnsUsers() {
        when(userRepository.findAll()).thenReturn(users);

        List<UserEntity> result = userService.getAllUsers();

        assertNotNull(result);
        assertEquals(users, result);
    }

    @Test
    @DisplayName("Get user by id using valid id and return the user")
    void getUserById_usingValidId_returnsUser() {
        Long validId = user1.getId();
        when(userRepository.findById(validId)).thenReturn(Optional.of(user1));

        UserEntity result = userService.getUserById(validId);

        assertNotNull(result);
        assertEquals(user1, result);
    }

    @Test
    @DisplayName("Get user by id using invalid id will throw ApiException")
    void getUserById_usingInvalidId_throwsApiException() {
        Long invalidId = 69420L;
        when(userRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> userService.getUserById(invalidId));
    }

    @Test
    @DisplayName("Get user by username using valid username returns User")
    void getUserByUsername_usingValidUsername_returnsUser() {
        String validUsername = user1.getUsername();
        when(userRepository.findByUsername(validUsername)).thenReturn(Optional.of(user1));

        UserEntity result = userService.getUserByUsername(validUsername);

        assertNotNull(result);
        assertEquals(user1, result);
    }

    @Test
    @DisplayName("Get user by username using invalid username throws ApiException")
    void getUserByUsername_usingInvalidUsername_throwsApiException() {
        String invalidUsername = "Invalid username";
        when(userRepository.findByUsername(invalidUsername)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> userService.getUserByUsername(invalidUsername));
    }

    @Test
    @DisplayName("Create user using available username returns user with encoded password and isActive flag true")
    void createUser_usingAvailableUsername_returnsUserWithEncodedPassAndIsActiveTrue() {
        String availableUsername = user1.getUsername();
        String encryptedPassword = "Encrypted password";
        UserEntity userToSave = UserEntity.builder()
                .username(user1.getUsername())
                .id(user1.getId())
                .userType(user1.getUserType())
                .createdAt(user1.getCreatedAt())
                .email(user1.getEmail())
                .orderList(user1.getOrderList())
                .password(encryptedPassword)
                .isActive(true)
                .fullName(user1.getFullName())
                .updatedAt(user1.getUpdatedAt())
                .build();
        when(userRepository.findByUsername(availableUsername)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(user1.getPassword())).thenReturn(encryptedPassword);
        when(userRepository.save(userToSave)).thenReturn(userToSave);

        UserEntity result = userService.createUser(user1);

        assertNotNull(result);
        assertEquals(encryptedPassword, result.getPassword());
        assertTrue(user1.getIsActive());
        assertEquals(userToSave, result);
    }

    @Test
    @DisplayName("Create user using already used username throws ApiException")
    void createUser_usingAlreadyUsedUsername_throwsApiException() {
        String alreadyUsedUsername = user1.getUsername();
        when(userRepository.findByUsername(alreadyUsedUsername)).thenReturn(Optional.of(user1));

        assertThrows(ApiException.class, () -> userService.createUser(user1));
    }

    @Test
    @DisplayName("Update user using invalid username throws ApiException")
    void updateUser_usingInvalidUsername_throwsApiException() {
        String invalidUsername = "Invalid username";
        when(userRepository.findByUsername(invalidUsername)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> userService.updateUser(invalidUsername, user1));
    }

    @Test
    @DisplayName("Update user using valid username returns updated User")
    void updateUser_usingValidUsername_returnsUpdatedUser() {
        String validUsername = user1.getUsername();
        String encryptedPassword = "Encrypted password";
        String newEmail = "NewEmail1@gmail.com";
        String newFullName = "NewFullName";
        UserEntity updatedUserForSaving = UserEntity.builder()
                .username(user1.getUsername())
                .id(user1.getId())
                .userType(user1.getUserType())
                .createdAt(user1.getCreatedAt())
                .email(newEmail)
                .orderList(user1.getOrderList())
                .password(user1.getPassword())
                .isActive(true)
                .fullName(newFullName)
                .updatedAt(user1.getUpdatedAt())
                .build();
        UserEntity updatedUserToSave = UserEntity.builder()
                .username(user1.getUsername())
                .id(user1.getId())
                .userType(user1.getUserType())
                .createdAt(user1.getCreatedAt())
                .email(newEmail)
                .orderList(user1.getOrderList())
                .password(encryptedPassword)
                .isActive(true)
                .fullName(newFullName)
                .updatedAt(user1.getUpdatedAt())
                .build();
        when(userRepository.findByUsername(validUsername)).thenReturn(Optional.of(user1));
        when(passwordEncoder.encode(user1.getPassword())).thenReturn(encryptedPassword);
        when(userRepository.save(updatedUserToSave)).thenReturn(updatedUserToSave);

        UserEntity result = userService.updateUser(validUsername, updatedUserForSaving);

        assertNotNull(result);
        assertEquals(updatedUserToSave, result);
    }

    @Test
    @DisplayName("Delete user using invalid username throws ApiException")
    void deleteUser_usingInvalidUsername_throwsApiException() {
        String invalidUsername = "Invalid username";
        when(userRepository.findByUsername(invalidUsername)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> userService.deleteUser(invalidUsername));
    }

    @Test
    @DisplayName("Delete user using valid username does not throw ApiException")
    void deleteUser_usingValidUsername_doesNotThrowApiException() {
        String validUsername = user1.getUsername();
        when(userRepository.findByUsername(validUsername)).thenReturn(Optional.of(user1));

        assertDoesNotThrow(() -> userService.deleteUser(validUsername));
    }

    @Test
    @DisplayName("Load user by username using valid username returns UserDetails")
    void loadByUsername_usingValidUsername_returnsUserDetails() {
        String validUsername = user1.getUsername();
        XUserDetails expectedUserDetails = new XUserDetails(user1);
        when(userRepository.findByUsername(user1.getUsername())).thenReturn(Optional.of(user1));

        XUserDetails result = (XUserDetails) userService.loadUserByUsername(validUsername);

        assertNotNull(result);
        assertEquals(expectedUserDetails, result);
    }

    @Test
    @DisplayName("Load user by username using invalid username throws ApiException")
    void loadByUsername_usingInvalidUsername_throwsApiException() {
        String invalidUsername = "InvalidUsername";
        when(userRepository.findByUsername(invalidUsername)).thenThrow(ApiException.class);

        assertThrows(ApiException.class, () -> userService.loadUserByUsername(invalidUsername));
    }


    @Test
    @DisplayName("Login using invalid username throws ApiException")
    void login_usingInvalidUsername_throwsApiException() {
        String invalidUsername = "InvalidUsername";
        when(userRepository.findByUsername(invalidUsername)).thenReturn(Optional.empty());

        assertThrows(ApiException.class, () -> userService.loadUserByUsername(invalidUsername));
    }

    @Test
    @DisplayName("Login using valid username and invalid password throws ApiException")
    void login_usingValidUsernameAndInvalidPassword_throwsApiException() {
        String validUsername = user1.getUsername();
        String invalidPassword = "Invalid password";
        when(userRepository.findByUsername(validUsername)).thenReturn(Optional.of(user1));
        when(passwordEncoder.matches(invalidPassword, user1.getPassword())).thenReturn(false);

        assertThrows(ApiException.class, () -> userService.login(validUsername, invalidPassword));
    }

    @Test
    @DisplayName("Login using valid username and password returns User")
    void login_usingValidUsernameAndPassword_returnsUser() {
        String validUsername = user1.getUsername();
        String validPassword = "Valid password";
        when(userRepository.findByUsername(validUsername)).thenReturn(Optional.of(user1));
        when(passwordEncoder.matches(validPassword, user1.getPassword())).thenReturn(true);

        UserEntity result = userService.login(validUsername, validPassword);

        assertNotNull(result);
        assertEquals(user1, result);
    }

    @Test
    @DisplayName("Register using unmodified user info returns Registered user with modified user info")
    void register_returnsRegisteredUserWithModifiedInfo() {
        String validUsername = user1.getUsername();
        String encryptedPassword = "Encrypted password";
        UserEntity userForRegistering = UserEntity.builder()
                .username(user1.getUsername())
                .email(user1.getEmail())
                .password(user1.getPassword())
                .fullName(user1.getFullName())
                .build();
        UserEntity userToBeSaved = UserEntity.builder()
                .username(user1.getUsername())
                .id(user1.getId())
                .userType(UserType.CUSTOMER)
                .createdAt(user1.getCreatedAt())
                .email(user1.getEmail())
                .orderList(user1.getOrderList())
                .password(encryptedPassword)
                .isActive(true)
                .fullName(user1.getFullName())
                .updatedAt(user1.getUpdatedAt())
                .build();
        when(userRepository.findByUsername(validUsername)).thenReturn(Optional.empty());
        when(passwordEncoder.encode(user1.getPassword())).thenReturn(encryptedPassword);
        when(userRepository.save(userToBeSaved)).thenReturn(userToBeSaved);

        UserEntity result = userService.register(userForRegistering);

        assertNotNull(result);
        assertEquals(userToBeSaved, result);
    }

    @Test
    @DisplayName("Register using unavailable username throws ApiException")
    void register_usingUnavailableUsername_throwsApiException() {
        String invalidUsername = "InvalidUsername";
        UserEntity userForRegistering = UserEntity.builder()
                .username(invalidUsername)
                .email(user1.getEmail())
                .password(user1.getPassword())
                .fullName(user1.getFullName())
                .build();
        when(userRepository.findByUsername(invalidUsername)).thenReturn(Optional.of(user1));

        assertThrows(ApiException.class, () -> userService.register(userForRegistering));
    }
}
