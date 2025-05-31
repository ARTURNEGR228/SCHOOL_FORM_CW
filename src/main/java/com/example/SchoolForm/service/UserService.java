package com.example.SchoolForm.service;

import com.example.SchoolForm.model.User;
import com.example.SchoolForm.repository.UserRepository;
import com.example.SchoolForm.util.EncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;



    public void registerUser(User user, String role) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(role);
        userRepository.save(user);
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElse(null);
    }



    public void updateUser(User updatedUser, String newPassword) {
        User user = userRepository.findById(updatedUser.getId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setPhone(updatedUser.getPhone());

        if (newPassword != null && !newPassword.isBlank()) {
            user.setPassword(passwordEncoder.encode(newPassword));
        }

        userRepository.save(user);
    }

    public void updateCardDetails(Long userId, String cardNumber, String cardExpiry, String cardCvv) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Пользователь не найден"));

        user.setCardNumber(cardNumber);
        user.setCardExpiry(cardExpiry);
        user.setCardCvv(EncryptionUtil.encrypt(cardCvv));

        userRepository.save(user);
    }

    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }

    public void saveUser(User user) {
        userRepository.save(user);
    }


    public void save(User updatedUser) {
        Optional<User> optionalUser = userRepository.findById(updatedUser.getId());
        if (optionalUser.isPresent()) {
            User existingUser = optionalUser.get();

            // Обновляем основные поля
            existingUser.setFirstName(updatedUser.getFirstName());
            existingUser.setLastName(updatedUser.getLastName());
            existingUser.setMiddleName(updatedUser.getMiddleName());
            existingUser.setGender(updatedUser.getGender());
            existingUser.setBirthDate(updatedUser.getBirthDate());
            existingUser.setCity(updatedUser.getCity());
            existingUser.setAddress(updatedUser.getAddress());
            existingUser.setPhone(updatedUser.getPhone());

            // Обновляем пароль, если он не пустой и отличается от текущего
            if (updatedUser.getPassword() != null && !updatedUser.getPassword().isBlank()) {
                String encodedPassword = passwordEncoder.encode(updatedUser.getPassword());
                existingUser.setPassword(encodedPassword);
            }

            // Не трогаем: username, role

            userRepository.save(existingUser);
        } else {
            throw new IllegalArgumentException("Пользователь с ID " + updatedUser.getId() + " не найден");
        }

    }

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }




}
