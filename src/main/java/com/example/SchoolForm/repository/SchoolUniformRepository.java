package com.example.SchoolForm.repository;

import com.example.SchoolForm.model.SchoolUniform;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SchoolUniformRepository extends JpaRepository<SchoolUniform, Long> {

    // Найти товары по категории (например, "Пиджаки")
    List<SchoolUniform> findByCategory(String category);

    // Найти товары по размеру (например, "42")
    List<SchoolUniform> findBySize(String size);

    // Найти товары по цене меньше или равной указанной
    List<SchoolUniform> findByPriceLessThanEqual(double maxPrice);

    // Найти товары по остатку на складе (stock > 0)
    List<SchoolUniform> findByStockGreaterThan(int stock);

    // Кастомный запрос: поиск по категории и размеру
    @Query("SELECT p FROM SchoolUniform p WHERE p.category = :category AND p.size = :size")
    List<SchoolUniform> findByCategoryAndSize(String category, String size);
}