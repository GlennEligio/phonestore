package com.glenneligio.phonestore.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.glenneligio.phonestore.entity.UserEntity;
import com.glenneligio.phonestore.enums.UserType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

@DataJpaTest
public class UserRepositoryTest {

    private UserRepository userRepository;
    private TestEntityManager testEntityManager;

    private UserEntity userEntity;

    @Autowired
    public UserRepositoryTest(UserRepository userRepository, TestEntityManager testEntityManager) {
        this.userRepository = userRepository;
        this.testEntityManager = testEntityManager;
    }

    @BeforeEach
    void setupEach() {
        userEntity = UserEntity.builder()
                .username("Username1")
                .fullName("Fullname1")
                .isActive(true)
                .password("Password1")
                .userType(UserType.CUSTOMER)
                .email("Email1@gmail.com")
                .build();
        testEntityManager.persist(userEntity);
    }

    @Test
    @DisplayName("Find user using valid username and return a non empty Optional of type user")
    void findByUsername_usingValidUsername_returnOptionalWithUser() {
        String validUsername = userEntity.getUsername();

        Optional<UserEntity> result = userRepository.findByUsername(validUsername);

        assertNotNull(result);
        assertTrue(result.isPresent());
        assertEquals(validUsername, result.get().getUsername());
    }

    @Test
    @DisplayName("Find user using invalid username and return a empty Optional of type user")
    void findByUsername_usingInvalidUsername_returnEmptyOptional() {
        String invalidUsername = "Invalid username";

        Optional<UserEntity> result = userRepository.findByUsername(invalidUsername);

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
