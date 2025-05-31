package com.example.SchoolForm.controller;

import com.example.SchoolForm.model.Category;
import com.example.SchoolForm.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/categories")
public class CategoryController {

    @Autowired
    private CategoryRepository categoryRepository;

    @GetMapping
    public String showCategoryPage(Model model) {
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("newCategory", new Category());
        return "admin/category-management";
    }

    @PostMapping("/add")
    public String addCategory(@ModelAttribute Category newCategory) {
        if (newCategory.getName() != null && !newCategory.getName().isBlank()) {
            categoryRepository.save(newCategory);
        }
        return "redirect:/admin/categories";
    }

    @PostMapping("/delete/{id}")
    public String deleteCategory(@PathVariable Long id) {
        categoryRepository.deleteById(id);
        return "redirect:/admin/categories";
    }
}
