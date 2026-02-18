package com.ryuqq.marketplace.adapter.in.rest.categorypreset.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.categorypreset.dto.command.DeleteCategoryPresetsApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.categorypreset.dto.command.RegisterCategoryPresetApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.categorypreset.dto.command.UpdateCategoryPresetApiRequest;
import com.ryuqq.marketplace.application.categorypreset.dto.command.DeleteCategoryPresetsCommand;
import com.ryuqq.marketplace.application.categorypreset.dto.command.RegisterCategoryPresetCommand;
import com.ryuqq.marketplace.application.categorypreset.dto.command.UpdateCategoryPresetCommand;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("CategoryPresetCommandApiMapper 단위 테스트")
class CategoryPresetCommandApiMapperTest {

    private final CategoryPresetCommandApiMapper sut = new CategoryPresetCommandApiMapper();

    @Nested
    @DisplayName("toRegisterCommand()")
    class RegisterTest {

        @Test
        @DisplayName("등록 요청을 RegisterCategoryPresetCommand로 변환한다")
        void toRegisterCommand_MapsAllFields() {
            // given
            RegisterCategoryPresetApiRequest request =
                    new RegisterCategoryPresetApiRequest(
                            1L, "식품 - 과자류 전송용", "50000123", List.of(1L, 2L, 3L));

            // when
            RegisterCategoryPresetCommand result = sut.toRegisterCommand(request);

            // then
            assertThat(result.shopId()).isEqualTo(1L);
            assertThat(result.presetName()).isEqualTo("식품 - 과자류 전송용");
            assertThat(result.categoryCode()).isEqualTo("50000123");
            assertThat(result.internalCategoryIds()).containsExactly(1L, 2L, 3L);
        }
    }

    @Nested
    @DisplayName("toUpdateCommand()")
    class UpdateTest {

        @Test
        @DisplayName("수정 요청을 UpdateCategoryPresetCommand로 변환한다")
        void toUpdateCommand_MapsAllFields() {
            // given
            UpdateCategoryPresetApiRequest request =
                    new UpdateCategoryPresetApiRequest("수정된 이름", "50000456", List.of(4L, 5L));

            // when
            UpdateCategoryPresetCommand result = sut.toUpdateCommand(200L, request);

            // then
            assertThat(result.categoryPresetId()).isEqualTo(200L);
            assertThat(result.presetName()).isEqualTo("수정된 이름");
            assertThat(result.categoryCode()).isEqualTo("50000456");
            assertThat(result.internalCategoryIds()).containsExactly(4L, 5L);
        }
    }

    @Nested
    @DisplayName("toDeleteCommand()")
    class DeleteTest {

        @Test
        @DisplayName("삭제 요청을 DeleteCategoryPresetsCommand로 변환한다")
        void toDeleteCommand_MapsIds() {
            // given
            DeleteCategoryPresetsApiRequest request =
                    new DeleteCategoryPresetsApiRequest(List.of(1001L, 1002L));

            // when
            DeleteCategoryPresetsCommand result = sut.toDeleteCommand(request);

            // then
            assertThat(result.ids()).containsExactly(1001L, 1002L);
        }
    }
}
