package com.ordernow.backend.order.repository;

import com.ordernow.backend.order.model.entity.Order;
import com.ordernow.backend.order.model.entity.OrderedStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderRepository extends MongoRepository<Order, String> {
    Order findByCustomerIdAndStatus(String customerId, OrderedStatus status);

    Page<Order> findAllByCustomerIdAndStatus(String customerId, OrderedStatus status, Pageable pageable);
    Page<Order> findAllByCustomerIdAndStatusNot(String id, OrderedStatus status, Pageable pageable);

    Page<Order> findAllByStoreIdAndStatus(String storeId, OrderedStatus status, Pageable pageable);
    Page<Order> findAllByStoreIdAndStatusNot(String id, OrderedStatus status, Pageable pageable);

    void deleteByStatus(OrderedStatus status);
}
