package com.example.SchoolForm.model;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "school_uniform")
public class SchoolUniform {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String category;

    @Column(nullable = false)
    private String size;

    @Column(nullable = false)
    private String color;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Season season;

    @Column(nullable = false)
    private double price;

    @Column(name = "stock", nullable = false)
    private int stock;

    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;


    @Column(name = "gender_category")
    private String genderCategory;

    @OneToMany(mappedBy = "schoolUniform", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SizeStock> sizeStocks;


    // геттеры/сеттеры
    public Brand getBrand() { return brand; }
    public void setBrand(Brand brand) { this.brand = brand; }



    public String getGenderCategory() { return genderCategory; }
    public void setGenderCategory(String genderCategory) { this.genderCategory = genderCategory; }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public Season getSeason() { return season; }
    public void setSeason(Season season) { this.season = season; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public List<SizeStock> getSizeStocks() {return sizeStocks;}

    public void setSizeStocks(List<SizeStock> sizeStocks) {this.sizeStocks = sizeStocks;}

}
