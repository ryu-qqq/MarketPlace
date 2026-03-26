package com.ryuqq.marketplace.domain.inboundproduct.event;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.inboundproduct.id.InboundProductId;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import java.time.Instant;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("InboundProductConvertedEvent 단위 테스트")
class InboundProductConvertedEventTest {

    @Nested
    @DisplayName("이벤트 생성 테스트")
    class CreationTest {

        @Test
        @DisplayName("신규 등록 변환 이벤트를 생성한다")
        void createNewRegistrationEvent() {
            InboundProductId inboundProductId = InboundProductId.of(1L);
            ProductGroupId productGroupId = ProductGroupId.of(100L);
            Instant now = CommonVoFixtures.now();

            InboundProductConvertedEvent event =
                    new InboundProductConvertedEvent(inboundProductId, productGroupId, true, now);

            assertThat(event.inboundProductId()).isEqualTo(inboundProductId);
            assertThat(event.productGroupId()).isEqualTo(productGroupId);
            assertThat(event.isNewRegistration()).isTrue();
            assertThat(event.occurredAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("갱신 변환 이벤트를 생성한다")
        void createUpdateEvent() {
            InboundProductId inboundProductId = InboundProductId.of(1L);
            ProductGroupId productGroupId = ProductGroupId.of(100L);
            Instant now = CommonVoFixtures.now();

            InboundProductConvertedEvent event =
                    new InboundProductConvertedEvent(inboundProductId, productGroupId, false, now);

            assertThat(event.isNewRegistration()).isFalse();
        }
    }

    @Nested
    @DisplayName("동등성 테스트")
    class EqualityTest {

        @Test
        @DisplayName("동일한 필드를 가진 이벤트는 같다")
        void sameEventAreEqual() {
            InboundProductId inboundProductId = InboundProductId.of(1L);
            ProductGroupId productGroupId = ProductGroupId.of(100L);
            Instant now = Instant.parse("2024-01-01T00:00:00Z");

            InboundProductConvertedEvent event1 =
                    new InboundProductConvertedEvent(inboundProductId, productGroupId, true, now);
            InboundProductConvertedEvent event2 =
                    new InboundProductConvertedEvent(inboundProductId, productGroupId, true, now);

            assertThat(event1).isEqualTo(event2);
        }
    }
}
