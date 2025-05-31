package com.example.SchoolForm.dto;

import com.example.SchoolForm.model.SizeStock;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SizeStockWrapper {
    private List<SizeStock> sizes = new ArrayList<>();
}
