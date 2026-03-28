package com.ryuqq.marketplace.domain.legacyconversion.aggregate;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.legacyconversion.LegacyConversionFixtures;
import com.ryuqq.marketplace.domain.legacyconversion.id.LegacyOrderIdMappingId;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("LegacyOrderIdMapping Aggregate 테스트")
class LegacyOrderIdMappingTest {

    @Nested
    @DisplayName("forNew() - 신규 주문 ID 매핑 생성")
    class ForNewTest {

        @Test
        @DisplayName("모든 필드로 신규 주문 ID 매핑을 생성한다")
        void createNewOrderMappingWithAllFields() {
            // given
            long legacyOrderId = LegacyConversionFixtures.DEFAULT_LEGACY_ORDER_ID;
            long legacyPaymentId = LegacyConversionFixtures.DEFAULT_LEGACY_PAYMENT_ID;
            String internalOrderId = LegacyConversionFixtures.DEFAULT_INTERNAL_ORDER_ID;
            long salesChannelId = LegacyConversionFixtures.DEFAULT_SALES_CHANNEL_ID;
            String channelName = LegacyConversionFixtures.DEFAULT_CHANNEL_NAME;
            Instant now = CommonVoFixtures.now();

            // when
            LegacyOrderIdMapping mapping =
                    LegacyOrderIdMapping.forNew(
                            legacyOrderId,
                            legacyPaymentId,
                            internalOrderId,
                            salesChannelId,
                            channelName,
                            now);

            // then
            assertThat(mapping.isNew()).isTrue();
            assertThat(mapping.id().isNew()).isTrue();
            assertThat(mapping.legacyOrderId()).isEqualTo(legacyOrderId);
            assertThat(mapping.legacyPaymentId()).isEqualTo(legacyPaymentId);
            assertThat(mapping.internalOrderId()).isEqualTo(internalOrderId);
            assertThat(mapping.salesChannelId()).isEqualTo(salesChannelId);
            assertThat(mapping.channelName()).isEqualTo(channelName);
            assertThat(mapping.createdAt()).isEqualTo(now);
        }
    }

    @Nested
    @DisplayName("reconstitute() - DB에서 재구성")
    class ReconstituteTest {

        @Test
        @DisplayName("모든 필드로 주문 ID 매핑을 재구성한다")
        void reconstituteOrderMappingWithAllFields() {
            // given
            LegacyOrderIdMappingId id =
                    LegacyOrderIdMappingId.of(LegacyConversionFixtures.DEFAULT_ORDER_MAPPING_ID);
            Instant now = CommonVoFixtures.now();

            // when
            LegacyOrderIdMapping mapping =
                    LegacyOrderIdMapping.reconstitute(
                            id,
                            LegacyConversionFixtures.DEFAULT_LEGACY_ORDER_ID,
                            LegacyConversionFixtures.DEFAULT_LEGACY_PAYMENT_ID,
                            LegacyConversionFixtures.DEFAULT_INTERNAL_ORDER_ID,
                            LegacyConversionFixtures.DEFAULT_SALES_CHANNEL_ID,
                            LegacyConversionFixtures.DEFAULT_CHANNEL_NAME,
                            now);

            // then
            assertThat(mapping.isNew()).isFalse();
            assertThat(mapping.id()).isEqualTo(id);
            assertThat(mapping.idValue())
                    .isEqualTo(LegacyConversionFixtures.DEFAULT_ORDER_MAPPING_ID);
            assertThat(mapping.legacyOrderId())
                    .isEqualTo(LegacyConversionFixtures.DEFAULT_LEGACY_ORDER_ID);
            assertThat(mapping.legacyPaymentId())
                    .isEqualTo(LegacyConversionFixtures.DEFAULT_LEGACY_PAYMENT_ID);
            assertThat(mapping.internalOrderId())
                    .isEqualTo(LegacyConversionFixtures.DEFAULT_INTERNAL_ORDER_ID);
            assertThat(mapping.salesChannelId())
                    .isEqualTo(LegacyConversionFixtures.DEFAULT_SALES_CHANNEL_ID);
            assertThat(mapping.channelName())
                    .isEqualTo(LegacyConversionFixtures.DEFAULT_CHANNEL_NAME);
        }
    }

    @Nested
    @DisplayName("Fixtures 활용 테스트")
    class FixturesTest {

        @Test
        @DisplayName("Fixtures로 생성한 신규 매핑은 ID가 null이다")
        void newOrderMappingHasNullId() {
            // given
            LegacyOrderIdMapping mapping = LegacyConversionFixtures.newOrderMapping();

            // then
            assertThat(mapping.isNew()).isTrue();
            assertThat(mapping.idValue()).isNull();
        }

        @Test
        @DisplayName("Fixtures로 생성한 재구성 매핑은 ID가 존재한다")
        void reconstitutedOrderMappingHasId() {
            // given
            LegacyOrderIdMapping mapping = LegacyConversionFixtures.orderMapping();

            // then
            assertThat(mapping.isNew()).isFalse();
            assertThat(mapping.idValue())
                    .isEqualTo(LegacyConversionFixtures.DEFAULT_ORDER_MAPPING_ID);
        }

        @Test
        @DisplayName("특정 ID로 재구성한 매핑의 ID가 일치한다")
        void reconstitutedOrderMappingWithSpecificIdMatchesId() {
            // given
            LegacyOrderIdMapping mapping = LegacyConversionFixtures.orderMapping(99L);

            // then
            assertThat(mapping.idValue()).isEqualTo(99L);
        }
    }
}
