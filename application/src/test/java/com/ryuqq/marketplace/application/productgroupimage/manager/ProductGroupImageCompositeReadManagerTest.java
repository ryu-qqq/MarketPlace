package com.ryuqq.marketplace.application.productgroupimage.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.productgroupimage.dto.response.ProductGroupImageUploadStatusResult;
import com.ryuqq.marketplace.application.productgroupimage.port.out.query.ProductGroupImageCompositeQueryPort;
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
@DisplayName("ProductGroupImageCompositeReadManager 단위 테스트")
class ProductGroupImageCompositeReadManagerTest {

    @InjectMocks private ProductGroupImageCompositeReadManager sut;

    @Mock private ProductGroupImageCompositeQueryPort compositeQueryPort;

    @Nested
    @DisplayName("getImageUploadStatus() - 이미지 업로드 상태 조회")
    class GetImageUploadStatusTest {

        @Test
        @DisplayName("productGroupId로 이미지 업로드 상태를 조회한다")
        void getImageUploadStatus_ValidProductGroupId_ReturnsUploadStatus() {
            // given
            Long productGroupId = 1L;
            ProductGroupImageUploadStatusResult expectedResult =
                    new ProductGroupImageUploadStatusResult(
                            productGroupId, 2, 1, 1, 0, 0, List.of());

            given(compositeQueryPort.findImageUploadStatus(productGroupId))
                    .willReturn(expectedResult);

            // when
            ProductGroupImageUploadStatusResult result = sut.getImageUploadStatus(productGroupId);

            // then
            assertThat(result).isEqualTo(expectedResult);
            assertThat(result.productGroupId()).isEqualTo(productGroupId);
            assertThat(result.totalCount()).isEqualTo(2);
            then(compositeQueryPort).should().findImageUploadStatus(productGroupId);
        }
    }
}
