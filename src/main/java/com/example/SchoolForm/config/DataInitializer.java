package com.example.SchoolForm.config;

import com.example.SchoolForm.model.*;
import com.example.SchoolForm.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(UserRepository userRepository,
                                   PasswordEncoder passwordEncoder,
                                   SchoolUniformRepository productRepository,
                                   BrandRepository brandRepository,
                                   CategoryRepository categoryRepository) {
        return args -> {

            if (userRepository.findByUsername("admin").isEmpty()) {
                User admin = new User();
                admin.setUsername("admin");
                admin.setPassword(passwordEncoder.encode("admin"));
                admin.setRole("ADMIN");
                admin.setFirstName("Админ");
                admin.setLastName("Администратор");
                admin.setPhone("+79000000000");
                admin.setAddress("г. Админск, ул. Примерная, д.1");
                userRepository.save(admin);
            }

            Brand brand = brandRepository.findByName("DefaultBrand")
                    .orElseGet(() -> {
                        Brand b = new Brand("DefaultBrand");
                        return brandRepository.save(b);
                    });


            Category category = categoryRepository.findByName("Одежда")
                    .orElseGet(() -> {
                        Category newCategory = new Category();
                        newCategory.setName("Одежда");
                        return categoryRepository.save(newCategory);
                    });



            if (productRepository.count() == 0) {

                SchoolUniform jacket = new SchoolUniform();
                jacket.setName("Пиджак школьный");
                jacket.setCategory(category.getName());
                jacket.setColor("Синий");
                jacket.setSeason(Season.ОСЕНЬ);
                jacket.setGenderCategory("Мальчик");
                jacket.setPrice(2500.0);
                jacket.setBrand(brand);

                SizeStock j1 = new SizeStock();
                j1.setSize("42");
                j1.setStock(8);
                j1.setSchoolUniform(jacket);

                SizeStock j2 = new SizeStock();
                j2.setSize("44");
                j2.setStock(7);
                j2.setSchoolUniform(jacket);

                jacket.setSizeStocks(List.of(j1, j2));
                jacket.setStock(15);
                productRepository.save(jacket);

                SchoolUniform skirt = new SchoolUniform();
                skirt.setName("Юбка клетчатая");
                skirt.setCategory(category.getName());
                skirt.setColor("Серый");
                skirt.setSeason(Season.ВЕСНА);
                skirt.setGenderCategory("Девочка");
                skirt.setPrice(1800.0);
                skirt.setBrand(brand);

                SizeStock s1 = new SizeStock();
                s1.setSize("S");
                s1.setStock(10);
                s1.setSchoolUniform(skirt);

                SizeStock s2 = new SizeStock();
                s2.setSize("M");
                s2.setStock(10);
                s2.setSchoolUniform(skirt);

                skirt.setSizeStocks(List.of(s1, s2));
                skirt.setStock(20);
                productRepository.save(skirt);
            }
        };
    }
}
