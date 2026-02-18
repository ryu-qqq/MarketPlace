package com.ryuqq.marketplace.application.productgroup.dto.response;

import java.util.List;

/** ProductGroup 저장 결과. */
public record ProductGroupPersistResult(
        Long productGroupId,
        List<Long> imageIds,
        List<OptionGroupPersistEntry> optionGroupEntries) {

    /** OptionGroup 저장 결과 (groupId + valueIds). */
    public record OptionGroupPersistEntry(Long groupId, List<Long> valueIds) {

        public OptionGroupPersistEntry {
            valueIds = List.copyOf(valueIds);
        }
    }

    /** 모든 OptionValue ID를 그룹 순서대로 플랫하게 반환. */
    public List<Long> allOptionValueIds() {
        return optionGroupEntries.stream().flatMap(entry -> entry.valueIds().stream()).toList();
    }
}
