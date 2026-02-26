package com.ryuqq.marketplace.application.productgroup.service.command;

import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.productgroup.ProductGroupCommandFixtures;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupUpdateBundle;
import com.ryuqq.marketplace.application.productgroup.dto.command.UpdateProductGroupFullCommand;
import com.ryuqq.marketplace.application.productgroup.factory.ProductGroupBundleFactory;
import com.ryuqq.marketplace.application.productgroup.internal.FullProductGroupUpdateCoordinator;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.UpdateProductGroupDescriptionCommand;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.UpdateProductGroupImagesCommand;
import com.ryuqq.marketplace.application.productnotice.dto.command.UpdateProductNoticeCommand;
import com.ryuqq.marketplace.application.selleroption.dto.command.UpdateSellerOptionGroupsCommand;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupUpdateData;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("UpdateProductGroupFullService 단위 테스트")
class UpdateProductGroupFullServiceTest {

    @InjectMocks private UpdateProductGroupFullService sut;

    @Mock private ProductGroupBundleFactory bundleFactory;
    @Mock private FullProductGroupUpdateCoordinator coordinator;

    @Nested
    @DisplayName("execute() - 상품 그룹 전체 수정")
    class ExecuteTest {

        @Test
        @DisplayName("커맨드로 상품 그룹 전체를 수정한다")
        void execute_UpdatesProductGroupFull_NoReturn() {
            // given
            long productGroupId = 1L;
            UpdateProductGroupFullCommand command =
                    ProductGroupCommandFixtures.updateFullCommand(productGroupId);
            ProductGroupUpdateBundle bundle = createUpdateBundle(productGroupId);

            BDDMockito.given(bundleFactory.createUpdateBundle(command)).willReturn(bundle);

            // when
            sut.execute(command);

            // then
            then(bundleFactory).should().createUpdateBundle(command);
            then(coordinator).should().update(bundle);
        }
    }

    private ProductGroupUpdateBundle createUpdateBundle(long productGroupId) {
        ProductGroupUpdateData basicInfoUpdateData =
                ProductGroupUpdateData.of(
                        ProductGroupId.of(productGroupId),
                        ProductGroupFixtures.defaultProductGroupName(),
                        com.ryuqq.marketplace.domain.brand.id.BrandId.of(
                                ProductGroupFixtures.DEFAULT_BRAND_ID),
                        com.ryuqq.marketplace.domain.category.id.CategoryId.of(
                                ProductGroupFixtures.DEFAULT_CATEGORY_ID),
                        com.ryuqq.marketplace.domain.shippingpolicy.id.ShippingPolicyId.of(
                                ProductGroupFixtures.DEFAULT_SHIPPING_POLICY_ID),
                        com.ryuqq.marketplace.domain.refundpolicy.id.RefundPolicyId.of(
                                ProductGroupFixtures.DEFAULT_REFUND_POLICY_ID),
                        com.ryuqq.marketplace.domain.productgroup.vo.OptionType.SINGLE,
                        Instant.now());

        return new ProductGroupUpdateBundle(
                basicInfoUpdateData,
                new UpdateProductGroupImagesCommand(productGroupId, List.of()),
                new UpdateSellerOptionGroupsCommand(productGroupId, List.of()),
                new UpdateProductGroupDescriptionCommand(productGroupId, "<p>수정된 상세설명</p>"),
                new UpdateProductNoticeCommand(productGroupId, 10L, List.of()),
                List.of());
    }
}
