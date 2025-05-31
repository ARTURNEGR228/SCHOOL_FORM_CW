package com.example.SchoolForm.controller;

import com.example.SchoolForm.model.Order;
import com.example.SchoolForm.model.SchoolUniform;
import com.example.SchoolForm.repository.OrderRepository;
import com.example.SchoolForm.repository.SchoolUniformRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class StatisticsController {

    @Autowired
    private OrderRepository orderRepository;

    @GetMapping("/statistics")
    public String showStatisticsPage(Model model) {
        List<Order> orders = orderRepository.findAll();

        DoubleSummaryStatistics orderStats = orders.stream()
                .collect(Collectors.summarizingDouble(Order::getTotalAmount));

        List<Double> orderTotals = orders.stream()
                .map(Order::getTotalAmount)
                .toList();

        model.addAttribute("averagePrice", orderStats.getAverage());
        model.addAttribute("maxPrice", orderStats.getMax());
        model.addAttribute("minPrice", orderStats.getMin());
        model.addAttribute("amounts", orderTotals);

        return "statistics";
    }
}