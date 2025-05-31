package com.example.SchoolForm.service;

import com.example.SchoolForm.model.Cart;
import com.example.SchoolForm.model.CartItem;
import com.example.SchoolForm.model.SchoolUniform;
import com.example.SchoolForm.model.User;
import com.example.SchoolForm.repository.CartItemRepository;
import com.example.SchoolForm.repository.CartRepository;
import com.example.SchoolForm.repository.SchoolUniformRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {

    private final CartRepository cartRepository;
    private final SchoolUniformRepository schoolUniformRepository;
    private final CartItemRepository cartItemRepository;

    @Autowired
    public CartService(CartRepository cartRepository,
                       SchoolUniformRepository schoolUniformRepository,
                       CartItemRepository cartItemRepository) {
        this.cartRepository = cartRepository;
        this.schoolUniformRepository = schoolUniformRepository;
        this.cartItemRepository = cartItemRepository;
    }

    public Cart getOrCreateCart(User user) {
        return cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });
    }


    public List<CartItem> getCartItems(User user) {
        Cart cart = getOrCreateCart(user);
        return cartItemRepository.findByCartOrderByIdAsc(cart); // Или другой критерий, например product.name
    }


    @Transactional
    public void addToCart(User user, Long productId, String size, int quantity) {
        Cart cart = getOrCreateCart(user);
        SchoolUniform product = schoolUniformRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<CartItem> existingItem = cartItemRepository.findByCartAndProductAndSize(cart, product, size);

        if (existingItem.isPresent()) {
            CartItem item = existingItem.get();
            item.setQuantity(item.getQuantity() + quantity);
            cartItemRepository.save(item);
        } else {
            CartItem newItem = new CartItem();
            newItem.setCart(cart);
            newItem.setUser(user);
            newItem.setProduct(product);
            newItem.setSize(size);
            newItem.setQuantity(quantity);
            cartItemRepository.save(newItem);
        }
    }


    @Transactional
    public void increaseQuantity(User user, Long productId, String size) {
        Cart cart = getOrCreateCart(user);
        SchoolUniform product = schoolUniformRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<CartItem> itemOpt = cartItemRepository.findByCartAndProductAndSize(cart, product, size);
        itemOpt.ifPresent(item -> {
            item.setQuantity(item.getQuantity() + 1);
            cartItemRepository.save(item);
        });
    }

    @Transactional
    public void decreaseQuantity(User user, Long productId, String size) {
        Cart cart = getOrCreateCart(user);
        SchoolUniform product = schoolUniformRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<CartItem> itemOpt = cartItemRepository.findByCartAndProductAndSize(cart, product, size);
        itemOpt.ifPresent(item -> {
            if (item.getQuantity() > 1) {
                item.setQuantity(item.getQuantity() - 1);
                cartItemRepository.save(item);
            } else {
                cartItemRepository.delete(item);
            }
        });
    }

    @Transactional
    public void removeFromCart(User user, Long productId, String size) {
        Cart cart = getOrCreateCart(user);
        SchoolUniform product = schoolUniformRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        Optional<CartItem> itemOpt = cartItemRepository.findByCartAndProductAndSize(cart, product, size);
        itemOpt.ifPresent(cartItemRepository::delete);
    }

    @Transactional
    public void clearCart(User user) {
        Optional<Cart> optionalCart = cartRepository.findByUser(user);
        if (optionalCart.isPresent()) {
            Cart cart = optionalCart.get();
            cart.getItems().clear();
            cartRepository.save(cart);
        }
    }

}
