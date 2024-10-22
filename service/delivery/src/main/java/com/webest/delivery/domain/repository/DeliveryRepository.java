package com.webest.delivery.domain.repository;

import com.webest.delivery.domain.model.Delivery;
import com.webest.delivery.domain.model.DeliveryStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DeliveryRepository extends JpaRepository<Delivery, Long>, DeliveryCustomRepository {
    Optional<Delivery> findByOrderId(Long orderId);
    List<Delivery> findByDeliveryStatusAndCreatedAtBefore(DeliveryStatus status, LocalDateTime time);

}

