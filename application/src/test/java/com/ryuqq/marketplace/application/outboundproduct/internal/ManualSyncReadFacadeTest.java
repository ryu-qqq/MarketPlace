package com.ryuqq.marketplace.application.outboundproduct.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.outboundproduct.dto.command.ManualSyncProductsCommand;
import com.ryuqq.marketplace.application.outboundproduct.manager.OutboundProductReadManager;
import com.ryuqq.marketplace.application.outboundproduct.validator.ManualSyncProductsValidator;
import com.ryuqq.marketplace.application.outboundsync.manager.OutboundSyncOutboxReadManager;
import com.ryuqq.marketplace.application.productgroup.manager.ProductGroupReadManager;
import com.ryuqq.marketplace.application.shop.manager.ShopReadManager;
import com.ryuqq.marketplace.domain.outboundproduct.OutboundProductFixtures;
import com.ryuqq.marketplace.domain.outboundsync.OutboundSyncOutboxFixtures;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.shop.ShopFixtures;
import com.ryuqq.marketplace.domain.shop.aggregate.Shop;
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
@DisplayName("ManualSyncReadFacade 단위 테스트")
class ManualSyncReadFacadeTest {

    @InjectMocks private ManualSyncReadFacade sut;

    @Mock private ShopReadManager shopReadManager;
    @Mock private ProductGroupReadManager productGroupReadManager;
    @Mock private ManualSyncProductsValidator validator;
    @Mock private OutboundProductReadManager outboundProductReadManager;
    @Mock private OutboundSyncOutboxReadManager outboxReadManager;

    @Nested
    @DisplayName("resolve() - 수동 전송 컨텍스트 수집")
    class ResolveTest {

        @Test
        @DisplayName("모든 조회 데이터를 수집하여 ManualSyncContext를 반환한다")
        void resolve_CollectsAllData_ReturnsContext() {
            // given
            ManualSyncProductsCommand command =
                    new ManualSyncProductsCommand(List.of(1L), List.of(10L));

            Shop shop = ShopFixtures.activeShop(10L);
            ProductGroup pg = ProductGroupFixtures.activeProductGroup();

            given(shopReadManager.findByIds(anyList())).willReturn(List.of(shop));
            given(productGroupReadManager.findByIds(anyList())).willReturn(List.of(pg));
            given(validator.findConnectedChannelIdsBySellerIds(any()))
                    .willReturn(Map.of(1L, Set.of(1L)));
            given(outboundProductReadManager.findByProductGroupIds(anyList()))
                    .willReturn(List.of());
            given(outboxReadManager.findPendingByProductGroupIds(any())).willReturn(List.of());

            // when
            ManualSyncContext ctx = sut.resolve(command);

            // then
            assertThat(ctx.salesChannelIds()).containsExactly(shop.salesChannelId());
            assertThat(ctx.productGroups()).hasSize(1);
            assertThat(ctx.connectedChannelIdsBySellerId()).containsKey(1L);
            assertThat(ctx.existingProductKeys()).isEmpty();
            assertThat(ctx.pendingKeys()).isEmpty();
        }

        @Test
        @DisplayName("OutboundProduct가 있으면 existingProductKeys에 키가 포함된다")
        void resolve_WithExistingProducts_IncludesKeys() {
            // given
            ManualSyncProductsCommand command =
                    new ManualSyncProductsCommand(List.of(100L), List.of(10L));

            Shop shop = ShopFixtures.activeShop(10L);
            ProductGroup pg = ProductGroupFixtures.activeProductGroup();

            given(shopReadManager.findByIds(anyList())).willReturn(List.of(shop));
            given(productGroupReadManager.findByIds(anyList())).willReturn(List.of(pg));
            given(validator.findConnectedChannelIdsBySellerIds(any()))
                    .willReturn(Map.of(1L, Set.of(1L)));
            given(outboundProductReadManager.findByProductGroupIds(anyList()))
                    .willReturn(List.of(OutboundProductFixtures.pendingProduct(100L, 10L)));
            given(outboxReadManager.findPendingByProductGroupIds(any())).willReturn(List.of());

            // when
            ManualSyncContext ctx = sut.resolve(command);

            // then
            assertThat(ctx.existingProductKeys()).contains("100:10");
        }

        @Test
        @DisplayName("PENDING Outbox가 있으면 pendingKeys에 키가 포함된다")
        void resolve_WithPendingOutboxes_IncludesPendingKeys() {
            // given
            ManualSyncProductsCommand command =
                    new ManualSyncProductsCommand(List.of(100L), List.of(10L));

            Shop shop = ShopFixtures.activeShop(10L);
            ProductGroup pg = ProductGroupFixtures.activeProductGroup();

            given(shopReadManager.findByIds(anyList())).willReturn(List.of(shop));
            given(productGroupReadManager.findByIds(anyList())).willReturn(List.of(pg));
            given(validator.findConnectedChannelIdsBySellerIds(any()))
                    .willReturn(Map.of(1L, Set.of(1L)));
            given(outboundProductReadManager.findByProductGroupIds(anyList()))
                    .willReturn(List.of());
            given(outboxReadManager.findPendingByProductGroupIds(any()))
                    .willReturn(List.of(OutboundSyncOutboxFixtures.pendingOutbox(100L, 10L)));

            // when
            ManualSyncContext ctx = sut.resolve(command);

            // then
            assertThat(ctx.pendingKeys()).contains("100:10");
        }
    }
}
