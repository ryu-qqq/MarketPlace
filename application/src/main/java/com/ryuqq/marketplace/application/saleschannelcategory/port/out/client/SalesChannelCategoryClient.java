package com.ryuqq.marketplace.application.saleschannelcategory.port.out.client;

import com.ryuqq.marketplace.application.saleschannelcategory.dto.response.ExternalCategoryResult;
import java.util.List;

/**
 * 외부 판매채널 카테고리 조회 클라이언트 인터페이스.
 *
 * <p>외부 판매채널 API를 통해 카테고리 정보를 조회합니다.
 *
 * <p>구현체는 adapter-out 레이어에서 실제 API 호출을 담당합니다.
 */
public interface SalesChannelCategoryClient {

    /**
     * 전체 카테고리를 조회합니다.
     *
     * @return 전체 카테고리 목록
     */
    List<ExternalCategoryResult> fetchAllCategories();
}
