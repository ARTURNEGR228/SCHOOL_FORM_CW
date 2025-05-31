package com.example.SchoolForm.service;

import com.example.SchoolForm.model.SchoolUniform;
import com.example.SchoolForm.model.Order;
import com.example.SchoolForm.repository.OrderRepository;
import com.example.SchoolForm.repository.SchoolUniformRepository;
import com.example.SchoolForm.repository.UserRepository;
import com.example.SchoolForm.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SchoolUniformService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private SchoolUniformRepository schoolUniformRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    public List<SchoolUniform> getAllProducts() {
        return schoolUniformRepository.findAll();
    }

    public List<Order> getUserOrders(String username, boolean isAdmin) {
        return isAdmin
                ? orderRepository.findAll()
                : orderRepository.findByUserUsernameOrderByOrderDateDesc(username);
    }

    public void updateProductStock(Long productId, int newStock) {
        SchoolUniform item = schoolUniformRepository.findById(productId).orElseThrow();
        item.setStock(newStock);
        schoolUniformRepository.save(item);
    }

    public void addProduct(SchoolUniform product) {
        schoolUniformRepository.save(product);
    }

    public List<SchoolUniform> getProductsByCategory(String category) {
        return schoolUniformRepository.findByCategory(category);
    }
}
