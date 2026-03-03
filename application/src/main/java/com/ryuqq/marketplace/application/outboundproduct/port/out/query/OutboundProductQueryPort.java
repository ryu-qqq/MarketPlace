package com.ryuqq.marketplace.application.outboundproduct.port.out.query;

import com.ryuqq.marketplace.domain.outboundproduct.aggregate.OutboundProduct;
import java.util.List;
import java.util.Optional;

public interface OutboundProductQueryPort {
    boolean existsByProductGroupIdAndSalesChannelId(Long productGroupId, Long salesChannelId);

    Optional<OutboundProduct> findByProductGroupIdAndSalesChannelId(
            Long productGroupId, Long salesChannelId);

    /**
     * REGISTERED 상태의 OutboundProduct를 상품그룹 ID로 조회.
     *
     * @param productGroupId 상품그룹 ID
     * @return REGISTERED 상태의 OutboundProduct 목록
     */
    List<OutboundProduct> findRegisteredByProductGroupId(Long productGroupId);

    /**
     * 외부 상품 ID + 판매채널 ID로 역조회.
     *
     * @param externalProductId 외부 상품 ID
     * @param salesChannelId 판매채널 ID
     * @return 매핑된 OutboundProduct (없으면 empty)
     */
    Optional<OutboundProduct> findByExternalProductIdAndSalesChannelId(
            String externalProductId, long salesChannelId);

    /**
     * DEREGISTERED 상태의 OutboundProduct를 상품그룹 ID로 조회.
     *
     * @param productGroupId 상품그룹 ID
     * @return DEREGISTERED 상태의 OutboundProduct 목록
     */
    List<OutboundProduct> findDeregisteredByProductGroupId(Long productGroupId);
}
