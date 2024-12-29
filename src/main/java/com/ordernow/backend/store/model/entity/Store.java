package com.ordernow.backend.store.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.util.Pair;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@Document(collection = "store")
public class Store {
    @Id
    private String id;
    private String name;
    private String picture;
    private String phoneNumber;
    private String address;
    private Double rating;
    private String menuId;
    private Double averageSpend;
    private String description;
    private Boolean isBusiness;
    private Pair<LocalTime, LocalTime>[][] businessHours;

    public Store() {
        businessHours = new Pair[7][2];
        initializeDefaultBusinessHours();
    }

    private void initializeDefaultBusinessHours() {
        for (int i = 0; i < 7; i++) {
            businessHours[i][0] = Pair.of(LocalTime.of(9, 0), LocalTime.of(12, 0));
            businessHours[i][1] = Pair.of(LocalTime.of(17, 0), LocalTime.of(20, 0));
        }
    }

    public static Store createDefaultStore() {
        return Store.builder()
                .name("")
                .picture("")
                .phoneNumber("")
                .address("")
                .rating(0.0)
                .menuId("")
                .averageSpend(0.0)
                .description("")
                .isBusiness(false)
                .build();
    }
}
