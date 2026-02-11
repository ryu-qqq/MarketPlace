package com.ryuqq.marketplace.application.canonicaloption.service.query;

import com.ryuqq.marketplace.application.canonicaloption.dto.response.CanonicalOptionGroupResult;
import com.ryuqq.marketplace.application.canonicaloption.internal.CanonicalOptionGroupReadFacade;
import com.ryuqq.marketplace.application.canonicaloption.port.in.query.GetCanonicalOptionGroupUseCase;
import org.springframework.stereotype.Service;

/** 캐노니컬 옵션 그룹 단건 조회 Service (ID 기반). */
@Service
public class GetCanonicalOptionGroupService implements GetCanonicalOptionGroupUseCase {

    private final CanonicalOptionGroupReadFacade readFacade;

    public GetCanonicalOptionGroupService(CanonicalOptionGroupReadFacade readFacade) {
        this.readFacade = readFacade;
    }

    @Override
    public CanonicalOptionGroupResult execute(Long canonicalOptionGroupId) {
        return readFacade.getById(canonicalOptionGroupId);
    }
}
