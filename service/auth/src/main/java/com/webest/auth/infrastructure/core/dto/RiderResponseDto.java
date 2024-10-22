package com.webest.auth.infrastructure.core.dto;

import com.webest.auth.domain.model.RiderTransportation;
import java.time.LocalDateTime;
import java.util.List;

public record RiderResponseDto(
    Long riderId,
    String userId,
    List<Long> addressCodeList,
    RiderTransportation transportation,
    LocalDateTime createdAt
) {


}