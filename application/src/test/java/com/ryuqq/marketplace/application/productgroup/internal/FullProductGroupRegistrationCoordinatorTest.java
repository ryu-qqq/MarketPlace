package com.ryuqq.marketplace.application.productgroup.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.product.dto.command.RegisterProductsCommand;
import com.ryuqq.marketplace.application.product.dto.command.SelectedOption;
import com.ryuqq.marketplace.application.product.internal.ProductCommandCoordinator;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.RegisterProductGroupDescriptionCommand;
import com.ryuqq.marketplace.application.productgroupdescription.internal.DescriptionCommandCoordinator;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.RegisterProductGroupImagesCommand;
import com.ryuqq.marketplace.application.productgroupimage.internal.ImageCommandCoordinator;
import com.ryuqq.marketplace.application.productgroupinspection.manager.ProductGroupInspectionOutboxCommandManager;
import com.ryuqq.marketplace.application.productnotice.dto.command.RegisterProductNoticeCommand;
import com.ryuqq.marketplace.application.productnotice.internal.ProductNoticeCommandCoordinator;
import com.ryuqq.marketplace.application.selleroption.dto.command.RegisterSellerOptionGroupsCommand;
import com.ryuqq.marketplace.application.selleroption.internal.SellerOptionCommandCoordinator;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionValueId;
import com.ryuqq.marketplace.domain.productgroupinspection.aggregate.ProductGroupInspectionOutbox;
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
@DisplayName("FullProductGroupRegistrationCoordinator 단위 테스트")
class FullProductGroupRegistrationCoordinatorTest {

    @InjectMocks private FullProductGroupRegistrationCoordinator sut;

    @Mock private ProductGroupCommandCoordinator productGroupCommandCoordinator;
    @Mock private ImageCommandCoordinator imageCommandCoordinator;
    @Mock private SellerOptionCommandCoordinator sellerOptionCommandCoordinator;
    @Mock private DescriptionCommandCoordinator descriptionCommandCoordinator;
    @Mock private ProductNoticeCommandCoordinator noticeCommandCoordinator;
    @Mock private ProductCommandCoordinator productCommandCoordinator;
    @Mock private ProductGroupInspectionOutboxCommandManager inspectionOutboxCommandManager;

    @Nested
    @DisplayName("register() - 상품 그룹 전체 등록 조율")
    class RegisterTest {

        @Test
        @DisplayName("등록 번들로 전체 하위 도메인을 순서에 맞게 등록하고 productGroupId를 반환한다")
        void register_ValidBundle_OrchestratesAllSubDomainsAndReturnsId() {
            // given
            Long expectedProductGroupId = 1L;
            ProductGroupRegistrationBundle bundle = createBundle();
            List<SellerOptionValueId> optionValueIds =
                    List.of(SellerOptionValueId.of(10L), SellerOptionValueId.of(11L));

            given(productGroupCommandCoordinator.register(bundle.productGroup()))
                    .willReturn(expectedProductGroupId);
            given(
                            sellerOptionCommandCoordinator.register(
                                    any(RegisterSellerOptionGroupsCommand.class)))
                    .willReturn(optionValueIds);

            // when
            Long result = sut.register(bundle);

            // then
            assertThat(result).isEqualTo(expectedProductGroupId);
        }

        @Test
        @DisplayName("ProductGroup 기본 정보 등록을 ProductGroupCommandCoordinator에 위임한다")
        void register_DelegatesProductGroupRegistrationToCommandCoordinator() {
            // given
            Long productGroupId = 1L;
            ProductGroupRegistrationBundle bundle = createBundle();
            List<SellerOptionValueId> optionValueIds = List.of(SellerOptionValueId.of(10L));

            given(productGroupCommandCoordinator.register(bundle.productGroup()))
                    .willReturn(productGroupId);
            given(
                            sellerOptionCommandCoordinator.register(
                                    any(RegisterSellerOptionGroupsCommand.class)))
                    .willReturn(optionValueIds);

            // when
            sut.register(bundle);

            // then
            then(productGroupCommandCoordinator).should().register(bundle.productGroup());
        }

        @Test
        @DisplayName("이미지 등록을 ImageCommandCoordinator에 위임한다")
        void register_DelegatesImageRegistrationToImageCommandCoordinator() {
            // given
            Long productGroupId = 1L;
            ProductGroupRegistrationBundle bundle = createBundle();
            List<SellerOptionValueId> optionValueIds = List.of(SellerOptionValueId.of(10L));

            given(productGroupCommandCoordinator.register(bundle.productGroup()))
                    .willReturn(productGroupId);
            given(
                            sellerOptionCommandCoordinator.register(
                                    any(RegisterSellerOptionGroupsCommand.class)))
                    .willReturn(optionValueIds);

            // when
            sut.register(bundle);

            // then
            then(imageCommandCoordinator)
                    .should()
                    .register(any(RegisterProductGroupImagesCommand.class));
        }

        @Test
        @DisplayName("옵션 그룹 등록을 SellerOptionCommandCoordinator에 위임한다")
        void register_DelegatesOptionGroupRegistrationToSellerOptionCommandCoordinator() {
            // given
            Long productGroupId = 1L;
            ProductGroupRegistrationBundle bundle = createBundle();
            List<SellerOptionValueId> optionValueIds = List.of(SellerOptionValueId.of(10L));

            given(productGroupCommandCoordinator.register(bundle.productGroup()))
                    .willReturn(productGroupId);
            given(
                            sellerOptionCommandCoordinator.register(
                                    any(RegisterSellerOptionGroupsCommand.class)))
                    .willReturn(optionValueIds);

            // when
            sut.register(bundle);

            // then
            then(sellerOptionCommandCoordinator)
                    .should()
                    .register(any(RegisterSellerOptionGroupsCommand.class));
        }

        @Test
        @DisplayName("상세설명 등록을 DescriptionCommandCoordinator에 위임한다")
        void register_DelegatesDescriptionRegistrationToDescriptionCommandCoordinator() {
            // given
            Long productGroupId = 1L;
            ProductGroupRegistrationBundle bundle = createBundle();
            List<SellerOptionValueId> optionValueIds = List.of(SellerOptionValueId.of(10L));

            given(productGroupCommandCoordinator.register(bundle.productGroup()))
                    .willReturn(productGroupId);
            given(
                            sellerOptionCommandCoordinator.register(
                                    any(RegisterSellerOptionGroupsCommand.class)))
                    .willReturn(optionValueIds);

            // when
            sut.register(bundle);

            // then
            then(descriptionCommandCoordinator)
                    .should()
                    .register(any(RegisterProductGroupDescriptionCommand.class));
        }

        @Test
        @DisplayName("고시정보 등록을 ProductNoticeCommandCoordinator에 위임한다")
        void register_DelegatesNoticeRegistrationToNoticeCommandCoordinator() {
            // given
            Long productGroupId = 1L;
            ProductGroupRegistrationBundle bundle = createBundle();
            List<SellerOptionValueId> optionValueIds = List.of(SellerOptionValueId.of(10L));

            given(productGroupCommandCoordinator.register(bundle.productGroup()))
                    .willReturn(productGroupId);
            given(
                            sellerOptionCommandCoordinator.register(
                                    any(RegisterSellerOptionGroupsCommand.class)))
                    .willReturn(optionValueIds);

            // when
            sut.register(bundle);

            // then
            then(noticeCommandCoordinator)
                    .should()
                    .register(any(RegisterProductNoticeCommand.class));
        }

        @Test
        @DisplayName("상품 등록을 ProductCommandCoordinator에 위임한다")
        void register_DelegatesProductRegistrationToProductCommandCoordinator() {
            // given
            Long productGroupId = 1L;
            ProductGroupRegistrationBundle bundle = createBundle();
            List<SellerOptionValueId> optionValueIds = List.of(SellerOptionValueId.of(10L));

            given(productGroupCommandCoordinator.register(bundle.productGroup()))
                    .willReturn(productGroupId);
            given(
                            sellerOptionCommandCoordinator.register(
                                    any(RegisterSellerOptionGroupsCommand.class)))
                    .willReturn(optionValueIds);

            // when
            sut.register(bundle);

            // then
            then(productCommandCoordinator).should().register(any(List.class));
        }

        @Test
        @DisplayName("번들의 createInspectionOutbox로 검수 Outbox를 생성하고 저장한다")
        void register_CreatesAndPersistsInspectionOutbox() {
            // given
            Long productGroupId = 1L;
            ProductGroupRegistrationBundle bundle = createBundle();
            List<SellerOptionValueId> optionValueIds = List.of(SellerOptionValueId.of(10L));

            given(productGroupCommandCoordinator.register(bundle.productGroup()))
                    .willReturn(productGroupId);
            given(
                            sellerOptionCommandCoordinator.register(
                                    any(RegisterSellerOptionGroupsCommand.class)))
                    .willReturn(optionValueIds);

            // when
            sut.register(bundle);

            // then
            then(inspectionOutboxCommandManager)
                    .should()
                    .persist(any(ProductGroupInspectionOutbox.class));
        }
    }

    // ===== 헬퍼 메서드 =====

    private ProductGroupRegistrationBundle createBundle() {
        ProductGroup productGroup = ProductGroupFixtures.newProductGroup();

        RegisterProductGroupImagesCommand imageCommand =
                new RegisterProductGroupImagesCommand(
                        0L,
                        List.of(
                                new RegisterProductGroupImagesCommand.ImageCommand(
                                        "THUMBNAIL", "https://example.com/image.jpg", 0)));

        RegisterSellerOptionGroupsCommand optionGroupCommand =
                new RegisterSellerOptionGroupsCommand(
                        0L,
                        "SINGLE",
                        List.of(
                                new RegisterSellerOptionGroupsCommand.OptionGroupCommand(
                                        "색상",
                                        null,
                                        List.of(
                                                new RegisterSellerOptionGroupsCommand
                                                        .OptionValueCommand("검정", null, 0)))));

        RegisterProductGroupDescriptionCommand descriptionCommand =
                new RegisterProductGroupDescriptionCommand(0L, "<p>상품 상세설명</p>");

        RegisterProductNoticeCommand noticeCommand =
                new RegisterProductNoticeCommand(
                        0L,
                        10L,
                        List.of(new RegisterProductNoticeCommand.NoticeEntryCommand(1L, "100% 면")));

        RegisterProductsCommand productCommand =
                new RegisterProductsCommand(
                        0L,
                        List.of(
                                new RegisterProductsCommand.ProductData(
                                        "SKU-001",
                                        10000,
                                        9000,
                                        100,
                                        0,
                                        List.of(new SelectedOption("색상", "검정")))));

        return new ProductGroupRegistrationBundle(
                productGroup,
                imageCommand,
                optionGroupCommand,
                descriptionCommand,
                noticeCommand,
                productCommand,
                CommonVoFixtures.now());
    }
}
