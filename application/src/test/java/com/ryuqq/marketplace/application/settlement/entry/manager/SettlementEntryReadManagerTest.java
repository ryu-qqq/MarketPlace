package com.ryuqq.marketplace.application.settlement.entry.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.settlement.entry.port.out.query.SettlementEntryQueryPort;
import com.ryuqq.marketplace.domain.settlement.entry.SettlementEntryFixtures;
import com.ryuqq.marketplace.domain.settlement.entry.aggregate.SettlementEntry;
import com.ryuqq.marketplace.domain.settlement.entry.exception.SettlementEntryNotFoundException;
import com.ryuqq.marketplace.domain.settlement.entry.id.SettlementEntryId;
import com.ryuqq.marketplace.domain.settlement.entry.vo.EntryStatus;
import java.time.Instant;
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
@DisplayName("SettlementEntryReadManager 단위 테스트")
class SettlementEntryReadManagerTest {

    @InjectMocks private SettlementEntryReadManager sut;

    @Mock private SettlementEntryQueryPort queryPort;

    @Nested
    @DisplayName("getById() - ID로 SettlementEntry 조회")
    class GetByIdTest {

        @Test
        @DisplayName("존재하는 ID로 SettlementEntry를 반환한다")
        void getById_ExistingId_ReturnsEntry() {
            // given
            SettlementEntryId id = SettlementEntryId.generate();
            SettlementEntry expected = SettlementEntryFixtures.salesEntry();

            given(queryPort.findById(id)).willReturn(Optional.of(expected));

            // when
            SettlementEntry result = sut.getById(id);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 SettlementEntryNotFoundException이 발생한다")
        void getById_NonExistingId_ThrowsSettlementEntryNotFoundException() {
            // given
            SettlementEntryId id = SettlementEntryId.generate();

            given(queryPort.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getById(id))
                    .isInstanceOf(SettlementEntryNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("findConfirmableEntries() - 확정 가능한 Entry 목록 조회")
    class FindConfirmableEntriesTest {

        @Test
        @DisplayName("cutoffTime 이전의 PENDING Entry 목록을 반환한다")
        void findConfirmableEntries_ValidParams_ReturnsEntryList() {
            // given
            Instant cutoffTime = Instant.now();
            int limit = 100;
            List<SettlementEntry> expected = List.of(SettlementEntryFixtures.salesEntry());

            given(queryPort.findConfirmableEntries(cutoffTime, limit)).willReturn(expected);

            // when
            List<SettlementEntry> result = sut.findConfirmableEntries(cutoffTime, limit);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("확정 가능한 Entry가 없으면 빈 목록을 반환한다")
        void findConfirmableEntries_NoEntries_ReturnsEmptyList() {
            // given
            Instant cutoffTime = Instant.now();
            int limit = 100;

            given(queryPort.findConfirmableEntries(cutoffTime, limit)).willReturn(List.of());

            // when
            List<SettlementEntry> result = sut.findConfirmableEntries(cutoffTime, limit);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findBySellerIdAndStatus() - 셀러 ID + 상태로 Entry 목록 조회")
    class FindBySellerIdAndStatusTest {

        @Test
        @DisplayName("셀러 ID와 상태로 Entry 목록을 반환한다")
        void findBySellerIdAndStatus_ValidParams_ReturnsEntryList() {
            // given
            long sellerId = SettlementEntryFixtures.DEFAULT_SELLER_ID;
            EntryStatus status = EntryStatus.CONFIRMED;
            List<SettlementEntry> expected = List.of(SettlementEntryFixtures.confirmedSalesEntry());

            given(queryPort.findBySellerIdAndStatus(sellerId, status)).willReturn(expected);

            // when
            List<SettlementEntry> result = sut.findBySellerIdAndStatus(sellerId, status);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("해당하는 Entry가 없으면 빈 목록을 반환한다")
        void findBySellerIdAndStatus_NoEntries_ReturnsEmptyList() {
            // given
            long sellerId = 999L;
            EntryStatus status = EntryStatus.CONFIRMED;

            given(queryPort.findBySellerIdAndStatus(sellerId, status)).willReturn(List.of());

            // when
            List<SettlementEntry> result = sut.findBySellerIdAndStatus(sellerId, status);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByOrderItemId() - 주문 항목 ID로 Entry 목록 조회")
    class FindByOrderItemIdTest {

        @Test
        @DisplayName("orderItemId에 해당하는 Entry 목록을 반환한다")
        void findByOrderItemId_ExistingOrderItemId_ReturnsEntryList() {
            // given
            String orderItemId = SettlementEntryFixtures.DEFAULT_ORDER_ITEM_ID;
            List<SettlementEntry> expected = List.of(SettlementEntryFixtures.salesEntry());

            given(queryPort.findByOrderItemId(orderItemId)).willReturn(expected);

            // when
            List<SettlementEntry> result = sut.findByOrderItemId(orderItemId);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("해당 orderItemId의 Entry가 없으면 빈 목록을 반환한다")
        void findByOrderItemId_NoEntries_ReturnsEmptyList() {
            // given
            String orderItemId = "not-existing-oi";

            given(queryPort.findByOrderItemId(orderItemId)).willReturn(List.of());

            // when
            List<SettlementEntry> result = sut.findByOrderItemId(orderItemId);

            // then
            assertThat(result).isEmpty();
        }
    }
}
