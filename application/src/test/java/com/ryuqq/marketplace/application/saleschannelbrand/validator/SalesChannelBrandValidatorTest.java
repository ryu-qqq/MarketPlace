package com.ryuqq.marketplace.application.saleschannelbrand.validator;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.saleschannelbrand.manager.SalesChannelBrandReadManager;
import com.ryuqq.marketplace.domain.saleschannelbrand.SalesChannelBrandFixtures;
import com.ryuqq.marketplace.domain.saleschannelbrand.aggregate.SalesChannelBrand;
import com.ryuqq.marketplace.domain.saleschannelbrand.exception.SalesChannelBrandCodeDuplicateException;
import com.ryuqq.marketplace.domain.saleschannelbrand.exception.SalesChannelBrandNotFoundException;
import com.ryuqq.marketplace.domain.saleschannelbrand.id.SalesChannelBrandId;
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
@DisplayName("SalesChannelBrandValidator 단위 테스트")
class SalesChannelBrandValidatorTest {

    @InjectMocks private SalesChannelBrandValidator sut;

    @Mock private SalesChannelBrandReadManager readManager;

    @Nested
    @DisplayName("findExistingOrThrow() - 존재하는 SalesChannelBrand 조회")
    class FindExistingOrThrowTest {

        @Test
        @DisplayName("존재하는 ID로 SalesChannelBrand를 반환한다")
        void findExistingOrThrow_Exists_ReturnsBrand() {
            // given
            SalesChannelBrandId id = SalesChannelBrandId.of(1L);
            SalesChannelBrand expected = SalesChannelBrandFixtures.activeSalesChannelBrand();

            given(readManager.getById(id)).willReturn(expected);

            // when
            SalesChannelBrand result = sut.findExistingOrThrow(id);

            // then
            assertThat(result).isEqualTo(expected);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 예외가 발생한다")
        void findExistingOrThrow_NotExists_ThrowsException() {
            // given
            SalesChannelBrandId id = SalesChannelBrandId.of(999L);

            given(readManager.getById(id))
                    .willThrow(new SalesChannelBrandNotFoundException(id.value()));

            // when & then
            assertThatThrownBy(() -> sut.findExistingOrThrow(id))
                    .isInstanceOf(SalesChannelBrandNotFoundException.class);
        }
    }

    @Nested
    @DisplayName("validateExternalCodeNotDuplicate() - 외부 브랜드 코드 중복 검증")
    class ValidateExternalCodeNotDuplicateTest {

        @Test
        @DisplayName("중복되지 않은 외부 브랜드 코드는 예외 없이 통과한다")
        void validateExternalCodeNotDuplicate_NoDuplicate_NoException() {
            // given
            Long salesChannelId = 1L;
            String externalBrandCode = "BRAND-NEW";

            given(
                            readManager.existsBySalesChannelIdAndExternalCode(
                                    salesChannelId, externalBrandCode))
                    .willReturn(false);

            // when & then (no exception)
            sut.validateExternalCodeNotDuplicate(salesChannelId, externalBrandCode);
        }

        @Test
        @DisplayName("중복된 외부 브랜드 코드면 예외가 발생한다")
        void validateExternalCodeNotDuplicate_Duplicate_ThrowsException() {
            // given
            Long salesChannelId = 1L;
            String externalBrandCode = "BRAND-DUPLICATE";

            given(
                            readManager.existsBySalesChannelIdAndExternalCode(
                                    salesChannelId, externalBrandCode))
                    .willReturn(true);

            // when & then
            assertThatThrownBy(
                            () ->
                                    sut.validateExternalCodeNotDuplicate(
                                            salesChannelId, externalBrandCode))
                    .isInstanceOf(SalesChannelBrandCodeDuplicateException.class);
        }

        @Test
        @DisplayName("다른 판매채널에서는 같은 외부 브랜드 코드를 사용할 수 있다")
        void validateExternalCodeNotDuplicate_DifferentSalesChannel_NoException() {
            // given
            Long salesChannelId1 = 1L;
            Long salesChannelId2 = 2L;
            String externalBrandCode = "BRAND-001";

            given(
                            readManager.existsBySalesChannelIdAndExternalCode(
                                    salesChannelId2, externalBrandCode))
                    .willReturn(false);

            // when & then (no exception)
            sut.validateExternalCodeNotDuplicate(salesChannelId2, externalBrandCode);
        }
    }
}
