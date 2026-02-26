package com.ryuqq.marketplace.application.canonicaloption.port.in.query;

import com.ryuqq.marketplace.application.canonicaloption.dto.response.CanonicalOptionGroupResult;

/** 캐노니컬 옵션 그룹 단건 조회 UseCase (ID 기반). */
public interface GetCanonicalOptionGroupUseCase {
    CanonicalOptionGroupResult execute(Long canonicalOptionGroupId);
}
