package com.bookstore.utility;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Random;

@Component
public class SecurityUtility {

    public static final String SALT = "salt"; //store later somewhere else and need to be complicated string


    @Bean
    public static BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12, new SecureRandom(SALT.getBytes()));
    }


    @Bean
    public static String randomPasswordGenerator() {
        String SALTCHARS = "ABCDEFGHIJKLMNOPRSTQUVWXYZ1234567890";
        StringBuilder salt = new StringBuilder();

        Random rnd = new Random();

        while (salt.length() < 18) {
            int index = (int) (rnd.nextFloat()*SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }

        String saltStr = salt.toString();

        return saltStr;

    }

    @Autowired
    public JavaMailSender emailSender;

    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        emailSender.send(message);
    }
}
