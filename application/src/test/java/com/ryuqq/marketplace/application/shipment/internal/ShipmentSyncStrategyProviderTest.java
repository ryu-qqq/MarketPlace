package com.ryuqq.marketplace.application.shipment.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.shipment.port.out.client.ShipmentSyncStrategy;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ShipmentSyncStrategyProvider 단위 테스트")
class ShipmentSyncStrategyProviderTest {

    @Mock private ShipmentSyncStrategy naverStrategy;
    @Mock private ShipmentSyncStrategy coupangStrategy;

    @Nested
    @DisplayName("getStrategy() - 채널 코드 기반 전략 조회")
    class GetStrategyTest {

        @Test
        @DisplayName("등록된 채널 코드로 전략을 O(1) 조회한다")
        void getStrategy_RegisteredChannelCode_ReturnsStrategy() {
            // given
            given(naverStrategy.channelCode()).willReturn("NAVER");
            ShipmentSyncStrategyProvider sut =
                    new ShipmentSyncStrategyProvider(List.of(naverStrategy));

            // when
            ShipmentSyncStrategy result = sut.getStrategy("NAVER");

            // then
            assertThat(result).isSameAs(naverStrategy);
        }

        @Test
        @DisplayName("복수의 전략이 등록된 경우 각 채널 코드로 올바른 전략을 반환한다")
        void getStrategy_MultipleStrategies_ReturnsCorrectStrategy() {
            // given
            given(naverStrategy.channelCode()).willReturn("NAVER");
            given(coupangStrategy.channelCode()).willReturn("COUPANG");
            ShipmentSyncStrategyProvider sut =
                    new ShipmentSyncStrategyProvider(List.of(naverStrategy, coupangStrategy));

            // when & then
            assertThat(sut.getStrategy("NAVER")).isSameAs(naverStrategy);
            assertThat(sut.getStrategy("COUPANG")).isSameAs(coupangStrategy);
        }

        @Test
        @DisplayName("지원하지 않는 채널 코드로 조회 시 IllegalStateException을 던진다")
        void getStrategy_UnsupportedChannelCode_ThrowsIllegalStateException() {
            // given
            given(naverStrategy.channelCode()).willReturn("NAVER");
            ShipmentSyncStrategyProvider sut =
                    new ShipmentSyncStrategyProvider(List.of(naverStrategy));

            // when & then
            assertThatThrownBy(() -> sut.getStrategy("UNKNOWN"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("UNKNOWN");
        }

        @Test
        @DisplayName("전략 목록이 비어 있을 때 조회 시 IllegalStateException을 던진다")
        void getStrategy_EmptyStrategies_ThrowsIllegalStateException() {
            // given
            ShipmentSyncStrategyProvider sut = new ShipmentSyncStrategyProvider(List.of());

            // when & then
            assertThatThrownBy(() -> sut.getStrategy("NAVER"))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("NAVER");
        }
    }
}
