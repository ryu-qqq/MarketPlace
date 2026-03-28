package com.ryuqq.marketplace.application.legacy.productcontext.resolver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.legacyconversion.manager.LegacyProductIdMappingReadManager;
import com.ryuqq.marketplace.domain.legacyconversion.LegacyConversionFixtures;
import com.ryuqq.marketplace.domain.legacyconversion.aggregate.LegacyProductIdMapping;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyProductIdResolver 단위 테스트")
class LegacyProductIdResolverTest {

    @InjectMocks private LegacyProductIdResolver sut;

    @Mock private LegacyProductIdMappingReadManager mappingReadManager;

    @Nested
    @DisplayName("resolveProductGroupId() - productGroupId resolve")
    class ResolveProductGroupIdTest {

        @Test
        @DisplayName("매핑이 존재하면 internal productGroupId를 반환한다")
        void resolveProductGroupId_MappingExists_ReturnsInternalId() {
            // given
            long legacyGroupId = LegacyConversionFixtures.DEFAULT_LEGACY_PRODUCT_GROUP_ID;
            LegacyProductIdMapping mapping =
                    LegacyConversionFixtures.mappingWithGroup(
                            1L, 200L, 300L, legacyGroupId, 400L);

            given(mappingReadManager.findByLegacyProductGroupId(legacyGroupId))
                    .willReturn(List.of(mapping));

            // when
            long result = sut.resolveProductGroupId(legacyGroupId);

            // then
            assertThat(result).isEqualTo(mapping.internalProductGroupId());
            then(mappingReadManager).should().findByLegacyProductGroupId(legacyGroupId);
        }

        @Test
        @DisplayName("매핑이 없으면 요청 ID를 그대로 반환한다")
        void resolveProductGroupId_NoMapping_ReturnsRequestId() {
            // given
            long legacyGroupId = 999L;
            given(mappingReadManager.findByLegacyProductGroupId(legacyGroupId))
                    .willReturn(List.of());

            // when
            long result = sut.resolveProductGroupId(legacyGroupId);

            // then
            assertThat(result).isEqualTo(legacyGroupId);
        }
    }

    @Nested
    @DisplayName("resolveProductId() - productId resolve")
    class ResolveProductIdTest {

        @Test
        @DisplayName("매핑이 존재하면 internal productId를 반환한다")
        void resolveProductId_MappingExists_ReturnsInternalId() {
            // given
            long legacyProductId = LegacyConversionFixtures.DEFAULT_LEGACY_PRODUCT_ID;
            LegacyProductIdMapping mapping = LegacyConversionFixtures.mapping(1L);

            given(mappingReadManager.findByLegacyProductId(legacyProductId))
                    .willReturn(Optional.of(mapping));

            // when
            long result = sut.resolveProductId(legacyProductId);

            // then
            assertThat(result).isEqualTo(mapping.internalProductId());
        }

        @Test
        @DisplayName("매핑이 없으면 요청 ID를 그대로 반환한다")
        void resolveProductId_NoMapping_ReturnsRequestId() {
            // given
            long legacyProductId = 9999L;
            given(mappingReadManager.findByLegacyProductId(legacyProductId))
                    .willReturn(Optional.empty());

            // when
            long result = sut.resolveProductId(legacyProductId);

            // then
            assertThat(result).isEqualTo(legacyProductId);
        }
    }

    @Nested
    @DisplayName("resolveProductIdsByLegacyGroupId() - 그룹 내 SKU Map 조회")
    class ResolveProductIdsByLegacyGroupIdTest {

        @Test
        @DisplayName("그룹 내 모든 SKU 매핑을 Map으로 반환한다")
        void resolveProductIdsByLegacyGroupId_MappingsExist_ReturnsMap() {
            // given
            long legacyGroupId = LegacyConversionFixtures.DEFAULT_LEGACY_PRODUCT_GROUP_ID;
            LegacyProductIdMapping m1 =
                    LegacyConversionFixtures.mappingWithGroup(1L, 201L, 301L, legacyGroupId, 400L);
            LegacyProductIdMapping m2 =
                    LegacyConversionFixtures.mappingWithGroup(2L, 202L, 302L, legacyGroupId, 400L);

            given(mappingReadManager.findByLegacyProductGroupId(legacyGroupId))
                    .willReturn(List.of(m1, m2));

            // when
            Map<Long, Long> result = sut.resolveProductIdsByLegacyGroupId(legacyGroupId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsEntry(201L, 301L);
            assertThat(result).containsEntry(202L, 302L);
        }

        @Test
        @DisplayName("매핑이 없으면 빈 Map을 반환한다")
        void resolveProductIdsByLegacyGroupId_NoMappings_ReturnsEmptyMap() {
            // given
            long legacyGroupId = 9999L;
            given(mappingReadManager.findByLegacyProductGroupId(legacyGroupId))
                    .willReturn(List.of());

            // when
            Map<Long, Long> result = sut.resolveProductIdsByLegacyGroupId(legacyGroupId);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("reverseResolveProductGroupId() - 역방향 productGroupId resolve")
    class ReverseResolveProductGroupIdTest {

        @Test
        @DisplayName("매핑이 존재하면 legacyProductGroupId를 반환한다")
        void reverseResolveProductGroupId_MappingExists_ReturnsLegacyId() {
            // given
            long internalGroupId = LegacyConversionFixtures.DEFAULT_INTERNAL_PRODUCT_GROUP_ID;
            LegacyProductIdMapping mapping =
                    LegacyConversionFixtures.mappingWithGroup(1L, 200L, 300L, 100L, internalGroupId);

            given(mappingReadManager.findByInternalProductGroupId(internalGroupId))
                    .willReturn(List.of(mapping));

            // when
            long result = sut.reverseResolveProductGroupId(internalGroupId);

            // then
            assertThat(result).isEqualTo(mapping.legacyProductGroupId());
        }

        @Test
        @DisplayName("매핑이 없으면 internal ID를 그대로 반환한다")
        void reverseResolveProductGroupId_NoMapping_ReturnsInternalId() {
            // given
            long internalGroupId = 9999L;
            given(mappingReadManager.findByInternalProductGroupId(internalGroupId))
                    .willReturn(List.of());

            // when
            long result = sut.reverseResolveProductGroupId(internalGroupId);

            // then
            assertThat(result).isEqualTo(internalGroupId);
        }
    }

    @Nested
    @DisplayName("reverseResolveProductId() - 역방향 productId resolve")
    class ReverseResolveProductIdTest {

        @Test
        @DisplayName("매핑이 존재하면 legacyProductId를 반환한다")
        void reverseResolveProductId_MappingExists_ReturnsLegacyId() {
            // given
            long internalProductId = LegacyConversionFixtures.DEFAULT_INTERNAL_PRODUCT_ID;
            LegacyProductIdMapping mapping = LegacyConversionFixtures.mapping(1L);

            given(mappingReadManager.findByInternalProductId(internalProductId))
                    .willReturn(Optional.of(mapping));

            // when
            long result = sut.reverseResolveProductId(internalProductId);

            // then
            assertThat(result).isEqualTo(mapping.legacyProductId());
        }

        @Test
        @DisplayName("매핑이 없으면 internal ID를 그대로 반환한다")
        void reverseResolveProductId_NoMapping_ReturnsInternalId() {
            // given
            long internalProductId = 9999L;
            given(mappingReadManager.findByInternalProductId(internalProductId))
                    .willReturn(Optional.empty());

            // when
            long result = sut.reverseResolveProductId(internalProductId);

            // then
            assertThat(result).isEqualTo(internalProductId);
        }
    }

    @Nested
    @DisplayName("reverseResolveProductIdsByInternalGroupId() - 역방향 SKU Map 조회")
    class ReverseResolveProductIdsByInternalGroupIdTest {

        @Test
        @DisplayName("그룹 내 모든 SKU 역매핑을 Map으로 반환한다")
        void reverseResolveProductIdsByInternalGroupId_MappingsExist_ReturnsMap() {
            // given
            long internalGroupId = LegacyConversionFixtures.DEFAULT_INTERNAL_PRODUCT_GROUP_ID;
            LegacyProductIdMapping m1 =
                    LegacyConversionFixtures.mappingWithGroup(1L, 201L, 301L, 100L, internalGroupId);
            LegacyProductIdMapping m2 =
                    LegacyConversionFixtures.mappingWithGroup(2L, 202L, 302L, 100L, internalGroupId);

            given(mappingReadManager.findByInternalProductGroupId(internalGroupId))
                    .willReturn(List.of(m1, m2));

            // when
            Map<Long, Long> result =
                    sut.reverseResolveProductIdsByInternalGroupId(internalGroupId);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).containsEntry(301L, 201L);
            assertThat(result).containsEntry(302L, 202L);
        }
    }
}
