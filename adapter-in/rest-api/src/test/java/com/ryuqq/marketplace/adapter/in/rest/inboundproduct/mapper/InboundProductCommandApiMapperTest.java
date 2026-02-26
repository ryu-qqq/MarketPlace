package com.ryuqq.marketplace.adapter.in.rest.inboundproduct.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.InboundProductApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.command.ReceiveInboundProductApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.command.UpdateInboundProductImagesApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.command.UpdateInboundProductStockApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.inboundproduct.dto.response.InboundProductConversionApiResponse;
import com.ryuqq.marketplace.application.inboundproduct.dto.command.ReceiveInboundProductCommand;
import com.ryuqq.marketplace.application.inboundproduct.dto.response.InboundProductConversionResult;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductStockCommand;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.UpdateProductGroupImagesCommand;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("InboundProductCommandApiMapper 단위 테스트")
class InboundProductCommandApiMapperTest {

    private InboundProductCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new InboundProductCommandApiMapper();
    }

    @Nested
    @DisplayName("toCommand(ReceiveInboundProductApiRequest) - 인바운드 상품 수신 요청 변환")
    class ToCommandTest {

        @Test
        @DisplayName("ReceiveInboundProductApiRequest를 ReceiveInboundProductCommand로 변환한다")
        void toCommand_ConvertsReceiveRequest_ReturnsCommand() {
            // given
            ReceiveInboundProductApiRequest request = InboundProductApiFixtures.receiveRequest();

            // when
            ReceiveInboundProductCommand command = mapper.toCommand(request);

            // then
            assertThat(command.inboundSourceId())
                    .isEqualTo(InboundProductApiFixtures.DEFAULT_INBOUND_SOURCE_ID);
            assertThat(command.externalProductCode())
                    .isEqualTo(InboundProductApiFixtures.DEFAULT_EXTERNAL_PRODUCT_CODE);
            assertThat(command.productName())
                    .isEqualTo(InboundProductApiFixtures.DEFAULT_PRODUCT_NAME);
            assertThat(command.externalBrandCode())
                    .isEqualTo(InboundProductApiFixtures.DEFAULT_EXTERNAL_BRAND_CODE);
            assertThat(command.externalCategoryCode())
                    .isEqualTo(InboundProductApiFixtures.DEFAULT_EXTERNAL_CATEGORY_CODE);
            assertThat(command.sellerId()).isEqualTo(InboundProductApiFixtures.DEFAULT_SELLER_ID);
            assertThat(command.regularPrice())
                    .isEqualTo(InboundProductApiFixtures.DEFAULT_REGULAR_PRICE);
            assertThat(command.currentPrice())
                    .isEqualTo(InboundProductApiFixtures.DEFAULT_CURRENT_PRICE);
            assertThat(command.optionType())
                    .isEqualTo(InboundProductApiFixtures.DEFAULT_OPTION_TYPE);
        }

        @Test
        @DisplayName("이미지 목록이 올바르게 변환된다")
        void toCommand_ConvertsImages_ReturnsImageCommands() {
            // given
            ReceiveInboundProductApiRequest request = InboundProductApiFixtures.receiveRequest();

            // when
            ReceiveInboundProductCommand command = mapper.toCommand(request);

            // then
            assertThat(command.images()).hasSize(2);
            assertThat(command.images().get(0).imageType()).isEqualTo("THUMBNAIL");
            assertThat(command.images().get(0).originUrl())
                    .isEqualTo("https://example.com/image1.jpg");
            assertThat(command.images().get(0).sortOrder()).isZero();
            assertThat(command.images().get(1).imageType()).isEqualTo("DETAIL");
        }

        @Test
        @DisplayName("이미지 목록이 null이면 빈 리스트로 변환된다")
        void toCommand_NullImages_ReturnsEmptyList() {
            // given
            ReceiveInboundProductApiRequest request =
                    InboundProductApiFixtures.receiveRequestWithoutNotice();

            // when
            ReceiveInboundProductCommand command = mapper.toCommand(request);

            // then
            assertThat(command.images()).isNotNull();
        }

        @Test
        @DisplayName("옵션 그룹 목록이 올바르게 변환된다")
        void toCommand_ConvertsOptionGroups_ReturnsOptionGroupCommands() {
            // given
            ReceiveInboundProductApiRequest request = InboundProductApiFixtures.receiveRequest();

            // when
            ReceiveInboundProductCommand command = mapper.toCommand(request);

            // then
            assertThat(command.optionGroups()).hasSize(1);
            assertThat(command.optionGroups().get(0).optionGroupName()).isEqualTo("색상");
            assertThat(command.optionGroups().get(0).inputType()).isEqualTo("PREDEFINED");
            assertThat(command.optionGroups().get(0).optionValues()).hasSize(2);
            assertThat(command.optionGroups().get(0).optionValues().get(0).optionValueName())
                    .isEqualTo("블랙");
            assertThat(command.optionGroups().get(0).optionValues().get(0).sortOrder()).isZero();
        }

        @Test
        @DisplayName("옵션 그룹이 null이면 빈 리스트로 변환된다")
        void toCommand_NullOptionGroups_ReturnsEmptyList() {
            // given
            ReceiveInboundProductApiRequest request =
                    InboundProductApiFixtures.receiveRequestWithoutNotice();

            // when
            ReceiveInboundProductCommand command = mapper.toCommand(request);

            // then
            assertThat(command.optionGroups()).isEmpty();
        }

        @Test
        @DisplayName("상품 목록이 올바르게 변환된다")
        void toCommand_ConvertsProducts_ReturnsProductCommands() {
            // given
            ReceiveInboundProductApiRequest request = InboundProductApiFixtures.receiveRequest();

            // when
            ReceiveInboundProductCommand command = mapper.toCommand(request);

            // then
            assertThat(command.products()).hasSize(2);
            assertThat(command.products().get(0).skuCode()).isEqualTo("SKU-001");
            assertThat(command.products().get(0).regularPrice())
                    .isEqualTo(InboundProductApiFixtures.DEFAULT_REGULAR_PRICE);
            assertThat(command.products().get(0).currentPrice())
                    .isEqualTo(InboundProductApiFixtures.DEFAULT_CURRENT_PRICE);
            assertThat(command.products().get(0).stockQuantity()).isEqualTo(100);
            assertThat(command.products().get(0).sortOrder()).isZero();
        }

        @Test
        @DisplayName("선택된 옵션 목록이 올바르게 변환된다")
        void toCommand_ConvertsSelectedOptions_ReturnsSelectedOptionCommands() {
            // given
            ReceiveInboundProductApiRequest request = InboundProductApiFixtures.receiveRequest();

            // when
            ReceiveInboundProductCommand command = mapper.toCommand(request);

            // then
            assertThat(command.products().get(0).selectedOptions()).hasSize(1);
            assertThat(command.products().get(0).selectedOptions().get(0).optionGroupName())
                    .isEqualTo("색상");
            assertThat(command.products().get(0).selectedOptions().get(0).optionValueName())
                    .isEqualTo("블랙");
        }

        @Test
        @DisplayName("상세설명이 올바르게 변환된다")
        void toCommand_ConvertsDescription_ReturnsDescriptionCommand() {
            // given
            ReceiveInboundProductApiRequest request = InboundProductApiFixtures.receiveRequest();

            // when
            ReceiveInboundProductCommand command = mapper.toCommand(request);

            // then
            assertThat(command.description()).isNotNull();
            assertThat(command.description().content()).isEqualTo("<p>상품 상세 설명입니다.</p>");
        }

        @Test
        @DisplayName("상세설명이 null이면 content가 null인 DescriptionCommand로 변환된다")
        void toCommand_NullDescription_ReturnsDescriptionCommandWithNullContent() {
            // given
            ReceiveInboundProductApiRequest request =
                    new ReceiveInboundProductApiRequest(
                            1L,
                            "EXT-001",
                            "상품명",
                            "BRAND-001",
                            "CAT-001",
                            1L,
                            30000,
                            25000,
                            "SINGLE",
                            List.of(
                                    new ReceiveInboundProductApiRequest.ImageRequest(
                                            "THUMBNAIL", "https://example.com/img.jpg", 0)),
                            List.of(),
                            List.of(
                                    new ReceiveInboundProductApiRequest.ProductRequest(
                                            "SKU-001", 30000, 25000, 100, 0, List.of())),
                            null,
                            null);

            // when
            ReceiveInboundProductCommand command = mapper.toCommand(request);

            // then
            assertThat(command.description()).isNotNull();
            assertThat(command.description().content()).isNull();
        }

        @Test
        @DisplayName("고시정보가 올바르게 변환된다")
        void toCommand_ConvertsNotice_ReturnsNoticeCommand() {
            // given
            ReceiveInboundProductApiRequest request = InboundProductApiFixtures.receiveRequest();

            // when
            ReceiveInboundProductCommand command = mapper.toCommand(request);

            // then
            assertThat(command.notice()).isNotNull();
            assertThat(command.notice().entries()).hasSize(2);
            assertThat(command.notice().entries().get(0).fieldCode()).isEqualTo("MATERIAL");
            assertThat(command.notice().entries().get(0).fieldValue()).isEqualTo("면 100%");
        }

        @Test
        @DisplayName("고시정보가 null이면 빈 entries를 가진 NoticeCommand로 변환된다")
        void toCommand_NullNotice_ReturnsNoticeCommandWithEmptyEntries() {
            // given
            ReceiveInboundProductApiRequest request =
                    InboundProductApiFixtures.receiveRequestWithoutNotice();

            // when
            ReceiveInboundProductCommand command = mapper.toCommand(request);

            // then
            assertThat(command.notice()).isNotNull();
            assertThat(command.notice().entries()).isEmpty();
        }
    }

    @Nested
    @DisplayName("toStockCommands(UpdateInboundProductStockApiRequest) - 재고 수정 요청 변환")
    class ToStockCommandsTest {

        @Test
        @DisplayName("재고 수정 요청을 UpdateProductStockCommand 리스트로 변환한다")
        void toStockCommands_ConvertsStockRequest_ReturnsStockCommands() {
            // given
            UpdateInboundProductStockApiRequest request =
                    InboundProductApiFixtures.updateStockRequest();

            // when
            List<UpdateProductStockCommand> commands = mapper.toStockCommands(request);

            // then
            assertThat(commands).hasSize(2);
            assertThat(commands.get(0).productId()).isEqualTo(1L);
            assertThat(commands.get(0).stockQuantity()).isEqualTo(100);
            assertThat(commands.get(1).productId()).isEqualTo(2L);
            assertThat(commands.get(1).stockQuantity()).isEqualTo(50);
        }

        @Test
        @DisplayName("빈 재고 목록은 빈 커맨드 리스트로 변환된다")
        void toStockCommands_EmptyStocks_ReturnsEmptyList() {
            // given
            UpdateInboundProductStockApiRequest request =
                    new UpdateInboundProductStockApiRequest(List.of());

            // when
            List<UpdateProductStockCommand> commands = mapper.toStockCommands(request);

            // then
            assertThat(commands).isEmpty();
        }
    }

    @Nested
    @DisplayName("toImagesCommand(UpdateInboundProductImagesApiRequest) - 이미지 수정 요청 변환")
    class ToImagesCommandTest {

        @Test
        @DisplayName("이미지 수정 요청을 UpdateProductGroupImagesCommand로 변환한다")
        void toImagesCommand_ConvertsImagesRequest_ReturnsImagesCommand() {
            // given
            UpdateInboundProductImagesApiRequest request =
                    InboundProductApiFixtures.updateImagesRequest();

            // when
            UpdateProductGroupImagesCommand command = mapper.toImagesCommand(request);

            // then
            assertThat(command.images()).hasSize(2);
            assertThat(command.images().get(0).imageType()).isEqualTo("THUMBNAIL");
            assertThat(command.images().get(0).originUrl())
                    .isEqualTo("https://example.com/new-thumbnail.jpg");
            assertThat(command.images().get(0).sortOrder()).isZero();
            assertThat(command.images().get(1).imageType()).isEqualTo("DETAIL");
            assertThat(command.images().get(1).originUrl())
                    .isEqualTo("https://example.com/new-detail.jpg");
        }

        @Test
        @DisplayName("빈 이미지 목록은 빈 images를 가진 커맨드로 변환된다")
        void toImagesCommand_EmptyImages_ReturnsCommandWithEmptyImages() {
            // given
            UpdateInboundProductImagesApiRequest request =
                    new UpdateInboundProductImagesApiRequest(List.of());

            // when
            UpdateProductGroupImagesCommand command = mapper.toImagesCommand(request);

            // then
            assertThat(command.images()).isEmpty();
        }
    }

    @Nested
    @DisplayName("toResponse(InboundProductConversionResult) - 변환 결과 응답 변환")
    class ToResponseTest {

        @Test
        @DisplayName("InboundProductConversionResult를 InboundProductConversionApiResponse로 변환한다")
        void toResponse_ConvertsResult_ReturnsApiResponse() {
            // given
            InboundProductConversionResult result = InboundProductApiFixtures.conversionResult();

            // when
            InboundProductConversionApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.inboundProductId())
                    .isEqualTo(InboundProductApiFixtures.DEFAULT_INBOUND_PRODUCT_ID);
            assertThat(response.internalProductGroupId())
                    .isEqualTo(InboundProductApiFixtures.DEFAULT_INTERNAL_PRODUCT_GROUP_ID);
            assertThat(response.status()).isEqualTo("CONVERTED");
            assertThat(response.action()).isEqualTo("CREATED");
        }

        @Test
        @DisplayName("PENDING_MAPPING 상태의 결과도 올바르게 변환된다")
        void toResponse_PendingMappingResult_ReturnsApiResponse() {
            // given
            InboundProductConversionResult result =
                    InboundProductApiFixtures.conversionPendingMappingResult();

            // when
            InboundProductConversionApiResponse response = mapper.toResponse(result);

            // then
            assertThat(response.inboundProductId())
                    .isEqualTo(InboundProductApiFixtures.DEFAULT_INBOUND_PRODUCT_ID);
            assertThat(response.internalProductGroupId()).isNull();
            assertThat(response.status()).isEqualTo("PENDING_MAPPING");
            assertThat(response.action()).isEqualTo("PENDING_MAPPING");
        }
    }
}
