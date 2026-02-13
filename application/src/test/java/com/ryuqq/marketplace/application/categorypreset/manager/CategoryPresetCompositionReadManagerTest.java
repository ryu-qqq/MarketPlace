package com.ryuqq.marketplace.application.categorypreset.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.categorypreset.CategoryPresetQueryFixtures;
import com.ryuqq.marketplace.application.categorypreset.dto.response.CategoryPresetDetailResult;
import com.ryuqq.marketplace.application.categorypreset.port.out.query.CategoryPresetCompositionQueryPort;
import com.ryuqq.marketplace.domain.categorypreset.exception.CategoryPresetNotFoundException;
import java.util.Optional;
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
@DisplayName("CategoryPresetCompositionReadManager 단위 테스트")
class CategoryPresetCompositionReadManagerTest {

    @InjectMocks private CategoryPresetCompositionReadManager sut;

    @Mock private CategoryPresetCompositionQueryPort compositionQueryPort;

    @Nested
    @DisplayName("getDetail() - 상세 조회")
    class GetDetailTest {

        @Test
        @DisplayName("존재하는 ID로 조회 시 CategoryPresetDetailResult를 반환한다")
        void getDetail_ExistingId_ReturnsDetailResult() {
            // given
            Long id = 1L;
            CategoryPresetDetailResult expected =
                    CategoryPresetQueryFixtures.categoryPresetDetailResult(id);
            given(compositionQueryPort.findDetailById(id)).willReturn(Optional.of(expected));

            // when
            CategoryPresetDetailResult result = sut.getDetail(id);

            // then
            assertThat(result).isEqualTo(expected);
            then(compositionQueryPort).should().findDetailById(id);
        }

        @Test
        @DisplayName("존재하지 않는 ID로 조회 시 CategoryPresetNotFoundException이 발생한다")
        void getDetail_NonExistingId_ThrowsException() {
            // given
            Long id = 999L;
            given(compositionQueryPort.findDetailById(id)).willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> sut.getDetail(id))
                    .isInstanceOf(CategoryPresetNotFoundException.class);
            then(compositionQueryPort).should().findDetailById(id);
        }
    }
}
