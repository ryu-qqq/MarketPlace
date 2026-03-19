package com.ryuqq.marketplace.application.settlement.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.settlement.port.out.query.SettlementQueryPort;
import com.ryuqq.marketplace.domain.settlement.SettlementFixtures;
import com.ryuqq.marketplace.domain.settlement.aggregate.Settlement;
import com.ryuqq.marketplace.domain.settlement.exception.SettlementException;
import com.ryuqq.marketplace.domain.settlement.id.SettlementId;
import com.ryuqq.marketplace.domain.settlement.vo.SettlementStatus;
import java.time.LocalDate;
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
@DisplayName("SettlementReadManager 단위 테스트")
class SettlementReadManagerTest {

    @InjectMocks private SettlementReadManager sut;

    @Mock private SettlementQueryPort queryPort;

    @Nested
    @DisplayName("getById() - ID로 Settlement 조회")
    class GetByIdTest {

        @Test
        @DisplayName("존재하는 ID로 Settlement를 반환한다")
        void getById_ExistingId_ReturnsSettlement() {
            // given
            SettlementId id = SettlementFixtures.defaultSettlementId();
            Settlement expected = SettlementFixtures.calculatingSettlement();

            given(queryPort.findById(id)).willReturn(Optional.of(expected));

            // when
            Settlement result = sut.getById(id);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 SettlementException이 발생한다")
        void getById_NonExistingId_ThrowsSettlementException() {
            // given
            SettlementId id = SettlementId.of("00000000-0000-7000-8000-000000000099");

            given(queryPort.findById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getById(id)).isInstanceOf(SettlementException.class);
        }
    }

    @Nested
    @DisplayName("findBySellerIdAndPeriod() - 셀러 ID + 기간으로 Settlement 조회")
    class FindBySellerIdAndPeriodTest {

        @Test
        @DisplayName("해당 셀러의 기간 내 Settlement가 존재하면 Optional로 반환한다")
        void findBySellerIdAndPeriod_ExistingSettlement_ReturnsOptional() {
            // given
            long sellerId = 1L;
            LocalDate startDate = LocalDate.now().minusDays(7);
            LocalDate endDate = LocalDate.now();
            Settlement expected = SettlementFixtures.calculatingSettlement();

            given(queryPort.findBySellerIdAndPeriod(sellerId, startDate, endDate))
                    .willReturn(Optional.of(expected));

            // when
            Optional<Settlement> result = sut.findBySellerIdAndPeriod(sellerId, startDate, endDate);

            // then
            assertThat(result).isPresent();
            assertThat(result.get()).isEqualTo(expected);
        }

        @Test
        @DisplayName("해당 기간에 Settlement가 없으면 빈 Optional을 반환한다")
        void findBySellerIdAndPeriod_NoSettlement_ReturnsEmpty() {
            // given
            long sellerId = 1L;
            LocalDate startDate = LocalDate.now().minusDays(7);
            LocalDate endDate = LocalDate.now();

            given(queryPort.findBySellerIdAndPeriod(sellerId, startDate, endDate))
                    .willReturn(Optional.empty());

            // when
            Optional<Settlement> result = sut.findBySellerIdAndPeriod(sellerId, startDate, endDate);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findBySellerIdAndStatus() - 셀러 ID + 상태로 Settlement 목록 조회")
    class FindBySellerIdAndStatusTest {

        @Test
        @DisplayName("해당 셀러의 상태별 Settlement 목록을 반환한다")
        void findBySellerIdAndStatus_ValidParams_ReturnsSettlementList() {
            // given
            long sellerId = 1L;
            SettlementStatus status = SettlementStatus.CONFIRMED;
            List<Settlement> expected = List.of(SettlementFixtures.confirmedSettlement());

            given(queryPort.findBySellerIdAndStatus(sellerId, status)).willReturn(expected);

            // when
            List<Settlement> result = sut.findBySellerIdAndStatus(sellerId, status);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("해당하는 Settlement가 없으면 빈 목록을 반환한다")
        void findBySellerIdAndStatus_NoMatching_ReturnsEmptyList() {
            // given
            long sellerId = 1L;
            SettlementStatus status = SettlementStatus.COMPLETED;

            given(queryPort.findBySellerIdAndStatus(sellerId, status)).willReturn(List.of());

            // when
            List<Settlement> result = sut.findBySellerIdAndStatus(sellerId, status);

            // then
            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("findByStatus() - 전체 상태별 Settlement 목록 조회")
    class FindByStatusTest {

        @Test
        @DisplayName("전체 셀러 중 해당 상태의 Settlement 목록을 반환한다")
        void findByStatus_ValidStatus_ReturnsSettlementList() {
            // given
            SettlementStatus status = SettlementStatus.CALCULATING;
            List<Settlement> expected =
                    List.of(
                            SettlementFixtures.calculatingSettlement(),
                            SettlementFixtures.calculatingSettlement());

            given(queryPort.findByStatus(status)).willReturn(expected);

            // when
            List<Settlement> result = sut.findByStatus(status);

            // then
            assertThat(result).hasSize(2);
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("해당 상태의 Settlement가 없으면 빈 목록을 반환한다")
        void findByStatus_NoMatching_ReturnsEmptyList() {
            // given
            SettlementStatus status = SettlementStatus.HOLD;

            given(queryPort.findByStatus(status)).willReturn(List.of());

            // when
            List<Settlement> result = sut.findByStatus(status);

            // then
            assertThat(result).isEmpty();
        }
    }
}
