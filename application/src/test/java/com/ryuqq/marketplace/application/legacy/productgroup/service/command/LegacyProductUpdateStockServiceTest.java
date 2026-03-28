package com.ryuqq.marketplace.application.legacy.productgroup.service.command;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.legacy.product.internal.LegacyProductBulkCommandCoordinator;
import com.ryuqq.marketplace.application.legacy.productcontext.LegacyProductContextFixtures;
import com.ryuqq.marketplace.application.legacy.productcontext.factory.LegacyProductIdResolveFactory;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductStockCommand;
import com.ryuqq.marketplace.domain.legacyconversion.vo.ResolvedLegacyProductIds;
import java.time.Instant;
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
@DisplayName("LegacyProductUpdateStockService 단위 테스트")
class LegacyProductUpdateStockServiceTest {

    @InjectMocks private LegacyProductUpdateStockService sut;

    @Mock private LegacyProductIdResolveFactory resolveFactory;
    @Mock private LegacyProductBulkCommandCoordinator bulkCommandCoordinator;

    @Nested
    @DisplayName("execute() - 재고 수정 실행")
    class ExecuteTest {

        @Test
        @DisplayName("레거시 productId를 resolve하고 각 Product의 재고를 수정한다")
        void execute_ValidCommands_ResolvesAndUpdatesStock() {
            // given
            long productGroupId = LegacyProductContextFixtures.LEGACY_PRODUCT_GROUP_ID;
            List<UpdateProductStockCommand> commands =
                    LegacyProductContextFixtures.updateStockCommands();
            Instant now = Instant.now();
            ResolvedLegacyProductIds resolved = LegacyProductContextFixtures.resolvedLegacyProductIds();

            given(resolveFactory.resolve(productGroupId)).willReturn(resolved);
            given(resolveFactory.now()).willReturn(now);

            // when
            sut.execute(productGroupId, commands);

            // then
            then(resolveFactory).should().resolve(productGroupId);
            then(resolveFactory).should().now();
            then(bulkCommandCoordinator)
                    .should()
                    .updateStockByProductIds(
                            eq(resolved.resolvedProductGroupId()), any(), eq(now));
        }

        @Test
        @DisplayName("빈 재고 커맨드 목록도 정상 처리한다")
        void execute_EmptyCommands_ProcessesNormally() {
            // given
            long productGroupId = LegacyProductContextFixtures.LEGACY_PRODUCT_GROUP_ID;
            List<UpdateProductStockCommand> commands = List.of();
            Instant now = Instant.now();
            ResolvedLegacyProductIds resolved =
                    LegacyProductContextFixtures.resolvedLegacyProductIdsEmpty();

            given(resolveFactory.resolve(productGroupId)).willReturn(resolved);
            given(resolveFactory.now()).willReturn(now);

            // when
            sut.execute(productGroupId, commands);

            // then
            then(bulkCommandCoordinator)
                    .should()
                    .updateStockByProductIds(
                            eq(resolved.resolvedProductGroupId()), any(), eq(now));
        }
    }
}
