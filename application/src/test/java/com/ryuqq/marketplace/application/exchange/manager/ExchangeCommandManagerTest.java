package com.ryuqq.marketplace.application.exchange.manager;

import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.exchange.port.out.command.ExchangeCommandPort;
import com.ryuqq.marketplace.domain.exchange.ExchangeFixtures;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import java.util.List;
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
@DisplayName("ExchangeCommandManager 단위 테스트")
class ExchangeCommandManagerTest {

    @InjectMocks private ExchangeCommandManager sut;

    @Mock private ExchangeCommandPort commandPort;

    @Nested
    @DisplayName("persist() - ExchangeClaim 저장")
    class PersistTest {

        @Test
        @DisplayName("ExchangeClaim을 CommandPort에 위임하여 저장한다")
        void persist_DelegatesToCommandPort() {
            // given
            ExchangeClaim claim = ExchangeFixtures.newExchangeClaim();

            // when
            sut.persist(claim);

            // then
            then(commandPort).should().persist(claim);
        }
    }

    @Nested
    @DisplayName("persistAll() - ExchangeClaim 목록 저장")
    class PersistAllTest {

        @Test
        @DisplayName("ExchangeClaim 목록을 각각 저장한다")
        void persistAll_SavesAllClaims() {
            // given
            ExchangeClaim claim1 = ExchangeFixtures.newExchangeClaim();
            ExchangeClaim claim2 = ExchangeFixtures.requestedExchangeClaim();
            List<ExchangeClaim> claims = List.of(claim1, claim2);

            // when
            sut.persistAll(claims);

            // then
            then(commandPort).should().persist(claim1);
            then(commandPort).should().persist(claim2);
        }
    }
}
