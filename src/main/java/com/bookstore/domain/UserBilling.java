package com.bookstore.domain;


import javax.persistence.*;

@Entity
public class UserBilling {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String userBillingName;
    private String userBillingStreet1;
    private String userBillingStreet2;
    private String userBillingCity;
    private String userBillingCountry;
    private String userBillingZipCode;


    @OneToOne(cascade = CascadeType.ALL)
    private UserPayment userPayment;

    public UserBilling() {
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUserBillingName() {
        return userBillingName;
    }

    public void setUserBillingName(String userBillingName) {
        this.userBillingName = userBillingName;
    }

    public String getUserBillingStreet1() {
        return userBillingStreet1;
    }

    public void setUserBillingStreet1(String userBillingStreet1) {
        this.userBillingStreet1 = userBillingStreet1;
    }

    public String getUserBillingStreet2() {
        return userBillingStreet2;
    }

    public void setUserBillingStreet2(String userBillingStreet2) {
        this.userBillingStreet2 = userBillingStreet2;
    }

    public String getUserBillingCity() {
        return userBillingCity;
    }

    public void setUserBillingCity(String userBillingCity) {
        this.userBillingCity = userBillingCity;
    }

    public String getUserBillingCountry() {
        return userBillingCountry;
    }

    public void setUserBillingCountry(String userBillingCountry) {
        this.userBillingCountry = userBillingCountry;
    }

    public String getUserBillingZipCode() {
        return userBillingZipCode;
    }

    public void setUserBillingZipCode(String userBillingZipCode) {
        this.userBillingZipCode = userBillingZipCode;
    }

    public UserPayment getUserPayment() {
        return userPayment;
    }

    public void setUserPayment(UserPayment userPayment) {
        this.userPayment = userPayment;
    }
}
