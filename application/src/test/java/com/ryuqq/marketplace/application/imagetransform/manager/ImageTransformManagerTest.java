package com.ryuqq.marketplace.application.imagetransform.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.imagetransform.ImageTransformResponseFixtures;
import com.ryuqq.marketplace.application.imagetransform.dto.response.ImageTransformResponse;
import com.ryuqq.marketplace.application.imagetransform.port.out.client.ImageTransformClient;
import com.ryuqq.marketplace.domain.imagevariant.vo.ImageVariantType;
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
@DisplayName("ImageTransformManager 단위 테스트")
class ImageTransformManagerTest {

    @InjectMocks private ImageTransformManager sut;

    @Mock private ImageTransformClient transformClient;

    @Nested
    @DisplayName("createTransformRequest() - 이미지 변환 요청 생성")
    class CreateTransformRequestTest {

        @Test
        @DisplayName("업로드 URL과 Variant 타입, fileAssetId로 변환 요청을 생성하고 응답을 반환한다")
        void createTransformRequest_ValidParams_ReturnsResponse() {
            // given
            String uploadedUrl = "https://cdn.example.com/uploaded/image.jpg";
            ImageVariantType variantType = ImageVariantType.SMALL_WEBP;
            String fileAssetId = "asset-abc-123";
            ImageTransformResponse expectedResponse =
                    ImageTransformResponseFixtures.processingResponse();

            given(transformClient.createTransformRequest(uploadedUrl, variantType, fileAssetId))
                    .willReturn(expectedResponse);

            // when
            ImageTransformResponse result =
                    sut.createTransformRequest(uploadedUrl, variantType, fileAssetId);

            // then
            assertThat(result).isEqualTo(expectedResponse);
            then(transformClient)
                    .should()
                    .createTransformRequest(uploadedUrl, variantType, fileAssetId);
        }
    }

    @Nested
    @DisplayName("getTransformRequest() - 이미지 변환 상태 조회")
    class GetTransformRequestTest {

        @Test
        @DisplayName("변환 요청 ID로 상태를 조회하고 응답을 반환한다")
        void getTransformRequest_ValidRequestId_ReturnsResponse() {
            // given
            String transformRequestId = ImageTransformResponseFixtures.DEFAULT_TRANSFORM_REQUEST_ID;
            ImageTransformResponse expectedResponse =
                    ImageTransformResponseFixtures.completedResponse();

            given(transformClient.getTransformRequest(transformRequestId))
                    .willReturn(expectedResponse);

            // when
            ImageTransformResponse result = sut.getTransformRequest(transformRequestId);

            // then
            assertThat(result).isEqualTo(expectedResponse);
            assertThat(result.isCompleted()).isTrue();
            then(transformClient).should().getTransformRequest(transformRequestId);
        }

        @Test
        @DisplayName("변환이 아직 완료되지 않으면 PROCESSING 상태 응답을 반환한다")
        void getTransformRequest_StillProcessing_ReturnsProcessingResponse() {
            // given
            String transformRequestId = ImageTransformResponseFixtures.DEFAULT_TRANSFORM_REQUEST_ID;
            ImageTransformResponse processingResponse =
                    ImageTransformResponseFixtures.processingResponse();

            given(transformClient.getTransformRequest(transformRequestId))
                    .willReturn(processingResponse);

            // when
            ImageTransformResponse result = sut.getTransformRequest(transformRequestId);

            // then
            assertThat(result.isCompleted()).isFalse();
            assertThat(result.isFailed()).isFalse();
            then(transformClient).should().getTransformRequest(transformRequestId);
        }
    }
}
