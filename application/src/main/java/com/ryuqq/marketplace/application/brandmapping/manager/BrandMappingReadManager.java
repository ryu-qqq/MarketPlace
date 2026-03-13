package com.ryuqq.marketplace.application.brandmapping.manager;

import com.ryuqq.marketplace.application.brandmapping.port.out.query.BrandMappingQueryPort;
import com.ryuqq.marketplace.domain.brandmapping.exception.BrandMappingNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 브랜드 매핑 읽기 매니저. */
@Component
public class BrandMappingReadManager {

    private final BrandMappingQueryPort queryPort;

    public BrandMappingReadManager(BrandMappingQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    /**
     * 판매채널 브랜드 ID 필수 조회.
     *
     * @param salesChannelId 판매채널 ID
     * @param internalBrandId 내부 브랜드 ID
     * @return 판매채널 브랜드 ID
     * @throws BrandMappingNotFoundException 매핑 미존재
     */
    @Transactional(readOnly = true)
    public Long getSalesChannelBrandId(Long salesChannelId, Long internalBrandId) {
        return queryPort
                .findSalesChannelBrandId(salesChannelId, internalBrandId)
                .orElseThrow(
                        () -> new BrandMappingNotFoundException(salesChannelId, internalBrandId));
    }

    /**
     * 외부 브랜드 코드 필수 조회.
     *
     * @param salesChannelId 판매채널 ID
     * @param internalBrandId 내부 브랜드 ID
     * @return 외부 브랜드 코드
     * @throws BrandMappingNotFoundException 매핑 미존재
     */
    @Transactional(readOnly = true)
    public String getExternalBrandCode(Long salesChannelId, Long internalBrandId) {
        return queryPort
                .findExternalBrandCode(salesChannelId, internalBrandId)
                .orElseThrow(
                        () -> new BrandMappingNotFoundException(salesChannelId, internalBrandId));
    }
}
