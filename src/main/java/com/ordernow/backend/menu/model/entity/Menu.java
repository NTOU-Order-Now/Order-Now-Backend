package com.ordernow.backend.menu.model.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.util.Pair;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@Document(collection = "menu")
public class Menu {
    @Id
    private String id;
    private List<Category> categories;

    public Menu() {
        categories = new ArrayList<>();
    }

    public static Menu createDefaultMenu() {
        return new Menu();
    }
}
