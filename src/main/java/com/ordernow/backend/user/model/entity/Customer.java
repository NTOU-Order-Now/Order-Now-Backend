package com.ordernow.backend.user.model.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Document(collection = "user")
@TypeAlias("customer")
public class Customer extends User{
    private List<String> storeCollection;

    public Customer() {// Springboot need it
        super();
        storeCollection = new ArrayList<>();
    }

    public Customer(User user) {
        super(user);
        storeCollection = new ArrayList<>();
    }
}
