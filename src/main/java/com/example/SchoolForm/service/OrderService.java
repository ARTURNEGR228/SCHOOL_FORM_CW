package com.example.SchoolForm.service;

import com.example.SchoolForm.model.*;
import com.example.SchoolForm.repository.CartItemRepository;
import com.example.SchoolForm.repository.CartRepository;
import com.example.SchoolForm.repository.OrderRepository;
import com.example.SchoolForm.repository.SchoolUniformRepository;
import com.example.SchoolForm.model.*;
import com.example.SchoolForm.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final SchoolUniformRepository productRepository;
    private final CartService cartService;


    public OrderService(OrderRepository orderRepository,
                        CartRepository cartRepository,
                        CartItemRepository cartItemRepository,
                        SchoolUniformRepository productRepository,
                        CartService cartService) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.cartService = cartService;
    }


    @Transactional
    public void submitOrder(User user, String paymentMethod) {
        Cart cart = cartRepository.findByUser(user).orElseThrow();

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("Обрабатывается");
        order.setPaymentMethod(paymentMethod);

        double total = 0.0;

        for (CartItem item : cart.getItems()) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(item.getProduct());
            orderItem.setQuantity(item.getQuantity());
            orderItem.setPrice(item.getProduct().getPrice());
            orderItem.setSize(item.getSize()); // ✅ правильно скопирован размер
            order.getItems().add(orderItem);
        }

        total = order.getItems().stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();

        order.setTotalAmount(total);

        orderRepository.save(order);
        cartService.clearCart(user);

        cartItemRepository.deleteAll(cart.getItems()); // ✅ очищаем корзину
    }

    @Transactional
    public Order createOrder(User user, String paymentMethod) {
        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Корзина не найдена"));

        Order order = new Order();
        order.setUser(user);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus("Ожидает обработки");
        order.setPaymentMethod(paymentMethod);

        List<OrderItem> orderItems = new ArrayList<>();

        for (CartItem cartItem : cartItemRepository.findByCart(cart)) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrder(order);
            orderItem.setProduct(cartItem.getProduct());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setPrice(cartItem.getProduct().getPrice());
            orderItem.setSize(cartItem.getSize());
            orderItems.add(orderItem);
        }

        order.setItems(orderItems);
        double total = orderItems.stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();
        order.setTotalAmount(total);

        orderRepository.save(order);

        // ✅ ОЧИСТКА КОРЗИНЫ
        cartService.clearCart(user);

        return order;
    }



    public List<Order> getOrdersForUser(User user) {
        return orderRepository.findByUser(user);
    }

    public List<Order> getAllOrders() {return orderRepository.findAllOrdersOrderByDateDesc();}
}
