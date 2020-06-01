package com.bookstore.service.impl;


import com.bookstore.domain.*;
import com.bookstore.domain.security.PasswordResetToken;
import com.bookstore.domain.security.UserRole;
import com.bookstore.repository.*;
import com.bookstore.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class UserServiceImpl implements UserService {

    public static final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private PasswordResetTokenRepository passwordResetTokenRepository;
    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private UserPaymentRepository userPaymentRepository;
    private UserShippingRepository userShippingRepository;

    public UserServiceImpl(PasswordResetTokenRepository passwordResetTokenRepository, UserRepository userRepository,
                           RoleRepository roleRepository, UserPaymentRepository userPaymentRepository,
                           UserShippingRepository userShippingRepository) {
        this.passwordResetTokenRepository = passwordResetTokenRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userPaymentRepository = userPaymentRepository;
        this.userShippingRepository = userShippingRepository;
    }

    @Override
    public void setUserDefaultPayment(Long userPaymentId, User user) {
        List<UserPayment> userPaymentList = userPaymentRepository.findAll();

        for (UserPayment up : userPaymentList) {
            if (up.getId() == userPaymentId) {
                up.setDefaultPayment(true);
                userPaymentRepository.save(up);
            } else {
                up.setDefaultPayment(false);
                userPaymentRepository.save(up);
            }
        }
    }

    @Override
    public void setUserDefaultShipping(Long userShippingId, User user) {
        List<UserShipping> userShippingList = userShippingRepository.findAll();

        for (UserShipping userShipping : userShippingList) {
            if(userShipping.getId() == userShippingId) {
                userShipping.setUserShippingDefault(true);
                userShippingRepository.save(userShipping);
            } else {
                userShipping.setUserShippingDefault(false);
                userShippingRepository.save(userShipping);
            }
        }
    }

    @Override
    @Transactional
    public User createUser(User user, Set<UserRole> userRoleSet) throws Exception {
        User localUser = userRepository.findByUsername(user.getUsername());

        if (localUser != null) {
            LOG.info("user {} already exists", user.getUsername());
        } else {
            for (UserRole ur : userRoleSet) {
                roleRepository.save(ur.getRole());
            }
            user.getUserRoles().addAll(userRoleSet);

            ShoppingCart shoppingCart = new ShoppingCart();
            shoppingCart.setUser(user);
            user.setShoppingCart(shoppingCart);

            localUser = userRepository.save(user);
        }


        return localUser;
    }

    @Override
    public void updateUserBilling(UserBilling userBilling, UserPayment userPayment, User user) {
        userPayment.setUser(user);
        userPayment.setUserBilling(userBilling);
        userPayment.setDefaultPayment(true);
        userBilling.setUserPayment(userPayment);
        user.getUserPaymentList().add(userPayment);
        save(user);

    }

    @Override
    public void updateUserShipping(UserShipping userShipping, User user) {
        userShipping.setUser(user);
        userShipping.setUserShippingDefault(true);
        user.getUserShippingList().add(userShipping);
        save(user);
    }

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public PasswordResetToken getPasswordResetToken(String token) {
        return passwordResetTokenRepository.findByToken(token);
    }

    @Override
    public void createPasswordResetTokenForUser(User user, String token) {
        final PasswordResetToken myToken = new PasswordResetToken(token, user);
        passwordResetTokenRepository.save(myToken);
    }

    @Override
    public User findById(Long id) {
        return userRepository.getOne(id);
    }

    @Override
    public User findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    @Override
    public User findByEmail(String email) {
        return userRepository.findByEmail(email);
    }


}
