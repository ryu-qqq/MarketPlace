package com.ryuqq.marketplace.application.canonicaloption.assembler;

import com.ryuqq.marketplace.application.canonicaloption.dto.response.CanonicalOptionGroupPageResult;
import com.ryuqq.marketplace.application.canonicaloption.dto.response.CanonicalOptionGroupResult;
import com.ryuqq.marketplace.application.canonicaloption.dto.response.CanonicalOptionValueResult;
import com.ryuqq.marketplace.domain.canonicaloption.aggregate.CanonicalOptionGroup;
import com.ryuqq.marketplace.domain.canonicaloption.aggregate.CanonicalOptionValue;
import java.util.List;
import org.springframework.stereotype.Component;

/** 캐노니컬 옵션 그룹 Assembler. */
@Component
public class CanonicalOptionGroupAssembler {

    public CanonicalOptionGroupResult toResult(
            CanonicalOptionGroup group, List<CanonicalOptionValue> values) {
        List<CanonicalOptionValueResult> valueResults = values.stream()
                .map(this::toValueResult)
                .toList();

        return new CanonicalOptionGroupResult(
                group.idValue(),
                group.codeValue(),
                group.nameKo(),
                group.nameEn(),
                group.isActive(),
                valueResults,
                group.createdAt());
    }

    public CanonicalOptionGroupPageResult toPageResult(
            List<CanonicalOptionGroupResult> results, int page, int size, long totalElements) {
        return CanonicalOptionGroupPageResult.of(results, page, size, totalElements);
    }

    private CanonicalOptionValueResult toValueResult(CanonicalOptionValue value) {
        return new CanonicalOptionValueResult(
                value.idValue(),
                value.codeValue(),
                value.nameKo(),
                value.nameEn(),
                value.sortOrder());
    }
}
