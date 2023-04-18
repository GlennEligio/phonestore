package com.glenneligio.phonestore.service;

import com.glenneligio.phonestore.dtos.XUserDetails;
import com.glenneligio.phonestore.entity.UserEntity;
import com.glenneligio.phonestore.enums.UserType;
import com.glenneligio.phonestore.exception.ApiException;
import com.glenneligio.phonestore.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {

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
        return userRepository.findAll();
    }

    public UserEntity getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ApiException("No user with id " + id + " exist", HttpStatus.NOT_FOUND));
    }

    public UserEntity getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ApiException("No user with username " + username + " exist", HttpStatus.NOT_FOUND));
    }

    public UserEntity createUser(UserEntity userEntity) {
        Optional<UserEntity> userEntityOptional = userRepository.findByUsername(userEntity.getUsername());
        if(userEntityOptional.isPresent()) throw new ApiException("User with username " + userEntity.getUsername() + " already exist", HttpStatus.BAD_REQUEST);
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        userEntity.setIsActive(true);
        return userRepository.save(userEntity);
    }

    public UserEntity updateUser(String username, UserEntity userEntity) {
        UserEntity userEntity1 = getUserByUsername(username);
        mapper.map(userEntity, userEntity1);
        userEntity1.setUsername(username);
        userEntity1.setPassword(passwordEncoder.encode(userEntity1.getPassword()));
        return userRepository.save(userEntity1);
    }

    public void deleteUser(String username) {
        UserEntity userEntity = getUserByUsername(username);
        userRepository.delete(userEntity);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("No user with username "+username + " was found"));
        return new XUserDetails(userEntity);
    }

    public UserEntity login(String username, String password) {
        UserEntity userEntity = getUserByUsername(username);
        // Check if the password in login and the encrypted password in database matches
        boolean match = passwordEncoder.matches(password, userEntity.getPassword());
        if(!match) throw new ApiException("Invalid credentials", HttpStatus.UNAUTHORIZED);
        return userEntity;
    }

    public UserEntity register (UserEntity userEntity) {
        Optional<UserEntity> existingUserEntity = userRepository.findByUsername(userEntity.getUsername());
        if(existingUserEntity.isPresent()) throw new ApiException("User with same username", HttpStatus.BAD_REQUEST);
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        userEntity.setUserType(UserType.CUSTOMER);
        userEntity.setIsActive(true);
        return userRepository.save(userEntity);
    }
}
