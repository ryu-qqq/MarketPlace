package com.ryuqq.marketplace.application.seller.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.never;

import com.ryuqq.marketplace.application.seller.validator.SellerBusinessInfoValidator;
import com.ryuqq.marketplace.application.seller.validator.SellerValidator;
import com.ryuqq.marketplace.domain.seller.SellerFixtures;
import com.ryuqq.marketplace.domain.seller.exception.SellerErrorCode;
import com.ryuqq.marketplace.domain.seller.exception.SellerException;
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
@DisplayName("SellerRegistrationCoordinator 단위 테스트")
class SellerRegistrationCoordinatorTest {

    @InjectMocks private SellerRegistrationCoordinator sut;

    @Mock private SellerValidator sellerValidator;
    @Mock private SellerBusinessInfoValidator businessInfoValidator;
    @Mock private SellerCommandFacade commandFacade;

    @Nested
    @DisplayName("register() - 셀러 등록 조율")
    class RegisterTest {

        @Test
        @DisplayName("검증 통과 시 셀러를 등록하고 ID를 반환한다")
        void register_ValidationPassed_ReturnsSellerId() {
            // given
            SellerRegistrationBundle bundle =
                    new SellerRegistrationBundle(
                            SellerFixtures.newSeller(), SellerFixtures.newSellerBusinessInfo());
            Long expectedSellerId = 1L;

            given(commandFacade.registerSeller(bundle)).willReturn(expectedSellerId);

            // when
            Long result = sut.register(bundle);

            // then
            assertThat(result).isEqualTo(expectedSellerId);
            then(sellerValidator).should().validateSellerNameNotDuplicate(bundle.sellerNameValue());
            then(businessInfoValidator)
                    .should()
                    .validateRegistrationNumberNotDuplicate(bundle.registrationNumberValue());
            then(commandFacade).should().registerSeller(bundle);
        }

        @Test
        @DisplayName("셀러명 중복 시 예외가 발생하고 등록되지 않는다")
        void register_DuplicateSellerName_ThrowsException() {
            // given
            SellerRegistrationBundle bundle =
                    new SellerRegistrationBundle(
                            SellerFixtures.newSeller(), SellerFixtures.newSellerBusinessInfo());

            willThrow(new SellerException(SellerErrorCode.SELLER_NAME_DUPLICATE))
                    .given(sellerValidator)
                    .validateSellerNameNotDuplicate(bundle.sellerNameValue());

            // when & then
            assertThatThrownBy(() -> sut.register(bundle)).isInstanceOf(SellerException.class);

            then(commandFacade).should(never()).registerSeller(bundle);
        }

        @Test
        @DisplayName("사업자등록번호 중복 시 예외가 발생하고 등록되지 않는다")
        void register_DuplicateRegistrationNumber_ThrowsException() {
            // given
            SellerRegistrationBundle bundle =
                    new SellerRegistrationBundle(
                            SellerFixtures.newSeller(), SellerFixtures.newSellerBusinessInfo());

            willThrow(new SellerException(SellerErrorCode.REGISTRATION_NUMBER_DUPLICATE))
                    .given(businessInfoValidator)
                    .validateRegistrationNumberNotDuplicate(bundle.registrationNumberValue());

            // when & then
            assertThatThrownBy(() -> sut.register(bundle)).isInstanceOf(SellerException.class);

            then(commandFacade).should(never()).registerSeller(bundle);
        }
    }
}
