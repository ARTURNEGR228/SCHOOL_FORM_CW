package com.example.SchoolForm.repository;

import com.example.SchoolForm.model.Cart;
import com.example.SchoolForm.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUser(User user);
}