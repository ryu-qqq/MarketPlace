package com.ryuqq.marketplace.application.selleroption.internal;

import com.ryuqq.marketplace.application.selleroption.manager.SellerOptionGroupCommandManager;
import com.ryuqq.marketplace.application.selleroption.manager.SellerOptionValueCommandManager;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionGroup;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionValue;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionValueId;
import com.ryuqq.marketplace.domain.productgroup.vo.SellerOptionGroupDiff;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * SellerOption Persist Facade.
 *
 * <p>SellerOptionGroup + SellerOptionValue 저장을 조율합니다.
 */
@Component
public class SellerOptionPersistFacade {

    private final SellerOptionGroupCommandManager groupCommandManager;
    private final SellerOptionValueCommandManager valueCommandManager;

    public SellerOptionPersistFacade(
            SellerOptionGroupCommandManager groupCommandManager,
            SellerOptionValueCommandManager valueCommandManager) {
        this.groupCommandManager = groupCommandManager;
        this.valueCommandManager = valueCommandManager;
    }

    /**
     * OptionGroup + OptionValue 저장 후 모든 OptionValueId 반환.
     *
     * @param optionGroups 저장할 옵션 그룹 목록
     * @return 생성된 SellerOptionValueId 목록
     */
    @Transactional
    public List<SellerOptionValueId> persistAll(List<SellerOptionGroup> optionGroups) {
        return optionGroups.stream()
                .flatMap(
                        group -> {
                            Long groupId = groupCommandManager.persist(group);
                            return valueCommandManager
                                    .persistAllForGroup(groupId, group.optionValues())
                                    .stream();
                        })
                .map(SellerOptionValueId::of)
                .toList();
    }

    /**
     * Diff 기반 OptionGroup + OptionValue 저장 후 resolved 활성 ValueId 반환.
     *
     * <p>카테고리별 배치 연산으로 처리합니다:
     *
     * <ol>
     *   <li>모든 그룹 일괄 persist (removed soft delete + added insert + retained dirty check)
     *   <li>삭제 대상 값 일괄 persist (soft delete dirty check)
     *   <li>신규 값 일괄 persist + 생성 ID 추적
     *   <li>유지 값 일괄 persist (속성 변경 dirty check)
     * </ol>
     *
     * <p>{@link IdentityHashMap}을 사용하여 동일 null 값을 가진 서로 다른 인스턴스를 객체 identity로 구분합니다.
     *
     * @param diff SellerOptionGroup 변경 비교 결과
     * @return persist 후 resolved된 활성 SellerOptionValueId 목록
     */
    @Transactional
    public List<SellerOptionValueId> persistDiff(SellerOptionGroupDiff diff) {
        // 1. 모든 그룹 일괄 persist (saveAll)
        groupCommandManager.persistAll(diff.allGroups());

        // 2. 삭제 대상 값 일괄 persist (soft delete dirty check)
        valueCommandManager.persistAll(diff.allRemovedValues());

        // 3. 신규 값 일괄 persist + 생성 ID 추적
        List<SellerOptionValue> addedValues = diff.allAddedValues();
        List<Long> addedIds = valueCommandManager.persistAll(addedValues);
        Map<SellerOptionValueId, Long> generatedIdMap = buildGeneratedIdMap(addedValues, addedIds);

        // 4. 유지 값 일괄 persist (속성 변경 dirty check)
        valueCommandManager.persistAll(diff.allRetainedValues());

        // 5. orderedActiveValueIds의 null ID를 실제 생성 ID로 치환
        return resolveActiveValueIds(diff.orderedActiveValueIds(), generatedIdMap);
    }

    private Map<SellerOptionValueId, Long> buildGeneratedIdMap(
            List<SellerOptionValue> values, List<Long> generatedIds) {
        Map<SellerOptionValueId, Long> map = new IdentityHashMap<>();
        for (int i = 0; i < values.size(); i++) {
            map.put(values.get(i).id(), generatedIds.get(i));
        }
        return map;
    }

    private List<SellerOptionValueId> resolveActiveValueIds(
            List<SellerOptionValueId> orderedIds, Map<SellerOptionValueId, Long> generatedIdMap) {
        return orderedIds.stream()
                .map(
                        id -> {
                            if (!id.isNew()) {
                                return id;
                            }
                            Long generatedId = generatedIdMap.get(id);
                            if (generatedId == null) {
                                throw new IllegalStateException(
                                        "신규 SellerOptionValueId에 대한 생성된 ID를 찾을 수 없습니다");
                            }
                            return SellerOptionValueId.of(generatedId);
                        })
                .toList();
    }
}
