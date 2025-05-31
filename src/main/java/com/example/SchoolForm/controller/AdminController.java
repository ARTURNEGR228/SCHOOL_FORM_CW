package com.example.SchoolForm.controller;

import com.example.SchoolForm.model.SchoolUniform;
import com.example.SchoolForm.model.User;
import com.example.SchoolForm.model.Order;
import com.example.SchoolForm.repository.OrderRepository;
import com.example.SchoolForm.service.OrderService;
import com.example.SchoolForm.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;
import com.example.SchoolForm.service.SchoolUniformService;
import org.springframework.web.server.ResponseStatusException;


import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private SchoolUniformService schoolUniformService;

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/dashboard")
    public String adminDashboard(@RequestParam(value = "sortField", required = false, defaultValue = "id") String sortField,
                                 @RequestParam(value = "sortDir", required = false, defaultValue = "asc") String sortDir,
                                 Model model) {
        List<User> users = userRepository.findAll();
        List<SchoolUniform> products = schoolUniformService.getAllProducts();

        Comparator<User> userComparator = switch (sortField) {
            case "username" -> Comparator.comparing(User::getUsername, String.CASE_INSENSITIVE_ORDER);
            case "lastName" -> Comparator.comparing(User::getLastName, String.CASE_INSENSITIVE_ORDER);
            case "firstName" -> Comparator.comparing(User::getFirstName, String.CASE_INSENSITIVE_ORDER);
            case "role" -> Comparator.comparing(User::getRole, String.CASE_INSENSITIVE_ORDER);
            default -> Comparator.comparing(User::getId);
        };

        Comparator<SchoolUniform> productComparator = switch (sortField) {
            case "name" -> Comparator.comparing(SchoolUniform::getName, String.CASE_INSENSITIVE_ORDER);
            case "category" -> Comparator.comparing(SchoolUniform::getCategory, String.CASE_INSENSITIVE_ORDER);
            case "size" -> Comparator.comparing(SchoolUniform::getSize);
            case "color" -> Comparator.comparing(SchoolUniform::getColor, String.CASE_INSENSITIVE_ORDER);
            case "season" -> Comparator.comparing(s -> s.getSeason().name(), String.CASE_INSENSITIVE_ORDER);
            case "price" -> Comparator.comparing(SchoolUniform::getPrice);
            case "stock" -> Comparator.comparing(SchoolUniform::getStock);
            default -> Comparator.comparing(SchoolUniform::getId);
        };

        if ("desc".equals(sortDir)) {
            userComparator = userComparator.reversed();
            productComparator = productComparator.reversed();
        }

        users.sort(userComparator);
        products.sort(productComparator);

        model.addAttribute("users", users);
        model.addAttribute("products", products);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("orders", orderService.getAllOrders());
        return "admin/admin-dashboard";
    }



    @PostMapping("/delete-user/{id}")
    public String deleteUser(@PathVariable("id") Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/make-admin/{id}")
    public String makeAdmin(@PathVariable("id") Long id) {
        User user = userRepository.findById(id).orElseThrow();
        user.setRole("ADMIN");
        userRepository.save(user);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/user-orders/{id}")
    public String viewUserOrders(@PathVariable("id") Long id, Model model) {
        User user = userRepository.findById(id).orElseThrow();
        List<Order> orders = orderService.getOrdersForUser(user);
        model.addAttribute("user", user);
        model.addAttribute("orders", orders);
        return "admin/user-orders";
    }

    @GetMapping("/orders/{id}")
    public String viewOrderDetails(@PathVariable Long id, Model model) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        model.addAttribute("order", order);
        return "admin/admin-order-details";
    }

    @PostMapping("/orders/{id}/status")
    public String updateOrderStatus(@PathVariable Long id, @RequestParam String status) {
        Order order = orderRepository.findById(id).orElseThrow();
        order.setStatus(status);
        orderRepository.save(order);
        return "redirect:/admin/orders/" + id;
    }

    @PostMapping("/orders/{id}/delete")
    public String deleteOrder(@PathVariable Long id) {
        orderRepository.deleteById(id);
        return "redirect:/admin/dashboard";
    }

}
