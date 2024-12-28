package com.ordernow.backend.store.controller.v3;

import com.ordernow.backend.common.dto.ApiResponse;
import com.ordernow.backend.store.model.entity.Store;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("StoreControllerV3")
@RequestMapping("/api/v3/stores")
public class StoreController {

    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<Store>>> searchStores(
            @RequestParam(defaultValue = "") String keyword,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        return null;
    }
}
