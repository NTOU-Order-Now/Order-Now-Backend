package com.ordernow.backend.review.model.entity;

import com.ordernow.backend.review.model.dto.ReviewRequest;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "review")
public class Review {
    @Id
    private String id;
    private Double averageSpend;
    private String comment;
    private String storeId;
    private String userId;
    private String userName;
    private Double rating;
    private LocalDateTime date;

    public static Review createReview(ReviewRequest reviewRequest, String userId, String userName, String storeId) {
        return Review.builder()
                .averageSpend(reviewRequest.getAverageSpend())
                .comment(reviewRequest.getComment())
                .storeId(storeId)
                .userId(userId)
                .userName(userName)
                .rating(reviewRequest.getRating())
                .date(LocalDateTime.now())
                .build();
    }
}
