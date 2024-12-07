//package com.ordernow.backend.service;
//
//import com.ordernow.backend.menu.model.entity.Dish;
//import com.ordernow.backend.menu.model.entity.Menu;
//import com.ordernow.backend.menu.repository.DishRepository;
//import com.ordernow.backend.menu.repository.MenuRepository;
//import com.ordernow.backend.menu.service.MenuService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.util.List;
//import java.util.Optional;
//
//import static org.junit.jupiter.api.Assertions.assertEquals;
//import static org.junit.jupiter.api.Assertions.assertNull;
//import static org.mockito.Mockito.*;
//
//class MenuServiceTest {
//
//    @Mock
//    private MenuRepository menuRepository;
//
//    @Mock
//    private DishRepository dishRepository;
//
//    @InjectMocks
//    private MenuService menuService;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//    }
//
//    @Test
//    void testGetMenuById() {
//        // Arrange
//        String menuId = "menu1";
//        Menu mockMenu = new Menu(menuId, List.of());
//        when(menuRepository.findById(menuId)).thenReturn(Optional.of(mockMenu));
//
//        // Act
//        Menu result = menuService.getMenuById(menuId);
//
//        // Assert
//        assertEquals(mockMenu, result);
//        verify(menuRepository).findById(menuId);
//    }
//
//    @Test
//    void testGetMenuByIdNotFound() {
//        // Arrange
//        String menuId = "menu1";
//        when(menuRepository.findById(menuId)).thenReturn(Optional.empty());
//
//        // Act
//        Menu result = menuService.getMenuById(menuId);
//
//        // Assert
//        assertNull(result);
//        verify(menuRepository).findById(menuId);
//    }
//
//    @Test
//    void testGetDishesByIds() {
//        // Arrange
//        List<String> dishIds = List.of("dish1", "dish2");
//
//        Dish dish1 = new Dish(
//                "dish1",
//                "Dish One",
//                "Description One",
//                "picture1.jpg",
//                10.0,
//                "Appetizers",
//                100,
//                List.of()
//        );
//
//        Dish dish2 = new Dish(
//                "dish2",
//                "Dish Two",
//                "Description Two",
//                "picture2.jpg",
//                15.0,
//                "Main Course",
//                200,
//                List.of()
//        );
//
//        when(dishRepository.findById("dish1")).thenReturn(Optional.of(dish1));
//        when(dishRepository.findById("dish2")).thenReturn(Optional.of(dish2));
//
//        // Act
//        List<Dish> result = menuService.getDishesByIds(dishIds);
//
//        // Assert
//        assertEquals(List.of(dish1, dish2), result);
//        verify(dishRepository).findById("dish1");
//        verify(dishRepository).findById("dish2");
//    }
//
//
//    @Test
//    void testGetDishesByIdsWithSomeMissingDish() {
//        // Arrange
//        List<String> dishIds = List.of("dish1", "dish2");
//
//        Dish dish1 = new Dish(
//                "dish1",
//                "Dish One",
//                "Description One",
//                "picture1.jpg",
//                10.0,
//                "Appetizers",
//                100,
//                List.of()
//        );
//
//        when(dishRepository.findById("dish1")).thenReturn(Optional.of(dish1));
//        when(dishRepository.findById("dish2")).thenReturn(Optional.empty());
//
//        // Act
//        List<Dish> result = menuService.getDishesByIds(dishIds);
//
//        // Assert
//        assertEquals(List.of(dish1), result);
//        verify(dishRepository).findById("dish1");
//        verify(dishRepository).findById("dish2");
//    }
//
//    @Test
//    void testGetDishesByIdsWithMissingDish() {
//        // Arrange
//        List<String> dishIds = List.of("dish3", "dish4");
//
//        Dish dish1 = new Dish(
//                "dish1",
//                "Dish One",
//                "Description One",
//                "picture1.jpg",
//                10.0,
//                "Appetizers",
//                100,
//                List.of()
//        );
//
//        when(dishRepository.findById("dish3")).thenReturn(Optional.empty());
//        when(dishRepository.findById("dish4")).thenReturn(Optional.empty());
//
//        // Act
//        List<Dish> result = menuService.getDishesByIds(dishIds);
//
//        // Assert
//        assertEquals(List.of(), result);
//        verify(dishRepository).findById("dish3");
//        verify(dishRepository).findById("dish4");
//    }
//
//
//
//}
