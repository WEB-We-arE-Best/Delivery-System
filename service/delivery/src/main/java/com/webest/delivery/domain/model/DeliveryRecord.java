package com.webest.delivery.domain.model;


import com.webest.app.jpa.BaseEntity;
import com.webest.delivery.application.service.DeliveryService;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "p_delivery_record")
public class DeliveryRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long deliveryId;

    private String riderId;

    private Long orderId;

    @Enumerated(EnumType.STRING)  // Enum을 문자열로 DB에 저장
    private DeliveryStatus deliveryStatus;

    private Double deliveryFeeAmount;

    public static DeliveryRecord create(Long deliveryId,
                                        String riderId,
                                  Long orderId,
                                  DeliveryStatus deliveryStatus,
                                  Double deliveryFeeAmount)
    {

        DeliveryRecord deliveryRecord = new DeliveryRecord();
        deliveryRecord.deliveryId = deliveryId;
        deliveryRecord.riderId = riderId;
        deliveryRecord.orderId = orderId;
        deliveryRecord.deliveryStatus = deliveryStatus;
        deliveryRecord.deliveryFeeAmount = deliveryFeeAmount;
        return deliveryRecord;
    }

    public void update(Long deliveryId,
                       String riderId,
                       Long orderId,
                       DeliveryStatus deliveryStatus,
                       Double deliveryFeeAmount) {
        this.deliveryId = deliveryId;
        this.riderId = riderId;
        this.orderId = orderId;
        this.deliveryStatus = deliveryStatus;
        this.deliveryFeeAmount = deliveryFeeAmount;
    }

    public void delete() {
        this.isDeleted = true;
    }


}
