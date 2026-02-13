package com.ryuqq.marketplace.application.brandpreset.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.brandpreset.BrandPresetQueryFixtures;
import com.ryuqq.marketplace.application.brandpreset.dto.response.BrandPresetDetailResult;
import com.ryuqq.marketplace.application.brandpreset.manager.BrandPresetCompositionReadManager;
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
@DisplayName("GetBrandPresetDetailService 단위 테스트")
class GetBrandPresetDetailServiceTest {

    @InjectMocks private GetBrandPresetDetailService sut;

    @Mock private BrandPresetCompositionReadManager compositionReadManager;

    @Nested
    @DisplayName("execute() - 상세 조회 실행")
    class ExecuteTest {

        @Test
        @DisplayName("CompositionReadManager에 위임하여 결과를 반환한다")
        void execute_DelegatesToManager_ReturnsResult() {
            // given
            Long brandPresetId = 1L;
            BrandPresetDetailResult expected =
                    BrandPresetQueryFixtures.brandPresetDetailResult(brandPresetId);
            given(compositionReadManager.getDetail(brandPresetId)).willReturn(expected);

            // when
            BrandPresetDetailResult result = sut.execute(brandPresetId);

            // then
            assertThat(result).isEqualTo(expected);
            then(compositionReadManager).should().getDetail(brandPresetId);
        }
    }
}
