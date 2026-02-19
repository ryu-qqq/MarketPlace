package com.ryuqq.marketplace.adapter.out.client.naver.mapper;

import com.ryuqq.marketplace.adapter.out.client.naver.dto.NaverCommerceBrand;
import com.ryuqq.marketplace.application.saleschannelbrand.dto.response.ExternalBrandResult;
import org.springframework.stereotype.Component;

/**
 * Naver Commerce 브랜드 매퍼.
 *
 * <p>Naver Commerce API 응답을 Application DTO로 변환합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class NaverCommerceBrandMapper {

    /**
     * Naver Commerce 브랜드를 ExternalBrandResult로 변환합니다.
     *
     * @param brand Naver Commerce 브랜드 응답
     * @return Application 레이어 브랜드 결과
     */
    public ExternalBrandResult toExternalBrandResult(NaverCommerceBrand brand) {
        return new ExternalBrandResult(String.valueOf(brand.id()), brand.name());
    }
}
