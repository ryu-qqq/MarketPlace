package com.ryuqq.marketplace.application.commoncodetype.port.out.query;

import com.ryuqq.marketplace.domain.commoncodetype.aggregate.CommonCodeType;
import com.ryuqq.marketplace.domain.commoncodetype.id.CommonCodeTypeId;
import com.ryuqq.marketplace.domain.commoncodetype.query.CommonCodeTypeSearchCriteria;
import java.util.List;
import java.util.Optional;

/** 공통 코드 타입 Query Port. */
public interface CommonCodeTypeQueryPort {

    Optional<CommonCodeType> findById(CommonCodeTypeId id);

    List<CommonCodeType> findByIds(List<CommonCodeTypeId> ids);

    boolean existsByCode(String code);

    /**
     * 특정 ID를 제외하고 동일한 displayOrder가 존재하는지 확인.
     *
     * @param displayOrder 확인할 표시 순서
     * @param excludeId 제외할 ID
     * @return 중복 존재 여부
     */
    boolean existsByDisplayOrderExcludingId(int displayOrder, Long excludeId);

    List<CommonCodeType> findByCriteria(CommonCodeTypeSearchCriteria criteria);

    long countByCriteria(CommonCodeTypeSearchCriteria criteria);
}
