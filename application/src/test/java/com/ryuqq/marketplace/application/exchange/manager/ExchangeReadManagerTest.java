package com.ryuqq.marketplace.application.exchange.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.exchange.port.out.query.ExchangeQueryPort;
import com.ryuqq.marketplace.domain.exchange.ExchangeFixtures;
import com.ryuqq.marketplace.domain.exchange.aggregate.ExchangeClaim;
import com.ryuqq.marketplace.domain.exchange.exception.ExchangeNotFoundException;
import com.ryuqq.marketplace.domain.exchange.id.ExchangeClaimId;
import com.ryuqq.marketplace.domain.exchange.query.ExchangeSearchCriteria;
import com.ryuqq.marketplace.domain.order.id.OrderItemId;
import java.util.List;
import java.util.Optional;
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
@DisplayName("ExchangeReadManager 단위 테스트")
class ExchangeReadManagerTest {

    @InjectMocks private ExchangeReadManager sut;

    @Mock private ExchangeQueryPort queryPort;

    @Nested
    @DisplayName("getById() - ID로 조회")
    class GetByIdTest {

        @Test
        @DisplayName("존재하는 ID로 ExchangeClaim을 조회한다")
        void getById_Exists_ReturnsExchangeClaim() {
            // given
            ExchangeClaimId id = ExchangeFixtures.defaultExchangeClaimId();
            ExchangeClaim expected = ExchangeFixtures.requestedExchangeClaim();

            given(queryPort.findById(id)).willReturn(Optional.of(expected));

            // when
            ExchangeClaim result = sut.getById(id);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().findById(id);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 ExchangeNotFoundException이 발생한다")
        void getById_NotExists_ThrowsExchangeNotFoundException() {
            // given
            ExchangeClaimId id =
                    ExchangeFixtures.exchangeClaimId("99900000-0000-7000-0000-000000000099");

            given(queryPort.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getById(id)).isInstanceOf(ExchangeNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findByOrderItemId() - OrderItemId로 조회")
    class FindByOrderItemIdTest {

        @Test
        @DisplayName("OrderItemId로 ExchangeClaim을 조회한다")
        void findByOrderItemId_Exists_ReturnsExchangeClaim() {
            // given
            OrderItemId orderItemId = ExchangeFixtures.defaultOrderItemId();
            ExchangeClaim expected = ExchangeFixtures.requestedExchangeClaim();

            given(queryPort.findByOrderItemId(orderItemId)).willReturn(Optional.of(expected));

            // when
            Optional<ExchangeClaim> result = sut.findByOrderItemId(orderItemId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expected);
        }

        @Test
        @DisplayName("해당 OrderItemId에 교환이 없으면 빈 Optional을 반환한다")
        void findByOrderItemId_NotExists_ReturnsEmpty() {
            // given
            OrderItemId orderItemId = ExchangeFixtures.defaultOrderItemId();

            given(queryPort.findByOrderItemId(orderItemId)).willReturn(Optional.empty());

            // when
            Optional<ExchangeClaim> result = sut.findByOrderItemId(orderItemId);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByIdIn() - ID 목록으로 조회")
    class FindByIdInTest {

        @Test
        @DisplayName("ID 목록과 sellerId로 ExchangeClaim 목록을 조회한다")
        void findByIdIn_ReturnsExchangeClaims() {
            // given
            List<String> claimIds = List.of("01900000-0000-7000-0000-000000000001");
            Long sellerId = 100L;
            List<ExchangeClaim> expected = List.of(ExchangeFixtures.requestedExchangeClaim());

            given(queryPort.findByIdIn(claimIds, sellerId)).willReturn(expected);

            // when
            List<ExchangeClaim> result = sut.findByIdIn(claimIds, sellerId);

            // then
            assertThat(result).hasSize(1);
            then(queryPort).should().findByIdIn(claimIds, sellerId);
        }
    }

    @Nested
    @DisplayName("findByCriteria() - 검색 조건으로 조회")
    class FindByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 ExchangeClaim 목록을 조회한다")
        void findByCriteria_ReturnsClaims() {
            // given
            ExchangeSearchCriteria criteria = ExchangeSearchCriteria.defaultCriteria();
            List<ExchangeClaim> expected = List.of(ExchangeFixtures.requestedExchangeClaim());

            given(queryPort.findByCriteria(criteria)).willReturn(expected);

            // when
            List<ExchangeClaim> result = sut.findByCriteria(criteria);

            // then
            assertThat(result).hasSize(1);
            then(queryPort).should().findByCriteria(criteria);
        }
    }

    @Nested
    @DisplayName("countByCriteria() - 검색 조건으로 카운트")
    class CountByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 ExchangeClaim 수를 반환한다")
        void countByCriteria_ReturnsCount() {
            // given
            ExchangeSearchCriteria criteria = ExchangeSearchCriteria.defaultCriteria();
            long expected = 5L;

            given(queryPort.countByCriteria(criteria)).willReturn(expected);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(expected);
            then(queryPort).should().countByCriteria(criteria);
        }
    }
}
