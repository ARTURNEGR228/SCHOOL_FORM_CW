package com.example.SchoolForm.model;

import com.example.SchoolForm.util.EncryptionUtil;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "app_user")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String password;

    private String firstName;
    private String lastName;
    private String middleName;
    private String gender;

    private LocalDate birthDate;

    private String city;
    private String address;
    private String phone;
    private String email;

    private String role;

    private String cardNumber;
    private String cardExpiry;
    private String cardCvv;


    // Явно добавим методы, если lombok не подключен или не работает:

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getCardExpiry() {
        return cardExpiry;
    }

    public void setCardExpiry(String cardExpiry) {
        this.cardExpiry = cardExpiry;
    }

    public String getCardCvv() {
        try {
            return EncryptionUtil.decrypt(cardCvv);
        } catch (Exception e) {
            return null;
        }
    }

    public void setCardCvv(String cardCvv) {
        try {
            this.cardCvv = EncryptionUtil.encrypt(cardCvv);
        } catch (Exception e) {
            this.cardCvv = null;
        }
    }
}
