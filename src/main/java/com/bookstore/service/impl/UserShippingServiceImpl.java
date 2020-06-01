package com.bookstore.service.impl;

import com.bookstore.domain.UserShipping;
import com.bookstore.repository.UserShippingRepository;
import com.bookstore.service.UserShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserShippingServiceImpl implements UserShippingService{
	
	private UserShippingRepository userShippingRepository;

	public UserShippingServiceImpl(UserShippingRepository userShippingRepository) {
		this.userShippingRepository = userShippingRepository;
	}

	public UserShipping findById(Long id) {
		return userShippingRepository.getOne(id);
	}
	
	public void removeById(Long id) {
		userShippingRepository.deleteById(id);
	}

}
