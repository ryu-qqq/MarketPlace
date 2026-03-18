package com.ryuqq.marketplace.application.legacyconversion.manager;

import com.ryuqq.marketplace.application.legacyconversion.dto.result.LegacyOrderCompositeResult;
import com.ryuqq.marketplace.application.legacyconversion.port.out.query.LegacyOrderCompositeQueryPort;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 레거시 주문 복합 조회 Manager. */
@Component
@Transactional(readOnly = true)
public class LegacyOrderCompositeReadManager {

    private final LegacyOrderCompositeQueryPort queryPort;

    public LegacyOrderCompositeReadManager(LegacyOrderCompositeQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    /**
     * 레거시 주문 ID로 복합 주문 정보를 조회합니다.
     *
     * <p>조회 결과가 없으면 IllegalStateException을 던집니다.
     *
     * @param legacyOrderId 레거시 주문 ID
     * @return 주문 복합 결과
     * @throws IllegalStateException 해당 주문이 존재하지 않는 경우
     */
    public LegacyOrderCompositeResult fetchOrderComposite(long legacyOrderId) {
        return queryPort.fetchOrderComposite(legacyOrderId)
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "레거시 주문 복합 정보를 찾을 수 없습니다. legacyOrderId=" + legacyOrderId));
    }
}
