package com.ryuqq.marketplace.application.selleradmin.port.in.query;

import java.util.List;
import java.util.Optional;

/**
 * sellerAdminId로 sellerId를 조회하는 UseCase.
 *
 * <p>셀러 관리자 접근 제어에서 대상 관리자의 소속 셀러를 확인할 때 사용합니다.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
public interface ResolveSellerIdBySellerAdminIdUseCase {

    /**
     * 단건 조회: sellerAdminId로 소속 sellerId를 반환합니다.
     *
     * @param sellerAdminId 셀러 관리자 ID
     * @return 소속 sellerId (Optional)
     */
    Optional<Long> execute(String sellerAdminId);

    /**
     * 일괄 조회: sellerAdminId 목록이 모두 같은 셀러 소속인지 확인하고, 해당 sellerId를 반환합니다.
     *
     * @param sellerAdminIds 셀러 관리자 ID 목록
     * @return 모두 같은 셀러 소속이면 해당 sellerId, 아니면 empty
     */
    Optional<Long> resolveIfAllSameSeller(List<String> sellerAdminIds);
}
