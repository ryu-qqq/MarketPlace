package com.ryuqq.marketplace.application.categorypreset.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.categorypreset.CategoryPresetQueryFixtures;
import com.ryuqq.marketplace.application.categorypreset.dto.response.CategoryPresetDetailResult;
import com.ryuqq.marketplace.application.categorypreset.manager.CategoryPresetCompositionReadManager;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("GetCategoryPresetDetailService 단위 테스트")
class GetCategoryPresetDetailServiceTest {

    @InjectMocks private GetCategoryPresetDetailService sut;

    @Mock private CategoryPresetCompositionReadManager compositionReadManager;

    @Nested
    @DisplayName("execute() - 상세 조회 실행")
    class ExecuteTest {

        @Test
        @DisplayName("CompositionReadManager에 위임하여 결과를 반환한다")
        void execute_DelegatesToManager_ReturnsResult() {
            // given
            Long categoryPresetId = 1L;
            CategoryPresetDetailResult expected =
                    CategoryPresetQueryFixtures.categoryPresetDetailResult(categoryPresetId);
            given(compositionReadManager.getDetail(categoryPresetId)).willReturn(expected);

            // when
            CategoryPresetDetailResult result = sut.execute(categoryPresetId);

            // then
            assertThat(result).isEqualTo(expected);
            then(compositionReadManager).should().getDetail(categoryPresetId);
        }
    }
}
