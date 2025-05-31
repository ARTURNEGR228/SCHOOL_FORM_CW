package com.example.SchoolForm.model;

import jakarta.persistence.*;

@Entity
@Table(name = "size_stock")
public class SizeStock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String size;

    @Column(nullable = false)
    private int stock;

    @ManyToOne
    @JoinColumn(name = "uniform_id")
    private SchoolUniform schoolUniform;

    // геттеры/сеттеры
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public SchoolUniform getSchoolUniform() { return schoolUniform; }
    public void setSchoolUniform(SchoolUniform schoolUniform) { this.schoolUniform = schoolUniform; }

    public SizeStock() {}

    public SizeStock(String size, int stock, SchoolUniform uniform) {
        this.size = size;
        this.stock = stock;
        this.schoolUniform = uniform;
    }



}

