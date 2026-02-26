package com.ryuqq.marketplace.application.legacycommoncode.port.out.query;

import com.ryuqq.marketplace.domain.legacy.commoncode.aggregate.LegacyCommonCode;
import java.util.List;

/** 세토프 DB common_code 조회 Port. */
public interface LegacyCommonCodeQueryPort {

    /**
     * 코드 그룹 ID로 공통 코드 목록을 조회합니다.
     *
     * @param codeGroupId 코드 그룹 ID
     * @return 공통 코드 목록
     */
    List<LegacyCommonCode> findByCodeGroupId(Long codeGroupId);
}
