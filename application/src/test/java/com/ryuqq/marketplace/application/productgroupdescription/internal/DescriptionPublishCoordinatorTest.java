package com.ryuqq.marketplace.application.productgroupdescription.internal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.application.common.manager.FileStorageManager;
import com.ryuqq.marketplace.application.productgroupdescription.manager.ProductGroupDescriptionCommandManager;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
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
@DisplayName("DescriptionPublishCoordinator 단위 테스트")
class DescriptionPublishCoordinatorTest {

    @InjectMocks private DescriptionPublishCoordinator sut;

    @Mock private FileStorageManager fileStorageManager;
    @Mock private DescriptionCommandFacade descriptionCommandFacade;
    @Mock private ProductGroupDescriptionCommandManager descriptionCommandManager;

    @Nested
    @DisplayName("publish() - Description CDN 퍼블리시")
    class PublishTest {

        @Test
        @DisplayName("CDN 업로드 성공 시 true를 반환한다")
        void publish_UploadSucceeds_ReturnsTrue() {
            // given
            ProductGroupDescription description = ProductGroupFixtures.descriptionWithImages();
            String cdnUrl = "https://cdn.example.com/description/1.html";

            given(fileStorageManager.uploadHtmlContent(anyString(), anyString(), anyString()))
                    .willReturn(cdnUrl);

            // when
            boolean result = sut.publish(description);

            // then
            assertThat(result).isTrue();
            then(fileStorageManager)
                    .should()
                    .uploadHtmlContent(anyString(), anyString(), anyString());
            then(descriptionCommandManager).should().persist(description);
        }

        @Test
        @DisplayName("CDN 업로드 실패 시 false를 반환한다")
        void publish_UploadFails_ReturnsFalse() {
            // given
            ProductGroupDescription description = ProductGroupFixtures.descriptionWithImages();

            given(fileStorageManager.uploadHtmlContent(anyString(), anyString(), anyString()))
                    .willThrow(new RuntimeException("CDN 업로드 실패"));

            // when
            boolean result = sut.publish(description);

            // then
            assertThat(result).isFalse();
            then(descriptionCommandManager).shouldHaveNoInteractions();
        }
    }
}
