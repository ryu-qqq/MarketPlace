package com.ryuqq.marketplace.adapter.out.client.setof.strategy;

import com.ryuqq.marketplace.adapter.out.client.setof.dto.SetofProductGroupDetailResponse;
import com.ryuqq.marketplace.application.productgroup.dto.response.ProductGroupSyncData;
import com.ryuqq.marketplace.domain.outboundsync.vo.ChangedArea;
import com.ryuqq.marketplace.domain.sellersaleschannel.aggregate.SellerSalesChannel;
import java.util.Set;

/**
 * 세토프 상품 수정 실행기 인터페이스.
 *
 * <p>변경 영역(changedAreas)에 따라 전체 수정 또는 부분 수정을 수행합니다.
 */
public interface SetofProductUpdateExecutor {

    /**
     * 이 실행기가 주어진 변경 영역을 처리할 수 있는지 확인합니다.
     *
     * @param changedAreas 변경된 영역 집합
     * @return 처리 가능 여부
     */
    boolean supports(Set<ChangedArea> changedAreas);

    /**
     * 상품 수정을 실행합니다.
     *
     * @param syncData 상품 그룹 동기화 데이터
     * @param externalCategoryId 외부 카테고리 ID
     * @param externalBrandId 외부 브랜드 ID
     * @param externalProductId 외부 상품 ID
     * @param channel 셀러 판매채널
     * @param changedAreas 변경된 영역 집합
     * @param existingProduct 기존 세토프 상품 조회 결과 (nullable, 조회 실패 시 null)
     */
    void execute(
            ProductGroupSyncData syncData,
            Long externalCategoryId,
            Long externalBrandId,
            String externalProductId,
            SellerSalesChannel channel,
            Set<ChangedArea> changedAreas,
            SetofProductGroupDetailResponse existingProduct);
}
