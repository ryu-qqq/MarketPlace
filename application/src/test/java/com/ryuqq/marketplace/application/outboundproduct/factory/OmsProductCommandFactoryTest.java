package com.ryuqq.marketplace.application.outboundproduct.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.common.dto.command.StatusChangeContext;
import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.domain.outboundsync.OutboundSyncOutboxFixtures;
import java.time.Instant;
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
@DisplayName("OmsProductCommandFactory 단위 테스트")
class OmsProductCommandFactoryTest {

    @InjectMocks private OmsProductCommandFactory sut;

    @Mock private TimeProvider timeProvider;

    @Nested
    @DisplayName("createRetryContext() - RetryContext 생성")
    class CreateRetryContextTest {

        @Test
        @DisplayName("outboxId와 현재 시간으로 StatusChangeContext를 생성한다")
        void createRetryContext_ValidOutboxId_ReturnsStatusChangeContext() {
            // given
            long outboxId = OutboundSyncOutboxFixtures.DEFAULT_ID;
            Instant now = Instant.now();

            given(timeProvider.now()).willReturn(now);

            // when
            StatusChangeContext<Long> result = sut.createRetryContext(outboxId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.id()).isEqualTo(outboxId);
            assertThat(result.changedAt()).isEqualTo(now);
        }

        @Test
        @DisplayName("다른 outboxId로 생성해도 각각 올바른 id를 담는다")
        void createRetryContext_DifferentOutboxIds_EachHasCorrectId() {
            // given
            long outboxId1 = 10L;
            long outboxId2 = 20L;
            Instant now = Instant.now();

            given(timeProvider.now()).willReturn(now);

            // when
            StatusChangeContext<Long> result1 = sut.createRetryContext(outboxId1);
            StatusChangeContext<Long> result2 = sut.createRetryContext(outboxId2);

            // then
            assertThat(result1.id()).isEqualTo(outboxId1);
            assertThat(result2.id()).isEqualTo(outboxId2);
            assertThat(result1.changedAt()).isEqualTo(now);
            assertThat(result2.changedAt()).isEqualTo(now);
        }
    }
}
