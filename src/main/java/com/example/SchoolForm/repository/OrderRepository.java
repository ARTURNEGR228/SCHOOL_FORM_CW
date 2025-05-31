package com.example.SchoolForm.repository;

import com.example.SchoolForm.model.Order;
import com.example.SchoolForm.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // Поиск заказов по имени пользователя (через связь User)
    List<Order> findByUserUsernameOrderByOrderDateDesc(String username);

    // Получить все заказы с сортировкой по дате
    @Query("SELECT o FROM Order o ORDER BY o.orderDate DESC")
    List<Order> findAllOrdersOrderByDateDesc();
    List<Order> findByUser(User user);

    // Дополнительно: поиск заказов по статусу (если требуется)
    List<Order> findByStatus(String status);
}