package com.bookstore;

import com.bookstore.domain.User;
import com.bookstore.domain.security.Role;
import com.bookstore.domain.security.UserRole;
import com.bookstore.service.UserService;
import com.bookstore.utility.SecurityUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.HashSet;
import java.util.Set;

@SpringBootApplication
public class BookstoreApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(BookstoreApplication.class, args);
    }

    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {
        User user1 = new User();
        user1.setFirstName("Mickey");
        user1.setLastName("Johns");
        user1.setUsername("user");
        user1.setPassword(SecurityUtility.passwordEncoder().encode("p"));
        user1.setEmail("JAdams@gmail.com");
        Set<UserRole> userRoleSet = new HashSet<>();
        Role role = new Role();
        role.setId(1);
        role.setName("ROLE_USER");

        userRoleSet.add(new UserRole(user1, role));

        userService.createUser(user1, userRoleSet);
    }
}
