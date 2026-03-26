package com.ryuqq.marketplace.application.seller.internal;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

import com.ryuqq.marketplace.application.seller.validator.SellerBusinessInfoValidator;
import com.ryuqq.marketplace.application.seller.validator.SellerContractValidator;
import com.ryuqq.marketplace.application.seller.validator.SellerCsValidator;
import com.ryuqq.marketplace.application.seller.validator.SellerSettlementValidator;
import com.ryuqq.marketplace.application.seller.validator.SellerValidator;
import com.ryuqq.marketplace.domain.seller.SellerFixtures;
import com.ryuqq.marketplace.domain.seller.aggregate.Seller;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerBusinessInfo;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerContract;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerCs;
import com.ryuqq.marketplace.domain.seller.aggregate.SellerSettlement;
import com.ryuqq.marketplace.domain.seller.exception.SellerNotFoundException;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import java.time.Instant;
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
@DisplayName("SellerUpdateCoordinator 단위 테스트")
class SellerUpdateCoordinatorTest {

    @InjectMocks private SellerUpdateCoordinator sut;

    @Mock private SellerValidator sellerValidator;
    @Mock private SellerBusinessInfoValidator businessInfoValidator;
    @Mock private SellerCsValidator csValidator;
    @Mock private SellerContractValidator contractValidator;
    @Mock private SellerSettlementValidator settlementValidator;
    @Mock private SellerCommandFacade commandFacade;

    @Nested
    @DisplayName("update() - 셀러 수정 조율")
    class UpdateTest {

        @Test
        @DisplayName("검증 통과 시 셀러 정보를 수정한다")
        void update_ValidationPassed_UpdatesSuccessfully() {
            // given
            Long sellerId = 1L;
            Instant changedAt = Instant.now();

            SellerUpdateBundle bundle =
                    new SellerUpdateBundle(
                            SellerId.of(sellerId),
                            SellerFixtures.sellerUpdateData(),
                            SellerFixtures.sellerBusinessInfoUpdateData(),
                            SellerFixtures.sellerCsUpdateData(),
                            SellerFixtures.sellerContractUpdateData(),
                            SellerFixtures.sellerSettlementUpdateData(),
                            changedAt);

            Seller seller = SellerFixtures.activeSeller(sellerId);
            SellerBusinessInfo businessInfo = SellerFixtures.activeSellerBusinessInfo();
            SellerCs sellerCs = SellerFixtures.activeSellerCs();
            SellerContract sellerContract = SellerFixtures.activeSellerContract();
            SellerSettlement sellerSettlement = SellerFixtures.activeSellerSettlement();

            given(sellerValidator.findExistingOrThrow(bundle.sellerId())).willReturn(seller);
            given(businessInfoValidator.findExistingOrThrow(bundle.sellerId()))
                    .willReturn(businessInfo);
            given(csValidator.findExistingOrThrow(bundle.sellerId())).willReturn(sellerCs);
            given(contractValidator.findExistingOrThrow(bundle.sellerId()))
                    .willReturn(sellerContract);
            given(settlementValidator.findExistingOrThrow(bundle.sellerId()))
                    .willReturn(sellerSettlement);

            // when
            sut.update(bundle);

            // then
            then(sellerValidator).should().findExistingOrThrow(bundle.sellerId());
            then(businessInfoValidator).should().findExistingOrThrow(bundle.sellerId());
            then(csValidator).should().findExistingOrThrow(bundle.sellerId());
            then(contractValidator).should().findExistingOrThrow(bundle.sellerId());
            then(settlementValidator).should().findExistingOrThrow(bundle.sellerId());
            then(commandFacade).should().updateSeller(bundle);
        }

        @Test
        @DisplayName("존재하지 않는 셀러 수정 시 예외가 발생한다")
        void update_NonExistingSeller_ThrowsException() {
            // given
            Long sellerId = 999L;
            Instant changedAt = Instant.now();

            SellerUpdateBundle bundle =
                    new SellerUpdateBundle(
                            SellerId.of(sellerId),
                            SellerFixtures.sellerUpdateData(),
                            SellerFixtures.sellerBusinessInfoUpdateData(),
                            SellerFixtures.sellerCsUpdateData(),
                            SellerFixtures.sellerContractUpdateData(),
                            SellerFixtures.sellerSettlementUpdateData(),
                            changedAt);

            given(sellerValidator.findExistingOrThrow(bundle.sellerId()))
                    .willThrow(new SellerNotFoundException(sellerId));

            // when & then
            assertThatThrownBy(() -> sut.update(bundle))
                    .isInstanceOf(SellerNotFoundException.class);

            then(commandFacade).should(never()).updateSeller(bundle);
        }
    }
}
