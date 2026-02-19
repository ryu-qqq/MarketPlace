package com.ryuqq.marketplace.application.productgroup.internal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.product.dto.command.ProductDiffUpdateEntry;
import com.ryuqq.marketplace.application.product.dto.command.SelectedOption;
import com.ryuqq.marketplace.application.product.internal.ProductCommandCoordinator;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupUpdateBundle;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.UpdateProductGroupDescriptionCommand;
import com.ryuqq.marketplace.application.productgroupdescription.internal.DescriptionCommandCoordinator;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.UpdateProductGroupImagesCommand;
import com.ryuqq.marketplace.application.productgroupimage.internal.ImageCommandCoordinator;
import com.ryuqq.marketplace.application.productnotice.dto.command.UpdateProductNoticeCommand;
import com.ryuqq.marketplace.application.productnotice.internal.ProductNoticeCommandCoordinator;
import com.ryuqq.marketplace.application.selleroption.dto.command.UpdateSellerOptionGroupsCommand;
import com.ryuqq.marketplace.application.selleroption.dto.result.SellerOptionUpdateResult;
import com.ryuqq.marketplace.application.selleroption.internal.SellerOptionCommandCoordinator;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionValueId;
import com.ryuqq.marketplace.domain.productgroup.vo.ProductGroupUpdateData;
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
@DisplayName("FullProductGroupUpdateCoordinator 단위 테스트")
class FullProductGroupUpdateCoordinatorTest {

    @InjectMocks private FullProductGroupUpdateCoordinator sut;

    @Mock private ProductGroupCommandCoordinator productGroupCommandCoordinator;
    @Mock private ImageCommandCoordinator imageCommandCoordinator;
    @Mock private SellerOptionCommandCoordinator sellerOptionCommandCoordinator;
    @Mock private DescriptionCommandCoordinator descriptionCommandCoordinator;
    @Mock private ProductNoticeCommandCoordinator noticeCommandCoordinator;
    @Mock private ProductCommandCoordinator productCommandCoordinator;

    @Nested
    @DisplayName("update() - 상품 그룹 전체 수정 조율")
    class UpdateTest {

        @Test
        @DisplayName("수정 번들로 전체 하위 도메인을 순서에 맞게 수정한다")
        void update_ValidBundle_OrchestratesAllSubDomains() {
            // given
            long productGroupId = 1L;
            ProductGroupUpdateBundle bundle = createUpdateBundle(productGroupId);
            SellerOptionUpdateResult optionResult =
                    new SellerOptionUpdateResult(
                            List.of(SellerOptionValueId.of(10L)), CommonVoFixtures.now());

            given(sellerOptionCommandCoordinator.update(any(UpdateSellerOptionGroupsCommand.class)))
                    .willReturn(optionResult);

            // when
            sut.update(bundle);

            // then (모든 하위 코디네이터가 호출되었는지 검증)
            then(productGroupCommandCoordinator).should().update(bundle.basicInfoUpdateData());
            then(imageCommandCoordinator)
                    .should()
                    .update(any(UpdateProductGroupImagesCommand.class));
            then(sellerOptionCommandCoordinator)
                    .should()
                    .update(any(UpdateSellerOptionGroupsCommand.class));
            then(descriptionCommandCoordinator)
                    .should()
                    .update(any(UpdateProductGroupDescriptionCommand.class));
            then(noticeCommandCoordinator).should().update(any(UpdateProductNoticeCommand.class));
        }

        @Test
        @DisplayName("ProductGroup 기본 정보 수정을 ProductGroupCommandCoordinator에 위임한다")
        void update_DelegatesBasicInfoUpdateToProductGroupCommandCoordinator() {
            // given
            long productGroupId = 1L;
            ProductGroupUpdateBundle bundle = createUpdateBundle(productGroupId);
            SellerOptionUpdateResult optionResult =
                    new SellerOptionUpdateResult(
                            List.of(SellerOptionValueId.of(10L)), CommonVoFixtures.now());

            given(sellerOptionCommandCoordinator.update(any(UpdateSellerOptionGroupsCommand.class)))
                    .willReturn(optionResult);

            // when
            sut.update(bundle);

            // then
            then(productGroupCommandCoordinator).should().update(bundle.basicInfoUpdateData());
        }

        @Test
        @DisplayName("이미지 수정을 ImageCommandCoordinator에 위임한다")
        void update_DelegatesImageUpdateToImageCommandCoordinator() {
            // given
            long productGroupId = 1L;
            ProductGroupUpdateBundle bundle = createUpdateBundle(productGroupId);
            SellerOptionUpdateResult optionResult =
                    new SellerOptionUpdateResult(
                            List.of(SellerOptionValueId.of(10L)), CommonVoFixtures.now());

            given(sellerOptionCommandCoordinator.update(any(UpdateSellerOptionGroupsCommand.class)))
                    .willReturn(optionResult);

            // when
            sut.update(bundle);

            // then
            then(imageCommandCoordinator)
                    .should()
                    .update(any(UpdateProductGroupImagesCommand.class));
        }

        @Test
        @DisplayName("옵션 그룹 수정을 SellerOptionCommandCoordinator에 위임하고 결과를 Product 수정에 활용한다")
        void update_DelegatesOptionGroupUpdateToSellerOptionCommandCoordinatorAndUsesResult() {
            // given
            long productGroupId = 1L;
            ProductGroupUpdateBundle bundle = createUpdateBundle(productGroupId);
            List<SellerOptionValueId> resolvedIds =
                    List.of(SellerOptionValueId.of(10L), SellerOptionValueId.of(11L));
            Instant occurredAt = CommonVoFixtures.now();
            SellerOptionUpdateResult optionResult =
                    new SellerOptionUpdateResult(resolvedIds, occurredAt);

            given(sellerOptionCommandCoordinator.update(any(UpdateSellerOptionGroupsCommand.class)))
                    .willReturn(optionResult);

            // when
            sut.update(bundle);

            // then
            then(sellerOptionCommandCoordinator)
                    .should()
                    .update(any(UpdateSellerOptionGroupsCommand.class));
            then(productCommandCoordinator)
                    .should()
                    .updateWithDiff(
                            eq(bundle.basicInfoUpdateData().productGroupId()),
                            eq(bundle.productEntries()),
                            eq(optionResult),
                            any());
        }

        @Test
        @DisplayName("상세설명 수정을 DescriptionCommandCoordinator에 위임한다")
        void update_DelegatesDescriptionUpdateToDescriptionCommandCoordinator() {
            // given
            long productGroupId = 1L;
            ProductGroupUpdateBundle bundle = createUpdateBundle(productGroupId);
            SellerOptionUpdateResult optionResult =
                    new SellerOptionUpdateResult(
                            List.of(SellerOptionValueId.of(10L)), CommonVoFixtures.now());

            given(sellerOptionCommandCoordinator.update(any(UpdateSellerOptionGroupsCommand.class)))
                    .willReturn(optionResult);

            // when
            sut.update(bundle);

            // then
            then(descriptionCommandCoordinator)
                    .should()
                    .update(any(UpdateProductGroupDescriptionCommand.class));
        }

        @Test
        @DisplayName("고시정보 수정을 ProductNoticeCommandCoordinator에 위임한다")
        void update_DelegatesNoticeUpdateToNoticeCommandCoordinator() {
            // given
            long productGroupId = 1L;
            ProductGroupUpdateBundle bundle = createUpdateBundle(productGroupId);
            SellerOptionUpdateResult optionResult =
                    new SellerOptionUpdateResult(
                            List.of(SellerOptionValueId.of(10L)), CommonVoFixtures.now());

            given(sellerOptionCommandCoordinator.update(any(UpdateSellerOptionGroupsCommand.class)))
                    .willReturn(optionResult);

            // when
            sut.update(bundle);

            // then
            then(noticeCommandCoordinator).should().update(any(UpdateProductNoticeCommand.class));
        }

        @Test
        @DisplayName("상품 수정을 ProductCommandCoordinator.updateWithDiff에 위임한다")
        void update_DelegatesProductUpdateToProductCommandCoordinator() {
            // given
            long productGroupId = 1L;
            ProductGroupUpdateBundle bundle = createUpdateBundle(productGroupId);
            SellerOptionUpdateResult optionResult =
                    new SellerOptionUpdateResult(
                            List.of(SellerOptionValueId.of(10L)), CommonVoFixtures.now());

            given(sellerOptionCommandCoordinator.update(any(UpdateSellerOptionGroupsCommand.class)))
                    .willReturn(optionResult);

            // when
            sut.update(bundle);

            // then
            then(productCommandCoordinator)
                    .should()
                    .updateWithDiff(
                            eq(bundle.basicInfoUpdateData().productGroupId()),
                            eq(bundle.productEntries()),
                            eq(optionResult),
                            any());
        }
    }

    // ===== 헬퍼 메서드 =====

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
                        Instant.now());

        UpdateProductGroupImagesCommand imageCommand =
                new UpdateProductGroupImagesCommand(
                        productGroupId,
                        List.of(
                                new UpdateProductGroupImagesCommand.ImageCommand(
                                        "THUMBNAIL", "https://example.com/image.jpg", 0)));

        UpdateSellerOptionGroupsCommand optionGroupCommand =
                new UpdateSellerOptionGroupsCommand(
                        productGroupId,
                        List.of(
                                new UpdateSellerOptionGroupsCommand.OptionGroupCommand(
                                        1L,
                                        "색상",
                                        null,
                                        List.of(
                                                new UpdateSellerOptionGroupsCommand
                                                        .OptionValueCommand(1L, "검정", null, 0)))));

        UpdateProductGroupDescriptionCommand descriptionCommand =
                new UpdateProductGroupDescriptionCommand(productGroupId, "<p>수정된 상세설명</p>");

        UpdateProductNoticeCommand noticeCommand =
                new UpdateProductNoticeCommand(
                        productGroupId,
                        10L,
                        List.of(new UpdateProductNoticeCommand.NoticeEntryCommand(1L, "95% 면")));

        List<ProductDiffUpdateEntry> productEntries =
                List.of(
                        new ProductDiffUpdateEntry(
                                1L,
                                "SKU-001",
                                12000,
                                10000,
                                80,
                                0,
                                List.of(new SelectedOption("색상", "검정"))));

        return new ProductGroupUpdateBundle(
                basicInfoUpdateData,
                imageCommand,
                optionGroupCommand,
                descriptionCommand,
                noticeCommand,
                productEntries);
    }
}
