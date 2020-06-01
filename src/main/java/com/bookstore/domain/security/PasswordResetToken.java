package com.bookstore.domain.security;


import com.bookstore.domain.User;

import javax.persistence.*;
import java.util.Calendar;
import java.util.Date;

@Entity
public class PasswordResetToken {

    private static int EXPIRATION = 60 * 24;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    private String token;

    @OneToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "userId")
    private User user;

    private Date expiryDate;


    public PasswordResetToken(final String token, final User user) {
        super();

        this.token = token;
        this.user = user;
        this.expiryDate = calucateExpiryDate(EXPIRATION);
    }

    private Date calucateExpiryDate(final int expiryTimeInMinutes) {
        final Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(new Date().getTime());
        calendar.add(Calendar.MINUTE, expiryTimeInMinutes);

        return new Date(calendar.getTime().getTime());
    }

    private void updateToken(final String token) {
        this.token = token;
        this.expiryDate = calucateExpiryDate(EXPIRATION);
    }

    public PasswordResetToken() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Date getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }
}
