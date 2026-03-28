package com.ryuqq.marketplace.application.cancel.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.cancel.port.out.query.CancelQueryPort;
import com.ryuqq.marketplace.domain.cancel.CancelFixtures;
import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
import com.ryuqq.marketplace.domain.cancel.exception.CancelNotFoundException;
import com.ryuqq.marketplace.domain.cancel.id.CancelId;
import com.ryuqq.marketplace.domain.cancel.query.CancelSearchCriteria;
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
@DisplayName("CancelReadManager 단위 테스트")
class CancelReadManagerTest {

    @InjectMocks private CancelReadManager sut;

    @Mock private CancelQueryPort queryPort;

    @Nested
    @DisplayName("getById() - ID로 Cancel 조회")
    class GetByIdTest {

        @Test
        @DisplayName("존재하는 ID로 Cancel을 반환한다")
        void getById_ExistingId_ReturnsCancel() {
            // given
            CancelId id = CancelFixtures.defaultCancelId();
            Cancel expected = CancelFixtures.requestedCancel();

            given(queryPort.findById(id)).willReturn(Optional.of(expected));

            // when
            Cancel result = sut.getById(id);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 CancelNotFoundException이 발생한다")
        void getById_NonExistingId_ThrowsCancelNotFoundException() {
            // given
            CancelId id = CancelId.of("00000000-0000-7000-8000-000000000099");

            given(queryPort.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getById(id)).isInstanceOf(CancelNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findByCriteria() - 조건으로 Cancel 목록 조회")
    class FindByCriteriaTest {

        @Test
        @DisplayName("검색 조건으로 Cancel 목록을 반환한다")
        void findByCriteria_ValidCriteria_ReturnsCancelList() {
            // given
            CancelSearchCriteria criteria = CancelSearchCriteria.defaultCriteria();
            List<Cancel> expected = List.of(CancelFixtures.requestedCancel());

            given(queryPort.findByCriteria(criteria)).willReturn(expected);

            // when
            List<Cancel> result = sut.findByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("countByCriteria() - 조건으로 Cancel 건수 조회")
    class CountByCriteriaTest {

        @Test
        @DisplayName("검색 조건에 맞는 총 건수를 반환한다")
        void countByCriteria_ValidCriteria_ReturnsCount() {
            // given
            CancelSearchCriteria criteria = CancelSearchCriteria.defaultCriteria();
            long expectedCount = 5L;

            given(queryPort.countByCriteria(criteria)).willReturn(expectedCount);

            // when
            long result = sut.countByCriteria(criteria);

            // then
            assertThat(result).isEqualTo(expectedCount);
        }
    }

    @Nested
    @DisplayName("findByIdIn() - ID 목록으로 Cancel 조회")
    class FindByIdInTest {

        @Test
        @DisplayName("cancelId 목록과 sellerId로 Cancel 목록을 반환한다")
        void findByIdIn_ValidIds_ReturnsCancelList() {
            // given
            List<String> cancelIds = List.of("01900000-0000-7000-8000-000000000001");
            Long sellerId = 10L;
            List<Cancel> expected = List.of(CancelFixtures.requestedCancel());

            given(queryPort.findByIdIn(cancelIds, sellerId)).willReturn(expected);

            // when
            List<Cancel> result = sut.findByIdIn(cancelIds, sellerId);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("sellerId가 null이면 슈퍼어드민으로 전체 조회한다")
        void findByIdIn_NullSellerId_ReturnsAllMatchingCancels() {
            // given
            List<String> cancelIds = List.of("01900000-0000-7000-8000-000000000001");
            List<Cancel> expected = List.of(CancelFixtures.requestedCancel());

            given(queryPort.findByIdIn(cancelIds, null)).willReturn(expected);

            // when
            List<Cancel> result = sut.findByIdIn(cancelIds, null);

            // then
            assertThat(result).isEqualTo(expected);
        }
    }

    @Nested
    @DisplayName("findByOrderItemId() - OrderItemId로 Cancel 조회")
    class FindByOrderItemIdTest {

        @Test
        @DisplayName("OrderItemId로 Cancel을 반환한다")
        void findByOrderItemId_ExistingOrderItemId_ReturnsCancel() {
            // given
            OrderItemId orderItemId = OrderItemId.of(1001L);
            Cancel expected = CancelFixtures.requestedCancel();

            given(queryPort.findByOrderItemId(orderItemId)).willReturn(Optional.of(expected));

            // when
            Optional<Cancel> result = sut.findByOrderItemId(orderItemId);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expected);
        }

        @Test
        @DisplayName("OrderItemId에 해당하는 Cancel이 없으면 빈 Optional을 반환한다")
        void findByOrderItemId_NonExistingOrderItemId_ReturnsEmpty() {
            // given
            OrderItemId orderItemId = OrderItemId.of(9999L);

            given(queryPort.findByOrderItemId(orderItemId)).willReturn(Optional.empty());

            // when
            Optional<Cancel> result = sut.findByOrderItemId(orderItemId);

            // then
            assertThat(result).isEmpty();
        }
    }
}
