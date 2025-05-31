package com.example.SchoolForm.repository;

import com.example.SchoolForm.model.Cart;
import com.example.SchoolForm.model.CartItem;
import com.example.SchoolForm.model.SchoolUniform;
import com.example.SchoolForm.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    @Query("SELECT c FROM CartItem c WHERE c.cart = :cart ORDER BY c.id ASC")
    List<CartItem> findByUser(User user);
    void deleteAllByCart(Cart cart);
    List<CartItem> findByCart(Cart cart);
    List<CartItem> findByCartOrderByIdAsc(Cart cart);
    Optional<CartItem> findByCartAndProduct(Cart cart, SchoolUniform product);
    Optional<CartItem> findByCartAndProductAndSize(Cart cart, SchoolUniform product, String size);
}
