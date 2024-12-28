package com.ordernow.backend.store.controller.v3;

import com.ordernow.backend.common.dto.ApiResponse;
import com.ordernow.backend.common.dto.PageResponse;
import com.ordernow.backend.store.model.entity.Store;
import com.ordernow.backend.store.service.StoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("StoreControllerV3")
@RequestMapping("/api/v3/stores")
@Slf4j
public class StoreController {

    private final StoreService storeService;

    @Autowired
    public StoreController(StoreService storeService) {
        this.storeService = storeService;
    }

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<PageResponse<Store>>> searchStores(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "rating") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size)
            throws IllegalArgumentException {

        PageResponse<Store> stores = storeService.searchStores(keyword, sortBy, sortDir, page, size);
        ApiResponse<PageResponse<Store>> apiResponse = ApiResponse.success(stores);
        log.info("Search stores successfully");
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
