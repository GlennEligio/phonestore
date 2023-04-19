package com.glenneligio.phonestore.service;

import com.glenneligio.phonestore.dtos.XUserDetails;
import com.glenneligio.phonestore.entity.UserEntity;
import com.glenneligio.phonestore.enums.UserType;
import com.glenneligio.phonestore.exception.ApiException;
import com.glenneligio.phonestore.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
public class UserService implements UserDetailsService {
    public static final String ENTERING_METHOD = "Entering method {}";
    public static final String EXITING_METHOD = "Exiting method {}";

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper mapper;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, ModelMapper mapper) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
    }

    public List<UserEntity> getAllUsers() {
        final String METHOD_NAME = "getAllUsers";
        log.info(ENTERING_METHOD, METHOD_NAME);
        List<UserEntity> userEntityList = userRepository.findAll();
        log.info(EXITING_METHOD, METHOD_NAME);
        return userEntityList;
    }

    public UserEntity getUserById(Long id) {
        final String METHOD_NAME = "getUserById";
        log.info(ENTERING_METHOD, METHOD_NAME);
        UserEntity userEntity = userRepository.findById(id)
                .orElseThrow(() -> new ApiException("No user with id " + id + " exist", HttpStatus.NOT_FOUND));
        log.info(EXITING_METHOD, METHOD_NAME);
        return userEntity;
    }

    public UserEntity getUserByUsername(String username) {
        final String METHOD_NAME = "getUserByUsername";
        log.info(ENTERING_METHOD, METHOD_NAME);
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("No user with username " + username + " exist", HttpStatus.NOT_FOUND));
        log.info(EXITING_METHOD, METHOD_NAME);
        return userEntity;
    }

    public UserEntity createUser(UserEntity userEntity) {
        final String METHOD_NAME = "createUser";
        log.info(ENTERING_METHOD, METHOD_NAME);
        Optional<UserEntity> userEntityOptional = userRepository.findByUsername(userEntity.getUsername());
        if(userEntityOptional.isPresent()) throw new ApiException("User with username " + userEntity.getUsername() + " already exist", HttpStatus.BAD_REQUEST);
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        userEntity.setIsActive(true);
        UserEntity userCreated = userRepository.save(userEntity);
        log.info(EXITING_METHOD, METHOD_NAME);
        return userCreated;
    }

    public UserEntity updateUser(String username, UserEntity userEntity) {
        final String METHOD_NAME = "updateUser";
        log.info(ENTERING_METHOD, METHOD_NAME);
        UserEntity userEntity1 = getUserByUsername(username);
        mapper.map(userEntity, userEntity1);
        userEntity1.setUsername(username);
        userEntity1.setPassword(passwordEncoder.encode(userEntity1.getPassword()));
        UserEntity userUpdated = userRepository.save(userEntity1);
        log.info(EXITING_METHOD, METHOD_NAME);
        return userUpdated;
    }

    public void deleteUser(String username) {
        final String METHOD_NAME = "deleteUser";
        log.info(ENTERING_METHOD, METHOD_NAME);
        UserEntity userEntity = getUserByUsername(username);
        userRepository.delete(userEntity);
        log.info(EXITING_METHOD, METHOD_NAME);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        final String METHOD_NAME = "laodUserByUsername";
        log.info(ENTERING_METHOD, METHOD_NAME);
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No user with username "+username + " was found"));
        XUserDetails userDetails = new XUserDetails(userEntity);
        log.info(EXITING_METHOD, METHOD_NAME);
        return userDetails;
    }

    public UserEntity login(String username, String password) {
        final String METHOD_NAME = "login";
        log.info(ENTERING_METHOD, METHOD_NAME);
        UserEntity userEntity = getUserByUsername(username);
        // Check if the password in login and the encrypted password in database matches
        boolean match = passwordEncoder.matches(password, userEntity.getPassword());
        if(!match) throw new ApiException("Invalid credentials", HttpStatus.UNAUTHORIZED);
        log.info(EXITING_METHOD, METHOD_NAME);
        return userEntity;
    }

    public UserEntity register (UserEntity userEntity) {
        final String METHOD_NAME = "register";
        log.info(ENTERING_METHOD, METHOD_NAME);
        Optional<UserEntity> existingUserEntity = userRepository.findByUsername(userEntity.getUsername());
        if(existingUserEntity.isPresent()) throw new ApiException("User with same username", HttpStatus.BAD_REQUEST);
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        userEntity.setUserType(UserType.CUSTOMER);
        userEntity.setIsActive(true);
        UserEntity userRegistered = userRepository.save(userEntity);
        log.info(EXITING_METHOD, METHOD_NAME);
        return userRegistered;
    }
}
