package com.ryuqq.marketplace.adapter.in.rest.productgroup.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.productgroup.ProductGroupApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.command.BatchChangeProductGroupStatusApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.command.RegisterProductGroupApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.command.RegisterProductGroupExcelApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.command.UpdateProductGroupBasicInfoApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.productgroup.dto.command.UpdateProductGroupFullApiRequest;
import com.ryuqq.marketplace.application.productgroup.dto.command.BatchChangeProductGroupStatusCommand;
import com.ryuqq.marketplace.application.productgroup.dto.command.RegisterProductGroupCommand;
import com.ryuqq.marketplace.application.productgroup.dto.command.UpdateProductGroupBasicInfoCommand;
import com.ryuqq.marketplace.application.productgroup.dto.command.UpdateProductGroupFullCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ProductGroupCommandApiMapper 단위 테스트")
class ProductGroupCommandApiMapperTest {

    private ProductGroupCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ProductGroupCommandApiMapper();
    }

    @Nested
    @DisplayName("toCommand(RegisterProductGroupApiRequest) - 상품 그룹 등록 요청 변환")
    class ToRegisterCommandTest {

        @Test
        @DisplayName("RegisterProductGroupApiRequest를 RegisterProductGroupCommand로 변환한다")
        void toCommand_ConvertsRegisterRequest_ReturnsCommand() {
            // given
            RegisterProductGroupApiRequest request = ProductGroupApiFixtures.registerRequest();

            // when
            RegisterProductGroupCommand command = mapper.toCommand(request);

            // then
            assertThat(command.sellerId()).isEqualTo(ProductGroupApiFixtures.DEFAULT_SELLER_ID);
            assertThat(command.brandId()).isEqualTo(ProductGroupApiFixtures.DEFAULT_BRAND_ID);
            assertThat(command.categoryId()).isEqualTo(ProductGroupApiFixtures.DEFAULT_CATEGORY_ID);
            assertThat(command.productGroupName())
                    .isEqualTo(ProductGroupApiFixtures.DEFAULT_PRODUCT_GROUP_NAME);
            assertThat(command.optionType()).isEqualTo(ProductGroupApiFixtures.DEFAULT_OPTION_TYPE);
        }

        @Test
        @DisplayName("이미지 목록이 올바르게 변환된다")
        void toCommand_ConvertsImages_ReturnsImageCommands() {
            // given
            RegisterProductGroupApiRequest request = ProductGroupApiFixtures.registerRequest();

            // when
            RegisterProductGroupCommand command = mapper.toCommand(request);

            // then
            assertThat(command.images()).hasSize(2);
            assertThat(command.images().get(0).imageType()).isEqualTo("THUMBNAIL");
            assertThat(command.images().get(0).originUrl())
                    .isEqualTo("https://origin.example.com/img1.jpg");
            assertThat(command.images().get(0).sortOrder()).isEqualTo(1);
        }

        @Test
        @DisplayName("옵션 그룹 목록이 올바르게 변환된다")
        void toCommand_ConvertsOptionGroups_ReturnsOptionGroupCommands() {
            // given
            RegisterProductGroupApiRequest request = ProductGroupApiFixtures.registerRequest();

            // when
            RegisterProductGroupCommand command = mapper.toCommand(request);

            // then
            assertThat(command.optionGroups()).hasSize(1);
            assertThat(command.optionGroups().get(0).optionGroupName()).isEqualTo("색상");
            assertThat(command.optionGroups().get(0).canonicalOptionGroupId()).isEqualTo(10L);
            assertThat(command.optionGroups().get(0).optionValues()).hasSize(2);
            assertThat(command.optionGroups().get(0).optionValues().get(0).optionValueName())
                    .isEqualTo("블랙");
        }

        @Test
        @DisplayName("상품 목록이 올바르게 변환된다")
        void toCommand_ConvertsProducts_ReturnsProductCommands() {
            // given
            RegisterProductGroupApiRequest request = ProductGroupApiFixtures.registerRequest();

            // when
            RegisterProductGroupCommand command = mapper.toCommand(request);

            // then
            assertThat(command.products()).hasSize(2);
            assertThat(command.products().get(0).skuCode()).isEqualTo("SKU-001");
            assertThat(command.products().get(0).regularPrice()).isEqualTo(30000);
            assertThat(command.products().get(0).currentPrice()).isEqualTo(25000);
            assertThat(command.products().get(0).stockQuantity()).isEqualTo(100);
        }

        @Test
        @DisplayName("상세설명이 올바르게 변환된다")
        void toCommand_ConvertsDescription_ReturnsDescriptionCommand() {
            // given
            RegisterProductGroupApiRequest request = ProductGroupApiFixtures.registerRequest();

            // when
            RegisterProductGroupCommand command = mapper.toCommand(request);

            // then
            assertThat(command.description()).isNotNull();
            assertThat(command.description().content()).isEqualTo("<p>상품 상세 설명</p>");
        }

        @Test
        @DisplayName("고시정보가 올바르게 변환된다")
        void toCommand_ConvertsNotice_ReturnsNoticeCommand() {
            // given
            RegisterProductGroupApiRequest request = ProductGroupApiFixtures.registerRequest();

            // when
            RegisterProductGroupCommand command = mapper.toCommand(request);

            // then
            assertThat(command.notice()).isNotNull();
            assertThat(command.notice().noticeCategoryId()).isEqualTo(1L);
            assertThat(command.notice().entries()).hasSize(2);
            assertThat(command.notice().entries().get(0).fieldValue()).isEqualTo("제조사");
        }
    }

    @Nested
    @DisplayName("toCommand(Long, UpdateProductGroupFullApiRequest) - 전체 수정 요청 변환")
    class ToUpdateFullCommandTest {

        @Test
        @DisplayName("UpdateProductGroupFullApiRequest를 UpdateProductGroupFullCommand로 변환한다")
        void toCommand_ConvertsUpdateFullRequest_ReturnsCommand() {
            // given
            Long productGroupId = 10L;
            UpdateProductGroupFullApiRequest request = ProductGroupApiFixtures.updateFullRequest();

            // when
            UpdateProductGroupFullCommand command = mapper.toCommand(productGroupId, request);

            // then
            assertThat(command.productGroupId()).isEqualTo(10L);
            assertThat(command.productGroupName())
                    .isEqualTo("수정된 " + ProductGroupApiFixtures.DEFAULT_PRODUCT_GROUP_NAME);
            assertThat(command.brandId()).isEqualTo(ProductGroupApiFixtures.DEFAULT_BRAND_ID);
            assertThat(command.categoryId()).isEqualTo(ProductGroupApiFixtures.DEFAULT_CATEGORY_ID);
        }

        @Test
        @DisplayName("이미지 목록이 올바르게 변환된다")
        void toCommand_ConvertsImages_ReturnsImageCommands() {
            // given
            Long productGroupId = 10L;
            UpdateProductGroupFullApiRequest request = ProductGroupApiFixtures.updateFullRequest();

            // when
            UpdateProductGroupFullCommand command = mapper.toCommand(productGroupId, request);

            // then
            assertThat(command.images()).hasSize(1);
            assertThat(command.images().get(0).imageType()).isEqualTo("THUMBNAIL");
            assertThat(command.images().get(0).originUrl())
                    .isEqualTo("https://origin.example.com/updated-img1.jpg");
        }

        @Test
        @DisplayName("옵션 그룹이 sellerOptionGroupId 포함하여 올바르게 변환된다")
        void toCommand_ConvertsOptionGroupsWithIds_ReturnsOptionGroupCommands() {
            // given
            Long productGroupId = 10L;
            UpdateProductGroupFullApiRequest request = ProductGroupApiFixtures.updateFullRequest();

            // when
            UpdateProductGroupFullCommand command = mapper.toCommand(productGroupId, request);

            // then
            assertThat(command.optionGroups()).hasSize(1);
            assertThat(command.optionGroups().get(0).sellerOptionGroupId()).isEqualTo(1L);
            assertThat(command.optionGroups().get(0).optionGroupName()).isEqualTo("색상");
            assertThat(command.optionGroups().get(0).optionValues()).hasSize(2);
            assertThat(command.optionGroups().get(0).optionValues().get(0).sellerOptionValueId())
                    .isEqualTo(1L);
        }

        @Test
        @DisplayName("상품 목록이 productId 포함하여 올바르게 변환된다")
        void toCommand_ConvertsProductsWithIds_ReturnsProductCommands() {
            // given
            Long productGroupId = 10L;
            UpdateProductGroupFullApiRequest request = ProductGroupApiFixtures.updateFullRequest();

            // when
            UpdateProductGroupFullCommand command = mapper.toCommand(productGroupId, request);

            // then
            assertThat(command.products()).hasSize(1);
            assertThat(command.products().get(0).productId()).isEqualTo(1L);
            assertThat(command.products().get(0).skuCode()).isEqualTo("SKU-001");
            assertThat(command.products().get(0).regularPrice()).isEqualTo(35000);
        }
    }

    @Nested
    @DisplayName("toCommand(Long, UpdateProductGroupBasicInfoApiRequest) - 기본정보 수정 요청 변환")
    class ToUpdateBasicInfoCommandTest {

        @Test
        @DisplayName(
                "UpdateProductGroupBasicInfoApiRequest를 UpdateProductGroupBasicInfoCommand로 변환한다")
        void toCommand_ConvertsBasicInfoRequest_ReturnsCommand() {
            // given
            Long productGroupId = 10L;
            UpdateProductGroupBasicInfoApiRequest request =
                    ProductGroupApiFixtures.updateBasicInfoRequest();

            // when
            UpdateProductGroupBasicInfoCommand command = mapper.toCommand(productGroupId, request);

            // then
            assertThat(command.productGroupId()).isEqualTo(10L);
            assertThat(command.productGroupName())
                    .isEqualTo("수정된 " + ProductGroupApiFixtures.DEFAULT_PRODUCT_GROUP_NAME);
            assertThat(command.brandId()).isEqualTo(ProductGroupApiFixtures.DEFAULT_BRAND_ID);
            assertThat(command.categoryId()).isEqualTo(ProductGroupApiFixtures.DEFAULT_CATEGORY_ID);
            assertThat(command.shippingPolicyId()).isEqualTo(1L);
            assertThat(command.refundPolicyId()).isEqualTo(1L);
        }
    }

    @Nested
    @DisplayName("toCommand(long, BatchChangeProductGroupStatusApiRequest) - 배치 상태 변경 요청 변환")
    class ToBatchChangeStatusCommandTest {

        @Test
        @DisplayName(
                "BatchChangeProductGroupStatusApiRequest를 BatchChangeProductGroupStatusCommand로"
                        + " 변환한다")
        void toCommand_ConvertsBatchStatusRequest_ReturnsCommand() {
            // given
            long sellerId = 5L;
            BatchChangeProductGroupStatusApiRequest request =
                    ProductGroupApiFixtures.batchChangeStatusRequest();

            // when
            BatchChangeProductGroupStatusCommand command = mapper.toCommand(sellerId, request);

            // then
            assertThat(command.sellerId()).isEqualTo(5L);
            assertThat(command.productGroupIds()).containsExactly(1L, 2L, 3L);
            assertThat(command.targetStatus()).isEqualTo("ACTIVE");
        }
    }

    @Nested
    @DisplayName("toCommand(long, RegisterProductGroupExcelApiRequest) - 엑셀 등록 요청 변환")
    class ToExcelRegisterCommandTest {

        @Test
        @DisplayName("엑셀 요청은 sellerId 주입 및 정책 미해결 상태로 변환된다")
        void toCommand_ExcelRequest_InjectsSellerAndLeavesPoliciesUnresolved() {
            // given
            long sellerId = 9L;
            RegisterProductGroupExcelApiRequest request =
                    ProductGroupApiFixtures.registerExcelRequest();

            // when
            RegisterProductGroupCommand command = mapper.toCommand(sellerId, request);

            // then
            assertThat(command.sellerId()).isEqualTo(sellerId);
            assertThat(command.shippingPolicyId()).isZero();
            assertThat(command.refundPolicyId()).isZero();
        }
    }
}
