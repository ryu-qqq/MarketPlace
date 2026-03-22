package com.ryuqq.marketplace.application.legacy.productcontext.resolver;

import com.ryuqq.marketplace.application.inboundproduct.internal.InboundProductMappingResolver;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 레거시 브랜드 ID → 표준 브랜드 ID 리졸버.
 *
 * <p>레거시 brandId → InboundProductMappingResolver로 internalBrandId 조회.
 * 매핑 실패 시 legacyBrandId를 그대로 반환합니다.
 */
@Component
public class LegacyBrandIdResolver {

    private static final Logger log = LoggerFactory.getLogger(LegacyBrandIdResolver.class);
    private static final long SETOF_SOURCE_ID = 2L;

    private final InboundProductMappingResolver mappingResolver;

    public LegacyBrandIdResolver(InboundProductMappingResolver mappingResolver) {
        this.mappingResolver = mappingResolver;
    }

    public long resolve(long legacyBrandId) {
        // TODO: 매핑 테이블 조회 구현 후 주석 해제
        // Optional<Long> internalBrandId =
        //         mappingResolver.resolveInternalBrandId(
        //                 SETOF_SOURCE_ID, String.valueOf(legacyBrandId));
        // if (internalBrandId.isPresent()) {
        //     return internalBrandId.get();
        // }
        // log.warn("레거시 브랜드 ID 매핑 실패. 원본 ID 사용: legacyBrandId={}", legacyBrandId);
        return legacyBrandId;
    }
}
