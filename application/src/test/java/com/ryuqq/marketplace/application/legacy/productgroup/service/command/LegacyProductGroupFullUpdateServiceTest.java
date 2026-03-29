package com.ryuqq.marketplace.application.legacy.productgroup.service.command;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.legacy.productcontext.LegacyProductContextFixtures;
import com.ryuqq.marketplace.application.legacy.productcontext.factory.LegacyProductIdResolveFactory;
import com.ryuqq.marketplace.application.productgroup.ProductGroupCommandFixtures;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupUpdateBundle;
import com.ryuqq.marketplace.application.productgroup.dto.command.UpdateProductGroupFullCommand;
import com.ryuqq.marketplace.application.productgroup.factory.ProductGroupBundleFactory;
import com.ryuqq.marketplace.application.productgroup.internal.FullProductGroupUpdateCoordinator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyProductGroupFullUpdateService 단위 테스트")
class LegacyProductGroupFullUpdateServiceTest {

    @InjectMocks private LegacyProductGroupFullUpdateService sut;

    @Mock private LegacyProductIdResolveFactory resolveFactory;
    @Mock private ProductGroupBundleFactory bundleFactory;
    @Mock private FullProductGroupUpdateCoordinator coordinator;

    @Nested
    @DisplayName("execute() - 상품그룹 전체 수정 실행")
    class ExecuteTest {

        @Test
        @DisplayName("Factory에서 PK resolve 후 BundleFactory로 번들 생성하고 Coordinator에 위임한다")
        void execute_ValidCommand_ResolvesAndUpdates() {
            // given
            UpdateProductGroupFullCommand originalCommand =
                    ProductGroupCommandFixtures.updateFullCommand(
                            LegacyProductContextFixtures.LEGACY_PRODUCT_GROUP_ID);
            UpdateProductGroupFullCommand resolvedCommand =
                    LegacyProductContextFixtures.updateProductGroupFullCommand();
            ProductGroupUpdateBundle bundle = Mockito.mock(ProductGroupUpdateBundle.class);

            given(resolveFactory.resolveUpdateFullCommand(originalCommand))
                    .willReturn(resolvedCommand);
            given(bundleFactory.createUpdateBundle(resolvedCommand)).willReturn(bundle);

            // when
            sut.execute(originalCommand);

            // then
            then(resolveFactory).should().resolveUpdateFullCommand(originalCommand);
            then(bundleFactory).should().createUpdateBundle(resolvedCommand);
            then(coordinator).should().update(bundle);
        }

        @Test
        @DisplayName("resolve된 Command의 productGroupId가 내부 PK로 변환되어 처리된다")
        void execute_LegacyCommand_UsesResolvedInternalIds() {
            // given
            UpdateProductGroupFullCommand originalCommand =
                    ProductGroupCommandFixtures.updateFullCommand(
                            LegacyProductContextFixtures.LEGACY_PRODUCT_GROUP_ID);
            UpdateProductGroupFullCommand resolvedCommand =
                    LegacyProductContextFixtures.updateProductGroupFullCommand();
            resolvedCommand =
                    new UpdateProductGroupFullCommand(
                            LegacyProductContextFixtures.INTERNAL_PRODUCT_GROUP_ID,
                            resolvedCommand.productGroupName(),
                            resolvedCommand.brandId(),
                            resolvedCommand.categoryId(),
                            resolvedCommand.shippingPolicyId(),
                            resolvedCommand.refundPolicyId(),
                            resolvedCommand.optionType(),
                            resolvedCommand.images(),
                            resolvedCommand.optionGroups(),
                            resolvedCommand.products(),
                            resolvedCommand.description(),
                            resolvedCommand.notice());
            ProductGroupUpdateBundle bundle = Mockito.mock(ProductGroupUpdateBundle.class);

            given(resolveFactory.resolveUpdateFullCommand(originalCommand))
                    .willReturn(resolvedCommand);
            given(bundleFactory.createUpdateBundle(resolvedCommand)).willReturn(bundle);

            // when
            sut.execute(originalCommand);

            // then
            then(bundleFactory).should().createUpdateBundle(resolvedCommand);
            then(coordinator).should().update(bundle);
        }
    }
}
