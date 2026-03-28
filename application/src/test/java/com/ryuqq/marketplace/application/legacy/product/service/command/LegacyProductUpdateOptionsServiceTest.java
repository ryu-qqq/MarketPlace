package com.ryuqq.marketplace.application.legacy.product.service.command;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.legacy.productcontext.LegacyProductContextFixtures;
import com.ryuqq.marketplace.application.legacy.productcontext.factory.LegacyProductIdResolveFactory;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductsCommand;
import com.ryuqq.marketplace.application.product.port.in.command.UpdateProductsUseCase;
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
@DisplayName("LegacyProductUpdateOptionsService 단위 테스트")
class LegacyProductUpdateOptionsServiceTest {

    @InjectMocks private LegacyProductUpdateOptionsService sut;

    @Mock private LegacyProductIdResolveFactory resolveFactory;
    @Mock private UpdateProductsUseCase updateProductsUseCase;

    @Nested
    @DisplayName("execute() - 상품 옵션/SKU 수정 실행")
    class ExecuteTest {

        @Test
        @DisplayName("Factory에서 PK resolve 후 표준 UseCase에 위임한다")
        void execute_ValidCommand_ResolvesAndDelegatesToUseCase() {
            // given
            UpdateProductsCommand originalCommand =
                    LegacyProductContextFixtures.updateProductsCommand();
            UpdateProductsCommand resolvedCommand =
                    new UpdateProductsCommand(
                            LegacyProductContextFixtures.INTERNAL_PRODUCT_GROUP_ID,
                            List.of(),
                            List.of(
                                    new UpdateProductsCommand.ProductData(
                                            LegacyProductContextFixtures.INTERNAL_PRODUCT_ID_1,
                                            "SKU-001",
                                            10000,
                                            9000,
                                            100,
                                            0,
                                            List.of()),
                                    new UpdateProductsCommand.ProductData(
                                            LegacyProductContextFixtures.INTERNAL_PRODUCT_ID_2,
                                            "SKU-002",
                                            10000,
                                            9000,
                                            50,
                                            1,
                                            List.of())));

            given(resolveFactory.resolveUpdateProductsCommand(originalCommand))
                    .willReturn(resolvedCommand);

            // when
            sut.execute(originalCommand);

            // then
            then(resolveFactory).should().resolveUpdateProductsCommand(originalCommand);
            then(updateProductsUseCase).should().execute(resolvedCommand);
        }

        @Test
        @DisplayName("신규 상품(productId=null)을 포함한 Command도 정상 처리한다")
        void execute_CommandWithNullProductId_ProcessesNormally() {
            // given
            UpdateProductsCommand originalCommand =
                    LegacyProductContextFixtures.updateProductsCommandWithNullProductId();
            UpdateProductsCommand resolvedCommand =
                    new UpdateProductsCommand(
                            LegacyProductContextFixtures.INTERNAL_PRODUCT_GROUP_ID,
                            List.of(),
                            List.of(
                                    new UpdateProductsCommand.ProductData(
                                            null, "SKU-NEW", 10000, 9000, 100, 0, List.of())));

            given(resolveFactory.resolveUpdateProductsCommand(originalCommand))
                    .willReturn(resolvedCommand);

            // when
            sut.execute(originalCommand);

            // then
            then(updateProductsUseCase).should().execute(resolvedCommand);
        }
    }
}
