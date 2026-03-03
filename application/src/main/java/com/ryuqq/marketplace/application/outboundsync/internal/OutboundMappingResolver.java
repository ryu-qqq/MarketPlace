package com.ryuqq.marketplace.application.outboundsync.internal;

import com.ryuqq.marketplace.application.outboundsync.port.out.query.OutboundMappingQueryPort;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * 아웃바운드 매핑 리졸버.
 *
 * <p>내부 카테고리/브랜드 ID → 외부 판매채널 ID 변환을 수행합니다. 카테고리는 필수(예외 발생), 브랜드는 선택(Optional 반환).
 */
@Component
public class OutboundMappingResolver {

    private final OutboundMappingQueryPort mappingQueryPort;

    public OutboundMappingResolver(OutboundMappingQueryPort mappingQueryPort) {
        this.mappingQueryPort = mappingQueryPort;
    }

    /**
     * 판매채널 카테고리 ID 필수 조회.
     *
     * @param salesChannelId 판매채널 ID
     * @param internalCategoryId 내부 카테고리 ID
     * @return 판매채널 카테고리 ID
     * @throws IllegalStateException 매핑 미존재
     */
    public Long resolveSalesChannelCategoryId(Long salesChannelId, Long internalCategoryId) {
        return mappingQueryPort
                .findSalesChannelCategoryId(salesChannelId, internalCategoryId)
                .orElseThrow(
                        () ->
                                new IllegalStateException(
                                        "카테고리 매핑 미존재: salesChannelId="
                                                + salesChannelId
                                                + ", internalCategoryId="
                                                + internalCategoryId));
    }

    /**
     * 판매채널 브랜드 ID 선택 조회.
     *
     * @param salesChannelId 판매채널 ID
     * @param internalBrandId 내부 브랜드 ID
     * @return 판매채널 브랜드 ID (매핑 없으면 empty)
     */
    public Optional<Long> findSalesChannelBrandId(Long salesChannelId, Long internalBrandId) {
        return mappingQueryPort.findSalesChannelBrandId(salesChannelId, internalBrandId);
    }
}
