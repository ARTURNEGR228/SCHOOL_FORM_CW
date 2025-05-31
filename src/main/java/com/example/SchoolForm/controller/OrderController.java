package com.example.SchoolForm.controller;

import com.example.SchoolForm.model.User;
import com.example.SchoolForm.repository.UserRepository;
import com.example.SchoolForm.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Optional;

@Controller
public class OrderController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderService orderService;

    @PostMapping("/checkout")
    public String processOrder(Principal principal,
                               @RequestParam("paymentMethod") String paymentMethod) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        orderService.submitOrder(user, paymentMethod);

        return "order-confirmation";
    }


}
