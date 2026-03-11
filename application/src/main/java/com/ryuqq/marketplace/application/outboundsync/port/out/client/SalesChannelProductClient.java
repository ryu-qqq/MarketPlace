package com.ryuqq.marketplace.application.outboundsync.port.out.client;

import com.ryuqq.marketplace.application.productgroup.dto.composite.ProductGroupDetailBundle;
import com.ryuqq.marketplace.domain.outboundsync.vo.ChangedArea;
import com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel;
import java.util.Set;

/**
 * 판매채널 상품 등록/수정 클라이언트 포트.
 *
 * <p>Strategy가 외부 채널 DTO를 직접 알지 않도록 추상화합니다. 채널별 Adapter에서 내부 데이터 → 외부 API DTO 변환 + API 호출을 수행합니다.
 */
public interface SalesChannelProductClient {

    /** 이 클라이언트가 담당하는 판매채널 코드. */
    String channelCode();

    /**
     * 외부 채널에 상품을 등록합니다.
     *
     * @param bundle 상품 그룹 상세 번들
     * @param externalCategoryId 외부 채널 카테고리 ID
     * @param externalBrandId 외부 채널 브랜드 ID (nullable, 선택 필드)
     * @param channel 셀러 판매채널 (인증 정보 포함)
     * @return 외부 상품 ID (String)
     */
    String registerProduct(
            ProductGroupDetailBundle bundle,
            Long externalCategoryId,
            Long externalBrandId,
            SellerSalesChannel channel);

    /**
     * 외부 채널의 상품을 수정합니다.
     *
     * <p>changedAreas가 비어있으면 전체 수정(Full Replacement)으로 동작합니다. 채널 어댑터는 changedAreas 기반으로 전체 수정 또는 부분
     * 수정을 내부적으로 결정합니다.
     *
     * @param bundle 상품 그룹 상세 번들 (최신 데이터)
     * @param externalCategoryId 외부 채널 카테고리 ID
     * @param externalBrandId 외부 채널 브랜드 ID (nullable, 선택 필드)
     * @param externalProductId 외부 상품 ID
     * @param channel 셀러 판매채널 (인증 정보 포함)
     * @param changedAreas 변경된 영역 집합 (비어있으면 전체 수정)
     */
    void updateProduct(
            ProductGroupDetailBundle bundle,
            Long externalCategoryId,
            Long externalBrandId,
            String externalProductId,
            SellerSalesChannel channel,
            Set<ChangedArea> changedAreas);

    /**
     * 외부 채널의 상품을 삭제합니다.
     *
     * @param externalProductId 외부 상품 ID
     * @param channel 셀러 판매채널 (인증 정보 포함)
     */
    void deleteProduct(String externalProductId, SellerSalesChannel channel);
}
