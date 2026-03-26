package com.ryuqq.marketplace.application.categorymapping.manager;

import com.ryuqq.marketplace.application.categorymapping.port.out.query.CategoryMappingQueryPort;
import com.ryuqq.marketplace.domain.categorymapping.exception.CategoryMappingNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 카테고리 매핑 읽기 매니저. */
@Component
public class CategoryMappingReadManager {

    private final CategoryMappingQueryPort queryPort;

    public CategoryMappingReadManager(CategoryMappingQueryPort queryPort) {
        this.queryPort = queryPort;
    }

    /**
     * 판매채널 카테고리 ID 필수 조회.
     *
     * @param salesChannelId 판매채널 ID
     * @param internalCategoryId 내부 카테고리 ID
     * @return 판매채널 카테고리 ID
     * @throws CategoryMappingNotFoundException 매핑 미존재
     */
    @Transactional(readOnly = true)
    public Long getSalesChannelCategoryId(Long salesChannelId, Long internalCategoryId) {
        return queryPort
                .findSalesChannelCategoryId(salesChannelId, internalCategoryId)
                .orElseThrow(
                        () ->
                                new CategoryMappingNotFoundException(
                                        salesChannelId, internalCategoryId));
    }

    /**
     * 외부 카테고리 코드 필수 조회.
     *
     * @param salesChannelId 판매채널 ID
     * @param internalCategoryId 내부 카테고리 ID
     * @return 외부 카테고리 코드
     * @throws CategoryMappingNotFoundException 매핑 미존재
     */
    @Transactional(readOnly = true)
    public String getExternalCategoryCode(Long salesChannelId, Long internalCategoryId) {
        return queryPort
                .findExternalCategoryCode(salesChannelId, internalCategoryId)
                .orElseThrow(
                        () ->
                                new CategoryMappingNotFoundException(
                                        salesChannelId, internalCategoryId));
    }
}
