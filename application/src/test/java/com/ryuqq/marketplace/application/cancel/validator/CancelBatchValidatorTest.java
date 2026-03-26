package com.ryuqq.marketplace.application.cancel.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.cancel.manager.CancelReadManager;
import com.ryuqq.marketplace.domain.cancel.CancelFixtures;
import com.ryuqq.marketplace.domain.cancel.aggregate.Cancel;
import com.ryuqq.marketplace.domain.cancel.exception.CancelOwnershipMismatchException;
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
@DisplayName("CancelBatchValidator 단위 테스트")
class CancelBatchValidatorTest {

    @InjectMocks private CancelBatchValidator sut;

    @Mock private CancelReadManager cancelReadManager;

    @Nested
    @DisplayName("validateAndGet() - 취소 배치 검증 및 조회")
    class ValidateAndGetTest {

        @Test
        @DisplayName("요청한 cancelIds가 모두 조회되면 Cancel 목록을 반환한다")
        void validateAndGet_AllIdsFound_ReturnsCancelList() {
            // given
            String cancelId = "01900000-0000-7000-8000-000000000001";
            List<String> cancelIds = List.of(cancelId);
            Long sellerId = 10L;
            Cancel cancel = CancelFixtures.requestedCancel();

            given(cancelReadManager.findByIdIn(cancelIds, sellerId)).willReturn(List.of(cancel));

            // when
            List<Cancel> result = sut.validateAndGet(cancelIds, sellerId);

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0)).isEqualTo(cancel);
        }

        @Test
        @DisplayName("sellerId가 null이면 슈퍼어드민으로 전체 조회가 가능하다")
        void validateAndGet_NullSellerId_AllowsAllAccess() {
            // given
            String cancelId = "01900000-0000-7000-8000-000000000001";
            List<String> cancelIds = List.of(cancelId);
            Cancel cancel = CancelFixtures.requestedCancel();

            given(cancelReadManager.findByIdIn(cancelIds, null)).willReturn(List.of(cancel));

            // when
            List<Cancel> result = sut.validateAndGet(cancelIds, null);

            // then
            assertThat(result).hasSize(1);
        }

        @Test
        @DisplayName("조회된 건수가 요청 건수보다 적으면 CancelOwnershipMismatchException이 발생한다")
        void validateAndGet_SomeIdsNotFound_ThrowsCancelOwnershipMismatchException() {
            // given
            List<String> cancelIds =
                    List.of(
                            "01900000-0000-7000-8000-000000000001",
                            "01900000-0000-7000-8000-000000000002");
            Long sellerId = 10L;
            Cancel cancel = CancelFixtures.requestedCancel();

            given(cancelReadManager.findByIdIn(cancelIds, sellerId))
                    .willReturn(List.of(cancel)); // 1건만 반환 (2건 요청)

            // when & then
            assertThatThrownBy(() -> sut.validateAndGet(cancelIds, sellerId))
                    .isInstanceOf(CancelOwnershipMismatchException.class);
        }

        @Test
        @DisplayName("다른 셀러의 Cancel을 요청하면 CancelOwnershipMismatchException이 발생한다")
        void validateAndGet_WrongSellerId_ThrowsCancelOwnershipMismatchException() {
            // given
            String cancelId = "01900000-0000-7000-8000-000000000001";
            List<String> cancelIds = List.of(cancelId);
            Long wrongSellerId = 999L;

            given(cancelReadManager.findByIdIn(cancelIds, wrongSellerId))
                    .willReturn(List.of()); // 다른 셀러 소유이므로 빈 결과

            // when & then
            assertThatThrownBy(() -> sut.validateAndGet(cancelIds, wrongSellerId))
                    .isInstanceOf(CancelOwnershipMismatchException.class);
        }

        @Test
        @DisplayName("여러 건 중 일부만 다른 셀러 소유이면 CancelOwnershipMismatchException이 발생한다")
        void validateAndGet_PartialOwnershipMismatch_ThrowsException() {
            // given
            List<String> cancelIds =
                    List.of(
                            "01900000-0000-7000-8000-000000000001",
                            "01900000-0000-7000-8000-000000000002",
                            "01900000-0000-7000-8000-000000000003");
            Long sellerId = 10L;
            Cancel cancel1 = CancelFixtures.requestedCancel();
            Cancel cancel2 = CancelFixtures.requestedCancel();

            given(cancelReadManager.findByIdIn(cancelIds, sellerId))
                    .willReturn(List.of(cancel1, cancel2)); // 2건만 반환

            // when & then
            assertThatThrownBy(() -> sut.validateAndGet(cancelIds, sellerId))
                    .isInstanceOf(CancelOwnershipMismatchException.class);
        }
    }
}
