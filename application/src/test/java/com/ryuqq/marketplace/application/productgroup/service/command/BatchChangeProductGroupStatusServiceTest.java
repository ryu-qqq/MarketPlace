package com.ryuqq.marketplace.application.productgroup.service.command;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.productgroup.ProductGroupCommandFixtures;
import com.ryuqq.marketplace.application.productgroup.dto.command.BatchChangeProductGroupStatusCommand;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupCommandManager;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupReadManager;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.exception.ProductGroupOwnershipViolationException;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
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
@DisplayName("BatchChangeProductGroupStatusService 단위 테스트")
class BatchChangeProductGroupStatusServiceTest {

    @InjectMocks private BatchChangeProductGroupStatusService sut;

    @Mock private TimeProvider timeProvider;
    @Mock private ProductGroupReadManager readManager;
    @Mock private ProductGroupCommandManager commandManager;

    @Nested
    @DisplayName("execute() - 상품 그룹 배치 상태 변경")
    class ExecuteTest {

        @Test
        @DisplayName("셀러 소유 상품 그룹을 ACTIVE로 일괄 변경한다")
        void execute_BatchChangeToActive_PersistsAllGroups() {
            // given
            long sellerId = 1L;
            List<Long> productGroupIds = List.of(1L, 2L, 3L);
            BatchChangeProductGroupStatusCommand command =
                    ProductGroupCommandFixtures.batchChangeStatusCommand(
                            sellerId, productGroupIds, "ACTIVE");

            List<ProductGroupId> ids = productGroupIds.stream().map(ProductGroupId::of).toList();

            ProductGroup group1 = ProductGroupFixtures.draftProductGroup(1L);
            ProductGroup group2 = ProductGroupFixtures.draftProductGroup(2L);
            ProductGroup group3 = ProductGroupFixtures.draftProductGroup(3L);
            List<ProductGroup> groups = List.of(group1, group2, group3);

            Instant now = Instant.now();

            given(readManager.getByIdsAndSellerId(ids, sellerId)).willReturn(groups);
            given(timeProvider.now()).willReturn(now);

            // when
            sut.execute(command);

            // then
            then(readManager).should().getByIdsAndSellerId(ids, sellerId);
            then(timeProvider).should().now();
            then(commandManager).should().persist(group1);
            then(commandManager).should().persist(group2);
            then(commandManager).should().persist(group3);
        }

        @Test
        @DisplayName("셀러 소유 상품 그룹을 INACTIVE로 일괄 변경한다")
        void execute_BatchChangeToInactive_PersistsAllGroups() {
            // given
            long sellerId = 1L;
            List<Long> productGroupIds = List.of(1L, 2L);
            BatchChangeProductGroupStatusCommand command =
                    ProductGroupCommandFixtures.batchChangeStatusCommand(
                            sellerId, productGroupIds, "INACTIVE");

            List<ProductGroupId> ids = productGroupIds.stream().map(ProductGroupId::of).toList();

            ProductGroup group1 = ProductGroupFixtures.activeProductGroup();
            ProductGroup group2 = ProductGroupFixtures.activeProductGroup();
            List<ProductGroup> groups = List.of(group1, group2);

            Instant now = Instant.now();

            given(readManager.getByIdsAndSellerId(ids, sellerId)).willReturn(groups);
            given(timeProvider.now()).willReturn(now);

            // when
            sut.execute(command);

            // then
            then(readManager).should().getByIdsAndSellerId(ids, sellerId);
            then(commandManager).should().persist(group1);
            then(commandManager).should().persist(group2);
        }

        @Test
        @DisplayName("소유권 위반 시 예외가 발생하고 상태 변경이 수행되지 않는다")
        void execute_OwnershipViolation_ThrowsException() {
            // given
            long sellerId = 1L;
            List<Long> productGroupIds = List.of(1L, 2L, 3L);
            BatchChangeProductGroupStatusCommand command =
                    ProductGroupCommandFixtures.batchChangeStatusCommand(
                            sellerId, productGroupIds, "ACTIVE");

            List<ProductGroupId> ids = productGroupIds.stream().map(ProductGroupId::of).toList();

            given(readManager.getByIdsAndSellerId(ids, sellerId))
                    .willThrow(new ProductGroupOwnershipViolationException(sellerId, 3, 2));

            // when & then
            assertThatThrownBy(() -> sut.execute(command))
                    .isInstanceOf(ProductGroupOwnershipViolationException.class);

            then(commandManager).shouldHaveNoInteractions();
        }
    }
}
