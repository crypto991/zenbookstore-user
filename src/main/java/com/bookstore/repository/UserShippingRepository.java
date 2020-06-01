package com.bookstore.repository;

import com.bookstore.domain.UserShipping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserShippingRepository extends JpaRepository<UserShipping, Long> {
}
