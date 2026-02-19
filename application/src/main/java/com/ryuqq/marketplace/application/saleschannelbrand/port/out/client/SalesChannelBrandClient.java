package com.ryuqq.marketplace.application.saleschannelbrand.port.out.client;

import com.ryuqq.marketplace.application.saleschannelbrand.dto.response.ExternalBrandResult;
import java.util.List;

/**
 * 외부 판매채널 브랜드 조회 클라이언트 인터페이스.
 *
 * <p>외부 판매채널 API를 통해 브랜드 정보를 조회합니다.
 *
 * <p>구현체는 adapter-out 레이어에서 실제 API 호출을 담당합니다.
 */
public interface SalesChannelBrandClient {

    /**
     * 브랜드를 이름으로 검색합니다.
     *
     * @param name 검색할 브랜드 이름
     * @return 검색된 브랜드 목록
     */
    List<ExternalBrandResult> searchBrands(String name);
}
