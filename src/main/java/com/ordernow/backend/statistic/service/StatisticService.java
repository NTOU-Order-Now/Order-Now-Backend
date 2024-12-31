package com.ordernow.backend.statistic.service;

import com.ordernow.backend.menu.model.entity.Dish;
import com.ordernow.backend.menu.service.MenuService;
import com.ordernow.backend.statistic.model.dto.DishSalesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class StatisticService {

    private final MenuService menuService;

    @Autowired
    public StatisticService(MenuService menuService) {
        this.menuService = menuService;
    }

    public List<DishSalesResponse> getCategoryDishesSalesByMenuId(String menuId, String category)
            throws NoSuchElementException {

        List<Dish> dishes = menuService.getCategoryDishesByMenuId(menuId, category);
        return dishes.stream()
                .map(DishSalesResponse::createResponse)
                .collect(Collectors.toList());
    }
}
