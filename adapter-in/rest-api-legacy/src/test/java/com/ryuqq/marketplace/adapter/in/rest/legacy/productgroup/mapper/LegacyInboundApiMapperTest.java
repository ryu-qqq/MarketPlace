package com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.LegacyProductGroupApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.request.LegacyCreateProductGroupRequest;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroup.dto.request.LegacyUpdateProductGroupRequest;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.command.LegacyRegisterProductGroupCommand;
import com.ryuqq.marketplace.application.legacy.productgroup.dto.command.LegacyUpdateProductGroupCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("LegacyInboundApiMapper 단위 테스트")
class LegacyInboundApiMapperTest {

    private LegacyInboundApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper =
                new LegacyInboundApiMapper(
                        new com.ryuqq.marketplace.adapter.in.rest.legacy.product.validator
                                .LegacyOptionValidator());
    }

    @Nested
    @DisplayName("toCommand - 상품그룹 등록 요청 변환")
    class ToCommandTest {

        @Test
        @DisplayName("LegacyCreateProductGroupRequest를 LegacyRegisterProductGroupCommand로 변환한다")
        void toCommand_ConvertsCreateRequest_ReturnsCommand() {
            // given
            LegacyCreateProductGroupRequest request = LegacyProductGroupApiFixtures.createRequest();

            // when
            LegacyRegisterProductGroupCommand command = mapper.toCommand(request);

            // then
            assertThat(command.sellerId())
                    .isEqualTo(LegacyProductGroupApiFixtures.DEFAULT_SELLER_ID);
            assertThat(command.brandId()).isEqualTo(LegacyProductGroupApiFixtures.DEFAULT_BRAND_ID);
            assertThat(command.categoryId())
                    .isEqualTo(LegacyProductGroupApiFixtures.DEFAULT_CATEGORY_ID);
            assertThat(command.productGroupName())
                    .isEqualTo(LegacyProductGroupApiFixtures.DEFAULT_PRODUCT_GROUP_NAME);
            assertThat(command.optionType())
                    .isEqualTo(LegacyProductGroupApiFixtures.DEFAULT_OPTION_TYPE);
            assertThat(command.managementType())
                    .isEqualTo(LegacyProductGroupApiFixtures.DEFAULT_MANAGEMENT_TYPE);
        }

        @Test
        @DisplayName("가격 정보가 올바르게 변환된다")
        void toCommand_ConvertsPriceInfo_ReturnsCorrectPrice() {
            // given
            LegacyCreateProductGroupRequest request = LegacyProductGroupApiFixtures.createRequest();

            // when
            LegacyRegisterProductGroupCommand command = mapper.toCommand(request);

            // then
            assertThat(command.regularPrice())
                    .isEqualTo(LegacyProductGroupApiFixtures.DEFAULT_REGULAR_PRICE);
            assertThat(command.currentPrice())
                    .isEqualTo(LegacyProductGroupApiFixtures.DEFAULT_CURRENT_PRICE);
        }

        @Test
        @DisplayName("상품 상태 정보가 올바르게 변환된다")
        void toCommand_ConvertsProductStatus_ReturnsCorrectStatus() {
            // given
            LegacyCreateProductGroupRequest request = LegacyProductGroupApiFixtures.createRequest();

            // when
            LegacyRegisterProductGroupCommand command = mapper.toCommand(request);

            // then
            assertThat(command.soldOutYn()).isEqualTo("N");
            assertThat(command.displayYn()).isEqualTo("Y");
        }

        @Test
        @DisplayName("의류 상세정보가 올바르게 변환된다")
        void toCommand_ConvertsClothesDetail_ReturnsCorrectDetail() {
            // given
            LegacyCreateProductGroupRequest request = LegacyProductGroupApiFixtures.createRequest();

            // when
            LegacyRegisterProductGroupCommand command = mapper.toCommand(request);

            // then
            assertThat(command.productCondition())
                    .isEqualTo(LegacyProductGroupApiFixtures.DEFAULT_PRODUCT_CONDITION);
            assertThat(command.origin()).isEqualTo(LegacyProductGroupApiFixtures.DEFAULT_ORIGIN);
            assertThat(command.styleCode())
                    .isEqualTo(LegacyProductGroupApiFixtures.DEFAULT_STYLE_CODE);
        }

        @Test
        @DisplayName("고시정보가 올바르게 변환된다")
        void toCommand_ConvertsNotice_ReturnsNoticeCommand() {
            // given
            LegacyCreateProductGroupRequest request = LegacyProductGroupApiFixtures.createRequest();

            // when
            LegacyRegisterProductGroupCommand command = mapper.toCommand(request);

            // then
            assertThat(command.notice()).isNotNull();
            assertThat(command.notice().material()).isEqualTo("면 100%");
            assertThat(command.notice().color()).isEqualTo("블랙");
            assertThat(command.notice().origin()).isEqualTo("대한민국");
        }

        @Test
        @DisplayName("배송/반품 정보가 올바르게 변환된다")
        void toCommand_ConvertsDeliveryAndRefund_ReturnsDeliveryCommand() {
            // given
            LegacyCreateProductGroupRequest request = LegacyProductGroupApiFixtures.createRequest();

            // when
            LegacyRegisterProductGroupCommand command = mapper.toCommand(request);

            // then
            assertThat(command.delivery()).isNotNull();
            assertThat(command.delivery().deliveryArea()).isEqualTo("전국");
            assertThat(command.delivery().deliveryFee()).isEqualTo(3000L);
            assertThat(command.delivery().returnMethodDomestic()).isEqualTo("택배");
            assertThat(command.delivery().returnCourierDomestic()).isEqualTo("CJ대한통운");
        }

        @Test
        @DisplayName("이미지 목록이 올바르게 변환된다")
        void toCommand_ConvertsImages_ReturnsImageCommands() {
            // given
            LegacyCreateProductGroupRequest request = LegacyProductGroupApiFixtures.createRequest();

            // when
            LegacyRegisterProductGroupCommand command = mapper.toCommand(request);

            // then
            assertThat(command.images()).hasSize(2);
            assertThat(command.images().get(0).imageType()).isEqualTo("MAIN");
            assertThat(command.images().get(0).imageUrl())
                    .isEqualTo("https://cdn.example.com/main.jpg");
        }

        @Test
        @DisplayName("옵션 목록이 올바르게 변환된다")
        void toCommand_ConvertsOptions_ReturnsOptionCommands() {
            // given
            LegacyCreateProductGroupRequest request = LegacyProductGroupApiFixtures.createRequest();

            // when
            LegacyRegisterProductGroupCommand command = mapper.toCommand(request);

            // then
            assertThat(command.options()).hasSize(2);
            assertThat(command.options().get(0).quantity()).isEqualTo(100);
            assertThat(command.options().get(0).optionDetails()).hasSize(1);
            assertThat(command.options().get(0).optionDetails().get(0).optionName())
                    .isEqualTo("색상");
        }

        @Test
        @DisplayName("SINGLE 옵션 타입인 경우 옵션이 0개여야 한다")
        void toCommand_SingleOptionType_NoOptionDetails() {
            // given
            LegacyCreateProductGroupRequest request =
                    LegacyProductGroupApiFixtures.createRequestSingleOption();

            // when
            LegacyRegisterProductGroupCommand command = mapper.toCommand(request);

            // then
            assertThat(command.optionType()).isEqualTo("SINGLE");
            assertThat(command.options().get(0).optionDetails()).isEmpty();
        }

        @Test
        @DisplayName("SINGLE 옵션 타입에 옵션 항목이 있으면 IllegalArgumentException이 발생한다")
        void toCommand_SingleOptionTypeWithOptions_ThrowsException() {
            // given - SINGLE 타입인데 options가 1개 있는 요청
            LegacyCreateProductGroupRequest request = LegacyProductGroupApiFixtures.createRequest();
            // createRequest()는 OPTION_ONE이므로 바로 SINGLE로 변환하면 예외

            // optionType을 SINGLE로 바꾸면 options()에 1개 있어 예외
            LegacyCreateProductGroupRequest invalidRequest =
                    new LegacyCreateProductGroupRequest(
                            request.productGroupName(),
                            request.sellerId(),
                            "SINGLE",
                            request.managementType(),
                            request.categoryId(),
                            request.brandId(),
                            request.productStatus(),
                            request.price(),
                            request.productNotice(),
                            request.clothesDetailInfo(),
                            request.deliveryNotice(),
                            request.refundNotice(),
                            request.productImageList(),
                            request.detailDescription(),
                            request.productOptions());

            // when & then
            assertThatThrownBy(() -> mapper.toCommand(invalidRequest))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining("SINGLE");
        }
    }

    @Nested
    @DisplayName("toUpdateCommand - 상품그룹 수정 요청 변환")
    class ToUpdateCommandTest {

        @Test
        @DisplayName("LegacyUpdateProductGroupRequest를 LegacyUpdateProductGroupCommand로 변환한다")
        void toUpdateCommand_ConvertsUpdateRequest_ReturnsCommand() {
            // given
            long productGroupId = LegacyProductGroupApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            LegacyUpdateProductGroupRequest request = LegacyProductGroupApiFixtures.updateRequest();

            // when
            LegacyUpdateProductGroupCommand command =
                    mapper.toUpdateCommand(request, productGroupId);

            // then
            assertThat(command.productGroupId()).isEqualTo(productGroupId);
        }

        @Test
        @DisplayName("productGroupDetails가 있으면 올바르게 변환된다")
        void toUpdateCommand_WithProductGroupDetails_ConvertsCorrectly() {
            // given
            long productGroupId = LegacyProductGroupApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            LegacyUpdateProductGroupRequest request = LegacyProductGroupApiFixtures.updateRequest();

            // when
            LegacyUpdateProductGroupCommand command =
                    mapper.toUpdateCommand(request, productGroupId);

            // then
            assertThat(command.productGroupDetails()).isNotNull();
            assertThat(command.productGroupDetails().productGroupName())
                    .isEqualTo(LegacyProductGroupApiFixtures.DEFAULT_PRODUCT_GROUP_NAME);
            assertThat(command.productGroupDetails().sellerId())
                    .isEqualTo(LegacyProductGroupApiFixtures.DEFAULT_SELLER_ID);
        }

        @Test
        @DisplayName("updateStatus 플래그가 올바르게 변환된다")
        void toUpdateCommand_ConvertsUpdateStatus_ReturnsCorrectFlags() {
            // given
            long productGroupId = LegacyProductGroupApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            LegacyUpdateProductGroupRequest request = LegacyProductGroupApiFixtures.updateRequest();

            // when
            LegacyUpdateProductGroupCommand command =
                    mapper.toUpdateCommand(request, productGroupId);

            // then
            assertThat(command.updateStatus()).isNotNull();
            assertThat(command.updateStatus().productStatus()).isTrue();
            assertThat(command.updateStatus().imageStatus()).isTrue();
            assertThat(command.updateStatus().stockOptionStatus()).isTrue();
        }

        @Test
        @DisplayName("productGroupDetails가 null이면 null로 변환된다")
        void toUpdateCommand_NullProductGroupDetails_ReturnsNullDetails() {
            // given
            long productGroupId = LegacyProductGroupApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            LegacyUpdateProductGroupRequest request =
                    LegacyProductGroupApiFixtures.updateRequestMinimal();

            // when
            LegacyUpdateProductGroupCommand command =
                    mapper.toUpdateCommand(request, productGroupId);

            // then
            assertThat(command.productGroupDetails()).isNull();
        }

        @Test
        @DisplayName("이미지 목록이 비어있으면 빈 리스트로 변환된다")
        void toUpdateCommand_EmptyImageList_ReturnsEmptyImages() {
            // given
            long productGroupId = LegacyProductGroupApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            LegacyUpdateProductGroupRequest request =
                    LegacyProductGroupApiFixtures.updateRequestMinimal();

            // when
            LegacyUpdateProductGroupCommand command =
                    mapper.toUpdateCommand(request, productGroupId);

            // then
            assertThat(command.images()).isEmpty();
        }
    }
}
