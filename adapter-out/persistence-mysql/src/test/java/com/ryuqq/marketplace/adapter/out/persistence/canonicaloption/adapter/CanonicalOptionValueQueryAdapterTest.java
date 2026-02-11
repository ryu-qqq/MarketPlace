package com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.CanonicalOptionValueJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.entity.CanonicalOptionValueJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.mapper.CanonicalOptionValueJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.repository.CanonicalOptionValueJpaRepository;
import com.ryuqq.marketplace.domain.canonicaloption.aggregate.CanonicalOptionValue;
import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionValueId;
import com.ryuqq.marketplace.domain.canonicaloption.vo.CanonicalOptionValueCode;
import com.ryuqq.marketplace.domain.canonicaloption.vo.CanonicalOptionValueName;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * CanonicalOptionValueQueryAdapterTest - 캐노니컬 옵션 값 Query Adapter 단위 테스트.
 *
 * <p>PER-ADP-004: QueryAdapter는 JpaRepository 사용.
 *
 * <p>PER-ADP-005: Entity -> Domain 변환 (Mapper 사용).
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("CanonicalOptionValueQueryAdapter 단위 테스트")
class CanonicalOptionValueQueryAdapterTest {

    @Mock private CanonicalOptionValueJpaRepository valueJpaRepository;

    @Mock private CanonicalOptionValueJpaEntityMapper mapper;

    @InjectMocks private CanonicalOptionValueQueryAdapter queryAdapter;

    // ========================================================================
    // 1. findByCanonicalOptionGroupId 테스트
    // ========================================================================

    @Nested
    @DisplayName("findByCanonicalOptionGroupId 메서드 테스트")
    class FindByCanonicalOptionGroupIdTest {

        @Test
        @DisplayName("존재하는 그룹 ID로 조회 시 정렬된 옵션 값 목록을 반환합니다")
        void findByCanonicalOptionGroupId_WithExistingGroupId_ReturnsSortedDomainList() {
            // given
            Long groupId = 1L;
            CanonicalOptionValueJpaEntity entity1 =
                    CanonicalOptionValueJpaEntityFixtures.colorRedEntity();
            CanonicalOptionValueJpaEntity entity2 =
                    CanonicalOptionValueJpaEntityFixtures.colorBlueEntity();
            CanonicalOptionValue domain1 = createDomain(1L, "RED", "빨강", "Red", 1);
            CanonicalOptionValue domain2 = createDomain(2L, "BLUE", "파랑", "Blue", 2);

            given(valueJpaRepository.findByCanonicalOptionGroupIdOrderBySortOrder(groupId))
                    .willReturn(List.of(entity1, entity2));
            given(mapper.toDomain(entity1)).willReturn(domain1);
            given(mapper.toDomain(entity2)).willReturn(domain2);

            // when
            List<CanonicalOptionValue> result =
                    queryAdapter.findByCanonicalOptionGroupId(groupId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsExactly(domain1, domain2);
            assertThat(result.get(0).sortOrder()).isEqualTo(1);
            assertThat(result.get(1).sortOrder()).isEqualTo(2);
            then(valueJpaRepository).should().findByCanonicalOptionGroupIdOrderBySortOrder(groupId);
        }

        @Test
        @DisplayName("존재하지 않는 그룹 ID로 조회 시 빈 리스트를 반환합니다")
        void findByCanonicalOptionGroupId_WithNonExistingGroupId_ReturnsEmptyList() {
            // given
            Long groupId = 999L;
            given(valueJpaRepository.findByCanonicalOptionGroupIdOrderBySortOrder(groupId))
                    .willReturn(List.of());

            // when
            List<CanonicalOptionValue> result =
                    queryAdapter.findByCanonicalOptionGroupId(groupId);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("SIZE 그룹의 옵션 값들을 정렬 순서대로 조회합니다")
        void findByCanonicalOptionGroupId_WithSizeGroup_ReturnsSortedList() {
            // given
            Long sizeGroupId = 2L;
            CanonicalOptionValueJpaEntity small =
                    CanonicalOptionValueJpaEntityFixtures.sizeSmallEntity();
            CanonicalOptionValueJpaEntity medium =
                    CanonicalOptionValueJpaEntityFixtures.sizeMediumEntity();
            CanonicalOptionValueJpaEntity large =
                    CanonicalOptionValueJpaEntityFixtures.sizeLargeEntity();
            CanonicalOptionValue domainSmall = createDomain(3L, "SMALL", "소형", "Small", 1);
            CanonicalOptionValue domainMedium = createDomain(4L, "MEDIUM", "중형", "Medium", 2);
            CanonicalOptionValue domainLarge = createDomain(5L, "LARGE", "대형", "Large", 3);

            given(valueJpaRepository.findByCanonicalOptionGroupIdOrderBySortOrder(sizeGroupId))
                    .willReturn(List.of(small, medium, large));
            given(mapper.toDomain(small)).willReturn(domainSmall);
            given(mapper.toDomain(medium)).willReturn(domainMedium);
            given(mapper.toDomain(large)).willReturn(domainLarge);

            // when
            List<CanonicalOptionValue> result =
                    queryAdapter.findByCanonicalOptionGroupId(sizeGroupId);

            // then
            assertThat(result).hasSize(3);
            assertThat(result.get(0).codeValue()).isEqualTo("SMALL");
            assertThat(result.get(1).codeValue()).isEqualTo("MEDIUM");
            assertThat(result.get(2).codeValue()).isEqualTo("LARGE");
        }
    }

    // ========================================================================
    // 2. findGroupedByCanonicalOptionGroupIds 테스트
    // ========================================================================

    @Nested
    @DisplayName("findGroupedByCanonicalOptionGroupIds 메서드 테스트")
    class FindGroupedByCanonicalOptionGroupIdsTest {

        @Test
        @DisplayName("여러 그룹 ID로 조회 시 그룹별로 묶인 맵을 반환합니다")
        void findGroupedByCanonicalOptionGroupIds_WithMultipleGroupIds_ReturnsGroupedMap() {
            // given
            List<Long> groupIds = List.of(1L, 2L);
            CanonicalOptionValueJpaEntity colorRed =
                    CanonicalOptionValueJpaEntityFixtures.colorRedEntity();
            CanonicalOptionValueJpaEntity colorBlue =
                    CanonicalOptionValueJpaEntityFixtures.colorBlueEntity();
            CanonicalOptionValueJpaEntity sizeSmall =
                    CanonicalOptionValueJpaEntityFixtures.sizeSmallEntity();

            CanonicalOptionValue domainRed = createDomain(1L, "RED", "빨강", "Red", 1);
            CanonicalOptionValue domainBlue = createDomain(2L, "BLUE", "파랑", "Blue", 2);
            CanonicalOptionValue domainSmall = createDomain(3L, "SMALL", "소형", "Small", 1);

            given(valueJpaRepository.findByCanonicalOptionGroupIdInOrderBySortOrder(groupIds))
                    .willReturn(List.of(colorRed, colorBlue, sizeSmall));
            given(mapper.toDomain(colorRed)).willReturn(domainRed);
            given(mapper.toDomain(colorBlue)).willReturn(domainBlue);
            given(mapper.toDomain(sizeSmall)).willReturn(domainSmall);

            // when
            Map<Long, List<CanonicalOptionValue>> result =
                    queryAdapter.findGroupedByCanonicalOptionGroupIds(groupIds);

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(1L)).hasSize(2);
            assertThat(result.get(2L)).hasSize(1);
            assertThat(result.get(1L).get(0).codeValue()).isEqualTo("RED");
            assertThat(result.get(2L).get(0).codeValue()).isEqualTo("SMALL");
            then(valueJpaRepository).should().findByCanonicalOptionGroupIdInOrderBySortOrder(groupIds);
        }

        @Test
        @DisplayName("존재하지 않는 그룹 ID로 조회 시 빈 맵을 반환합니다")
        void findGroupedByCanonicalOptionGroupIds_WithNonExistingGroupIds_ReturnsEmptyMap() {
            // given
            List<Long> groupIds = List.of(999L);
            given(valueJpaRepository.findByCanonicalOptionGroupIdInOrderBySortOrder(groupIds))
                    .willReturn(List.of());

            // when
            Map<Long, List<CanonicalOptionValue>> result =
                    queryAdapter.findGroupedByCanonicalOptionGroupIds(groupIds);

            // then
            assertThat(result).isEmpty();
        }

        @Test
        @DisplayName("각 그룹의 옵션 값들이 정렬 순서대로 그룹화됩니다")
        void findGroupedByCanonicalOptionGroupIds_PreservesSortOrderWithinGroups() {
            // given
            List<Long> groupIds = List.of(2L);
            CanonicalOptionValueJpaEntity small =
                    CanonicalOptionValueJpaEntityFixtures.sizeSmallEntity();
            CanonicalOptionValueJpaEntity medium =
                    CanonicalOptionValueJpaEntityFixtures.sizeMediumEntity();
            CanonicalOptionValueJpaEntity large =
                    CanonicalOptionValueJpaEntityFixtures.sizeLargeEntity();

            CanonicalOptionValue domainSmall = createDomain(3L, "SMALL", "소형", "Small", 1);
            CanonicalOptionValue domainMedium = createDomain(4L, "MEDIUM", "중형", "Medium", 2);
            CanonicalOptionValue domainLarge = createDomain(5L, "LARGE", "대형", "Large", 3);

            given(valueJpaRepository.findByCanonicalOptionGroupIdInOrderBySortOrder(groupIds))
                    .willReturn(List.of(small, medium, large));
            given(mapper.toDomain(small)).willReturn(domainSmall);
            given(mapper.toDomain(medium)).willReturn(domainMedium);
            given(mapper.toDomain(large)).willReturn(domainLarge);

            // when
            Map<Long, List<CanonicalOptionValue>> result =
                    queryAdapter.findGroupedByCanonicalOptionGroupIds(groupIds);

            // then
            List<CanonicalOptionValue> sizeValues = result.get(2L);
            assertThat(sizeValues).hasSize(3);
            assertThat(sizeValues.get(0).sortOrder()).isEqualTo(1);
            assertThat(sizeValues.get(1).sortOrder()).isEqualTo(2);
            assertThat(sizeValues.get(2).sortOrder()).isEqualTo(3);
        }
    }

    // ========================================================================
    // Helper Methods
    // ========================================================================

    private CanonicalOptionValue createDomain(
            Long id, String code, String nameKo, String nameEn, int sortOrder) {
        return CanonicalOptionValue.reconstitute(
                CanonicalOptionValueId.of(id),
                CanonicalOptionValueCode.of(code),
                CanonicalOptionValueName.of(nameKo, nameEn),
                sortOrder);
    }
}
