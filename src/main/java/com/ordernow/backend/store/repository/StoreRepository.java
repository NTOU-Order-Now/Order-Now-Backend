package com.ordernow.backend.store.repository;

import com.ordernow.backend.store.model.entity.Store;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreRepository extends MongoRepository<Store, String> {
    @Query(value = "{ 'name': { $regex: ?0, $options: 'i' } }", fields = "{ 'id' : 1 }", sort = "{ 'name': 1 }")
    List<Store> findByNameContainingOnlyIdOrderByNameAsc(String keyword);
    @Query(value = "{ 'name': { $regex: ?0, $options: 'i' } }", fields = "{ 'id' : 1 }", sort = "{ 'name': -1 }")
    List<Store> findByNameContainingOnlyIdOrderByNameDesc(String keyword);
    @Query(value = "{ 'name': { $regex: ?0, $options: 'i' } }", fields = "{ 'id' : 1 }", sort = "{ 'rating': 1 }")
    List<Store> findByNameContainingOnlyIdOrderByRatingAsc(String keyword);
    @Query(value = "{ 'name': { $regex: ?0, $options: 'i' } }", fields = "{ 'id' : 1 }", sort = "{ 'rating': -1 }")
    List<Store> findByNameContainingOnlyIdOrderByRatingDesc(String keyword);
    @Query(value = "{ 'name': { $regex: ?0, $options: 'i' } }", fields = "{ 'id' : 1 }", sort = "{ 'averageSpend': 1 }")
    List<Store> findByNameContainingOnlyIdOrderByAverageSpendAsc(String keyword);
    @Query(value = "{ 'name': { $regex: ?0, $options: 'i' } }", fields = "{ 'id' : 1 }", sort = "{ 'averageSpend': -1 }")
    List<Store> findByNameContainingOnlyIdOrderByAverageSpendDesc(String keyword);

    Page<Store> findByNameContainingIgnoreCase(String keyword, PageRequest pageRequest);
}
