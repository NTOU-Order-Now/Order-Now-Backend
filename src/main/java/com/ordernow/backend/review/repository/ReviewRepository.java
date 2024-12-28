package com.ordernow.backend.review.repository;

import com.ordernow.backend.review.model.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ReviewRepository extends MongoRepository<Review, String> {
    Page<Review> findAllByStoreId(String storeId, Pageable pageable);
}
