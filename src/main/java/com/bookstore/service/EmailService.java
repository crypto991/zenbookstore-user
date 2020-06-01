package com.bookstore.service;

import com.bookstore.domain.Order;
import com.bookstore.domain.User;

import javax.mail.MessagingException;
import java.util.Locale;

public interface EmailService {

    void constructAndSendEmail(String contextPath, Locale locale, String token, User user, String password);


    void sendOrderMail(User user, Order order, Locale locale) throws MessagingException;
}
