package com.bookstore.service.impl;

import com.bookstore.domain.BillingAddress;
import com.bookstore.domain.ShippingAddress;
import com.bookstore.domain.UserBilling;
import com.bookstore.service.BillingAddressService;
import org.springframework.stereotype.Service;


@Service
public class BillingAddressServiceImpl implements BillingAddressService {

    @Override
    public BillingAddress setByUserBilling(UserBilling userBilling, BillingAddress billingAddress) {

        billingAddress.setBillingAddressName(userBilling.getUserBillingName());
        billingAddress.setBillingAddressStreet1(userBilling.getUserBillingStreet1());
        billingAddress.setBillingAddressStreet2(userBilling.getUserBillingStreet2());
        billingAddress.setBillingAddressCity(userBilling.getUserBillingCity());
        billingAddress.setBillingAddressCountry(userBilling.getUserBillingCountry());
        billingAddress.setBillingAddressZipCode(userBilling.getUserBillingZipCode());

        return billingAddress;
    }

    @Override
    public BillingAddress setByShippingAddress(ShippingAddress shippingAddress, BillingAddress billingAddress) {
        billingAddress.setBillingAddressName(shippingAddress.getShippingAddressName());
        billingAddress.setBillingAddressStreet1(shippingAddress.getShippingAddressStreet1());
        billingAddress.setBillingAddressStreet2(shippingAddress.getShippingAddressStreet2());
        billingAddress.setBillingAddressCity(shippingAddress.getShippingAddressCity());
        billingAddress.setBillingAddressCountry(shippingAddress.getShippingAddressCountry());
        billingAddress.setBillingAddressZipCode(shippingAddress.getShippingAddressZipCode());

        return billingAddress;
    }
}
