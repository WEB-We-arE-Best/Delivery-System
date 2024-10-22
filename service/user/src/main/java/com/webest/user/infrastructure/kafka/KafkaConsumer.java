package com.webest.user.infrastructure.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.webest.user.domain.model.vo.OrderProductDto;
import com.webest.user.domain.model.vo.ProductDto;
import com.webest.user.domain.model.vo.ShoppingCartDto;
import com.webest.user.infrastructure.redis.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaConsumer {

    private final RedisUtil redisUtil;
    private final KafkaTemplate<String, String> kafkaTemplate;

    @KafkaListener(topics = "cart-topic")
    public void updateQty(String message) {
        log.info("cart-topic: " + message);
        Map<Object,Object> map = new HashMap<>();
        ObjectMapper mapper = new ObjectMapper();
        try{
            map = mapper.readValue(message,new TypeReference<Map<Object,Object>>(){});
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        String userId = (String) map.get("userId");
        Long storeId = ((Number) map.get("storeId")).longValue();

        // productDto 객체 추출
        Map<String, Object> productDtoMap = (Map<String, Object>) map.get("productDto");
        ProductDto productDto = mapper.convertValue(productDtoMap, ProductDto.class);


        log.info(userId);
        log.info(storeId.toString());
        log.info(productDto.toString());

        ShoppingCartDto redisShopDto = redisUtil.getShoppingCart(userId);

        // 현재 장바구니가 존재하지 않다면 장바구니 새로 생성
        // 장바구니에 담겨있는 제품의 가게의 정보와 새로 장바구니를 담을려는 가게의 정보가 다를경우 새로 덮어씌움
        if((redisShopDto == null)|| !storeId.equals(redisShopDto.storeId())){
            Set<OrderProductDto> orderList = new HashSet<>();
            OrderProductDto orderProductDto = new OrderProductDto(productDto.id(),1,productDto.price());
            orderList.add(orderProductDto);
            ShoppingCartDto shopDto = new ShoppingCartDto(userId, storeId, orderList);
            redisUtil.setDataShoppingCart(shopDto);
        }
        // 같은 매장에 주문하면서 기존에 데이터가 존재할 경우
        else {
            Set<OrderProductDto> orderList = new HashSet<>();
            boolean productExists = false; // 제품 존재 여부

            for (OrderProductDto dto : redisShopDto.product()) {
                OrderProductDto updateDto;

                // 장바구니에 저장되어 있는 제품으로 똑같은 주문이 들어왔을 때
                if (dto.productId().equals(productDto.id())) {
                    productExists = true; // 제품이 존재함
                    // 현재 장바구니에 저장된 제품의 수량을 증가시킴
                    int totalQuantity = dto.quantity() + 1; // 수량 증가
                    updateDto = new OrderProductDto(dto.productId(), totalQuantity, dto.price()); // 새로운 객체 생성
                    orderList.add(updateDto); // 변경된 객체 추가
                } else {
                    // 기존의 제품을 그대로 추가
                    orderList.add(dto);
                }
            }

            // 만약 장바구니에 해당 제품이 없었다면 새로운 제품 추가
            if (!productExists) {
                OrderProductDto newOrderProductDto = new OrderProductDto(productDto.id(), 1, productDto.price());
                orderList.add(newOrderProductDto);
            }

            ShoppingCartDto shopDto = new ShoppingCartDto(userId, storeId, orderList);
            redisUtil.setDataShoppingCart(shopDto);
        }
    }
}
