package com.ryuqq.marketplace.adapter.in.rest.legacy.product.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.adapter.in.rest.legacy.product.LegacyProductApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.legacy.product.dto.request.LegacyCreateOptionRequest;
import com.ryuqq.marketplace.application.product.dto.command.ProductDiffUpdateEntry;
import com.ryuqq.marketplace.application.product.dto.command.UpdateProductsCommand;
import com.ryuqq.marketplace.application.selleroption.dto.command.UpdateSellerOptionGroupsCommand;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("LegacyOptionCommandApiMapper 단위 테스트")
class LegacyOptionCommandApiMapperTest {

    private LegacyOptionCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new LegacyOptionCommandApiMapper();
    }

    @Nested
    @DisplayName("toUpdateProductsCommand - 내부 시스템용 Command 변환")
    class ToUpdateProductsCommandTest {

        @Test
        @DisplayName("LegacyCreateOptionRequest 목록을 UpdateProductsCommand로 변환한다")
        void toUpdateProductsCommand_ConvertsRequests_ReturnsCommand() {
            // given
            long productGroupId = LegacyProductApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            List<LegacyCreateOptionRequest> requests =
                    LegacyProductApiFixtures.optionRequestsWithIds();

            // when
            UpdateProductsCommand command =
                    mapper.toUpdateProductsCommand(productGroupId, requests);

            // then
            assertThat(command.productGroupId()).isEqualTo(productGroupId);
            assertThat(command.optionGroups()).isNotEmpty();
            assertThat(command.products()).hasSize(2);
        }

        @Test
        @DisplayName("옵션 그룹이 중복 없이 올바르게 그룹화된다")
        void toUpdateProductsCommand_GroupsOptionsByName_NoDuplicates() {
            // given
            long productGroupId = LegacyProductApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            List<LegacyCreateOptionRequest> requests =
                    LegacyProductApiFixtures.optionRequestsWithIds();

            // when
            UpdateProductsCommand command =
                    mapper.toUpdateProductsCommand(productGroupId, requests);

            // then
            // 두 옵션이 동일한 그룹명("색상")에 속하므로 optionGroups는 1개여야 함
            assertThat(command.optionGroups()).hasSize(1);
            assertThat(command.optionGroups().get(0).optionGroupName()).isEqualTo("색상");
            assertThat(command.optionGroups().get(0).optionValues()).hasSize(2);
        }

        @Test
        @DisplayName("null 목록이면 IllegalArgumentException이 발생한다")
        void toUpdateProductsCommand_NullList_ThrowsException() {
            // when & then
            assertThatThrownBy(
                            () ->
                                    mapper.toUpdateProductsCommand(
                                            LegacyProductApiFixtures.DEFAULT_PRODUCT_GROUP_ID,
                                            null))
                    .isInstanceOf(IllegalArgumentException.class);
        }
    }

    @Nested
    @DisplayName("toUpdateSellerOptionGroupsCommand - UpdateSellerOptionGroupsCommand 변환")
    class ToUpdateSellerOptionGroupsCommandTest {

        @Test
        @DisplayName("UpdateProductsCommand를 UpdateSellerOptionGroupsCommand로 변환한다")
        void toUpdateSellerOptionGroupsCommand_ConvertsCommand_ReturnsOptionGroupsCommand() {
            // given
            long productGroupId = LegacyProductApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            List<LegacyCreateOptionRequest> requests =
                    LegacyProductApiFixtures.optionRequestsWithIds();
            UpdateProductsCommand command =
                    mapper.toUpdateProductsCommand(productGroupId, requests);

            // when
            UpdateSellerOptionGroupsCommand result =
                    mapper.toUpdateSellerOptionGroupsCommand(productGroupId, command);

            // then
            assertThat(result).isNotNull();
            assertThat(result.productGroupId()).isEqualTo(productGroupId);
            assertThat(result.optionGroups()).hasSize(1);
        }

        @Test
        @DisplayName("optionGroups가 비어있으면 null을 반환한다")
        void toUpdateSellerOptionGroupsCommand_EmptyOptionGroups_ReturnsNull() {
            // given
            long productGroupId = LegacyProductApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            List<LegacyCreateOptionRequest> singleOptions =
                    LegacyProductApiFixtures.singleOptionRequests();
            UpdateProductsCommand command =
                    mapper.toUpdateProductsCommand(productGroupId, singleOptions);

            // when
            UpdateSellerOptionGroupsCommand result =
                    mapper.toUpdateSellerOptionGroupsCommand(productGroupId, command);

            // then
            // SINGLE 옵션이면 optionGroups가 비어있어 null 반환
            assertThat(result).isNull();
        }
    }

    @Nested
    @DisplayName("toProductEntries - ProductDiffUpdateEntry 목록 변환")
    class ToProductEntriesTest {

        @Test
        @DisplayName("UpdateProductsCommand.products를 List<ProductDiffUpdateEntry>로 변환한다")
        void toProductEntries_ConvertsProducts_ReturnsDiffEntries() {
            // given
            long productGroupId = LegacyProductApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            List<LegacyCreateOptionRequest> requests =
                    LegacyProductApiFixtures.optionRequestsWithIds();
            UpdateProductsCommand command =
                    mapper.toUpdateProductsCommand(productGroupId, requests);

            // when
            List<ProductDiffUpdateEntry> entries = mapper.toProductEntries(command);

            // then
            assertThat(entries).hasSize(2);
        }

        @Test
        @DisplayName("ProductDiffUpdateEntry의 productId가 올바르게 매핑된다")
        void toProductEntries_MapsProductId_Correctly() {
            // given
            long productGroupId = LegacyProductApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            List<LegacyCreateOptionRequest> requests =
                    LegacyProductApiFixtures.optionRequestsWithIds();
            UpdateProductsCommand command =
                    mapper.toUpdateProductsCommand(productGroupId, requests);

            // when
            List<ProductDiffUpdateEntry> entries = mapper.toProductEntries(command);

            // then
            assertThat(entries.get(0).productId())
                    .isEqualTo(LegacyProductApiFixtures.DEFAULT_PRODUCT_ID_1);
        }
    }
}
