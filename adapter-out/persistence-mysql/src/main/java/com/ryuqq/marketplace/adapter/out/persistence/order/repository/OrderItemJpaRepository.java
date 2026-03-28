package com.ryuqq.marketplace.adapter.out.persistence.order.repository;

import com.ryuqq.marketplace.adapter.out.persistence.order.entity.OrderItemJpaEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

/** OrderItem JPA Repository (save 용). */
public interface OrderItemJpaRepository extends JpaRepository<OrderItemJpaEntity, Long> {

    /**
     * 상태별 주문상품 카운트 조회.
     *
     * @return [orderItemStatus, count] 쌍의 Object 배열 목록
     */
    @Query(
            "SELECT e.orderItemStatus, COUNT(e) FROM OrderItemJpaEntity e GROUP BY"
                    + " e.orderItemStatus")
    List<Object[]> countGroupByStatus();

    /** 주문상품번호로 주문상품 조회. */
    Optional<OrderItemJpaEntity> findByOrderItemNumber(String orderItemNumber);
}
