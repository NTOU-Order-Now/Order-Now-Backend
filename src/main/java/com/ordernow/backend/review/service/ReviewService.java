package com.ordernow.backend.review.service;

import com.ordernow.backend.auth.repository.UserRepository;
import com.ordernow.backend.review.model.dto.ReviewRequest;
import com.ordernow.backend.review.model.entity.Review;
import com.ordernow.backend.review.repository.ReviewRepository;
import com.ordernow.backend.store.model.entity.Store;
import com.ordernow.backend.store.repository.StoreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final StoreRepository storeRepository;

    @Autowired
    public ReviewService(ReviewRepository reviewRepository, StoreRepository storeRepository) {
        this.reviewRepository = reviewRepository;
        this.storeRepository = storeRepository;
    }

    public List<Review> getReviewByIds(List<String> ids) {
        return reviewRepository.findAllById(ids);
    }

    public int[] getReviewNumberByStoreId(String storeId)
            throws NoSuchElementException {

        Store store =  storeRepository.findById(storeId)
                .orElseThrow(() -> new NoSuchElementException("Store not found"));
        List<Review> reviews = getReviewByIds(store.getReviewIdList());
        int[] reviewNumbers = new int[5];
        for (Review review : reviews) {
            reviewNumbers[5-review.getRating().intValue()]++;
        }
        return reviewNumbers;
    }

    public void addNewReviewToStore(String storeId, ReviewRequest reviewRequest, String userId, String userName)
            throws NoSuchElementException {

        Store store = storeRepository.findById(storeId).orElse(null);
        if(store == null) {
            throw new NoSuchElementException("Store not found");
        }
        if(reviewRequest.getRating() <= 0 || reviewRequest.getRating() > 5) {
            throw new NoSuchElementException("Invalid rating");
        }
        if(reviewRequest.getAverageSpend() <= 0) {
            throw new NoSuchElementException("Invalid average spend");
        }

        Review review = Review.createReview(reviewRequest, userId, userName);
        reviewRepository.save(review);

        store.addReview(review.getId(), review.getRating(), review.getAverageSpend());
        storeRepository.save(store);
    }
}
