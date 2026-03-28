package com.ryuqq.marketplace.application.legacy.productgroup.service.command;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.legacy.product.internal.LegacyProductBulkCommandCoordinator;
import com.ryuqq.marketplace.application.legacy.productcontext.LegacyProductContextFixtures;
import com.ryuqq.marketplace.application.legacy.productcontext.factory.LegacyProductIdResolveFactory;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.command.LegacyUpdateDisplayStatusCommand;
import com.ryuqq.marketplace.domain.legacyconversion.vo.ResolvedLegacyProductIds;
import com.ryuqq.marketplace.domain.product.vo.ProductStatus;
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
@DisplayName("LegacyProductUpdateDisplayStatusService 단위 테스트")
class LegacyProductUpdateDisplayStatusServiceTest {

    @InjectMocks private LegacyProductUpdateDisplayStatusService sut;

    @Mock private LegacyProductIdResolveFactory resolveFactory;
    @Mock private LegacyProductBulkCommandCoordinator bulkCommandCoordinator;

    @Nested
    @DisplayName("execute() - 전시 상태 변경 실행")
    class ExecuteTest {

        @Test
        @DisplayName("displayYn=Y이면 모든 Product를 ACTIVE 상태로 변경한다")
        void execute_DisplayYnY_ChangesToActive() {
            // given
            long productGroupId = LegacyProductContextFixtures.LEGACY_PRODUCT_GROUP_ID;
            LegacyUpdateDisplayStatusCommand command =
                    new LegacyUpdateDisplayStatusCommand(productGroupId, "Y");
            Instant now = Instant.now();
            ResolvedLegacyProductIds resolved = LegacyProductContextFixtures.resolvedLegacyProductIds();

            given(resolveFactory.resolve(productGroupId)).willReturn(resolved);
            given(resolveFactory.now()).willReturn(now);

            // when
            sut.execute(command);

            // then
            then(resolveFactory).should().resolve(productGroupId);
            then(resolveFactory).should().now();
            then(bulkCommandCoordinator)
                    .should()
                    .changeStatusAll(resolved.resolvedProductGroupId(), ProductStatus.ACTIVE, now);
        }

        @Test
        @DisplayName("displayYn=N이면 모든 Product를 INACTIVE 상태로 변경한다")
        void execute_DisplayYnN_ChangesToInactive() {
            // given
            long productGroupId = LegacyProductContextFixtures.LEGACY_PRODUCT_GROUP_ID;
            LegacyUpdateDisplayStatusCommand command =
                    new LegacyUpdateDisplayStatusCommand(productGroupId, "N");
            Instant now = Instant.now();
            ResolvedLegacyProductIds resolved = LegacyProductContextFixtures.resolvedLegacyProductIds();

            given(resolveFactory.resolve(productGroupId)).willReturn(resolved);
            given(resolveFactory.now()).willReturn(now);

            // when
            sut.execute(command);

            // then
            then(bulkCommandCoordinator)
                    .should()
                    .changeStatusAll(resolved.resolvedProductGroupId(), ProductStatus.INACTIVE, now);
        }

        @Test
        @DisplayName("displayYn이 Y/N이 아닌 경우 INACTIVE 상태로 변경한다")
        void execute_DisplayYnOtherValue_ChangesToInactive() {
            // given
            long productGroupId = LegacyProductContextFixtures.LEGACY_PRODUCT_GROUP_ID;
            LegacyUpdateDisplayStatusCommand command =
                    new LegacyUpdateDisplayStatusCommand(productGroupId, "UNKNOWN");
            Instant now = Instant.now();
            ResolvedLegacyProductIds resolved = LegacyProductContextFixtures.resolvedLegacyProductIds();

            given(resolveFactory.resolve(productGroupId)).willReturn(resolved);
            given(resolveFactory.now()).willReturn(now);

            // when
            sut.execute(command);

            // then
            then(bulkCommandCoordinator)
                    .should()
                    .changeStatusAll(resolved.resolvedProductGroupId(), ProductStatus.INACTIVE, now);
        }
    }
}
