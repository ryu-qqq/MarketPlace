package com.ryuqq.marketplace.domain.productgroup.vo;

import static org.assertj.core.api.Assertions.*;

import com.ryuqq.marketplace.domain.common.vo.DeletionStatus;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionGroup;
import com.ryuqq.marketplace.domain.productgroup.aggregate.SellerOptionValue;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionGroupId;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionValueId;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SellerOptionGroups update() ID 기반 diff 로직 단위 테스트")
class SellerOptionGroupsUpdateTest {

    private static final ProductGroupId PRODUCT_GROUP_ID = ProductGroupId.of(1L);
    private static final Instant UPDATED_AT = Instant.parse("2026-02-16T00:00:00Z");

    // === 헬퍼 메서드: 기존 그룹/값 (reconstitute, 고정 ID) ===

    private static SellerOptionValue existingValue(
            long valueId, long groupId, String name, int sortOrder) {
        return SellerOptionValue.reconstitute(
                SellerOptionValueId.of(valueId),
                SellerOptionGroupId.of(groupId),
                OptionValueName.of(name),
                null,
                sortOrder,
                DeletionStatus.active());
    }

    private static SellerOptionGroup existingGroup(
            long groupId, String name, int sortOrder, List<SellerOptionValue> values) {
        return SellerOptionGroup.reconstitute(
                SellerOptionGroupId.of(groupId),
                PRODUCT_GROUP_ID,
                OptionGroupName.of(name),
                null,
                OptionInputType.PREDEFINED,
                sortOrder,
                values,
                DeletionStatus.active());
    }

    // === 헬퍼 메서드: entry 생성 ===

    private static SellerOptionGroupUpdateData.GroupEntry retainedGroupEntry(
            long groupId,
            String name,
            int sortOrder,
            List<SellerOptionGroupUpdateData.ValueEntry> values) {
        return new SellerOptionGroupUpdateData.GroupEntry(
                groupId, name, null, OptionInputType.PREDEFINED, sortOrder, values);
    }

    private static SellerOptionGroupUpdateData.GroupEntry newGroupEntry(
            String name, int sortOrder, List<SellerOptionGroupUpdateData.ValueEntry> values) {
        return new SellerOptionGroupUpdateData.GroupEntry(
                null, name, null, OptionInputType.PREDEFINED, sortOrder, values);
    }

    private static SellerOptionGroupUpdateData.ValueEntry retainedValueEntry(
            long valueId, String name, int sortOrder) {
        return new SellerOptionGroupUpdateData.ValueEntry(valueId, name, null, sortOrder);
    }

    private static SellerOptionGroupUpdateData.ValueEntry newValueEntry(
            String name, int sortOrder) {
        return new SellerOptionGroupUpdateData.ValueEntry(null, name, null, sortOrder);
    }

    // === 헬퍼 메서드: update 실행 ===

    private static SellerOptionGroupDiff executeUpdate(
            List<SellerOptionGroup> existingGroups,
            List<SellerOptionGroupUpdateData.GroupEntry> entries) {
        SellerOptionGroups existing = SellerOptionGroups.of(existingGroups);
        SellerOptionGroupUpdateData updateData =
                SellerOptionGroupUpdateData.of(PRODUCT_GROUP_ID, entries, UPDATED_AT);
        return existing.update(updateData);
    }

    @Nested
    @DisplayName("모든 그룹과 값이 ID로 유지되는 경우")
    class AllRetainedTest {

        @Test
        @DisplayName("기존 ID로 매칭하면 hasNoChanges()가 true를 반환한다")
        void sameGroupsAndValuesByIds_hasNoChanges() {
            // given
            SellerOptionValue val1 = existingValue(101L, 10L, "검정", 0);
            SellerOptionValue val2 = existingValue(102L, 10L, "흰색", 1);
            SellerOptionGroup colorGroup = existingGroup(10L, "색상", 0, List.of(val1, val2));

            List<SellerOptionGroupUpdateData.GroupEntry> entries =
                    List.of(
                            retainedGroupEntry(
                                    10L,
                                    "색상",
                                    0,
                                    List.of(
                                            retainedValueEntry(101L, "검정", 0),
                                            retainedValueEntry(102L, "흰색", 1))));

            // when
            SellerOptionGroupDiff diff = executeUpdate(List.of(colorGroup), entries);

            // then
            assertThat(diff.hasNoChanges()).isTrue();
            assertThat(diff.addedGroups()).isEmpty();
            assertThat(diff.removedGroups()).isEmpty();
            assertThat(diff.retainedGroups()).hasSize(1);
        }

        @Test
        @DisplayName("유지된 그룹의 이름 변경이 반영된다 (ID 보존)")
        void retainedGroupNameChangePreservesId() {
            // given
            SellerOptionValue val1 = existingValue(101L, 10L, "검정", 0);
            SellerOptionGroup colorGroup = existingGroup(10L, "색상", 0, List.of(val1));

            List<SellerOptionGroupUpdateData.GroupEntry> entries =
                    List.of(
                            retainedGroupEntry(
                                    10L, "컬러", 0, List.of(retainedValueEntry(101L, "블랙", 0))));

            // when
            SellerOptionGroupDiff diff = executeUpdate(List.of(colorGroup), entries);

            // then
            assertThat(diff.hasNoChanges()).isTrue();
            assertThat(diff.retainedGroups()).hasSize(1);
            SellerOptionGroup retained = diff.retainedGroups().get(0).group();
            assertThat(retained.idValue()).isEqualTo(10L);
            assertThat(retained.optionGroupNameValue()).isEqualTo("컬러");
            assertThat(retained.optionValues().get(0).optionValueNameValue()).isEqualTo("블랙");
            assertThat(retained.optionValues().get(0).idValue()).isEqualTo(101L);
        }

        @Test
        @DisplayName("orderedActiveValueIds가 기존 값의 ID를 유지한다")
        void orderedActiveValueIds_preservesExistingIds() {
            // given
            SellerOptionValue val1 = existingValue(101L, 10L, "검정", 0);
            SellerOptionValue val2 = existingValue(102L, 10L, "흰색", 1);
            SellerOptionGroup colorGroup = existingGroup(10L, "색상", 0, List.of(val1, val2));

            List<SellerOptionGroupUpdateData.GroupEntry> entries =
                    List.of(
                            retainedGroupEntry(
                                    10L,
                                    "색상",
                                    0,
                                    List.of(
                                            retainedValueEntry(101L, "검정", 0),
                                            retainedValueEntry(102L, "흰색", 1))));

            // when
            SellerOptionGroupDiff diff = executeUpdate(List.of(colorGroup), entries);

            // then
            List<SellerOptionValueId> activeIds = diff.orderedActiveValueIds();
            assertThat(activeIds).hasSize(2);
            assertThat(activeIds.get(0).value()).isEqualTo(101L);
            assertThat(activeIds.get(1).value()).isEqualTo(102L);
        }
    }

    @Nested
    @DisplayName("그룹이 추가되는 경우")
    class GroupAddedTest {

        @Test
        @DisplayName("ID가 null인 entry는 addedGroups에 포함된다")
        void nullIdEntry_appearsInAddedGroups() {
            // given
            SellerOptionValue val1 = existingValue(101L, 10L, "검정", 0);
            SellerOptionGroup colorGroup = existingGroup(10L, "색상", 0, List.of(val1));

            List<SellerOptionGroupUpdateData.GroupEntry> entries =
                    List.of(
                            retainedGroupEntry(
                                    10L, "색상", 0, List.of(retainedValueEntry(101L, "검정", 0))),
                            newGroupEntry(
                                    "사이즈",
                                    1,
                                    List.of(newValueEntry("S", 0), newValueEntry("M", 1))));

            // when
            SellerOptionGroupDiff diff = executeUpdate(List.of(colorGroup), entries);

            // then
            assertThat(diff.addedGroups()).hasSize(1);
            assertThat(diff.addedGroups().get(0).optionGroupNameValue()).isEqualTo("사이즈");
            assertThat(diff.removedGroups()).isEmpty();
            assertThat(diff.retainedGroups()).hasSize(1);
            assertThat(diff.hasNoChanges()).isFalse();
        }

        @Test
        @DisplayName("추가된 그룹의 값 ID가 orderedActiveValueIds에 포함된다")
        void addedGroupValueIds_includedInOrderedActiveValueIds() {
            // given - 기존에 그룹이 없는 상태에서 새 그룹 추가
            List<SellerOptionGroupUpdateData.GroupEntry> entries =
                    List.of(
                            newGroupEntry(
                                    "색상",
                                    0,
                                    List.of(newValueEntry("검정", 0), newValueEntry("흰색", 1))));

            // when
            SellerOptionGroupDiff diff = executeUpdate(List.of(), entries);

            // then
            assertThat(diff.addedGroups()).hasSize(1);
            List<SellerOptionValueId> activeIds = diff.orderedActiveValueIds();
            assertThat(activeIds).hasSize(2);
            assertThat(activeIds.get(0).value()).isNull();
            assertThat(activeIds.get(1).value()).isNull();
        }
    }

    @Nested
    @DisplayName("그룹이 삭제되는 경우")
    class GroupRemovedTest {

        @Test
        @DisplayName("entries에 포함되지 않은 기존 그룹이 removedGroups에 포함된다")
        void unmatchedGroup_appearsInRemovedGroups() {
            // given
            SellerOptionValue val1 = existingValue(101L, 10L, "검정", 0);
            SellerOptionGroup colorGroup = existingGroup(10L, "색상", 0, List.of(val1));

            SellerOptionValue val2 = existingValue(201L, 20L, "S", 0);
            SellerOptionGroup sizeGroup = existingGroup(20L, "사이즈", 1, List.of(val2));

            // entries에는 색상만 포함
            List<SellerOptionGroupUpdateData.GroupEntry> entries =
                    List.of(
                            retainedGroupEntry(
                                    10L, "색상", 0, List.of(retainedValueEntry(101L, "검정", 0))));

            // when
            SellerOptionGroupDiff diff = executeUpdate(List.of(colorGroup, sizeGroup), entries);

            // then
            assertThat(diff.removedGroups()).hasSize(1);
            assertThat(diff.removedGroups().get(0).optionGroupNameValue()).isEqualTo("사이즈");
            assertThat(diff.retainedGroups()).hasSize(1);
            assertThat(diff.hasNoChanges()).isFalse();
        }

        @Test
        @DisplayName("삭제된 그룹은 soft delete 처리된다")
        void removedGroup_isSoftDeleted() {
            // given
            SellerOptionValue val1 = existingValue(101L, 10L, "검정", 0);
            SellerOptionGroup colorGroup = existingGroup(10L, "색상", 0, List.of(val1));

            // when
            SellerOptionGroupDiff diff = executeUpdate(List.of(colorGroup), List.of());

            // then
            assertThat(diff.removedGroups()).hasSize(1);
            SellerOptionGroup removed = diff.removedGroups().get(0);
            assertThat(removed.isDeleted()).isTrue();
            assertThat(removed.deletionStatus().deletedAt()).isEqualTo(UPDATED_AT);
        }

        @Test
        @DisplayName("삭제된 그룹의 하위 값도 soft delete 처리된다")
        void removedGroupValues_areSoftDeleted() {
            // given
            SellerOptionValue val1 = existingValue(101L, 10L, "검정", 0);
            SellerOptionValue val2 = existingValue(102L, 10L, "흰색", 1);
            SellerOptionGroup colorGroup = existingGroup(10L, "색상", 0, List.of(val1, val2));

            // when
            SellerOptionGroupDiff diff = executeUpdate(List.of(colorGroup), List.of());

            // then
            SellerOptionGroup removed = diff.removedGroups().get(0);
            for (SellerOptionValue value : removed.optionValues()) {
                assertThat(value.isDeleted()).isTrue();
                assertThat(value.deletionStatus().deletedAt()).isEqualTo(UPDATED_AT);
            }
        }

        @Test
        @DisplayName("삭제된 그룹의 값 ID는 orderedActiveValueIds에 포함되지 않는다")
        void removedGroupValueIds_excludedFromOrderedActiveValueIds() {
            // given
            SellerOptionValue val1 = existingValue(101L, 10L, "검정", 0);
            SellerOptionGroup colorGroup = existingGroup(10L, "색상", 0, List.of(val1));

            SellerOptionValue val2 = existingValue(201L, 20L, "S", 0);
            SellerOptionGroup sizeGroup = existingGroup(20L, "사이즈", 1, List.of(val2));

            List<SellerOptionGroupUpdateData.GroupEntry> entries =
                    List.of(
                            retainedGroupEntry(
                                    10L, "색상", 0, List.of(retainedValueEntry(101L, "검정", 0))));

            // when
            SellerOptionGroupDiff diff = executeUpdate(List.of(colorGroup, sizeGroup), entries);

            // then
            List<SellerOptionValueId> activeIds = diff.orderedActiveValueIds();
            assertThat(activeIds).hasSize(1);
            assertThat(activeIds.get(0).value()).isEqualTo(101L);
        }
    }

    @Nested
    @DisplayName("유지된 그룹 내에서 값이 추가되는 경우")
    class RetainedGroupWithValueAddedTest {

        @Test
        @DisplayName("ID가 null인 value entry는 valueDiff.added()에 포함된다")
        void nullIdValueEntry_appearsInValueDiffAdded() {
            // given
            SellerOptionValue val1 = existingValue(101L, 10L, "검정", 0);
            SellerOptionGroup colorGroup = existingGroup(10L, "색상", 0, List.of(val1));

            List<SellerOptionGroupUpdateData.GroupEntry> entries =
                    List.of(
                            retainedGroupEntry(
                                    10L,
                                    "색상",
                                    0,
                                    List.of(
                                            retainedValueEntry(101L, "검정", 0),
                                            newValueEntry("흰색", 1),
                                            newValueEntry("빨강", 2))));

            // when
            SellerOptionGroupDiff diff = executeUpdate(List.of(colorGroup), entries);

            // then
            assertThat(diff.retainedGroups()).hasSize(1);
            SellerOptionValueDiff valueDiff = diff.retainedGroups().get(0).valueDiff();
            assertThat(valueDiff.added()).hasSize(2);
            assertThat(valueDiff.retained()).hasSize(1);
            assertThat(valueDiff.removed()).isEmpty();
            assertThat(valueDiff.hasNoChanges()).isFalse();
        }

        @Test
        @DisplayName("추가된 값의 ID도 orderedActiveValueIds에 포함된다")
        void addedValueIds_includedInOrderedActiveValueIds() {
            // given
            SellerOptionValue val1 = existingValue(101L, 10L, "검정", 0);
            SellerOptionGroup colorGroup = existingGroup(10L, "색상", 0, List.of(val1));

            List<SellerOptionGroupUpdateData.GroupEntry> entries =
                    List.of(
                            retainedGroupEntry(
                                    10L,
                                    "색상",
                                    0,
                                    List.of(
                                            retainedValueEntry(101L, "검정", 0),
                                            newValueEntry("흰색", 1))));

            // when
            SellerOptionGroupDiff diff = executeUpdate(List.of(colorGroup), entries);

            // then
            List<SellerOptionValueId> activeIds = diff.orderedActiveValueIds();
            assertThat(activeIds).hasSize(2);
            assertThat(activeIds.get(0).value()).isEqualTo(101L);
            assertThat(activeIds.get(1).value()).isNull();
        }
    }

    @Nested
    @DisplayName("유지된 그룹 내에서 값이 삭제되는 경우")
    class RetainedGroupWithValueRemovedTest {

        @Test
        @DisplayName("entries에 포함되지 않은 기존 값이 valueDiff.removed()에 포함된다")
        void unmatchedValue_appearsInValueDiffRemoved() {
            // given
            SellerOptionValue val1 = existingValue(101L, 10L, "검정", 0);
            SellerOptionValue val2 = existingValue(102L, 10L, "흰색", 1);
            SellerOptionValue val3 = existingValue(103L, 10L, "빨강", 2);
            SellerOptionGroup colorGroup = existingGroup(10L, "색상", 0, List.of(val1, val2, val3));

            List<SellerOptionGroupUpdateData.GroupEntry> entries =
                    List.of(
                            retainedGroupEntry(
                                    10L, "색상", 0, List.of(retainedValueEntry(101L, "검정", 0))));

            // when
            SellerOptionGroupDiff diff = executeUpdate(List.of(colorGroup), entries);

            // then
            SellerOptionValueDiff valueDiff = diff.retainedGroups().get(0).valueDiff();
            assertThat(valueDiff.removed()).hasSize(2);
            assertThat(valueDiff.retained()).hasSize(1);
            assertThat(valueDiff.added()).isEmpty();
            assertThat(valueDiff.hasNoChanges()).isFalse();
        }

        @Test
        @DisplayName("삭제된 값은 soft delete 처리된다")
        void removedValues_areSoftDeleted() {
            // given
            SellerOptionValue val1 = existingValue(101L, 10L, "검정", 0);
            SellerOptionValue val2 = existingValue(102L, 10L, "흰색", 1);
            SellerOptionGroup colorGroup = existingGroup(10L, "색상", 0, List.of(val1, val2));

            List<SellerOptionGroupUpdateData.GroupEntry> entries =
                    List.of(
                            retainedGroupEntry(
                                    10L, "색상", 0, List.of(retainedValueEntry(101L, "검정", 0))));

            // when
            SellerOptionGroupDiff diff = executeUpdate(List.of(colorGroup), entries);

            // then
            SellerOptionValueDiff valueDiff = diff.retainedGroups().get(0).valueDiff();
            for (SellerOptionValue removed : valueDiff.removed()) {
                assertThat(removed.isDeleted()).isTrue();
                assertThat(removed.deletionStatus().deletedAt()).isEqualTo(UPDATED_AT);
            }
        }
    }

    @Nested
    @DisplayName("복합 시나리오: 그룹과 값이 동시에 추가/삭제/유지되는 경우")
    class MixedScenarioTest {

        @Test
        @DisplayName("그룹 추가 + 삭제 + 유지(내부 값 변경)가 동시에 발생한다")
        void mixedGroupChanges_allCategoriesPresent() {
            // given
            SellerOptionValue colorVal1 = existingValue(101L, 10L, "검정", 0);
            SellerOptionValue colorVal2 = existingValue(102L, 10L, "흰색", 1);
            SellerOptionGroup colorGroup =
                    existingGroup(10L, "색상", 0, List.of(colorVal1, colorVal2));

            SellerOptionValue sizeVal1 = existingValue(201L, 20L, "S", 0);
            SellerOptionValue sizeVal2 = existingValue(202L, 20L, "M", 1);
            SellerOptionGroup sizeGroup = existingGroup(20L, "사이즈", 1, List.of(sizeVal1, sizeVal2));

            SellerOptionValue materialVal1 = existingValue(301L, 30L, "면", 0);
            SellerOptionGroup materialGroup = existingGroup(30L, "소재", 2, List.of(materialVal1));

            // entries: 색상(검정 유지 + 빨강 추가), 무게(새 그룹), 사이즈/소재 삭제
            List<SellerOptionGroupUpdateData.GroupEntry> entries =
                    List.of(
                            retainedGroupEntry(
                                    10L,
                                    "색상",
                                    0,
                                    List.of(
                                            retainedValueEntry(101L, "검정", 0),
                                            newValueEntry("빨강", 1))),
                            newGroupEntry("무게", 1, List.of(newValueEntry("100g", 0))));

            // when
            SellerOptionGroupDiff diff =
                    executeUpdate(List.of(colorGroup, sizeGroup, materialGroup), entries);

            // then - 그룹 레벨 검증
            assertThat(diff.addedGroups()).hasSize(1);
            assertThat(diff.addedGroups().get(0).optionGroupNameValue()).isEqualTo("무게");

            assertThat(diff.removedGroups()).hasSize(2);
            assertThat(diff.removedGroups())
                    .extracting(SellerOptionGroup::optionGroupNameValue)
                    .containsExactlyInAnyOrder("사이즈", "소재");

            assertThat(diff.retainedGroups()).hasSize(1);
            assertThat(diff.retainedGroups().get(0).group().optionGroupNameValue()).isEqualTo("색상");

            // then - 유지된 색상 그룹의 값 레벨 검증
            SellerOptionValueDiff colorValueDiff = diff.retainedGroups().get(0).valueDiff();
            assertThat(colorValueDiff.retained()).hasSize(1);
            assertThat(colorValueDiff.retained().get(0).idValue()).isEqualTo(101L);
            assertThat(colorValueDiff.added()).hasSize(1);
            assertThat(colorValueDiff.removed()).hasSize(1);
            assertThat(colorValueDiff.removed().get(0).idValue()).isEqualTo(102L);

            // then - soft delete 검증
            assertThat(diff.removedGroups())
                    .allSatisfy(
                            g -> {
                                assertThat(g.isDeleted()).isTrue();
                                assertThat(g.deletionStatus().deletedAt()).isEqualTo(UPDATED_AT);
                            });
            assertThat(colorValueDiff.removed().get(0).isDeleted()).isTrue();

            assertThat(diff.hasNoChanges()).isFalse();
        }

        @Test
        @DisplayName("복합 시나리오에서 orderedActiveValueIds가 올바른 값만 포함한다")
        void mixedScenario_orderedActiveValueIds_containsOnlyActiveValues() {
            // given
            SellerOptionValue colorVal1 = existingValue(101L, 10L, "검정", 0);
            SellerOptionValue colorVal2 = existingValue(102L, 10L, "흰색", 1);
            SellerOptionGroup colorGroup =
                    existingGroup(10L, "색상", 0, List.of(colorVal1, colorVal2));

            SellerOptionValue sizeVal1 = existingValue(201L, 20L, "S", 0);
            SellerOptionGroup sizeGroup = existingGroup(20L, "사이즈", 1, List.of(sizeVal1));

            // entries: 색상(검정만 유지) + 무게(새 그룹), 사이즈 삭제
            List<SellerOptionGroupUpdateData.GroupEntry> entries =
                    List.of(
                            retainedGroupEntry(
                                    10L, "색상", 0, List.of(retainedValueEntry(101L, "검정", 0))),
                            newGroupEntry("무게", 1, List.of(newValueEntry("100g", 0))));

            // when
            SellerOptionGroupDiff diff = executeUpdate(List.of(colorGroup, sizeGroup), entries);

            // then
            List<SellerOptionValueId> activeIds = diff.orderedActiveValueIds();
            // 검정(101L, retained) + 100g(null, added)
            assertThat(activeIds).hasSize(2);
            assertThat(activeIds.get(0).value()).isEqualTo(101L);
            assertThat(activeIds.get(1).value()).isNull();

            // 삭제된 흰색(102L), S(201L)는 포함되지 않아야 함
            List<Long> activeIdValues = activeIds.stream().map(SellerOptionValueId::value).toList();
            assertThat(activeIdValues).doesNotContain(102L, 201L);
        }
    }

    @Nested
    @DisplayName("orderedActiveValueIds 순서 검증")
    class OrderedActiveValueIdsOrderingTest {

        @Test
        @DisplayName("orderedActiveValueIds는 entry 순서를 따른다")
        void orderedActiveValueIds_followsEntryOrder() {
            // given
            SellerOptionValue colorVal1 = existingValue(101L, 10L, "검정", 0);
            SellerOptionValue colorVal2 = existingValue(102L, 10L, "흰색", 1);
            SellerOptionGroup colorGroup =
                    existingGroup(10L, "색상", 0, List.of(colorVal1, colorVal2));

            SellerOptionValue sizeVal1 = existingValue(201L, 20L, "S", 0);
            SellerOptionValue sizeVal2 = existingValue(202L, 20L, "M", 1);
            SellerOptionValue sizeVal3 = existingValue(203L, 20L, "L", 2);
            SellerOptionGroup sizeGroup =
                    existingGroup(20L, "사이즈", 1, List.of(sizeVal1, sizeVal2, sizeVal3));

            // entries: 사이즈(M, L, XL) 먼저, 색상(흰색, 검정) 나중
            List<SellerOptionGroupUpdateData.GroupEntry> entries =
                    List.of(
                            retainedGroupEntry(
                                    20L,
                                    "사이즈",
                                    0,
                                    List.of(
                                            retainedValueEntry(202L, "M", 0),
                                            retainedValueEntry(203L, "L", 1),
                                            newValueEntry("XL", 2))),
                            retainedGroupEntry(
                                    10L,
                                    "색상",
                                    1,
                                    List.of(
                                            retainedValueEntry(102L, "흰색", 0),
                                            retainedValueEntry(101L, "검정", 1))));

            // when
            SellerOptionGroupDiff diff = executeUpdate(List.of(colorGroup, sizeGroup), entries);

            // then
            List<SellerOptionValueId> activeIds = diff.orderedActiveValueIds();
            // 사이즈 먼저: M(202), L(203), XL(null)
            // 색상 나중: 흰색(102), 검정(101)
            assertThat(activeIds).hasSize(5);
            assertThat(activeIds.get(0).value()).isEqualTo(202L); // M
            assertThat(activeIds.get(1).value()).isEqualTo(203L); // L
            assertThat(activeIds.get(2).value()).isNull(); // XL
            assertThat(activeIds.get(3).value()).isEqualTo(102L); // 흰색
            assertThat(activeIds.get(4).value()).isEqualTo(101L); // 검정
        }
    }

    @Nested
    @DisplayName("유지된 그룹의 inputType이 변경되는 경우")
    class InputTypeChangeTest {

        @Test
        @DisplayName("PREDEFINED에서 FREE_INPUT으로 inputType이 변경된다")
        void retainedGroup_inputTypeChangesFromPredefinedToFreeInput() {
            // given
            SellerOptionValue val1 = existingValue(101L, 10L, "검정", 0);
            SellerOptionGroup colorGroup = existingGroup(10L, "색상", 0, List.of(val1));
            assertThat(colorGroup.inputType()).isEqualTo(OptionInputType.PREDEFINED);

            List<SellerOptionGroupUpdateData.GroupEntry> entries =
                    List.of(
                            new SellerOptionGroupUpdateData.GroupEntry(
                                    10L, "색상", null, OptionInputType.FREE_INPUT, 0,
                                    List.of(retainedValueEntry(101L, "검정", 0))));

            // when
            SellerOptionGroupDiff diff = executeUpdate(List.of(colorGroup), entries);

            // then
            assertThat(diff.retainedGroups()).hasSize(1);
            SellerOptionGroup retained = diff.retainedGroups().get(0).group();
            assertThat(retained.inputType()).isEqualTo(OptionInputType.FREE_INPUT);
        }

        @Test
        @DisplayName("FREE_INPUT에서 PREDEFINED로 inputType이 변경된다")
        void retainedGroup_inputTypeChangesFromFreeInputToPredefined() {
            // given
            SellerOptionValue val1 = existingValue(101L, 10L, "각인입력", 0);
            SellerOptionGroup freeInputGroup =
                    SellerOptionGroup.reconstitute(
                            SellerOptionGroupId.of(10L),
                            PRODUCT_GROUP_ID,
                            OptionGroupName.of("각인"),
                            null,
                            OptionInputType.FREE_INPUT,
                            0,
                            List.of(val1),
                            com.ryuqq.marketplace.domain.common.vo.DeletionStatus.active());
            assertThat(freeInputGroup.inputType()).isEqualTo(OptionInputType.FREE_INPUT);

            List<SellerOptionGroupUpdateData.GroupEntry> entries =
                    List.of(
                            new SellerOptionGroupUpdateData.GroupEntry(
                                    10L, "각인", null, OptionInputType.PREDEFINED, 0,
                                    List.of(retainedValueEntry(101L, "각인입력", 0))));

            // when
            SellerOptionGroupDiff diff = executeUpdate(List.of(freeInputGroup), entries);

            // then
            SellerOptionGroup retained = diff.retainedGroups().get(0).group();
            assertThat(retained.inputType()).isEqualTo(OptionInputType.PREDEFINED);
        }
    }

    @Nested
    @DisplayName("경계 조건 테스트")
    class EdgeCaseTest {

        @Test
        @DisplayName("기존 그룹이 비어 있고 새 그룹만 추가되는 경우")
        void emptyExisting_allNewGroupsAdded() {
            List<SellerOptionGroupUpdateData.GroupEntry> entries =
                    List.of(newGroupEntry("색상", 0, List.of(newValueEntry("검정", 0))));

            // when
            SellerOptionGroupDiff diff = executeUpdate(List.of(), entries);

            // then
            assertThat(diff.addedGroups()).hasSize(1);
            assertThat(diff.removedGroups()).isEmpty();
            assertThat(diff.retainedGroups()).isEmpty();
            assertThat(diff.hasNoChanges()).isFalse();
        }

        @Test
        @DisplayName("모든 기존 그룹이 삭제되는 경우")
        void allExistingRemoved_allMovedToRemoved() {
            SellerOptionValue val1 = existingValue(101L, 10L, "검정", 0);
            SellerOptionGroup colorGroup = existingGroup(10L, "색상", 0, List.of(val1));

            SellerOptionValue val2 = existingValue(201L, 20L, "S", 0);
            SellerOptionGroup sizeGroup = existingGroup(20L, "사이즈", 1, List.of(val2));

            // when
            SellerOptionGroupDiff diff = executeUpdate(List.of(colorGroup, sizeGroup), List.of());

            // then
            assertThat(diff.addedGroups()).isEmpty();
            assertThat(diff.removedGroups()).hasSize(2);
            assertThat(diff.retainedGroups()).isEmpty();
            assertThat(diff.orderedActiveValueIds()).isEmpty();
            assertThat(diff.hasNoChanges()).isFalse();
        }

        @Test
        @DisplayName("존재하지 않는 groupId로 매칭 시 예외가 발생한다")
        void nonExistentGroupId_throwsException() {
            SellerOptionValue val1 = existingValue(101L, 10L, "검정", 0);
            SellerOptionGroup colorGroup = existingGroup(10L, "색상", 0, List.of(val1));

            List<SellerOptionGroupUpdateData.GroupEntry> entries =
                    List.of(retainedGroupEntry(999L, "색상", 0, List.of()));

            assertThatThrownBy(() -> executeUpdate(List.of(colorGroup), entries))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("999");
        }

        @Test
        @DisplayName("존재하지 않는 valueId로 매칭 시 예외가 발생한다")
        void nonExistentValueId_throwsException() {
            SellerOptionValue val1 = existingValue(101L, 10L, "검정", 0);
            SellerOptionGroup colorGroup = existingGroup(10L, "색상", 0, List.of(val1));

            List<SellerOptionGroupUpdateData.GroupEntry> entries =
                    List.of(
                            retainedGroupEntry(
                                    10L, "색상", 0, List.of(retainedValueEntry(999L, "검정", 0))));

            assertThatThrownBy(() -> executeUpdate(List.of(colorGroup), entries))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("999");
        }

        @Test
        @DisplayName("occurredAt이 diff 결과에 올바르게 전달된다")
        void occurredAt_propagatedCorrectly() {
            SellerOptionValue val1 = existingValue(101L, 10L, "검정", 0);
            SellerOptionGroup colorGroup = existingGroup(10L, "색상", 0, List.of(val1));

            List<SellerOptionGroupUpdateData.GroupEntry> entries =
                    List.of(
                            retainedGroupEntry(
                                    10L, "색상", 0, List.of(retainedValueEntry(101L, "검정", 0))));

            // when
            SellerOptionGroupDiff diff = executeUpdate(List.of(colorGroup), entries);

            // then
            assertThat(diff.occurredAt()).isEqualTo(UPDATED_AT);
        }
    }
}
