package com.ordernow.backend.menu.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class Category {
    private String categoryName;
    private List<String> dishIds;
}
