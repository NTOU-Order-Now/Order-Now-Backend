package com.ordernow.backend.menu.model.dto;

import com.ordernow.backend.menu.model.entity.Dish;
import com.ordernow.backend.menu.model.entity.DishAttribute;
import lombok.Data;

import java.util.List;

@Data
public class DishUpdateRequest {
    private String name;
    private String description;
    private String picture;
    private Double price;
    private String category;
    private List<DishAttribute> dishAttributes;

    public Dish convertToEntity(String id, int salesVolume) {
        return Dish.builder()
                .id(id)
                .name(name)
                .description(description)
                .picture(picture)
                .price(price)
                .category(category)
                .salesVolume(salesVolume)
                .dishAttributes(dishAttributes)
                .build();
    }
}
