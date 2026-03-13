package com.ryuqq.marketplace.application.outboundproductimage.manager;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.outboundproductimage.ResolvedExternalImageFixtures;
import com.ryuqq.marketplace.application.outboundproductimage.port.out.client.SalesChannelImageClient;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SalesChannelImageClientManager 단위 테스트")
class SalesChannelImageClientManagerTest {

    private SalesChannelImageClientManager sut;

    @Mock private SalesChannelImageClient naverClient;

    @BeforeEach
    void setUp() {
        given(naverClient.channelCode())
                .willReturn(ResolvedExternalImageFixtures.DEFAULT_CHANNEL_CODE);
        sut = new SalesChannelImageClientManager(List.of(naverClient));
    }

    @Nested
    @DisplayName("uploadImages() - 채널 이미지 업로드")
    class UploadImagesTest {

        @Test
        @DisplayName("지원하는 채널 코드로 이미지를 업로드하면 external URL 목록을 반환한다")
        void uploadImages_SupportedChannel_ReturnsExternalUrls() {
            // given
            String channelCode = ResolvedExternalImageFixtures.DEFAULT_CHANNEL_CODE;
            List<String> imageUrls = List.of(
                    "https://s3.example.com/image1.jpg",
                    "https://s3.example.com/image2.jpg");
            List<String> expectedExternalUrls = List.of(
                    ResolvedExternalImageFixtures.DEFAULT_THUMBNAIL_EXTERNAL_URL,
                    ResolvedExternalImageFixtures.DEFAULT_DETAIL_EXTERNAL_URL_1);

            given(naverClient.uploadImages(imageUrls)).willReturn(expectedExternalUrls);

            // when
            List<String> result = sut.uploadImages(channelCode, imageUrls);

            // then
            assertThat(result).isEqualTo(expectedExternalUrls);
            then(naverClient).should().uploadImages(imageUrls);
        }

        @Test
        @DisplayName("지원하지 않는 채널 코드로 업로드 시 IllegalArgumentException을 던진다")
        void uploadImages_UnsupportedChannel_ThrowsIllegalArgumentException() {
            // given
            String unsupportedChannelCode = ResolvedExternalImageFixtures.UNSUPPORTED_CHANNEL_CODE;
            List<String> imageUrls = List.of("https://s3.example.com/image.jpg");

            // when & then
            assertThatThrownBy(() -> sut.uploadImages(unsupportedChannelCode, imageUrls))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessageContaining(unsupportedChannelCode);
        }

        @Test
        @DisplayName("단일 이미지 URL 목록을 업로드하면 클라이언트에 그대로 전달된다")
        void uploadImages_SingleUrl_DelegatesToClient() {
            // given
            String channelCode = ResolvedExternalImageFixtures.DEFAULT_CHANNEL_CODE;
            List<String> imageUrls = List.of("https://s3.example.com/single.jpg");
            List<String> expectedUrls = List.of(
                    ResolvedExternalImageFixtures.DEFAULT_THUMBNAIL_EXTERNAL_URL);

            given(naverClient.uploadImages(imageUrls)).willReturn(expectedUrls);

            // when
            sut.uploadImages(channelCode, imageUrls);

            // then
            then(naverClient).should().uploadImages(imageUrls);
        }
    }

    @Nested
    @DisplayName("supports() - 채널 코드 지원 여부 확인")
    class SupportsTest {

        @Test
        @DisplayName("등록된 채널 코드이면 true를 반환한다")
        void supports_RegisteredChannelCode_ReturnsTrue() {
            boolean result = sut.supports(ResolvedExternalImageFixtures.DEFAULT_CHANNEL_CODE);

            assertThat(result).isTrue();
        }

        @Test
        @DisplayName("등록되지 않은 채널 코드이면 false를 반환한다")
        void supports_UnregisteredChannelCode_ReturnsFalse() {
            boolean result = sut.supports(ResolvedExternalImageFixtures.UNSUPPORTED_CHANNEL_CODE);

            assertThat(result).isFalse();
        }

        @Test
        @DisplayName("null 채널 코드이면 false를 반환한다")
        void supports_NullChannelCode_ReturnsFalse() {
            boolean result = sut.supports(null);

            assertThat(result).isFalse();
        }
    }

    @Nested
    @DisplayName("uploadImages() - 빈 목록 가드")
    class EmptyListGuardTest {

        @Test
        @DisplayName("빈 이미지 URL 목록이면 클라이언트를 호출하지 않고 빈 리스트를 반환한다")
        void uploadImages_EmptyList_ReturnsEmptyWithoutClientCall() {
            // given
            String channelCode = ResolvedExternalImageFixtures.DEFAULT_CHANNEL_CODE;

            // when
            List<String> result = sut.uploadImages(channelCode, List.of());

            // then
            assertThat(result).isEmpty();
            then(naverClient).shouldHaveNoMoreInteractions();
        }
    }

    @Nested
    @DisplayName("다중 클라이언트 등록 시나리오")
    class MultipleClientsTest {

        @Mock private SalesChannelImageClient coupangClient;

        @Test
        @DisplayName("여러 클라이언트가 등록되면 채널 코드에 맞는 클라이언트로 라우팅된다")
        void uploadImages_MultipleClients_RoutesToCorrectClient() {
            // given
            given(coupangClient.channelCode()).willReturn("COUPANG");
            SalesChannelImageClientManager multiClientManager =
                    new SalesChannelImageClientManager(List.of(naverClient, coupangClient));

            List<String> imageUrls = List.of("https://s3.example.com/image.jpg");
            List<String> coupangUrls = List.of("https://cdn.coupang.com/image.jpg");

            given(coupangClient.uploadImages(imageUrls)).willReturn(coupangUrls);

            // when
            List<String> result = multiClientManager.uploadImages("COUPANG", imageUrls);

            // then
            assertThat(result).isEqualTo(coupangUrls);
            then(naverClient).shouldHaveNoMoreInteractions();
            then(coupangClient).should().uploadImages(imageUrls);
        }

        @Test
        @DisplayName("중복된 channelCode가 있으면 IllegalStateException을 던진다")
        void duplicateChannelCode_ThrowsIllegalStateException() {
            // given
            given(coupangClient.channelCode())
                    .willReturn(ResolvedExternalImageFixtures.DEFAULT_CHANNEL_CODE);

            // when & then
            assertThatThrownBy(() ->
                    new SalesChannelImageClientManager(List.of(naverClient, coupangClient)))
                    .isInstanceOf(IllegalStateException.class)
                    .hasMessageContaining("중복된 channelCode");
        }
    }
}
