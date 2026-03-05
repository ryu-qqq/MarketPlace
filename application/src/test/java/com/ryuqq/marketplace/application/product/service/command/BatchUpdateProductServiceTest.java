package com.ryuqq.marketplace.application.product.service.command;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.outboundsync.internal.ProductGroupUpdateOutboxCoordinator;
import com.ryuqq.marketplace.application.product.dto.command.BatchUpdateProductCommand;
import com.ryuqq.marketplace.application.product.manager.ProductCommandManager;
import com.ryuqq.marketplace.application.product.validator.ProductOwnershipValidator;
import com.ryuqq.marketplace.domain.product.ProductFixtures;
import com.ryuqq.marketplace.domain.product.aggregate.Product;
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
@DisplayName("BatchUpdateProductService 단위 테스트")
class BatchUpdateProductServiceTest {

    @InjectMocks private BatchUpdateProductService sut;

    @Mock private ProductOwnershipValidator ownershipValidator;
    @Mock private ProductCommandManager commandManager;
    @Mock private ProductGroupUpdateOutboxCoordinator updateOutboxCoordinator;

    @Nested
    @DisplayName("execute() - 상품 배치 가격/재고 수정")
    class ExecuteTest {

        @Test
        @DisplayName("유효한 커맨드를 실행하면 소유권 검증 후 상품을 수정하고 저장한다")
        void execute_ValidCommand_UpdatesAndPersistsProducts() {
            // given
            long sellerId = 100L;
            Product product1 = ProductFixtures.activeProduct(1L);
            Product product2 = ProductFixtures.activeProduct(2L);
            List<Product> products = List.of(product1, product2);

            BatchUpdateProductCommand command =
                    new BatchUpdateProductCommand(
                            sellerId,
                            List.of(
                                    new BatchUpdateProductCommand.Entry(1L, 50000, 45000, 100),
                                    new BatchUpdateProductCommand.Entry(2L, 60000, 55000, 200)));

            given(ownershipValidator.validateAndGet(anyList(), anyLong())).willReturn(products);

            // when
            sut.execute(command);

            // then
            then(ownershipValidator).should().validateAndGet(anyList(), anyLong());
            then(commandManager).should().persistAll(products);
        }

        @Test
        @DisplayName("SUPER_ADMIN(sellerId=null)은 소유권 검증 없이 상품을 수정한다")
        void execute_SuperAdmin_SkipsOwnershipCheck() {
            // given
            Product product1 = ProductFixtures.activeProduct(1L);
            Product product2 = ProductFixtures.activeProduct(2L);
            List<Product> products = List.of(product1, product2);

            BatchUpdateProductCommand command =
                    new BatchUpdateProductCommand(
                            null,
                            List.of(
                                    new BatchUpdateProductCommand.Entry(1L, 50000, 45000, 100),
                                    new BatchUpdateProductCommand.Entry(2L, 60000, 55000, 200)));

            given(ownershipValidator.getWithoutOwnershipCheck(anyList())).willReturn(products);

            // when
            sut.execute(command);

            // then
            then(ownershipValidator).should().getWithoutOwnershipCheck(anyList());
            then(ownershipValidator).shouldHaveNoMoreInteractions();
            then(commandManager).should().persistAll(products);
        }

        @Test
        @DisplayName("단일 상품 수정도 정상적으로 처리된다")
        void execute_SingleEntry_UpdatesAndPersists() {
            // given
            long sellerId = 100L;
            Product product = ProductFixtures.activeProduct(1L);
            List<Product> products = List.of(product);

            BatchUpdateProductCommand command =
                    new BatchUpdateProductCommand(
                            sellerId,
                            List.of(new BatchUpdateProductCommand.Entry(1L, 50000, 45000, 50)));

            given(ownershipValidator.validateAndGet(anyList(), anyLong())).willReturn(products);

            // when
            sut.execute(command);

            // then
            then(ownershipValidator).should().validateAndGet(anyList(), anyLong());
            then(commandManager).should().persistAll(products);
        }
    }
}
