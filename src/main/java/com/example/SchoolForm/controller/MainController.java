package com.example.SchoolForm.controller;

import com.example.SchoolForm.model.*;
import com.example.SchoolForm.repository.*;
import com.example.SchoolForm.service.CartService;
import com.example.SchoolForm.service.OrderService;
import com.example.SchoolForm.service.SchoolUniformService;
import com.example.SchoolForm.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.example.SchoolForm.model.Brand;
import com.example.SchoolForm.repository.BrandRepository;
import com.example.SchoolForm.dto.SizeStockWrapper;



import java.beans.PropertyEditorSupport;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Controller
public class MainController {

    @Autowired private SchoolUniformService schoolUniformService;
    @Autowired private UserService userService;
    @Autowired private CartService cartService;
    @Autowired private OrderService orderService;
    @Autowired private SchoolUniformRepository schoolUniformRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private CartRepository cartRepository;
    @Autowired private CartItemRepository cartItemRepository;
    @Autowired private OrderRepository orderRepository;
    @Autowired private PasswordEncoder passwordEncoder;
    @Autowired private CategoryRepository categoryRepository;
    @Autowired private BrandRepository brandRepository;


    private final String uploadDir = "uploads";

    @GetMapping("/")
    public String home(Authentication authentication, Model model) {
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
            model.addAttribute("username", username);
            model.addAttribute("orders", schoolUniformService.getUserOrders(username, isAdmin));
            model.addAttribute("isAuthenticated", authentication != null && authentication.isAuthenticated());
            model.addAttribute("isAdmin", authentication != null && authentication.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));

        }
        return "index";
    }

    @GetMapping("/catalog")
    public String showCatalog(@RequestParam(required = false) Long brand,
                              @RequestParam(required = false) String gender,
                              @RequestParam(required = false) String season,
                              @RequestParam(required = false) String category,
                              @RequestParam(required = false) String sort,
                              Model model,
                              Authentication authentication) {

        List<SchoolUniform> products = schoolUniformService.getAllProducts();

        if (brand != null) {
            products = products.stream()
                    .filter(p -> p.getBrand() != null && p.getBrand().getId().equals(brand))
                    .collect(Collectors.toList());
        }

        if (gender != null && !gender.isEmpty()) {
            products = products.stream()
                    .filter(p -> gender.equals(p.getGenderCategory()))
                    .collect(Collectors.toList());
        }

        if (season != null && !season.isEmpty()) {
            products = products.stream()
                    .filter(p -> p.getSeason().toString().equalsIgnoreCase(season))
                    .collect(Collectors.toList());
        }

        if (category != null && !category.trim().isEmpty()) {
            products = products.stream()
                    .filter(p -> category.equalsIgnoreCase(p.getCategory()))
                    .collect(Collectors.toList());
        }


        if (sort != null) {
            switch (sort) {
                case "priceAsc" -> products.sort(Comparator.comparing(SchoolUniform::getPrice));
                case "priceDesc" -> products.sort(Comparator.comparing(SchoolUniform::getPrice).reversed());
                case "nameAsc" -> products.sort(Comparator.comparing(SchoolUniform::getName, String.CASE_INSENSITIVE_ORDER));
                case "nameDesc" -> products.sort(Comparator.comparing(SchoolUniform::getName, String.CASE_INSENSITIVE_ORDER).reversed());
            }
        }

        model.addAttribute("products", products);
        model.addAttribute("brands", brandRepository.findAll());
        model.addAttribute("seasons", Season.values());
        model.addAttribute("sort", sort);
        model.addAttribute("selectedBrand", brand);
        model.addAttribute("selectedGender", gender);
        model.addAttribute("selectedSeason", season);
        model.addAttribute("categories", products.stream().map(SchoolUniform::getCategory).distinct().toList());
        model.addAttribute("selectedCategory", category);


        boolean isAdmin = authentication != null &&
                authentication.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);

        return "catalog";
    }



    @GetMapping("/cart")
    public String showCart(Model model, Authentication auth) {
        User user = userService.getUserByUsername(auth.getName());
        List<CartItem> cartItems = cartService.getCartItems(user);
        model.addAttribute("cartItems", cartItems);
        return "cart";
    }

    @GetMapping("/cart/add/{id}")
    public String addToCart(@PathVariable("id") Long productId,
                            @RequestParam("size") String size,
                            Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            User user = userService.getUserByUsername(auth.getName());
            cartService.addToCart(user, productId, size, 1);
        }
        return "redirect:/catalog";
    }


    @PostMapping("/cart/add/{id}")
    public String postAddToCart(@PathVariable("id") Long productId,
                                @RequestParam("size") String size,
                                Authentication auth) {
        if (auth != null && auth.isAuthenticated()) {
            User user = userService.getUserByUsername(auth.getName());
            cartService.addToCart(user, productId, size, 1);
        }
        return "redirect:/catalog";
    }


    @PostMapping("/cart/increase/{id}/{size}")
    public String increaseQuantity(@PathVariable Long id,
                                   @PathVariable String size,
                                   Authentication auth) {
        User user = userService.getUserByUsername(auth.getName());
        cartService.increaseQuantity(user, id, size);
        return "redirect:/cart";
    }


    @PostMapping("/cart/decrease/{id}/{size}")
    public String decreaseQuantity(@PathVariable Long id,
                                   @PathVariable String size,
                                   Authentication auth) {
        User user = userService.getUserByUsername(auth.getName());
        cartService.decreaseQuantity(user, id, size);
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove/{id}/{size}")
    public String removeItem(@PathVariable Long id,
                             @PathVariable String size,
                             Authentication auth) {
        User user = userService.getUserByUsername(auth.getName());
        cartService.removeFromCart(user, id, size);
        return "redirect:/cart";
    }


    @PostMapping("/cart/clear")
    public String clearCart(Authentication auth) {
        User user = userService.getUserByUsername(auth.getName());
        cartService.clearCart(user);
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String showCheckoutPage(Principal principal, Model model) {
        User user = userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        model.addAttribute("user", user);

        Cart cart = cartRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Cart not found for user"));

        List<CartItem> items = cartItemRepository.findByCart(cart);

        BigDecimal total = items.stream()
                .map(item -> BigDecimal.valueOf(item.getProduct().getPrice())
                        .multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        model.addAttribute("cartItems", items);
        model.addAttribute("total", total);

        return "checkout";
    }




    @PostMapping("/checkout/confirm")
    public String confirmOrder(@AuthenticationPrincipal UserDetails userDetails,
                               @RequestParam(required = false) String cardNumber,
                               @RequestParam(required = false) String cardExpiry,
                               @RequestParam(required = false) String cardCVV) {

        User user = userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        if (cardNumber == null || cardExpiry == null || cardCVV == null) {
            // использовать сохранённые данные
            cardNumber = user.getCardNumber();
            cardExpiry = user.getCardExpiry();
            cardCVV = user.getCardCvv();
        }

        // proceed with creating the order using cardNumber, cardExpiry, cardCVV
        return "redirect:/order/confirmation";
    }

    @PostMapping("/checkout/online")
    public String showOnlineForm(Model model, HttpSession session, Authentication auth) {
        User user = userService.getUserByUsername(auth.getName());


        if (user.getCardNumber() != null && user.getCardExpiry() != null && user.getCardCvv() != null) {
            String paymentMethod = (String) session.getAttribute("userPayment");
            orderService.submitOrder(user, paymentMethod);
            return "redirect:/order-confirmation";
        }

        // Иначе — показываем форму ввода карты
        model.addAttribute("user", user);
        return "checkout/online-form";
    }



    @PostMapping("/checkout/payment-method")
    public String handlePaymentChoice(@RequestParam("paymentMethod") String paymentMethod, HttpSession session) {
        session.setAttribute("userPayment", paymentMethod);
        return "online".equals(paymentMethod) ? "redirect:/checkout/online" : "redirect:/checkout/offline";
    }




    @GetMapping("/checkout/cod")
    public String showCodForm(Model model, HttpSession session) {
        User user = (User) session.getAttribute("userPayment");
        model.addAttribute("user", user);
        return "checkout/cod-form";
    }

    @PostMapping("/checkout/submit")
    public String submitOrder(@RequestParam("paymentMethod") String paymentMethod,
                              Authentication auth,
                              HttpSession session) {
        User existingUser = userService.getUserByUsername(auth.getName());
        orderService.submitOrder(existingUser, paymentMethod);
        cartService.clearCart(existingUser); // ← добавь это
        return "redirect:/order-confirmation";
    }


    @GetMapping("/order-confirmation")
    public String orderConfirmation() {
        return "order-confirmation";
    }

    @GetMapping("/orders")
    public String userOrders(Model model, Authentication auth) {
        User user = userService.getUserByUsername(auth.getName());
        model.addAttribute("orders", orderService.getOrdersForUser(user));
        return "orders";
    }

    @GetMapping("/admin/orders")
    public String allOrders(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        return "orders";
    }


    @GetMapping("/admin/products/add")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new SchoolUniform());
        model.addAttribute("brands", brandRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("seasons", Season.values());
        return "admin/add-product";
    }


    @PostMapping("/admin/products/add")
    public String addProduct(@ModelAttribute SchoolUniform product,
                             @RequestParam("brand.id") Long brandId,
                             @RequestParam("imageFile") MultipartFile imageFile,
                             @ModelAttribute("sizes") SizeStockWrapper sizeStockWrapper) throws IOException {

        Brand brand = brandRepository.findById(brandId).orElseThrow();
        product.setBrand(brand);

        if (!imageFile.isEmpty()) {
            String imagePath = saveImage(imageFile);
            product.setImageUrl(imagePath);
        }

        for (SizeStock ss : sizeStockWrapper.getSizes()) {
            ss.setSchoolUniform(product);
        }
        product.setSizeStocks(sizeStockWrapper.getSizes());
        int totalStock = sizeStockWrapper.getSizes().stream()
                .mapToInt(SizeStock::getStock)
                .sum();
        product.setStock(totalStock);

        schoolUniformRepository.save(product);
        return "redirect:/admin/dashboard";
    }




    @PostMapping("/admin/products/update/{id}")
    public String updateProduct(@PathVariable Long id,
                                @ModelAttribute("product") SchoolUniform updatedProduct,
                                @RequestParam("brand.id") Long brandId,
                                @RequestParam("imageFile") MultipartFile imageFile,
                                @ModelAttribute("sizes") SizeStockWrapper sizeStockWrapper) throws IOException {

        SchoolUniform product = schoolUniformRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Товар не найден: " + id));

        product.setName(updatedProduct.getName());
        product.setCategory(updatedProduct.getCategory());
        product.setColor(updatedProduct.getColor());
        product.setSeason(updatedProduct.getSeason());
        product.setPrice(updatedProduct.getPrice());
        product.setGenderCategory(updatedProduct.getGenderCategory());

        Brand brand = brandRepository.findById(brandId).orElseThrow();
        product.setBrand(brand);

        if (imageFile != null && !imageFile.isEmpty()) {
            String imagePath = saveImage(imageFile);
            product.setImageUrl(imagePath);
        }

        product.getSizeStocks().clear();
        for (SizeStock ss : sizeStockWrapper.getSizes()) {
            ss.setSchoolUniform(product);
            product.getSizeStocks().add(ss);
        }

        int totalStock = product.getSizeStocks().stream()
                .mapToInt(SizeStock::getStock)
                .sum();
        product.setStock(totalStock);

        schoolUniformRepository.save(product);
        return "redirect:/admin/dashboard";
    }






    @PostMapping("/admin/products/update-stock")
    public String updateStock(@RequestParam Long productId, @RequestParam int stock) {
        schoolUniformService.updateProductStock(productId, stock);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/admin/products/edit/{id}")
    public String editProductForm(@PathVariable Long id, Model model) {
        SchoolUniform product = schoolUniformRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Товар не найден: " + id));
        model.addAttribute("product", product);
        model.addAttribute("brands", brandRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("seasons", Season.values());
        return "admin/edit-product";
    }


    @GetMapping("/profile")
    public String userProfile(Model model, Authentication auth, CsrfToken csrfToken) {
        User user = userService.getUserByUsername(auth.getName());
        model.addAttribute("user", user);
        model.addAttribute("_csrf", csrfToken);
        return "profile";
    }

    @GetMapping("/profile/cards")
    public String showCardForm(Model model, Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        model.addAttribute("user", user);
        return "cards";
    }


    @PostMapping("/profile/edit")
    public String updateProfile(@ModelAttribute("user") User updatedUser, Principal principal) {
        User currentUser = userService.getUserByUsername(principal.getName());
        updatedUser.setId(currentUser.getId());
        updatedUser.setUsername(currentUser.getUsername());
        updatedUser.setPassword(currentUser.getPassword());
        updatedUser.setRole(currentUser.getRole());
        userService.save(updatedUser);
        return "redirect:/profile";
    }

    @PostMapping("/profile/cards")
    public String saveCardDetails(@ModelAttribute("user") User cardFormUser,
                                  Principal principal) {
        String username = principal.getName();
        User user = userRepository.findByUsername(username).orElseThrow();

        user.setCardNumber(cardFormUser.getCardNumber());
        user.setCardExpiry(cardFormUser.getCardExpiry());
        user.setCardCvv(cardFormUser.getCardCvv());

        userRepository.save(user);
        return "redirect:/profile/cards";
    }


    @PostMapping("/profile/save")
    public String saveProfile(@ModelAttribute("user") User updatedUser,
                              @RequestParam("password") String password,
                              Model model,
                              Authentication authentication) {
        String currentUsername = authentication.getName();
        User existingUser = userRepository.findById(updatedUser.getId()).orElseThrow();

        // Проверка на дублирование логина
        if (!updatedUser.getUsername().equals(currentUsername)) {
            userRepository.findByUsername(updatedUser.getUsername()).ifPresent(conflict -> {
                // найден другой пользователь с таким логином
                if (!conflict.getId().equals(existingUser.getId())) {
                    model.addAttribute("user", existingUser);
                    model.addAttribute("error", "Пользователь с таким логином уже существует.");
                    return;
                }
            });
        }

        existingUser.setUsername(updatedUser.getUsername());

        if (password != null && !password.isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(password));
        }

        existingUser.setFirstName(updatedUser.getFirstName());
        existingUser.setLastName(updatedUser.getLastName());
        existingUser.setMiddleName(updatedUser.getMiddleName());
        existingUser.setGender(updatedUser.getGender());
        existingUser.setBirthDate(updatedUser.getBirthDate());
        existingUser.setCity(updatedUser.getCity());
        existingUser.setAddress(updatedUser.getAddress());
        existingUser.setPhone(updatedUser.getPhone());

        userRepository.save(existingUser);
        return "redirect:/profile";
    }


    private String saveImage(MultipartFile imageFile) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            String uploadDir = System.getProperty("user.dir") + "/uploads";
            File uploadPath = new File(uploadDir);
            if (!uploadPath.exists()) uploadPath.mkdirs();

            String originalFilename = imageFile.getOriginalFilename();
            String uniqueFilename = System.currentTimeMillis() + "_" + originalFilename;
            File destination = new File(uploadPath, uniqueFilename);

            imageFile.transferTo(destination);
            return "uploads/" + uniqueFilename;
        }
        return null;
    }

    @GetMapping("/product/{id}")
    public String viewProduct(@PathVariable Long id, Model model, Authentication auth) {
        SchoolUniform product = schoolUniformRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Товар не найден: " + id));
        model.addAttribute("product", product);

        boolean isAdmin = auth != null &&
                auth.getAuthorities().stream()
                        .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
        model.addAttribute("isAdmin", isAdmin);

        return "product-details";
    }

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }

    @PostMapping("/register")
    public String processRegistration(@ModelAttribute("user") User user) {
        userService.registerUser(user, "USER");
        return "redirect:/login";
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.registerCustomEditor(LocalDate.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) {
                if (text == null || text.trim().isEmpty()) {
                    setValue(null);
                } else {
                    setValue(LocalDate.parse(text, DateTimeFormatter.ofPattern("yyyy-MM-dd")));
                }
            }

            @Override
            public String getAsText() {
                LocalDate date = (LocalDate) getValue();
                return date != null ? date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) : "";
            }
        });
    }
}
