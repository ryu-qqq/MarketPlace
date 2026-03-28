package com.ryuqq.marketplace.domain.legacyconversion.aggregate;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.legacyconversion.LegacyConversionFixtures;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("LegacySellerIdMapping Aggregate 테스트")
class LegacySellerIdMappingTest {

    @Nested
    @DisplayName("reconstitute() - DB에서 재구성")
    class ReconstituteTest {

        @Test
        @DisplayName("모든 필드로 셀러 ID 매핑을 재구성한다")
        void reconstituteSellerMappingWithAllFields() {
            // given
            Long id = 1L;
            long legacySellerId = LegacyConversionFixtures.DEFAULT_LEGACY_SELLER_ID;
            long internalSellerId = LegacyConversionFixtures.DEFAULT_INTERNAL_SELLER_ID;
            String sellerName = LegacyConversionFixtures.DEFAULT_SELLER_NAME;

            // when
            LegacySellerIdMapping mapping =
                    LegacySellerIdMapping.reconstitute(
                            id, legacySellerId, internalSellerId, sellerName);

            // then
            assertThat(mapping.id()).isEqualTo(id);
            assertThat(mapping.legacySellerId()).isEqualTo(legacySellerId);
            assertThat(mapping.internalSellerId()).isEqualTo(internalSellerId);
            assertThat(mapping.sellerName()).isEqualTo(sellerName);
        }

        @Test
        @DisplayName("id가 null인 셀러 ID 매핑을 재구성한다")
        void reconstituteSellerMappingWithNullId() {
            // when
            LegacySellerIdMapping mapping =
                    LegacySellerIdMapping.reconstitute(
                            null,
                            LegacyConversionFixtures.DEFAULT_LEGACY_SELLER_ID,
                            LegacyConversionFixtures.DEFAULT_INTERNAL_SELLER_ID,
                            LegacyConversionFixtures.DEFAULT_SELLER_NAME);

            // then
            assertThat(mapping.id()).isNull();
            assertThat(mapping.legacySellerId())
                    .isEqualTo(LegacyConversionFixtures.DEFAULT_LEGACY_SELLER_ID);
        }
    }

    @Nested
    @DisplayName("Fixtures 활용 테스트")
    class FixturesTest {

        @Test
        @DisplayName("기본 Fixtures로 셀러 매핑을 생성한다")
        void createDefaultSellerMapping() {
            // when
            LegacySellerIdMapping mapping = LegacyConversionFixtures.sellerMapping();

            // then
            assertThat(mapping.id()).isEqualTo(1L);
            assertThat(mapping.legacySellerId())
                    .isEqualTo(LegacyConversionFixtures.DEFAULT_LEGACY_SELLER_ID);
            assertThat(mapping.internalSellerId())
                    .isEqualTo(LegacyConversionFixtures.DEFAULT_INTERNAL_SELLER_ID);
            assertThat(mapping.sellerName())
                    .isEqualTo(LegacyConversionFixtures.DEFAULT_SELLER_NAME);
        }

        @Test
        @DisplayName("특정 값으로 셀러 매핑을 생성한다")
        void createSellerMappingWithCustomValues() {
            // given & when
            LegacySellerIdMapping mapping =
                    LegacyConversionFixtures.sellerMapping(10L, 99L, 2L, "커스텀 셀러");

            // then
            assertThat(mapping.id()).isEqualTo(10L);
            assertThat(mapping.legacySellerId()).isEqualTo(99L);
            assertThat(mapping.internalSellerId()).isEqualTo(2L);
            assertThat(mapping.sellerName()).isEqualTo("커스텀 셀러");
        }
    }

    @Nested
    @DisplayName("N:1 매핑 시나리오 테스트")
    class NToOneMappingTest {

        @Test
        @DisplayName("같은 internalSellerId에 여러 legacySellerId가 매핑될 수 있다")
        void multipleLegacySellersMappedToSameInternalSeller() {
            // given
            LegacySellerIdMapping mapping1 =
                    LegacySellerIdMapping.reconstitute(1L, 100L, 1L, "셀러A");
            LegacySellerIdMapping mapping2 =
                    LegacySellerIdMapping.reconstitute(2L, 200L, 1L, "셀러B");

            // then
            assertThat(mapping1.internalSellerId()).isEqualTo(mapping2.internalSellerId());
            assertThat(mapping1.legacySellerId()).isNotEqualTo(mapping2.legacySellerId());
        }
    }
}
