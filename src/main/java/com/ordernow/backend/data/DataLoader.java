package com.ordernow.backend.data;

import com.ordernow.backend.auth.repository.UserRepository;
import com.ordernow.backend.order.repository.OrderRepository;
import com.ordernow.backend.menu.repository.DishRepository;
import com.ordernow.backend.menu.repository.MenuRepository;
import com.ordernow.backend.review.repository.ReviewRepository;
import com.ordernow.backend.store.repository.StoreRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataLoader {

    private final UserRepository userRepository;
    private final MenuRepository menuRepository;
    private final OrderRepository orderRepository;
    private final ReviewRepository reviewRepository;
    private final DishRepository dishRepository;
    private final StoreRepository storeRepository;

    @Autowired
    public DataLoader(UserRepository userRepository, MenuRepository menuRepository, OrderRepository orderRepository, ReviewRepository reviewRepository, DishRepository dishRepository, StoreRepository storeRepository) {
        this.userRepository = userRepository;
        this.menuRepository = menuRepository;
        this.orderRepository = orderRepository;
        this.reviewRepository = reviewRepository;
        this.dishRepository = dishRepository;
        this.storeRepository = storeRepository;
    }

    @PostConstruct
    public void init() {
//        userRepository.deleteAll();
//        menuRepository.deleteAll();
//        orderRepository.deleteAll();
//        reviewRepository.deleteAll();
//        dishRepository.deleteAll();
//        storeRepository.deleteAll();
    }
}
