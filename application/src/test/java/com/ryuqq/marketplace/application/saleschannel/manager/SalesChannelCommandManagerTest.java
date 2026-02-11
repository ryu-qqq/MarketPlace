package com.ryuqq.marketplace.application.saleschannel.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.saleschannel.port.out.command.SalesChannelCommandPort;
import com.ryuqq.marketplace.domain.saleschannel.SalesChannelFixtures;
import com.ryuqq.marketplace.domain.saleschannel.aggregate.SalesChannel;
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
@DisplayName("SalesChannelCommandManager 단위 테스트")
class SalesChannelCommandManagerTest {

    @InjectMocks private SalesChannelCommandManager sut;

    @Mock private SalesChannelCommandPort commandPort;

    @Nested
    @DisplayName("persist() - 판매채널 저장")
    class PersistTest {

        @Test
        @DisplayName("새로운 판매채널을 저장하고 ID를 반환한다")
        void persist_NewSalesChannel_ReturnsId() {
            // given
            SalesChannel salesChannel = SalesChannelFixtures.newSalesChannel();
            Long expectedId = 1L;

            given(commandPort.persist(salesChannel)).willReturn(expectedId);

            // when
            Long result = sut.persist(salesChannel);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(commandPort).should().persist(salesChannel);
        }

        @Test
        @DisplayName("기존 판매채널을 수정하고 ID를 반환한다")
        void persist_ExistingSalesChannel_ReturnsId() {
            // given
            SalesChannel salesChannel = SalesChannelFixtures.activeSalesChannel(1L);
            Long expectedId = 1L;

            given(commandPort.persist(salesChannel)).willReturn(expectedId);

            // when
            Long result = sut.persist(salesChannel);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(commandPort).should().persist(salesChannel);
        }
    }
}
