package com.ordernow.backend.order.model.entity;

import com.ordernow.backend.menu.model.entity.Dish;
import com.ordernow.backend.order.model.dto.OrderedDishRequest;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Data
@AllArgsConstructor
public class OrderedDish {
    private String id;
    private String dishId;
    private String dishName;
    private Double price;
    private Integer quantity;
    private String note;
    private String storeId;
    private List<ChosenAttribute> chosenAttributes;


    public OrderedDish() {
        this.id = UUID.randomUUID().toString();
        this.chosenAttributes = new ArrayList<>();
    }

    public OrderedDish(OrderedDishRequest orderedDishRequest, Dish dish) {
        this.id = UUID.randomUUID().toString();
        this.dishId = orderedDishRequest.getDishId();
        this.dishName = dish.getName();
        this.price = dish.getPrice();
        this.quantity = orderedDishRequest.getQuantity();
        this.note = orderedDishRequest.getNote();
        this.chosenAttributes = orderedDishRequest.getChosenAttributes();
    }

    public boolean equals(OrderedDishRequest orderedDishRequest) {
        return Objects.equals(dishId, orderedDishRequest.getDishId())
                && Objects.equals(note, orderedDishRequest.getNote())
                && Objects.equals(chosenAttributes, orderedDishRequest.getChosenAttributes());
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
