package com.bookstore.service.impl;


import com.bookstore.domain.UserPayment;
import com.bookstore.repository.UserPaymentRepository;
import com.bookstore.service.UserPaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserPaymentServiceImpl implements UserPaymentService {

    private UserPaymentRepository userPaymentRepository;

    public UserPaymentServiceImpl(UserPaymentRepository userPaymentRepository) {
        this.userPaymentRepository = userPaymentRepository;
    }

    @Override
    public void deleteById(Long creditCardId) {
        userPaymentRepository.deleteById(creditCardId);
    }

    @Override
    public UserPayment findById(Long id) {
        return userPaymentRepository.getOne(id);
    }
}
