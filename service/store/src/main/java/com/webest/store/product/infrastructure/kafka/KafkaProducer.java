package com.webest.store.product.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webest.store.product.domain.model.Product;
import com.webest.store.product.domain.repository.ProductRepository;
import com.webest.store.product.exception.ProductErrorCode;
import com.webest.store.product.exception.ProductException;
import com.webest.store.product.infrastructure.kafka.dto.CartDto;
import com.webest.store.product.infrastructure.kafka.dto.ProductDto;
import com.webest.web.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ProductRepository productRepository;

    public String send(Long id,String userId){
        // 제품 검증
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductException(ProductErrorCode.PRODUCT_NOT_FOUND));

        ProductDto productDto = new ProductDto(id,product.getStore().getId(),product.getName(),product.getPrice(),product.getDescription());


        // cart 객체 생성
        CartDto cartDto = new CartDto(userId,productDto.storeId(),productDto);

        ObjectMapper mapper = new ObjectMapper();
        String jsonInString = "";
        try {
            jsonInString = mapper.writeValueAsString(cartDto);
        }catch (JsonProcessingException e){
            e.printStackTrace();
        }

        kafkaTemplate.send("cart-topic", jsonInString);
        log.info("Kafka Producer send success :"+cartDto);

        return productDto.name();
    }
}
