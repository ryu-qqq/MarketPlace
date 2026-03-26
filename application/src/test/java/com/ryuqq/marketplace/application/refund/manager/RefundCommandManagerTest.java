package com.ryuqq.marketplace.application.refund.manager;

import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.refund.port.out.command.RefundCommandPort;
import com.ryuqq.marketplace.domain.refund.RefundFixtures;
import com.ryuqq.marketplace.domain.refund.aggregate.RefundClaim;
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
@DisplayName("RefundCommandManager 단위 테스트")
class RefundCommandManagerTest {

    @InjectMocks private RefundCommandManager sut;

    @Mock private RefundCommandPort commandPort;

    @Nested
    @DisplayName("persist() - 단건 RefundClaim 저장")
    class PersistTest {

        @Test
        @DisplayName("RefundClaim을 CommandPort를 통해 저장한다")
        void persist_RefundClaim_DelegatesToCommandPort() {
            // given
            RefundClaim claim = RefundFixtures.requestedRefundClaim();

            // when
            sut.persist(claim);

            // then
            then(commandPort).should().persist(claim);
        }
    }

    @Nested
    @DisplayName("persistAll() - 다건 RefundClaim 저장")
    class PersistAllTest {

        @Test
        @DisplayName("RefundClaim 목록을 CommandPort를 통해 일괄 저장한다")
        void persistAll_RefundClaimList_DelegatesToCommandPort() {
            // given
            List<RefundClaim> claims =
                    List.of(
                            RefundFixtures.requestedRefundClaim(),
                            RefundFixtures.requestedRefundClaim());

            // when
            sut.persistAll(claims);

            // then
            then(commandPort).should().persist(claims.get(0));
            then(commandPort).should().persist(claims.get(1));
        }

        @Test
        @DisplayName("빈 목록이면 CommandPort를 호출하지 않는다")
        void persistAll_EmptyList_NoCommandPortInteraction() {
            // given
            List<RefundClaim> emptyList = List.of();

            // when
            sut.persistAll(emptyList);

            // then
            then(commandPort).shouldHaveNoInteractions();
        }
    }
}
