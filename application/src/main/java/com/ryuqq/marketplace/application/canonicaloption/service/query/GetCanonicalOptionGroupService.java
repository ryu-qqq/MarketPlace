package com.ryuqq.marketplace.application.canonicaloption.service.query;

import com.ryuqq.marketplace.application.canonicaloption.assembler.CanonicalOptionGroupAssembler;
import com.ryuqq.marketplace.application.canonicaloption.dto.response.CanonicalOptionGroupResult;
import com.ryuqq.marketplace.application.canonicaloption.manager.CanonicalOptionGroupReadManager;
import com.ryuqq.marketplace.application.canonicaloption.port.in.query.GetCanonicalOptionGroupUseCase;
import com.ryuqq.marketplace.domain.canonicaloption.aggregate.CanonicalOptionGroup;
import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionGroupId;
import org.springframework.stereotype.Service;

/** 캐노니컬 옵션 그룹 단건 조회 Service (ID 기반). */
@Service
public class GetCanonicalOptionGroupService implements GetCanonicalOptionGroupUseCase {

    private final CanonicalOptionGroupReadManager readManager;
    private final CanonicalOptionGroupAssembler assembler;

    public GetCanonicalOptionGroupService(
            CanonicalOptionGroupReadManager readManager, CanonicalOptionGroupAssembler assembler) {
        this.readManager = readManager;
        this.assembler = assembler;
    }

    @Override
    public CanonicalOptionGroupResult execute(Long canonicalOptionGroupId) {
        CanonicalOptionGroup group =
                readManager.getById(CanonicalOptionGroupId.of(canonicalOptionGroupId));
        return assembler.toResult(group);
    }
}
