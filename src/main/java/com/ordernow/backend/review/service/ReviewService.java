package com.ordernow.backend.review.service;

import com.ordernow.backend.common.dto.PageResponse;
import com.ordernow.backend.review.model.dto.ReviewRequest;
import com.ordernow.backend.review.model.entity.Review;
import com.ordernow.backend.review.repository.ReviewRepository;
import com.ordernow.backend.store.service.StoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final StoreService storeService;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, StoreService storeService) {
        this.reviewRepository = reviewRepository;
        this.storeService = storeService;
    }

    public List<Review> getReviewByIds(List<String> ids) {
        return reviewRepository.findAllById(ids);
    }

    public PageResponse<Review> queryStoreReviews(String storeId, int page, int size)
            throws IllegalArgumentException {

        if(page < 0 || size <= 0) {
            throw new IllegalArgumentException("Invalid page number or page size");
        }
        Pageable pageable = PageRequest.of(page, size);
        Page<Review> reviews = reviewRepository.findAllByStoreId(storeId, pageable);
        return PageResponse.of(reviews);
    }

    public int[] getReviewNumberByStoreId(String storeId)
            throws NoSuchElementException {

        List<Review> reviews = reviewRepository.findAllByStoreId(storeId);
        int[] reviewNumbers = new int[5];
        for (Review review : reviews) {
            reviewNumbers[5-review.getRating().intValue()]++;
        }
        return reviewNumbers;
    }

    public void addNewReviewToStore(String storeId, ReviewRequest reviewRequest, String userId, String userName)
            throws NoSuchElementException {

        if(reviewRequest.getRating() <= 0 || reviewRequest.getRating() > 5) {
            throw new NoSuchElementException("Invalid rating");
        }
        if(reviewRequest.getAverageSpend() <= 0) {
            throw new NoSuchElementException("Invalid average spend");
        }

        Review review = Review.createReview(reviewRequest, userId, userName, storeId);
        storeService.updateStoreByReview(
                storeId, reviewRequest.getRating(),
                reviewRequest.getAverageSpend(),
                reviewRepository.countByStoreId(storeId));
        reviewRepository.save(review);
    }
}
