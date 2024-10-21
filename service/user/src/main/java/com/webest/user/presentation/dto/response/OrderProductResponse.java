package com.webest.user.presentation.dto.response;

import com.webest.user.domain.model.vo.OrderProductDto;

import java.util.Set;

public record OrderProductResponse(
    Long storeId,
    Set<OrderProductDto> orderProductRequests
    ){

}
