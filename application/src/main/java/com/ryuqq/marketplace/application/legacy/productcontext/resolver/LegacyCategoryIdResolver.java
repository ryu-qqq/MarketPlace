package com.ryuqq.marketplace.application.legacy.productcontext.resolver;

import com.ryuqq.marketplace.application.inboundproduct.internal.InboundProductMappingResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 레거시 카테고리 ID → 표준 카테고리 ID 리졸버.
 *
 * <p>레거시 categoryId → InboundProductMappingResolver로 internalCategoryId 조회. 매핑 실패 시
 * legacyCategoryId를 그대로 반환합니다.
 */
@Component
public class LegacyCategoryIdResolver {

    private static final Logger log = LoggerFactory.getLogger(LegacyCategoryIdResolver.class);
    private static final long SETOF_SOURCE_ID = 2L;

    private final InboundProductMappingResolver mappingResolver;

    public LegacyCategoryIdResolver(InboundProductMappingResolver mappingResolver) {
        this.mappingResolver = mappingResolver;
    }

    public long resolve(long legacyCategoryId) {
        return mappingResolver
                .resolveInternalCategoryId(SETOF_SOURCE_ID, String.valueOf(legacyCategoryId))
                .orElseGet(
                        () -> {
                            log.warn(
                                    "레거시 카테고리 ID 매핑 실패. 원본 ID 사용: legacyCategoryId={}",
                                    legacyCategoryId);
                            return legacyCategoryId;
                        });
    }
}
