package com.ryuqq.marketplace.adapter.in.rest.productnotice.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.productnotice.ProductNoticeApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.productnotice.dto.command.UpdateProductNoticeApiRequest;
import com.ryuqq.marketplace.application.productnotice.dto.command.UpdateProductNoticeCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("ProductNoticeCommandApiMapper 단위 테스트")
class ProductNoticeCommandApiMapperTest {

    private ProductNoticeCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ProductNoticeCommandApiMapper();
    }

    @Nested
    @DisplayName("toCommand(Long, UpdateProductNoticeApiRequest) - 고시정보 수정 Command 변환")
    class ToCommandTest {

        @Test
        @DisplayName("productGroupId와 noticeCategoryId가 정확히 Command로 변환된다")
        void toCommand_ValidRequest_ReturnsCommand() {
            // given
            Long productGroupId = ProductNoticeApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            UpdateProductNoticeApiRequest request = ProductNoticeApiFixtures.updateRequest();

            // when
            UpdateProductNoticeCommand command = mapper.toCommand(productGroupId, request);

            // then
            assertThat(command.productGroupId()).isEqualTo(productGroupId);
            assertThat(command.noticeCategoryId())
                    .isEqualTo(ProductNoticeApiFixtures.DEFAULT_NOTICE_CATEGORY_ID);
        }

        @Test
        @DisplayName("고시 항목 목록이 정확히 Command로 변환된다")
        void toCommand_Entries_AreCorrectlyMapped() {
            // given
            Long productGroupId = ProductNoticeApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            UpdateProductNoticeApiRequest request = ProductNoticeApiFixtures.updateRequest();

            // when
            UpdateProductNoticeCommand command = mapper.toCommand(productGroupId, request);

            // then
            assertThat(command.entries()).hasSize(2);
            assertThat(command.entries().get(0).noticeFieldId())
                    .isEqualTo(ProductNoticeApiFixtures.DEFAULT_NOTICE_FIELD_ID_1);
            assertThat(command.entries().get(0).fieldValue()).isEqualTo("제조사");
            assertThat(command.entries().get(1).noticeFieldId())
                    .isEqualTo(ProductNoticeApiFixtures.DEFAULT_NOTICE_FIELD_ID_2);
            assertThat(command.entries().get(1).fieldValue()).isEqualTo("한국");
        }

        @Test
        @DisplayName("단일 고시 항목 요청도 정확히 변환된다")
        void toCommand_SingleEntry_ReturnsCommandWithOneEntry() {
            // given
            Long productGroupId = ProductNoticeApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            UpdateProductNoticeApiRequest request =
                    ProductNoticeApiFixtures.updateRequestSingleEntry();

            // when
            UpdateProductNoticeCommand command = mapper.toCommand(productGroupId, request);

            // then
            assertThat(command.entries()).hasSize(1);
            assertThat(command.entries().get(0).noticeFieldId())
                    .isEqualTo(ProductNoticeApiFixtures.DEFAULT_NOTICE_FIELD_ID_1);
            assertThat(command.entries().get(0).fieldValue()).isEqualTo("단일 항목 값");
        }

        @Test
        @DisplayName("다른 productGroupId도 정확히 Command에 전달된다")
        void toCommand_DifferentProductGroupId_IsCorrectlyMapped() {
            // given
            Long productGroupId = 999L;
            UpdateProductNoticeApiRequest request = ProductNoticeApiFixtures.updateRequest();

            // when
            UpdateProductNoticeCommand command = mapper.toCommand(productGroupId, request);

            // then
            assertThat(command.productGroupId()).isEqualTo(999L);
        }

        @Test
        @DisplayName("다른 noticeCategoryId도 정확히 Command에 전달된다")
        void toCommand_DifferentNoticeCategoryId_IsCorrectlyMapped() {
            // given
            Long productGroupId = ProductNoticeApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            UpdateProductNoticeApiRequest request =
                    ProductNoticeApiFixtures.updateRequest(
                            99L,
                            java.util.List.of(
                                    new UpdateProductNoticeApiRequest.NoticeEntryRequest(
                                            200L, "테스트 값")));

            // when
            UpdateProductNoticeCommand command = mapper.toCommand(productGroupId, request);

            // then
            assertThat(command.noticeCategoryId()).isEqualTo(99L);
        }
    }
}
