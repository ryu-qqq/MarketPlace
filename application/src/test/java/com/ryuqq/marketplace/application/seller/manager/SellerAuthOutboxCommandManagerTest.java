package com.ryuqq.marketplace.application.seller.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.seller.port.out.command.SellerAuthOutboxCommandPort;
import com.ryuqq.marketplace.domain.seller.SellerFixtures;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerAuthOutbox;
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
@DisplayName("SellerAuthOutboxCommandManager 단위 테스트")
class SellerAuthOutboxCommandManagerTest {

    @InjectMocks private SellerAuthOutboxCommandManager sut;

    @Mock private SellerAuthOutboxCommandPort commandPort;

    @Nested
    @DisplayName("persist() - Outbox 저장")
    class PersistTest {

        @Test
        @DisplayName("SellerAuthOutbox를 저장하고 ID를 반환한다")
        void persist_ReturnsOutboxId() {
            // given
            SellerAuthOutbox outbox = SellerFixtures.newSellerAuthOutbox();
            Long expectedId = 1L;

            given(commandPort.persist(outbox)).willReturn(expectedId);

            // when
            Long result = sut.persist(outbox);

            // then
            assertThat(result).isEqualTo(expectedId);
            then(commandPort).should().persist(outbox);
        }
    }
}
