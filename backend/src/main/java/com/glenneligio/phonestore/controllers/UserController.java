package com.glenneligio.phonestore.controllers;

import com.glenneligio.phonestore.dtos.*;
import com.glenneligio.phonestore.entity.UserEntity;
import com.glenneligio.phonestore.service.OrderService;
import com.glenneligio.phonestore.service.UserService;
import com.glenneligio.phonestore.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
public class UserController {

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
        return ResponseEntity.ok(userService.getAllUsers()
                .stream()
                .map(UserDto::convertToDto)
                .toList());
    }

    @GetMapping("/{username}")
    public ResponseEntity<UserDto> getUserByUsername(@PathVariable String username) {
        return ResponseEntity.ok(UserDto.convertToDto(userService.getUserByUsername(username)));
    }

    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserEntity entity) {
        UserEntity userCreated = userService.createUser(entity);
        return ResponseEntity.created(ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{username}")
                .buildAndExpand(userCreated.getUsername())
                .toUri()).body(UserDto.convertToDto(userCreated));
    }

    @PutMapping("/{username}")
    public ResponseEntity<UserDto> updateUser(@PathVariable String username,
                                              @RequestBody UserEntity entity) {
        UserEntity userUpdated = userService.updateUser(username, entity);
        return ResponseEntity.ok(UserDto.convertToDto(userUpdated));
    }

    @DeleteMapping("/{username}")
    public ResponseEntity deleteUser(@PathVariable String username) {
        userService.deleteUser(username);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(@RequestBody LoginRequestDto dto) {
        var user = userService.login(dto.getUsername(), dto.getPassword());
        LoginResponseDto loginResponseDto = getLoginResponseDto(user);
        return ResponseEntity.ok(loginResponseDto);
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponseDto> register(@RequestBody RegisterRequestDto dto) {
        UserEntity user = new UserEntity();
        user.setFullName(dto.getFullName());
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());
        user.setPassword(dto.getPassword());
        UserEntity registeredUser = userService.register(user);
        LoginResponseDto loginResponseDto = getLoginResponseDto(registeredUser);
        return ResponseEntity.ok(loginResponseDto);
    }

    @GetMapping("/@self/orders")
    public ResponseEntity<List<OrderDto>> getAllOrderOfUser(Authentication authentication) {
        XUserDetails userDetails = (XUserDetails) authentication.getPrincipal();
        return ResponseEntity.ok(orderService.getOrderByUserUsername(userDetails.getUsername())
                .stream()
                .map(OrderDto::convertToDto)
                .toList());
    }

    private LoginResponseDto getLoginResponseDto(UserEntity user) {
        var xUserDetails = new XUserDetails(user);
        var accessToken = jwtUtil.generateToken(xUserDetails);
        var refreshToken = jwtUtil.generateRefreshToken(xUserDetails);
        return LoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .accountType(user.getUserType().getType())
                .fullName(user.getFullName())
                .username(user.getUsername())
                .build();
    }
}
