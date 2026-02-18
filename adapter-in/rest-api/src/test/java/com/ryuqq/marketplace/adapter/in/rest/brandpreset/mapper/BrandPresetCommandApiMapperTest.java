package com.ryuqq.marketplace.adapter.in.rest.brandpreset.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.brandpreset.dto.command.RegisterBrandPresetApiRequest;
import com.ryuqq.marketplace.adapter.in.rest.brandpreset.dto.command.UpdateBrandPresetApiRequest;
import com.ryuqq.marketplace.application.brandpreset.dto.command.DeleteBrandPresetsCommand;
import com.ryuqq.marketplace.application.brandpreset.dto.command.RegisterBrandPresetCommand;
import com.ryuqq.marketplace.application.brandpreset.dto.command.UpdateBrandPresetCommand;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("BrandPresetCommandApiMapper 단위 테스트")
class BrandPresetCommandApiMapperTest {

    private final BrandPresetCommandApiMapper sut = new BrandPresetCommandApiMapper();

    @Nested
    @DisplayName("toCommand(RegisterBrandPresetApiRequest)")
    class RegisterTest {

        @Test
        @DisplayName("등록 요청을 RegisterBrandPresetCommand로 변환한다")
        void toCommand_Register_MapsAllFields() {
            // given
            RegisterBrandPresetApiRequest request =
                    new RegisterBrandPresetApiRequest(1L, 10L, "나이키 전송용", List.of(1L, 2L, 3L));

            // when
            RegisterBrandPresetCommand result = sut.toCommand(request);

            // then
            assertThat(result.shopId()).isEqualTo(1L);
            assertThat(result.salesChannelBrandId()).isEqualTo(10L);
            assertThat(result.presetName()).isEqualTo("나이키 전송용");
            assertThat(result.internalBrandIds()).containsExactly(1L, 2L, 3L);
        }
    }

    @Nested
    @DisplayName("toCommand(Long, UpdateBrandPresetApiRequest)")
    class UpdateTest {

        @Test
        @DisplayName("수정 요청을 UpdateBrandPresetCommand로 변환한다")
        void toCommand_Update_MapsAllFields() {
            // given
            UpdateBrandPresetApiRequest request =
                    new UpdateBrandPresetApiRequest("수정된 이름", 20L, List.of(4L, 5L));

            // when
            UpdateBrandPresetCommand result = sut.toCommand(100L, request);

            // then
            assertThat(result.brandPresetId()).isEqualTo(100L);
            assertThat(result.presetName()).isEqualTo("수정된 이름");
            assertThat(result.salesChannelBrandId()).isEqualTo(20L);
            assertThat(result.internalBrandIds()).containsExactly(4L, 5L);
        }
    }

    @Nested
    @DisplayName("toDeleteCommand(List<Long>)")
    class DeleteTest {

        @Test
        @DisplayName("삭제 요청을 DeleteBrandPresetsCommand로 변환한다")
        void toDeleteCommand_MapsIds() {
            // given
            List<Long> ids = List.of(1L, 2L, 3L);

            // when
            DeleteBrandPresetsCommand result = sut.toDeleteCommand(ids);

            // then
            assertThat(result.ids()).containsExactly(1L, 2L, 3L);
        }
    }
}
