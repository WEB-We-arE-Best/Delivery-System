package com.webest.store.product.domain.repository;

import com.webest.store.product.domain.model.Cart;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CartRepository extends MongoRepository<Cart, String> {

    Optional<Cart> findByUserId(String userId);
}
