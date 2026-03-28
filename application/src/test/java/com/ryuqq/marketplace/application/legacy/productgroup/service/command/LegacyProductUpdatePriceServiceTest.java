package com.ryuqq.marketplace.application.legacy.productgroup.service.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.legacy.product.internal.LegacyProductBulkCommandCoordinator;
import com.ryuqq.marketplace.application.legacy.productcontext.LegacyProductContextFixtures;
import com.ryuqq.marketplace.application.legacy.productcontext.factory.LegacyProductIdResolveFactory;
import com.ryuqq.marketplace.domain.legacyconversion.vo.ResolvedLegacyProductIds;
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
@DisplayName("LegacyProductUpdatePriceService 단위 테스트")
class LegacyProductUpdatePriceServiceTest {

    @InjectMocks private LegacyProductUpdatePriceService sut;

    @Mock private LegacyProductIdResolveFactory resolveFactory;
    @Mock private LegacyProductBulkCommandCoordinator bulkCommandCoordinator;

    @Nested
    @DisplayName("execute() - 상품 가격 수정 실행")
    class ExecuteTest {

        @Test
        @DisplayName("레거시 productGroupId를 resolve하고 모든 Product 가격을 일괄 변경한다")
        void execute_ValidParams_ResolvesAndUpdatesPrice() {
            // given
            long productGroupId = LegacyProductContextFixtures.LEGACY_PRODUCT_GROUP_ID;
            long regularPrice = 10000L;
            long currentPrice = 9000L;
            Instant now = Instant.now();
            ResolvedLegacyProductIds resolved = LegacyProductContextFixtures.resolvedLegacyProductIds();

            given(resolveFactory.resolve(productGroupId)).willReturn(resolved);
            given(resolveFactory.now()).willReturn(now);

            // when
            sut.execute(productGroupId, regularPrice, currentPrice);

            // then
            then(resolveFactory).should().resolve(productGroupId);
            then(resolveFactory).should().now();
            then(bulkCommandCoordinator)
                    .should()
                    .updatePriceAll(
                            eq(resolved.resolvedProductGroupId()),
                            any(),
                            any(),
                            eq(now));
        }

        @Test
        @DisplayName("동일한 정가와 판매가로도 정상 처리한다")
        void execute_SamePrices_ProcessesNormally() {
            // given
            long productGroupId = LegacyProductContextFixtures.LEGACY_PRODUCT_GROUP_ID;
            long samePrice = 8000L;
            Instant now = Instant.now();
            ResolvedLegacyProductIds resolved = LegacyProductContextFixtures.resolvedLegacyProductIds();

            given(resolveFactory.resolve(productGroupId)).willReturn(resolved);
            given(resolveFactory.now()).willReturn(now);

            // when
            sut.execute(productGroupId, samePrice, samePrice);

            // then
            then(bulkCommandCoordinator)
                    .should()
                    .updatePriceAll(
                            eq(resolved.resolvedProductGroupId()),
                            any(),
                            any(),
                            eq(now));
        }
    }
}
