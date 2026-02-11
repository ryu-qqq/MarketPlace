package com.ryuqq.marketplace.application.canonicaloption.internal;

import com.ryuqq.marketplace.application.canonicaloption.assembler.CanonicalOptionGroupAssembler;
import com.ryuqq.marketplace.application.canonicaloption.dto.response.CanonicalOptionGroupResult;
import com.ryuqq.marketplace.application.canonicaloption.manager.CanonicalOptionGroupReadManager;
import com.ryuqq.marketplace.application.canonicaloption.manager.CanonicalOptionValueReadManager;
import com.ryuqq.marketplace.domain.canonicaloption.aggregate.CanonicalOptionGroup;
import com.ryuqq.marketplace.domain.canonicaloption.aggregate.CanonicalOptionValue;
import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionGroupId;
import com.ryuqq.marketplace.domain.canonicaloption.query.CanonicalOptionGroupSearchCriteria;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/** 캐노니컬 옵션 그룹 + 값 조합 Read Facade. */
@Component
public class CanonicalOptionGroupReadFacade {

    private final CanonicalOptionGroupReadManager groupReadManager;
    private final CanonicalOptionValueReadManager valueReadManager;
    private final CanonicalOptionGroupAssembler assembler;

    public CanonicalOptionGroupReadFacade(
            CanonicalOptionGroupReadManager groupReadManager,
            CanonicalOptionValueReadManager valueReadManager,
            CanonicalOptionGroupAssembler assembler) {
        this.groupReadManager = groupReadManager;
        this.valueReadManager = valueReadManager;
        this.assembler = assembler;
    }

    @Transactional(readOnly = true)
    public CanonicalOptionGroupResult getById(Long canonicalOptionGroupId) {
        CanonicalOptionGroup group =
                groupReadManager.getById(CanonicalOptionGroupId.of(canonicalOptionGroupId));
        List<CanonicalOptionValue> values =
                valueReadManager.getByCanonicalOptionGroupId(group.idValue());
        return assembler.toResult(group, values);
    }

    @Transactional(readOnly = true)
    public List<CanonicalOptionGroupResult> findByCriteria(
            CanonicalOptionGroupSearchCriteria criteria) {
        List<CanonicalOptionGroup> groups = groupReadManager.findByCriteria(criteria);
        if (groups.isEmpty()) {
            return List.of();
        }

        List<Long> groupIds = groups.stream()
                .map(CanonicalOptionGroup::idValue)
                .toList();

        Map<Long, List<CanonicalOptionValue>> valuesMap =
                valueReadManager.getGroupedByCanonicalOptionGroupIds(groupIds);

        return groups.stream()
                .map(group -> assembler.toResult(
                        group,
                        valuesMap.getOrDefault(group.idValue(), List.of())))
                .toList();
    }

    @Transactional(readOnly = true)
    public long countByCriteria(CanonicalOptionGroupSearchCriteria criteria) {
        return groupReadManager.countByCriteria(criteria);
    }
}
