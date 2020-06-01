package com.bookstore.service.impl;

import com.bookstore.domain.Order;
import com.bookstore.domain.User;
import com.bookstore.service.EmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Locale;

@Component
public class EmailServiceImpl implements EmailService {

    private Environment environment;
    public JavaMailSender emailSender;
    private TemplateEngine templateEngine;

    public EmailServiceImpl(Environment environment, JavaMailSender emailSender, TemplateEngine templateEngine) {
        this.environment = environment;
        this.emailSender = emailSender;
        this.templateEngine = templateEngine;
    }

    @Override
    public void constructAndSendEmail(String contextPath, Locale locale, String token, User user, String password) {

        String url = contextPath + "/newUser?token="+ token;
        String message = "\nPlease click on this link to verify your email and edit your personal information. Your temporary password is: \n" + password;
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(user.getEmail());
        email.setSubject("Zen Book - New User");
        email.setText(url + message);
        email.setFrom(environment.getProperty("spring.mail.username"));

        emailSender.send(email);
    }

    @Override
    public void sendOrderMail(User user, Order order, Locale locale) throws MessagingException {
        // ...
        Context context = new Context();
        context.setVariable("order", order);
        context.setVariable("user", user);
        context.setVariable("cartItemList", order.getCartItemList());

        String text = templateEngine.process("orderConfirmationEmailTemplate", context);


        MimeMessagePreparator messagePreparator = new MimeMessagePreparator() {
            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {
                MimeMessageHelper email = new MimeMessageHelper(mimeMessage);
                email.setTo(user.getEmail());
                email.setSubject("Order Confirmation - "+order.getId());
                email.setText(text, true);
                email.setFrom(new InternetAddress("djordje190@gmail.com"));
            }
        };



        emailSender.send(messagePreparator);

    }


}