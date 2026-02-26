package com.ryuqq.marketplace.application.productgroupimage.service.query;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.productgroupimage.dto.response.ProductGroupImageUploadStatusResult;
import com.ryuqq.marketplace.application.productgroupimage.manager.ProductGroupImageCompositeReadManager;
import java.util.List;
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
@DisplayName("GetProductGroupImageUploadStatusService 단위 테스트")
class GetProductGroupImageUploadStatusServiceTest {

    @InjectMocks private GetProductGroupImageUploadStatusService sut;

    @Mock private ProductGroupImageCompositeReadManager compositeReadManager;

    @Nested
    @DisplayName("execute() - 이미지 업로드 상태 조회")
    class ExecuteTest {

        @Test
        @DisplayName("productGroupId로 이미지 업로드 상태를 조회한다")
        void execute_ValidProductGroupId_ReturnsUploadStatus() {
            // given
            Long productGroupId = 1L;
            ProductGroupImageUploadStatusResult expectedResult =
                    new ProductGroupImageUploadStatusResult(
                            productGroupId, 3, 2, 1, 0, 0, List.of());

            given(compositeReadManager.getImageUploadStatus(productGroupId))
                    .willReturn(expectedResult);

            // when
            ProductGroupImageUploadStatusResult result = sut.execute(productGroupId);

            // then
            assertThat(result).isEqualTo(expectedResult);
            assertThat(result.productGroupId()).isEqualTo(productGroupId);
            assertThat(result.totalCount()).isEqualTo(3);
            then(compositeReadManager).should().getImageUploadStatus(productGroupId);
        }
    }
}
