package com.ryuqq.marketplace.application.canonicaloption.port.in.query;

import com.ryuqq.marketplace.application.canonicaloption.dto.query.CanonicalOptionGroupSearchParams;
import com.ryuqq.marketplace.application.canonicaloption.dto.response.CanonicalOptionGroupPageResult;

/** 캐노니컬 옵션 그룹 목록 조회 UseCase (Offset 기반 페이징). */
public interface SearchCanonicalOptionGroupByOffsetUseCase {
    CanonicalOptionGroupPageResult execute(CanonicalOptionGroupSearchParams params);
}
