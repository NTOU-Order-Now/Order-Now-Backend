package com.ordernow.backend.user.model.entity;

import com.ordernow.backend.order.model.entity.Order;
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
@TypeAlias("merchant")
public class Merchant extends User{
    private String storeId;

    public Merchant() {// Springboot need it
        super();
    }

    public Merchant(User user, String storeId) {
        super(user);
        this.storeId = storeId;
    }
}
