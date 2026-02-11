package com.ryuqq.marketplace.adapter.in.rest.saleschannelcategory.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.saleschannelcategory.SalesChannelCategoryApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.saleschannelcategory.dto.command.RegisterSalesChannelCategoryApiRequest;
import com.ryuqq.marketplace.application.saleschannelcategory.dto.command.RegisterSalesChannelCategoryCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("SalesChannelCategoryCommandApiMapper 단위 테스트")
class SalesChannelCategoryCommandApiMapperTest {

    private SalesChannelCategoryCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new SalesChannelCategoryCommandApiMapper();
    }

    @Nested
    @DisplayName("toCommand(Long, RegisterSalesChannelCategoryApiRequest) - 등록 요청 변환")
    class ToRegisterCommandTest {

        @Test
        @DisplayName("RegisterSalesChannelCategoryApiRequest를 RegisterSalesChannelCategoryCommand로 변환한다")
        void toCommand_ConvertsRegisterRequest_ReturnsCommand() {
            // given
            Long salesChannelId = 1L;
            RegisterSalesChannelCategoryApiRequest request =
                    SalesChannelCategoryApiFixtures.registerRequest();

            // when
            RegisterSalesChannelCategoryCommand command = mapper.toCommand(salesChannelId, request);

            // then
            assertThat(command.salesChannelId()).isEqualTo(1L);
            assertThat(command.externalCategoryCode()).isEqualTo("CAT001");
            assertThat(command.externalCategoryName()).isEqualTo("의류");
            assertThat(command.parentId()).isEqualTo(0L);
            assertThat(command.depth()).isEqualTo(0);
            assertThat(command.path()).isEqualTo("1");
            assertThat(command.sortOrder()).isEqualTo(1);
            assertThat(command.leaf()).isFalse();
            assertThat(command.displayPath()).isEqualTo("식품 > 과자 > 스낵 > 젤리");
        }

        @Test
        @DisplayName("salesChannelId가 올바르게 설정된다")
        void toCommand_SetsSalesChannelId_ReturnsCommandWithId() {
            // given
            Long salesChannelId = 999L;
            RegisterSalesChannelCategoryApiRequest request =
                    SalesChannelCategoryApiFixtures.registerRequest();

            // when
            RegisterSalesChannelCategoryCommand command = mapper.toCommand(salesChannelId, request);

            // then
            assertThat(command.salesChannelId()).isEqualTo(999L);
        }

        @Test
        @DisplayName("leaf 값이 true일 때 올바르게 변환된다")
        void toCommand_LeafTrue_ReturnsCommandWithLeafTrue() {
            // given
            Long salesChannelId = 1L;
            RegisterSalesChannelCategoryApiRequest request =
                    new RegisterSalesChannelCategoryApiRequest(
                            "CAT002", "하위카테고리", 1L, 1, "1/2", 1, true, "상위 > 하위");

            // when
            RegisterSalesChannelCategoryCommand command = mapper.toCommand(salesChannelId, request);

            // then
            assertThat(command.leaf()).isTrue();
            assertThat(command.depth()).isEqualTo(1);
            assertThat(command.parentId()).isEqualTo(1L);
        }

        @Test
        @DisplayName("displayPath가 null이어도 정상적으로 변환된다")
        void toCommand_DisplayPathNull_ReturnsCommandWithNull() {
            // given
            Long salesChannelId = 1L;
            RegisterSalesChannelCategoryApiRequest request =
                    new RegisterSalesChannelCategoryApiRequest(
                            "CAT003", "카테고리명", 0L, 0, "1", 1, false, null);

            // when
            RegisterSalesChannelCategoryCommand command = mapper.toCommand(salesChannelId, request);

            // then
            assertThat(command.displayPath()).isNull();
            assertThat(command.externalCategoryCode()).isEqualTo("CAT003");
        }
    }
}
