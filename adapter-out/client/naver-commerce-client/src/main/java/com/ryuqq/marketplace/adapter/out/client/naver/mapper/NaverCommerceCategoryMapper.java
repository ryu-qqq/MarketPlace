package com.ryuqq.marketplace.adapter.out.client.naver.mapper;

import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverCommerceCategory;
import com.ryuqq.marketplace.application.saleschannelcategory.dto.response.ExternalCategoryResult;
import org.springframework.stereotype.Component;

/**
 * Naver Commerce 카테고리 매퍼.
 *
 * <p>Naver Commerce API 응답을 Application DTO로 변환합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class NaverCommerceCategoryMapper {

    /**
     * Naver Commerce 카테고리를 ExternalCategoryResult로 변환합니다.
     *
     * @param category Naver Commerce 카테고리 응답
     * @return Application 레이어 카테고리 결과
     */
    public ExternalCategoryResult toExternalCategoryResult(NaverCommerceCategory category) {
        return new ExternalCategoryResult(
                category.id(), category.name(), category.wholeCategoryName(), category.last());
    }
}
