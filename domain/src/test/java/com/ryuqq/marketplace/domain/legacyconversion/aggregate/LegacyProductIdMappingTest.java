package com.ryuqq.marketplace.domain.legacyconversion.aggregate;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.legacyconversion.LegacyConversionFixtures;
import com.ryuqq.marketplace.domain.legacyconversion.id.LegacyProductIdMappingId;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("LegacyProductIdMapping Aggregate 테스트")
class LegacyProductIdMappingTest {

    @Nested
    @DisplayName("forNew() - 신규 매핑 생성")
    class ForNewTest {

        @Test
        @DisplayName("레거시/내부 상품 ID로 신규 매핑을 생성한다")
        void createNewMappingWithAllIds() {
            // given
            long legacyProductId = LegacyConversionFixtures.DEFAULT_LEGACY_PRODUCT_ID;
            long internalProductId = LegacyConversionFixtures.DEFAULT_INTERNAL_PRODUCT_ID;
            long legacyProductGroupId = LegacyConversionFixtures.DEFAULT_LEGACY_PRODUCT_GROUP_ID;
            long internalProductGroupId =
                    LegacyConversionFixtures.DEFAULT_INTERNAL_PRODUCT_GROUP_ID;
            Instant now = CommonVoFixtures.now();

            // when
            LegacyProductIdMapping mapping =
                    LegacyProductIdMapping.forNew(
                            legacyProductId,
                            internalProductId,
                            legacyProductGroupId,
                            internalProductGroupId,
                            now);

            // then
            assertThat(mapping.isNew()).isTrue();
            assertThat(mapping.id().isNew()).isTrue();
            assertThat(mapping.legacyProductId()).isEqualTo(legacyProductId);
            assertThat(mapping.internalProductId()).isEqualTo(internalProductId);
            assertThat(mapping.legacyProductGroupId()).isEqualTo(legacyProductGroupId);
            assertThat(mapping.internalProductGroupId()).isEqualTo(internalProductGroupId);
            assertThat(mapping.createdAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("reconstitute() - DB에서 재구성")
    class ReconstituteTest {

        @Test
        @DisplayName("ID가 있는 매핑을 재구성한다")
        void reconstituteMappingWithId() {
            // given
            LegacyProductIdMappingId id = LegacyProductIdMappingId.of(1L);
            Instant now = CommonVoFixtures.now();

            // when
            LegacyProductIdMapping mapping =
                    LegacyProductIdMapping.reconstitute(
                            id,
                            LegacyConversionFixtures.DEFAULT_LEGACY_PRODUCT_ID,
                            LegacyConversionFixtures.DEFAULT_INTERNAL_PRODUCT_ID,
                            LegacyConversionFixtures.DEFAULT_LEGACY_PRODUCT_GROUP_ID,
                            LegacyConversionFixtures.DEFAULT_INTERNAL_PRODUCT_GROUP_ID,
                            now);

            // then
            assertThat(mapping.isNew()).isFalse();
            assertThat(mapping.id()).isEqualTo(id);
            assertThat(mapping.idValue()).isEqualTo(1L);
            assertThat(mapping.legacyProductId())
                    .isEqualTo(LegacyConversionFixtures.DEFAULT_LEGACY_PRODUCT_ID);
            assertThat(mapping.internalProductId())
                    .isEqualTo(LegacyConversionFixtures.DEFAULT_INTERNAL_PRODUCT_ID);
        }

        @Test
        @DisplayName("그룹 정보를 포함하여 재구성한다")
        void reconstituteMappingWithGroupInfo() {
            // when
            LegacyProductIdMapping mapping =
                    LegacyConversionFixtures.mappingWithGroup(
                            5L, 201L, 301L, 101L, 401L);

            // then
            assertThat(mapping.legacyProductGroupId()).isEqualTo(101L);
            assertThat(mapping.internalProductGroupId()).isEqualTo(401L);
        }
    }

    @Nested
    @DisplayName("Getter 메서드 테스트")
    class GetterTest {

        @Test
        @DisplayName("idValue()는 ID의 Long 값을 반환한다")
        void idValueReturnsLongValue() {
            // given
            LegacyProductIdMapping mapping = LegacyConversionFixtures.mapping(10L);

            // when
            Long idValue = mapping.idValue();

            // then
            assertThat(idValue).isEqualTo(10L);
        }

        @Test
        @DisplayName("신규 생성 시 idValue()는 null을 반환한다")
        void idValueReturnsNullForNewMapping() {
            // given
            LegacyProductIdMapping mapping = LegacyConversionFixtures.newMapping();

            // when
            Long idValue = mapping.idValue();

            // then
            assertThat(idValue).isNull();
        }
    }
}
