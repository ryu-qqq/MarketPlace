package com.ryuqq.marketplace.application.legacy.productgroup.service.command;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.legacy.product.internal.LegacyProductBulkCommandCoordinator;
import com.ryuqq.marketplace.application.legacy.productcontext.LegacyProductContextFixtures;
import com.ryuqq.marketplace.application.legacy.productcontext.factory.LegacyProductIdResolveFactory;
import com.ryuqq.marketplace.application.legacy.productgroup.LegacyProductGroupQueryFixtures;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.command.LegacyMarkOutOfStockCommand;
import com.ryuqq.marketplace.application.legacy.productgroup.port.in.query.LegacyProductQueryUseCase;
import com.ryuqq.marketplace.application.legacy.shared.dto.result.LegacyProductGroupDetailResult;
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
@DisplayName("LegacyProductMarkOutOfStockService 단위 테스트")
class LegacyProductMarkOutOfStockServiceTest {

    @InjectMocks private LegacyProductMarkOutOfStockService sut;

    @Mock private LegacyProductIdResolveFactory resolveFactory;
    @Mock private LegacyProductBulkCommandCoordinator bulkCommandCoordinator;
    @Mock private LegacyProductQueryUseCase legacyProductQueryUseCase;

    @Nested
    @DisplayName("execute() - 품절 처리 실행")
    class ExecuteTest {

        @Test
        @DisplayName("모든 Product를 품절 처리하고 상품 상세 결과를 반환한다")
        void execute_ValidCommand_MarksSoldOutAndReturnsDetail() {
            // given
            long productGroupId = LegacyProductContextFixtures.LEGACY_PRODUCT_GROUP_ID;
            LegacyMarkOutOfStockCommand command = new LegacyMarkOutOfStockCommand(productGroupId);
            Instant now = Instant.now();
            ResolvedLegacyProductIds resolved = LegacyProductContextFixtures.resolvedLegacyProductIds();
            LegacyProductGroupDetailResult expectedResult =
                    LegacyProductGroupQueryFixtures.detailResult(productGroupId);

            given(resolveFactory.resolve(productGroupId)).willReturn(resolved);
            given(resolveFactory.now()).willReturn(now);
            given(legacyProductQueryUseCase.execute(productGroupId)).willReturn(expectedResult);

            // when
            LegacyProductGroupDetailResult result = sut.execute(command);

            // then
            assertThat(result).isEqualTo(expectedResult);
            then(resolveFactory).should().resolve(productGroupId);
            then(resolveFactory).should().now();
            then(bulkCommandCoordinator)
                    .should()
                    .markSoldOutAll(resolved.resolvedProductGroupId(), now);
            then(legacyProductQueryUseCase).should().execute(productGroupId);
        }
    }
}
