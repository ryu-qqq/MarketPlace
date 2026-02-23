package com.ryuqq.marketplace.application.productintelligence.internal.changedetector;

import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupReadManager;
import com.ryuqq.marketplace.application.productintelligence.port.out.query.OptionChangeDetector;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionGroup;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionValue;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productintelligence.vo.OptionMappingSuggestion;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * Option 변경 감지 구현체.
 *
 * <p>DB에서 현재 셀러 옵션 데이터를 조회하여 이전 분석 결과와 필드 단위로 비교합니다. 옵션 값 ID, 이름, 그룹 ID 변경을 감지합니다.
 */
@Component
public class OptionChangeDetectorImpl implements OptionChangeDetector {

    private final ProductGroupReadManager productGroupReadManager;

    public OptionChangeDetectorImpl(ProductGroupReadManager productGroupReadManager) {
        this.productGroupReadManager = productGroupReadManager;
    }

    @Override
    public boolean hasChanged(Long productGroupId, List<OptionMappingSuggestion> previousResults) {
        if (previousResults.isEmpty()) {
            return true;
        }

        ProductGroup productGroup =
                productGroupReadManager.getById(ProductGroupId.of(productGroupId));

        List<SellerOptionGroup> activeGroups =
                productGroup.sellerOptionGroups().stream().filter(g -> !g.isDeleted()).toList();

        if (activeGroups.isEmpty()) {
            return false;
        }

        List<SellerOptionValue> activeValues =
                activeGroups.stream()
                        .flatMap(g -> g.optionValues().stream())
                        .filter(v -> !v.isDeleted())
                        .toList();

        // 1. 옵션 값 ID Set 비교 (추가/삭제 감지)
        Set<Long> previousValueIds =
                previousResults.stream()
                        .map(OptionMappingSuggestion::sellerOptionValueId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
        Set<Long> currentValueIds =
                activeValues.stream().map(SellerOptionValue::idValue).collect(Collectors.toSet());
        if (!previousValueIds.equals(currentValueIds)) {
            return true;
        }

        // 2. 옵션 값 이름 비교 (이름 변경 감지)
        Map<Long, String> previousNameMap =
                previousResults.stream()
                        .filter(s -> s.sellerOptionValueId() != null)
                        .collect(
                                Collectors.toMap(
                                        OptionMappingSuggestion::sellerOptionValueId,
                                        s ->
                                                s.sellerOptionName() != null
                                                        ? s.sellerOptionName()
                                                        : "",
                                        (a, b) -> a));
        for (SellerOptionValue value : activeValues) {
            String prevName = previousNameMap.get(value.idValue());
            if (prevName == null || !prevName.equals(value.optionValueNameValue())) {
                return true;
            }
        }

        // 3. 옵션 그룹 ID Set 비교 (그룹 추가/삭제 감지)
        Set<Long> previousGroupIds =
                previousResults.stream()
                        .map(OptionMappingSuggestion::sellerOptionGroupId)
                        .filter(Objects::nonNull)
                        .collect(Collectors.toSet());
        Set<Long> currentGroupIds =
                activeGroups.stream().map(SellerOptionGroup::idValue).collect(Collectors.toSet());
        if (!previousGroupIds.equals(currentGroupIds)) {
            return true;
        }

        return false;
    }
}
