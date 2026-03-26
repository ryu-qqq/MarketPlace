package com.ryuqq.marketplace.application.shadow.port.out;

import com.ryuqq.marketplace.application.shadow.dto.ShadowSnapshot;
import java.util.Optional;

/**
 * Shadow 스냅샷 저장소 포트.
 *
 * <p>Shadow 트랜잭션에서 캡처한 응답 스냅샷을 저장하고, Python Shadow Lambda가 나중에 조회합니다.
 */
public interface ShadowSnapshotStore {

    void save(ShadowSnapshot snapshot);

    Optional<ShadowSnapshot> findByCorrelationId(String correlationId);
}
