package com.ryuqq.marketplace.application.outboundsync.internal;

import com.ryuqq.marketplace.application.brandmapping.manager.BrandMappingReadManager;
import com.ryuqq.marketplace.application.categorymapping.manager.CategoryMappingReadManager;
import com.ryuqq.marketplace.application.outboundsync.dto.vo.SalesChannelMappingResult;
import org.springframework.stereotype.Component;

/**
 * 아웃바운드 매핑 리졸버.
 *
 * <p>내부 카테고리/브랜드 ID → 외부 판매채널 ID 변환을 수행합니다. 매핑 미존재 시 도메인 예외를 던집니다.
 */
@Component
public class OutboundMappingResolver {

    private final CategoryMappingReadManager categoryMappingReadManager;
    private final BrandMappingReadManager brandMappingReadManager;

    public OutboundMappingResolver(
            CategoryMappingReadManager categoryMappingReadManager,
            BrandMappingReadManager brandMappingReadManager) {
        this.categoryMappingReadManager = categoryMappingReadManager;
        this.brandMappingReadManager = brandMappingReadManager;
    }

    /**
     * 판매채널 카테고리/브랜드 매핑 조회.
     *
     * @param salesChannelId 판매채널 ID
     * @param internalCategoryId 내부 카테고리 ID
     * @param internalBrandId 내부 브랜드 ID
     * @return 판매채널 매핑 결과
     * @throws
     *     com.ryuqq.marketplace.domain.categorymapping.exception.CategoryMappingNotFoundException
     *     카테고리 매핑 미존재
     * @throws com.ryuqq.marketplace.domain.brandmapping.exception.BrandMappingNotFoundException 브랜드
     *     매핑 미존재
     */
    public SalesChannelMappingResult resolve(
            Long salesChannelId, Long internalCategoryId, Long internalBrandId) {
        Long categoryId =
                categoryMappingReadManager.getSalesChannelCategoryId(
                        salesChannelId, internalCategoryId);
        String externalCategoryCode =
                categoryMappingReadManager.getExternalCategoryCode(
                        salesChannelId, internalCategoryId);
        Long brandId =
                brandMappingReadManager.getSalesChannelBrandId(salesChannelId, internalBrandId);
        String externalBrandCode =
                brandMappingReadManager.getExternalBrandCode(salesChannelId, internalBrandId);
        return new SalesChannelMappingResult(
                categoryId, brandId, externalCategoryCode, externalBrandCode);
    }
}
