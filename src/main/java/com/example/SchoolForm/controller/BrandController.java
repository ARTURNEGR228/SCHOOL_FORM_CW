package com.example.SchoolForm.controller;

import org.springframework.ui.Model;
import com.example.SchoolForm.model.Brand;
import com.example.SchoolForm.repository.BrandRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin/brands")
public class BrandController {

    @Autowired
    private BrandRepository brandRepository;

    @GetMapping
    public String listBrands(Model model) {
        model.addAttribute("brands", brandRepository.findAll());
        return "admin/brands"; // сделаем этот шаблон
    }

    @PostMapping
    public String addBrand(@RequestParam String name) {
        if (!name.trim().isEmpty() && brandRepository.findByName(name).isEmpty()) {
            Brand brand = new Brand();
            brand.setName(name.trim());
            brandRepository.save(brand);
        }
        return "redirect:/admin/brands";
    }

    @PostMapping("/delete/{id}")
    public String deleteBrand(@PathVariable Long id) {
        brandRepository.deleteById(id);
        return "redirect:/admin/brands";
    }

}
