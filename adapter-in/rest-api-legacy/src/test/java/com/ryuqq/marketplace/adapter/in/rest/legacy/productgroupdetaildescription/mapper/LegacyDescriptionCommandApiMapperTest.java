package com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupdetaildescription.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupdetaildescription.LegacyDescriptionApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupdetaildescription.dto.request.LegacyUpdateProductDescriptionRequest;
import com.ryuqq.marketplace.application.legacy.productgroupdescription.dto.command.LegacyUpdateDescriptionCommand;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.UpdateProductGroupDescriptionCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("LegacyDescriptionCommandApiMapper 단위 테스트")
class LegacyDescriptionCommandApiMapperTest {

    private LegacyDescriptionCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new LegacyDescriptionCommandApiMapper();
    }

    @Nested
    @DisplayName("toLegacyUpdateDescriptionCommand - 레거시 상세설명 수정 Command 변환")
    class ToLegacyUpdateDescriptionCommandTest {

        @Test
        @DisplayName("LegacyUpdateProductDescriptionRequest를 LegacyUpdateDescriptionCommand로 변환한다")
        void toLegacyUpdateDescriptionCommand_ConvertsRequest_ReturnsCommand() {
            // given
            long productGroupId = LegacyDescriptionApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            LegacyUpdateProductDescriptionRequest request = LegacyDescriptionApiFixtures.request();

            // when
            LegacyUpdateDescriptionCommand command =
                    mapper.toLegacyUpdateDescriptionCommand(productGroupId, request);

            // then
            assertThat(command.productGroupId()).isEqualTo(productGroupId);
            assertThat(command.detailDescription())
                    .isEqualTo(LegacyDescriptionApiFixtures.DEFAULT_DETAIL_DESCRIPTION);
        }

        @Test
        @DisplayName("productGroupId가 Command에 올바르게 설정된다")
        void toLegacyUpdateDescriptionCommand_SetsProductGroupId_Correctly() {
            // given
            long productGroupId = 777L;
            LegacyUpdateProductDescriptionRequest request = LegacyDescriptionApiFixtures.request();

            // when
            LegacyUpdateDescriptionCommand command =
                    mapper.toLegacyUpdateDescriptionCommand(productGroupId, request);

            // then
            assertThat(command.productGroupId()).isEqualTo(777L);
        }

        @Test
        @DisplayName("상세설명 내용이 그대로 전달된다")
        void toLegacyUpdateDescriptionCommand_PreservesDescription_Correctly() {
            // given
            long productGroupId = LegacyDescriptionApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            String htmlContent = "<div><p>복잡한 <strong>HTML</strong> 내용</p></div>";
            LegacyUpdateProductDescriptionRequest request =
                    LegacyDescriptionApiFixtures.requestWith(htmlContent);

            // when
            LegacyUpdateDescriptionCommand command =
                    mapper.toLegacyUpdateDescriptionCommand(productGroupId, request);

            // then
            assertThat(command.detailDescription()).isEqualTo(htmlContent);
        }
    }

    @Nested
    @DisplayName("toDescriptionCommand - 내부 상세설명 수정 Command 변환")
    class ToDescriptionCommandTest {

        @Test
        @DisplayName(
                "LegacyUpdateProductDescriptionRequest를 UpdateProductGroupDescriptionCommand로 변환한다")
        void toDescriptionCommand_ConvertsRequest_ReturnsCommand() {
            // given
            long productGroupId = LegacyDescriptionApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            LegacyUpdateProductDescriptionRequest request = LegacyDescriptionApiFixtures.request();

            // when
            UpdateProductGroupDescriptionCommand command =
                    mapper.toDescriptionCommand(productGroupId, request);

            // then
            assertThat(command.productGroupId()).isEqualTo(productGroupId);
            assertThat(command.content())
                    .isEqualTo(LegacyDescriptionApiFixtures.DEFAULT_DETAIL_DESCRIPTION);
        }

        @Test
        @DisplayName("productGroupId가 Command에 올바르게 설정된다")
        void toDescriptionCommand_SetsProductGroupId_Correctly() {
            // given
            long productGroupId = 888L;
            LegacyUpdateProductDescriptionRequest request = LegacyDescriptionApiFixtures.request();

            // when
            UpdateProductGroupDescriptionCommand command =
                    mapper.toDescriptionCommand(productGroupId, request);

            // then
            assertThat(command.productGroupId()).isEqualTo(888L);
        }
    }
}
