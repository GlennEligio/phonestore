package com.glenneligio.phonestore.util;

import com.glenneligio.phonestore.entity.UserEntity;
import com.glenneligio.phonestore.enums.UserType;
import com.glenneligio.phonestore.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataloadUtil implements ApplicationRunner {

    @Autowired
    private UserService service;

    @Value("${phone-store.admin.username}")
    private String adminUsername;

    @Value("${phone-store.admin.password}")
    private String adminPassword;

    @Value("${phone-store.admin.email}")
    private String adminEmail;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        UserEntity user = new UserEntity();
        user.setUsername(adminUsername);
        user.setPassword(adminPassword);
        user.setEmail(adminEmail);
        user.setIsActive(true);
        user.setFullName("Administrator");
        user.setUserType(UserType.ADMIN);
        service.createUser(user);
    }
}
