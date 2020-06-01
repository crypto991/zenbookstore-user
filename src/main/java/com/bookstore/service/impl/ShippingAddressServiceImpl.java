package com.bookstore.service.impl;

import com.bookstore.domain.ShippingAddress;
import com.bookstore.domain.UserShipping;
import com.bookstore.repository.ShippingAddressRepository;
import com.bookstore.service.ShippingAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ShippingAddressServiceImpl implements ShippingAddressService {

    private ShippingAddressRepository shippingAddressRepository;

    public ShippingAddressServiceImpl(ShippingAddressRepository shippingAddressRepository) {
        this.shippingAddressRepository = shippingAddressRepository;
    }

    @Override
    public ShippingAddress setByUserShipping(UserShipping userShipping, ShippingAddress shippingAddress) {
        shippingAddress.setShippingAddressName(userShipping.getUserShippingName());
        shippingAddress.setShippingAddressStreet1(userShipping.getUserShippingStreet1());
        shippingAddress.setShippingAddressStreet2(userShipping.getUserShippingStreet2());
        shippingAddress.setShippingAddressCity(userShipping.getUserShippingCity());
        shippingAddress.setShippingAddressCountry(userShipping.getUserShippingCountry());
        shippingAddress.setShippingAddressZipCode(userShipping.getUserShippingZipCode());

        return shippingAddress;

    }
}
