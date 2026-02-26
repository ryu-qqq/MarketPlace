package com.ryuqq.marketplace.application.commoncode.port.out.query;

import com.ryuqq.marketplace.domain.commoncode.aggregate.CommonCode;
import com.ryuqq.marketplace.domain.commoncode.id.CommonCodeId;
import com.ryuqq.marketplace.domain.commoncode.query.CommonCodeSearchCriteria;
import java.util.List;
import java.util.Optional;

/**
 * CommonCodeQueryPort - 공통 코드 Query Port.
 *
 * <p>APP-PRT-002: Query Port는 조회 메서드만 정의.
 *
 * <p>APP-PRT-003: Port는 Domain 객체만 반환 (DTO 금지).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
public interface CommonCodeQueryPort {

    /**
     * ID로 공통 코드 조회.
     *
     * @param id 공통 코드 ID
     * @return 공통 코드 Optional
     */
    Optional<CommonCode> findById(CommonCodeId id);

    /**
     * ID 목록으로 공통 코드 목록 조회.
     *
     * @param ids 공통 코드 ID 목록
     * @return 공통 코드 목록
     */
    List<CommonCode> findByIds(List<CommonCodeId> ids);

    /**
     * 타입 ID + 코드 조합 존재 여부 확인.
     *
     * @param commonCodeTypeId 공통 코드 타입 ID
     * @param code 코드값
     * @return 존재하면 true
     */
    boolean existsByCommonCodeTypeIdAndCode(Long commonCodeTypeId, String code);

    /**
     * 특정 타입 ID의 활성화된 CommonCode가 존재하는지 확인.
     *
     * @param commonCodeTypeId 공통 코드 타입 ID
     * @return 활성화된 CommonCode 존재 여부
     */
    boolean existsActiveByCommonCodeTypeId(Long commonCodeTypeId);

    /**
     * 검색 조건으로 공통 코드 목록 조회.
     *
     * @param criteria 검색 조건
     * @return 공통 코드 목록
     */
    List<CommonCode> findByCriteria(CommonCodeSearchCriteria criteria);

    /**
     * 검색 조건으로 공통 코드 개수 조회.
     *
     * @param criteria 검색 조건
     * @return 공통 코드 개수
     */
    long countByCriteria(CommonCodeSearchCriteria criteria);
}
