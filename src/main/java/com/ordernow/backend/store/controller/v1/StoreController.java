package com.ordernow.backend.store.controller.v1;

import com.ordernow.backend.menu.model.entity.Dish;
import com.ordernow.backend.menu.model.entity.Menu;
import com.ordernow.backend.review.model.entity.Review;
import com.ordernow.backend.store.model.entity.Store;
import com.ordernow.backend.menu.service.MenuService;
import com.ordernow.backend.review.service.ReviewService;
import com.ordernow.backend.store.service.StoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController("StoreControllerV1")
@RequestMapping("/api/v1/stores")
@Slf4j
public class StoreController {

    private final StoreService storeService;
    private final MenuService menuService;
    private final ReviewService reviewService;
    private static final Set<String> ALLOWED_SORT_BY_FIELDS = Set.of("averageSpend", "rating", "name");
    private static final Set<String> ALLOWED_SORT_DIR_FIELDS = Set.of("asc", "desc");

    @Autowired
    public StoreController(StoreService storeService,
                           MenuService menuService,
                           ReviewService reviewService) {
        this.storeService = storeService;
        this.menuService = menuService;
        this.reviewService = reviewService;
    }

    @GetMapping("/search")
    public ResponseEntity<List<String>> getIdList(
            @RequestParam(value = "keyword", defaultValue = "") String keyword,
            @RequestParam(value = "sortBy", defaultValue = "rating") String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "desc") String sortDir) {

        if(!ALLOWED_SORT_BY_FIELDS.contains(sortBy)) {
            log.error("Invalid sortBy value. Allowed values are 'averageSpend', 'rating' and 'name'.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(!ALLOWED_SORT_DIR_FIELDS.contains(sortDir)) {
            log.error("Invalid sortDir value. Allowed values are 'asc' and 'desc'.");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        log.info("Fetch API: getIdList Success");
        List<String> storeIdList = storeService.getStoresIdFilteredAndSorted(keyword, sortBy, sortDir);
        return ResponseEntity.status(HttpStatus.OK).body(storeIdList);
    }

    @PostMapping("/query")
    public ResponseEntity<List<Store>> getStoresByIds(@RequestBody List<String> storeIds) {
        log.info("Fetch API: getStoresByIds Success");
        List<Store> stores = storeService.getStoreByIds(storeIds);
        return ResponseEntity.status(HttpStatus.OK).body(stores);
    }

    @GetMapping("/{storeId}/menu")
    public ResponseEntity<Menu> getMenuByStoreId(@PathVariable String storeId) {

        Store store = storeService.validStoreId(storeId);
        if(store == null) {
            log.error("Store not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Menu menu = menuService.getMenuById(store.getMenuId());
        if(menu == null) {
            log.error("Menu not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        log.info("Fetch API: getMenuById Success");
        return ResponseEntity.status(HttpStatus.OK).body(menu);
    }

    @GetMapping("/{storeId}/menu/dishes")
    public ResponseEntity<List<Dish>> getDishesByCategory(
            @PathVariable String storeId,
            @RequestParam(value = "category") String category
            ) {
        Store store = storeService.validStoreId(storeId);
        if(store == null) {
            log.error("Store not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        log.info("Fetch API: getDishesByCategory Success");
        List<Dish> dishes = menuService.getDishesByCategory(category);
        return ResponseEntity.status(HttpStatus.OK).body(dishes);
    }

    @PostMapping("/reviews/query")
    public ResponseEntity<List<Review>> getReviewsByIds(@RequestBody List<String> ids) {
        log.info("Fetch API: getReviewsByIds Success");
        List<Review> reviews = reviewService.getReviewByIds(ids);
        return ResponseEntity.status(HttpStatus.OK).body(reviews);
    }
}
