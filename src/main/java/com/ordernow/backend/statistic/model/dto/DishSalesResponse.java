package com.ordernow.backend.statistic.model.dto;

import com.ordernow.backend.menu.model.entity.Dish;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class DishSalesResponse {
    private String dishId;
    private String dishName;
    private Integer salesVolume;

    public static DishSalesResponse createResponse(Dish dish) {
        return new DishSalesResponse(dish.getId(), dish.getName(), dish.getSalesVolume());
    }
}
