package com.ryuqq.marketplace.application.productgroup.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.product.dto.command.SelectedOption;
import com.ryuqq.marketplace.application.product.internal.ProductCommandCoordinator;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle.ImageEntry;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle.NoticeRegistrationData;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle.NoticeRegistrationData.NoticeEntry;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle.OptionRegistrationData;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle.OptionRegistrationData.OptionGroupEntry;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle.OptionRegistrationData.OptionGroupEntry.OptionValueEntry;
import com.ryuqq.marketplace.application.productgroup.dto.bundle.ProductGroupRegistrationBundle.ProductEntry;
import com.ryuqq.marketplace.application.productgroup.dto.result.ProductGroupRegistrationResult;
import com.ryuqq.marketplace.application.productgroupdescription.internal.DescriptionCommandCoordinator;
import com.ryuqq.marketplace.application.productgroupimage.internal.ImageCommandCoordinator;
import com.ryuqq.marketplace.application.productintelligence.manager.IntelligenceOutboxCommandManager;
import com.ryuqq.marketplace.application.productnotice.internal.ProductNoticeCommandCoordinator;
import com.ryuqq.marketplace.application.selleroption.internal.SellerOptionCommandCoordinator;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroup;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productgroup.id.SellerOptionValueId;
import com.ryuqq.marketplace.domain.productgroup.vo.OptionType;
import com.ryuqq.marketplace.domain.productgroup.vo.SellerOptionGroups;
import com.ryuqq.marketplace.domain.productgroupimage.vo.ProductGroupImages;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice;
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
    @Mock private IntelligenceOutboxCommandManager intelligenceOutboxCommandManager;

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
                                    any(SellerOptionGroups.class), any(OptionType.class)))
                    .willReturn(optionValueIds);

            // when
            ProductGroupRegistrationResult result = sut.register(bundle);

            // then
            assertThat(result.productGroupId()).isEqualTo(expectedProductGroupId);
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
                                    any(SellerOptionGroups.class), any(OptionType.class)))
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
                                    any(SellerOptionGroups.class), any(OptionType.class)))
                    .willReturn(optionValueIds);

            // when
            sut.register(bundle);

            // then
            then(imageCommandCoordinator).should().register(any(ProductGroupImages.class));
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
                                    any(SellerOptionGroups.class), any(OptionType.class)))
                    .willReturn(optionValueIds);

            // when
            sut.register(bundle);

            // then
            then(sellerOptionCommandCoordinator)
                    .should()
                    .register(any(SellerOptionGroups.class), any(OptionType.class));
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
                                    any(SellerOptionGroups.class), any(OptionType.class)))
                    .willReturn(optionValueIds);

            // when
            sut.register(bundle);

            // then
            then(descriptionCommandCoordinator)
                    .should()
                    .persist(any(ProductGroupDescription.class));
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
                                    any(SellerOptionGroups.class), any(OptionType.class)))
                    .willReturn(optionValueIds);

            // when
            sut.register(bundle);

            // then
            then(noticeCommandCoordinator).should().register(any(ProductNotice.class));
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
                                    any(SellerOptionGroups.class), any(OptionType.class)))
                    .willReturn(optionValueIds);

            // when
            sut.register(bundle);

            // then
            then(productCommandCoordinator).should().register(any(List.class));
        }
    }

    // ===== 헬퍼 메서드 =====

    private ProductGroupRegistrationBundle createBundle() {
        ProductGroup productGroup = ProductGroupFixtures.newProductGroup();

        List<ImageEntry> images =
                List.of(new ImageEntry("THUMBNAIL", "https://example.com/image.jpg", 0));

        OptionRegistrationData optionData =
                new OptionRegistrationData(
                        OptionType.SINGLE,
                        List.of(
                                new OptionGroupEntry(
                                        "색상",
                                        null,
                                        null,
                                        List.of(new OptionValueEntry("검정", null, 0)))));

        String descriptionContent = "<p>상품 상세설명</p>";

        NoticeRegistrationData noticeData =
                new NoticeRegistrationData(10L, List.of(new NoticeEntry(1L, "100% 면")));

        List<ProductEntry> products =
                List.of(
                        new ProductEntry(
                                "SKU-001",
                                10000,
                                9000,
                                100,
                                0,
                                List.of(new SelectedOption("색상", "검정"))));

        return new ProductGroupRegistrationBundle(
                productGroup,
                images,
                optionData,
                descriptionContent,
                noticeData,
                products,
                CommonVoFixtures.now());
    }
}
