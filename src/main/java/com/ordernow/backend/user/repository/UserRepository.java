package com.ordernow.backend.user.repository;


import com.ordernow.backend.user.model.entity.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface UserRepository extends MongoRepository<User, String> {
    User findByEmail(String email);
}
