package com.ryuqq.marketplace.application.saleschannelcategory.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.saleschannelcategory.manager.SalesChannelCategoryReadManager;
import com.ryuqq.marketplace.domain.saleschannelcategory.SalesChannelCategoryFixtures;
import com.ryuqq.marketplace.domain.saleschannelcategory.aggregate.SalesChannelCategory;
import com.ryuqq.marketplace.domain.saleschannelcategory.exception.SalesChannelCategoryCodeDuplicateException;
import com.ryuqq.marketplace.domain.saleschannelcategory.exception.SalesChannelCategoryNotFoundException;
import com.ryuqq.marketplace.domain.saleschannelcategory.id.SalesChannelCategoryId;
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
@DisplayName("SalesChannelCategoryValidator 단위 테스트")
class SalesChannelCategoryValidatorTest {

    @InjectMocks private SalesChannelCategoryValidator sut;

    @Mock private SalesChannelCategoryReadManager readManager;

    @Nested
    @DisplayName("findExistingOrThrow() - 존재하는 SalesChannelCategory 조회")
    class FindExistingOrThrowTest {

        @Test
        @DisplayName("존재하는 ID로 SalesChannelCategory를 반환한다")
        void findExistingOrThrow_Exists_ReturnsCategory() {
            // given
            SalesChannelCategoryId id = SalesChannelCategoryId.of(1L);
            SalesChannelCategory expected =
                    SalesChannelCategoryFixtures.activeSalesChannelCategory();

            given(readManager.getById(id)).willReturn(expected);

            // when
            SalesChannelCategory result = sut.findExistingOrThrow(id);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 예외가 발생한다")
        void findExistingOrThrow_NotExists_ThrowsException() {
            // given
            SalesChannelCategoryId id = SalesChannelCategoryId.of(999L);

            given(readManager.getById(id))
                    .willThrow(new SalesChannelCategoryNotFoundException(id.value()));

            // when & then
            assertThatThrownBy(() -> sut.findExistingOrThrow(id))
                    .isInstanceOf(SalesChannelCategoryNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("validateExternalCodeNotDuplicate() - 외부 코드 중복 검증")
    class ValidateExternalCodeNotDuplicateTest {

        @Test
        @DisplayName("중복되지 않은 외부 코드는 예외 없이 통과한다")
        void validateExternalCodeNotDuplicate_NoDuplicate_NoException() {
            // given
            Long salesChannelId = 1L;
            String externalCategoryCode = "CAT001";

            given(readManager.existsBySalesChannelIdAndExternalCode(
                            salesChannelId, externalCategoryCode))
                    .willReturn(false);

            // when & then (no exception)
            sut.validateExternalCodeNotDuplicate(salesChannelId, externalCategoryCode);
        }

        @Test
        @DisplayName("중복된 외부 코드이면 예외가 발생한다")
        void validateExternalCodeNotDuplicate_Duplicate_ThrowsException() {
            // given
            Long salesChannelId = 1L;
            String externalCategoryCode = "CAT001";

            given(readManager.existsBySalesChannelIdAndExternalCode(
                            salesChannelId, externalCategoryCode))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(
                            () ->
                                    sut.validateExternalCodeNotDuplicate(
                                            salesChannelId, externalCategoryCode))
                    .isInstanceOf(SalesChannelCategoryCodeDuplicateException.class);
        }

        @Test
        @DisplayName("다른 판매채널에서는 같은 외부 코드를 사용할 수 있다")
        void validateExternalCodeNotDuplicate_DifferentChannel_NoException() {
            // given
            Long salesChannelId = 2L;
            String externalCategoryCode = "CAT001";

            given(readManager.existsBySalesChannelIdAndExternalCode(
                            salesChannelId, externalCategoryCode))
                    .willReturn(false);

            // when & then (no exception)
            sut.validateExternalCodeNotDuplicate(salesChannelId, externalCategoryCode);
        }
    }
}
