package com.ordernow.backend.menu.service;

import com.ordernow.backend.menu.model.dto.DishUpdateRequest;
import com.ordernow.backend.menu.model.entity.Category;
import com.ordernow.backend.menu.model.entity.Dish;
import com.ordernow.backend.menu.model.entity.Menu;
import com.ordernow.backend.menu.repository.DishRepository;
import com.ordernow.backend.menu.repository.MenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MenuService {
    private final MenuRepository menuRepository;
    private final DishRepository dishRepository;

    @Autowired
    public MenuService(MenuRepository menuRepository, DishRepository dishRepository) {
        this.menuRepository = menuRepository;
        this.dishRepository = dishRepository;
    }

    public Menu getMenuById(String id)
            throws NoSuchElementException {
        return menuRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Menu not found"));
    }

    public Dish getDishById(String id)
        throws NoSuchElementException {
        return dishRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Dish not found"));
    }

    public void addDishToCategory(Menu menu, Dish dish) {
        for (var category : menu.getCategories()) {
            if (category.getCategoryName().equals(dish.getCategory())) {
                category.getDishIds().add(dish.getId());
                return;
            }
        }

        List<String> dishIds = new ArrayList<>();
        dishIds.add(dish.getId());
        menu.getCategories().add(new Category(dish.getCategory(), dishIds));
    }

    public List<Dish> getDishesByIds(List<String> ids) {
        return ids.stream()
                .map(dishRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public List<Dish> getDishesByCategory(String category) {
        return dishRepository.findAllByCategory(category);
    }

    public List<Dish> getCategoryDishesByMenuId(String menuId, String category)
            throws NoSuchElementException {

        Menu menu = getMenuById(menuId);
        for(Category c : menu.getCategories()) {
            if(c.getCategoryName().equals(category)){
                return getDishesByIds(c.getDishIds());
            }
        }
        throw new NoSuchElementException("Category not found");
    }

    public void updateDishesOrder(String menuId, String category, List<String> dishIds)
            throws NoSuchElementException {

        Menu menu = getMenuById(menuId);
        for(Category c : menu.getCategories()) {
            if(c.getCategoryName().equals(category)){
                if(new HashSet<>(c.getDishIds()).containsAll(dishIds)) {
                    c.setDishIds(dishIds);
                    menuRepository.save(menu);
                    return;
                } else {
                    throw new NoSuchElementException("DishId List is wrong");
                }
            }
        }
        throw new NoSuchElementException("Category not found");
    }

    public void updateCategoryName(String menuId, String category, String newName)
            throws NoSuchElementException {

        Menu menu = getMenuById(menuId);
        for(Category c : menu.getCategories()) {
            System.out.println(c.getCategoryName());
            if(c.getCategoryName().equals(category)){
                c.setCategoryName(newName);
                List<Dish> dishes = getCategoryDishesByMenuId(menu.getId(), category);
                for(Dish d : dishes) {
                    d.setCategory(newName);
                }
                menuRepository.save(menu);
                dishRepository.saveAll(dishes);
                return;
            }
        }
        throw new NoSuchElementException("Category not found");
    }

    public String createDishInMenu(String menuId) {

        Dish dish = Dish.createDefaultDish();
        dish.setId(null);
        dishRepository.save(dish);

        Menu menu = getMenuById(menuId);
        addDishToCategory(menu, dish);
        menuRepository.save(menu);
        return dish.getId();
    }

    public void updateDishInMenu(String menuId, String dishId, DishUpdateRequest request)
            throws NoSuchElementException {

        Menu menu = getMenuById(menuId);
        Dish originalDish = getDishById(dishId);

        Dish updatedDish = request.convertToEntity(
                originalDish.getId(),
                originalDish.getSalesVolume());
        
        if (!originalDish.getCategory().equals(updatedDish.getCategory())) {
            menu.getCategories().stream()
                    .filter(category -> category.getCategoryName().equals(originalDish.getCategory()))
                    .findFirst()
                    .ifPresent(category -> {
                        category.getDishIds().remove(dishId);
                        if(category.getDishIds().isEmpty()) {
                            menu.getCategories().remove(category);
                        }
                    });
            addDishToCategory(menu, updatedDish);
            menuRepository.save(menu);
        }
        
        dishRepository.save(updatedDish);
    }

    public void deleteDishFromMenu(String menuId, String dishId)
            throws NoSuchElementException {
        Menu menu = getMenuById(menuId);
        Dish dish = getDishById(dishId);

        menu.getCategories().stream()
                .filter(category -> category.getCategoryName().equals(dish.getCategory()))
                .findFirst()
                .ifPresent(category -> {
                    category.getDishIds().remove(dishId);
                    if (category.getDishIds().isEmpty()) {
                        menu.getCategories().remove(category);
                    }
                });

        menuRepository.save(menu);
        dishRepository.deleteById(dishId);
    }

    public String createAndSaveMenu() {
        Menu menu = Menu.createDefaultMenu();
        return menuRepository.save(menu).getId();
    }

    public void updateSalesVolume(String dishId, int quantity) {
        Dish dish = getDishById(dishId);
        dish.setSalesVolume(dish.getSalesVolume() + quantity);
        dishRepository.save(dish);
    }
}
