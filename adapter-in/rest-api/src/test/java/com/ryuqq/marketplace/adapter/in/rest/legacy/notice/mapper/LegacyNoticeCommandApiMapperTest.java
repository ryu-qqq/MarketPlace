package com.ryuqq.marketplace.adapter.in.rest.legacy.notice.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.legacy.notice.LegacyNoticeApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.legacy.notice.dto.request.LegacyCreateProductNoticeRequest;
import com.ryuqq.marketplace.application.legacy.notice.dto.command.LegacyUpdateNoticeCommand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("LegacyNoticeCommandApiMapper 단위 테스트")
class LegacyNoticeCommandApiMapperTest {

    private LegacyNoticeCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new LegacyNoticeCommandApiMapper();
    }

    @Nested
    @DisplayName("toLegacyNoticeCommand - 고시정보 수정 요청 변환")
    class ToLegacyNoticeCommandTest {

        @Test
        @DisplayName("LegacyCreateProductNoticeRequest를 LegacyUpdateNoticeCommand로 변환한다")
        void toLegacyNoticeCommand_ConvertsRequest_ReturnsCommand() {
            // given
            long productGroupId = LegacyNoticeApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            LegacyCreateProductNoticeRequest request = LegacyNoticeApiFixtures.request();

            // when
            LegacyUpdateNoticeCommand command =
                    mapper.toLegacyNoticeCommand(productGroupId, request);

            // then
            assertThat(command.productGroupId()).isEqualTo(productGroupId);
            assertThat(command.material()).isEqualTo(LegacyNoticeApiFixtures.DEFAULT_MATERIAL);
            assertThat(command.color()).isEqualTo(LegacyNoticeApiFixtures.DEFAULT_COLOR);
            assertThat(command.size()).isEqualTo(LegacyNoticeApiFixtures.DEFAULT_SIZE);
            assertThat(command.maker()).isEqualTo(LegacyNoticeApiFixtures.DEFAULT_MAKER);
            assertThat(command.origin()).isEqualTo(LegacyNoticeApiFixtures.DEFAULT_ORIGIN);
            assertThat(command.washingMethod())
                    .isEqualTo(LegacyNoticeApiFixtures.DEFAULT_WASHING_METHOD);
            assertThat(command.yearMonthDay())
                    .isEqualTo(LegacyNoticeApiFixtures.DEFAULT_YEAR_MONTH);
            assertThat(command.assuranceStandard())
                    .isEqualTo(LegacyNoticeApiFixtures.DEFAULT_ASSURANCE_STANDARD);
            assertThat(command.asPhone()).isEqualTo(LegacyNoticeApiFixtures.DEFAULT_AS_PHONE);
        }

        @Test
        @DisplayName("productGroupId가 Command에 올바르게 설정된다")
        void toLegacyNoticeCommand_SetsProductGroupId_Correctly() {
            // given
            long productGroupId = 999L;
            LegacyCreateProductNoticeRequest request = LegacyNoticeApiFixtures.request();

            // when
            LegacyUpdateNoticeCommand command =
                    mapper.toLegacyNoticeCommand(productGroupId, request);

            // then
            assertThat(command.productGroupId()).isEqualTo(999L);
        }
    }

    @Nested
    @DisplayName("null 값 처리 - nullToEmpty 변환")
    class NullToEmptyTest {

        @Test
        @DisplayName("null 필드가 있으면 빈 문자열로 변환한다")
        void toLegacyNoticeCommand_NullFields_ConvertedToEmpty() {
            // given
            long productGroupId = LegacyNoticeApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            LegacyCreateProductNoticeRequest request = LegacyNoticeApiFixtures.requestWithNulls();

            // when
            LegacyUpdateNoticeCommand command =
                    mapper.toLegacyNoticeCommand(productGroupId, request);

            // then
            assertThat(command.material()).isEqualTo("");
            assertThat(command.color()).isEqualTo("");
            assertThat(command.size()).isEqualTo("");
            assertThat(command.maker()).isEqualTo("");
            assertThat(command.origin()).isEqualTo("");
            assertThat(command.washingMethod()).isEqualTo("");
            assertThat(command.yearMonthDay()).isEqualTo("");
            assertThat(command.assuranceStandard()).isEqualTo("");
            assertThat(command.asPhone()).isEqualTo("");
        }

        @Test
        @DisplayName("일부 필드만 null인 경우 해당 필드만 빈 문자열로 변환한다")
        void toLegacyNoticeCommand_PartialNullFields_OnlyNullsConvertedToEmpty() {
            // given
            long productGroupId = LegacyNoticeApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            LegacyCreateProductNoticeRequest request =
                    LegacyNoticeApiFixtures.requestWith("면 100%", null, null);

            // when
            LegacyUpdateNoticeCommand command =
                    mapper.toLegacyNoticeCommand(productGroupId, request);

            // then
            assertThat(command.material()).isEqualTo("면 100%");
            assertThat(command.color()).isEqualTo("");
            assertThat(command.size()).isEqualTo("");
        }
    }
}
