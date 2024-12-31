package com.ordernow.backend.statistic.controller.v1;

import com.ordernow.backend.common.dto.ApiResponse;
import com.ordernow.backend.statistic.model.dto.DishSalesResponse;
import com.ordernow.backend.statistic.service.StatisticService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("StatisticControllerV1")
@RequestMapping("/api/v1/statistic")
@Slf4j
public class StatisticController {

    private final StatisticService statisticService;

    public StatisticController(StatisticService statisticService) {
        this.statisticService = statisticService;
    }

    @GetMapping("/{menuId}/sales")
    @PreAuthorize("hasRole('MERCHANT')")
    public ResponseEntity<ApiResponse<List<DishSalesResponse>>> getDishesSalesByCategory(
            @PathVariable String menuId,
            @RequestParam(value = "category") String category) {

        List<DishSalesResponse> responses = statisticService.getCategoryDishesSalesByMenuId(menuId, category);
        ApiResponse<List<DishSalesResponse>> apiResponse = ApiResponse.success(responses);
        log.info("Get dishes successfully");
        return ResponseEntity.status(HttpStatus.OK).body(apiResponse);
    }
}
