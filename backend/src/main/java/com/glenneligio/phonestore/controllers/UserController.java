package com.glenneligio.phonestore.controllers;

import com.glenneligio.phonestore.dtos.*;
import com.glenneligio.phonestore.entity.UserEntity;
import com.glenneligio.phonestore.service.OrderService;
import com.glenneligio.phonestore.service.UserService;
import com.glenneligio.phonestore.util.JwtUtil;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@Slf4j
public class UserController {
    public static final String ENTERING_METHOD = "Entering method {}";
    public static final String EXITING_METHOD = "Exiting method {}";
    public static final String SERVICE_RESPONSE = "Service response: {}";

    private final UserService userService;
    private final OrderService orderService;
    private final JwtUtil jwtUtil;

    @Autowired
    public UserController(UserService userService, OrderService orderService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<UserDto>> getAllUsers() {
        final String METHOD_NAME = "getAllUsers";
        log.info(ENTERING_METHOD, METHOD_NAME);
        log.debug("Fetching all users");
        List<UserDto> userDtoList = userService.getAllUsers()
                .stream()
                .map(UserDto::convertToDto)
                .toList();
        log.info(EXITING_METHOD, METHOD_NAME);
        log.debug("Successfully fetched all users. Count: {}", userDtoList.size());
        return ResponseEntity.ok(userDtoList);
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username) {
        final String METHOD_NAME = "getUserByUsername";
        log.info(ENTERING_METHOD, METHOD_NAME);
        log.debug("Getting user with username {}", username);

        UserEntity userEntity = userService.getUserByUsername(username);
        UserDto userDto = UserDto.convertToDto(userEntity);

        log.debug("Successfully fetched user with username: {}", username);
        log.debug(SERVICE_RESPONSE, userDto);
        log.info(EXITING_METHOD, METHOD_NAME);
        return ResponseEntity.ok(userDto);
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid CreateUpdateUserDto userDto) {
        final String METHOD_NAME = "createUser";
        log.info(ENTERING_METHOD, METHOD_NAME);
        log.debug("Creating user with info {}", userDto);
        UserEntity userEntityInput = CreateUpdateUserDto.convertToEntity(userDto);
        UserEntity userCreated = userService.createUser(userEntityInput);
        UserDto userDtoCreated = UserDto.convertToDto(userCreated);
        log.debug("Successfully created user");
        log.debug(SERVICE_RESPONSE, userDtoCreated);
        log.info(EXITING_METHOD, METHOD_NAME);

        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{username}")
                .buildAndExpand(userCreated.getUsername())
                .toUri()).body(userDtoCreated);
    }

    @PutMapping("/{username}")
    public ResponseEntity<UserDto> updateUser(@PathVariable String username,
                                              @RequestBody @Valid CreateUpdateUserDto userDto) {
        final String METHOD_NAME = "updateUser";
        log.info(ENTERING_METHOD, METHOD_NAME);
        log.debug("Updating user with username {}, and info {}", userDto, userDto);
        UserEntity userEntityInput = CreateUpdateUserDto.convertToEntity(userDto);
        UserEntity userUpdated = userService.updateUser(username, userEntityInput);
        UserDto userDtoUpdated = UserDto.convertToDto(userUpdated);
        log.info(EXITING_METHOD, METHOD_NAME);
        log.debug("Successfully updated user with username {}", username);
        log.debug(SERVICE_RESPONSE, userDtoUpdated);
        return ResponseEntity.ok(userDtoUpdated);
    }

    @DeleteMapping("/{username}")
    public ResponseEntity deleteUser(@PathVariable String username) {
        final String METHOD_NAME = "deleteUser";
        log.info(ENTERING_METHOD, METHOD_NAME);
        log.debug("Deleting user with username {}", username);
        userService.deleteUser(username);
        log.info(EXITING_METHOD, METHOD_NAME);
        log.debug("Successfully deleted user with username {}", username);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody @Valid LoginRequestDto dto) {
        final String METHOD_NAME = "login";
        log.info(ENTERING_METHOD, METHOD_NAME);
        log.debug("Logging in with credentials {}", dto);
        var user = userService.login(dto.getUsername(), dto.getPassword());
        LoginResponseDto loginResponseDto = getLoginResponseDto(user);
        log.info(EXITING_METHOD, METHOD_NAME);
        log.info("Successfully logged in");
        log.debug(SERVICE_RESPONSE, loginResponseDto);
        return ResponseEntity.ok(loginResponseDto);
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponseDto> register(@RequestBody @Valid RegisterRequestDto dto) {
        final String METHOD_NAME = "register";
        log.info(ENTERING_METHOD, METHOD_NAME);
        log.debug("Registering user with info {}", dto);
        UserEntity user = new UserEntity();
        user.setFullName(dto.getFullName());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        UserEntity registeredUser = userService.register(user);
        LoginResponseDto loginResponseDto = getLoginResponseDto(registeredUser);
        log.info(EXITING_METHOD, METHOD_NAME);
        log.info("Successfully registered");
        log.debug(SERVICE_RESPONSE, loginResponseDto);
        return ResponseEntity.ok(loginResponseDto);
    }

    @GetMapping("/@self/orders")
    public ResponseEntity<List<OrderDto>> getAllOrderOfUser(Authentication authentication) {
        final String METHOD_NAME = "getAllOrderOfUser";
        log.info(ENTERING_METHOD, METHOD_NAME);
        log.debug("Fetching all order of the user");
        XUserDetails userDetails = (XUserDetails) authentication.getPrincipal();
        List<OrderDto> orderDtoList = orderService.getOrderByUserUsername(userDetails.getUsername())
                .stream()
                .map(OrderDto::convertToDto)
                .toList();
        log.info(EXITING_METHOD, METHOD_NAME);
        log.debug("Successfully fetched all order of user. Count: {}", orderDtoList.size());
        log.debug(SERVICE_RESPONSE, orderDtoList);
        return ResponseEntity.ok(orderDtoList);
    }

    private LoginResponseDto getLoginResponseDto(UserEntity user) {
        final String METHOD_NAME = "getLoginResponseDto";
        log.info(ENTERING_METHOD, METHOD_NAME);
        log.debug("Creating login response dto for user with username {}", user.getUsername());
        var xUserDetails = new XUserDetails(user);
        var accessToken = jwtUtil.generateToken(xUserDetails);
        var refreshToken = jwtUtil.generateRefreshToken(xUserDetails);
        LoginResponseDto loginResponseDto = LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accountType(user.getUserType().getType())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .build();
        log.info(EXITING_METHOD,METHOD_NAME);
        log.debug("Successfully created login response dto for user with username {}", user.getUsername());
        return loginResponseDto;
    }
}
