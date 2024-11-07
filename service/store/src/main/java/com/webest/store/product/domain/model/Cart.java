package com.webest.store.product.domain.model;

import com.webest.store.product.domain.model.dto.ProductDto;
import jakarta.persistence.Id;
import lombok.Getter;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "FoodCart")
@Getter
public class Cart {

    @Id
    String id;

    @Indexed(unique = true)
    String userId;

    Long storeId;
    ProductDto productDto;

    private Cart(String userId, Long storeId, ProductDto productDto) {
        this.userId = userId;
        this.storeId = storeId;
        this.productDto = productDto;
    }

    public static Cart createdCart(String userId, Long storeId, Product product) {
        return new Cart(userId, storeId, ProductDto.createProduct(product));
    }

}
