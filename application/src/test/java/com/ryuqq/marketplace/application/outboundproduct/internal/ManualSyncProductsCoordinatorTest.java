package com.ryuqq.marketplace.application.outboundproduct.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;

import com.ryuqq.marketplace.application.outboundproduct.dto.command.ManualSyncProductsCommand;
import com.ryuqq.marketplace.application.outboundproduct.dto.result.ManualSyncResult;
import com.ryuqq.marketplace.domain.brand.id.BrandId;
import com.ryuqq.marketplace.domain.category.id.CategoryId;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionType;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupName;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupStatus;
import com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId;
import com.ryuqq.marketplace.domain.saleschannel.id.SalesChannelId;
import com.ryuqq.marketplace.domain.seller.id.SellerId;
import com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
@DisplayName("ManualSyncProductsCoordinator 단위 테스트")
class ManualSyncProductsCoordinatorTest {

    @InjectMocks private ManualSyncProductsCoordinator sut;
    @Mock private ManualSyncReadFacade readFacade;
    @Mock private ManualSyncCommandFacade manualSyncCommandFacade;

    @Nested
    @DisplayName("execute() - 수동 전송 실행")
    class ExecuteTest {

        @Test
        @DisplayName("OutboundProduct가 없으면 CREATE로 처리한다")
        void execute_NoExistingProduct_CreatesNewProductAndOutbox() {
            // given
            ManualSyncProductsCommand command =
                    new ManualSyncProductsCommand(List.of(1L), List.of(10L));
            ProductGroup pg = productGroup(1L, 1L);
            ManualSyncContext ctx =
                    new ManualSyncContext(
                            Set.of(10L),
                            Map.of(10L, 1L),
                            List.of(pg),
                            Map.of(1L, Set.of(10L)),
                            Set.of(),
                            Set.of());
            given(readFacade.resolve(command)).willReturn(ctx);

            // when
            ManualSyncResult result = sut.execute(command);

            // then
            assertThat(result.createCount()).isEqualTo(1);
            assertThat(result.updateCount()).isZero();
            assertThat(result.skippedCount()).isZero();
            then(manualSyncCommandFacade)
                    .should()
                    .createProductAndOutbox(
                            eq(ProductGroupId.of(1L)),
                            eq(SalesChannelId.of(10L)),
                            eq(1L),
                            eq(SellerId.of(1L)),
                            any(Instant.class));
        }

        @Test
        @DisplayName("OutboundProduct가 있으면 UPDATE로 처리한다")
        void execute_ExistingProduct_CreatesUpdateOutbox() {
            // given
            ManualSyncProductsCommand command =
                    new ManualSyncProductsCommand(List.of(1L), List.of(10L));
            ProductGroup pg = productGroup(1L, 1L);
            ManualSyncContext ctx =
                    new ManualSyncContext(
                            Set.of(10L),
                            Map.of(10L, 1L),
                            List.of(pg),
                            Map.of(1L, Set.of(10L)),
                            Set.of("1:10"),
                            Set.of());
            given(readFacade.resolve(command)).willReturn(ctx);

            // when
            ManualSyncResult result = sut.execute(command);

            // then
            assertThat(result.createCount()).isZero();
            assertThat(result.updateCount()).isEqualTo(1);
            assertThat(result.skippedCount()).isZero();
            then(manualSyncCommandFacade)
                    .should()
                    .createUpdateOutbox(
                            eq(ProductGroupId.of(1L)),
                            eq(SalesChannelId.of(10L)),
                            eq(1L),
                            eq(SellerId.of(1L)),
                            any(Instant.class));
        }

        @Test
        @DisplayName("셀러가 채널에 미연결이면 스킵한다")
        void execute_SellerNotConnected_Skips() {
            // given
            ManualSyncProductsCommand command =
                    new ManualSyncProductsCommand(List.of(1L), List.of(10L));
            ProductGroup pg = productGroup(1L, 1L);
            ManualSyncContext ctx =
                    new ManualSyncContext(
                            Set.of(10L),
                            Map.of(10L, 1L),
                            List.of(pg),
                            Map.of(1L, Set.of(20L)),
                            Set.of(),
                            Set.of());
            given(readFacade.resolve(command)).willReturn(ctx);

            // when
            ManualSyncResult result = sut.execute(command);

            // then
            assertThat(result.createCount()).isZero();
            assertThat(result.updateCount()).isZero();
            assertThat(result.skippedCount()).isEqualTo(1);
            then(manualSyncCommandFacade).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("PENDING Outbox가 이미 있으면 스킵한다")
        void execute_PendingOutboxExists_Skips() {
            // given
            ManualSyncProductsCommand command =
                    new ManualSyncProductsCommand(List.of(1L), List.of(10L));
            ProductGroup pg = productGroup(1L, 1L);
            ManualSyncContext ctx =
                    new ManualSyncContext(
                            Set.of(10L),
                            Map.of(10L, 1L),
                            List.of(pg),
                            Map.of(1L, Set.of(10L)),
                            Set.of(),
                            Set.of("1:10"));
            given(readFacade.resolve(command)).willReturn(ctx);

            // when
            ManualSyncResult result = sut.execute(command);

            // then
            assertThat(result.createCount()).isZero();
            assertThat(result.updateCount()).isZero();
            assertThat(result.skippedCount()).isEqualTo(1);
            then(manualSyncCommandFacade).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("여러 상품과 채널이 혼합된 경우 CREATE/UPDATE/SKIP을 올바르게 처리한다")
        void execute_MixedScenario_ProcessesCorrectly() {
            // given
            // pg1:ch10 → existing product → UPDATE
            // pg1:ch20 → no existing, no pending → CREATE
            // pg2:ch10 → no existing, no pending → CREATE
            // pg2:ch20 → pending → SKIP
            ManualSyncProductsCommand command =
                    new ManualSyncProductsCommand(List.of(1L, 2L), List.of(10L, 20L));
            ProductGroup pg1 = productGroup(1L, 1L);
            ProductGroup pg2 = productGroup(2L, 1L);

            ManualSyncContext ctx =
                    new ManualSyncContext(
                            Set.of(10L, 20L),
                            Map.of(10L, 1L, 20L, 2L),
                            List.of(pg1, pg2),
                            Map.of(1L, Set.of(10L, 20L)),
                            Set.of("1:10"),
                            Set.of("2:20"));
            given(readFacade.resolve(command)).willReturn(ctx);

            // when
            ManualSyncResult result = sut.execute(command);

            // then
            assertThat(result.createCount()).isEqualTo(2);
            assertThat(result.updateCount()).isEqualTo(1);
            assertThat(result.skippedCount()).isEqualTo(1);
            then(manualSyncCommandFacade)
                    .should(times(2))
                    .createProductAndOutbox(any(), any(), anyLong(), any(), any());
            then(manualSyncCommandFacade)
                    .should(times(1))
                    .createUpdateOutbox(any(), any(), anyLong(), any(), any());
        }

        @Test
        @DisplayName("connectedChannelIdsBySellerId에 셀러가 없으면 모든 채널을 스킵한다")
        void execute_SellerNotInConnectedMap_SkipsAll() {
            // given
            ManualSyncProductsCommand command =
                    new ManualSyncProductsCommand(List.of(1L), List.of(10L, 20L));
            ProductGroup pg = productGroup(1L, 99L);
            ManualSyncContext ctx =
                    new ManualSyncContext(
                            Set.of(10L, 20L),
                            Map.of(10L, 1L, 20L, 2L),
                            List.of(pg),
                            Map.of(),
                            Set.of(),
                            Set.of());
            given(readFacade.resolve(command)).willReturn(ctx);

            // when
            ManualSyncResult result = sut.execute(command);

            // then
            assertThat(result.createCount()).isZero();
            assertThat(result.updateCount()).isZero();
            assertThat(result.skippedCount()).isEqualTo(2);
            then(manualSyncCommandFacade).shouldHaveNoInteractions();
        }

        @Test
        @DisplayName("빈 상품 목록이면 카운트 모두 0인 결과를 반환한다")
        void execute_EmptyProductGroups_ReturnsZeroCounts() {
            // given
            ManualSyncProductsCommand command =
                    new ManualSyncProductsCommand(List.of(), List.of(10L));
            ManualSyncContext ctx =
                    new ManualSyncContext(
                            Set.of(10L), Map.of(10L, 1L), List.of(), Map.of(), Set.of(), Set.of());
            given(readFacade.resolve(command)).willReturn(ctx);

            // when
            ManualSyncResult result = sut.execute(command);

            // then
            assertThat(result.createCount()).isZero();
            assertThat(result.updateCount()).isZero();
            assertThat(result.skippedCount()).isZero();
            assertThat(result.status()).isEqualTo("ACCEPTED");
        }

        private ProductGroup productGroup(Long pgId, Long sellerId) {
            Instant now = Instant.now();
            return ProductGroup.reconstitute(
                    ProductGroupId.of(pgId),
                    SellerId.of(sellerId),
                    BrandId.of(100L),
                    CategoryId.of(200L),
                    ShippingPolicyId.of(1L),
                    RefundPolicyId.of(1L),
                    ProductGroupName.of("Test PG " + pgId),
                    OptionType.NONE,
                    ProductGroupStatus.ACTIVE,
                    List.of(),
                    List.of(),
                    now,
                    now);
        }
    }
}
