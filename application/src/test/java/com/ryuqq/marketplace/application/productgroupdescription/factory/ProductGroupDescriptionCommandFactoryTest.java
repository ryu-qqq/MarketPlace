package com.ryuqq.marketplace.application.productgroupdescription.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.RegisterProductGroupDescriptionCommand;
import com.ryuqq.marketplace.application.productgroupdescription.dto.command.UpdateProductGroupDescriptionCommand;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.imageupload.aggregate.ImageUploadOutbox;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.aggregate.DescriptionImage;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionUpdateData;
import java.time.Instant;
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
@DisplayName("ProductGroupDescriptionCommandFactory 단위 테스트")
class ProductGroupDescriptionCommandFactoryTest {

    @InjectMocks private ProductGroupDescriptionCommandFactory sut;

    @Mock private TimeProvider timeProvider;

    @Nested
    @DisplayName("create() - 신규 Description 생성")
    class CreateTest {

        @Test
        @DisplayName("RegisterProductGroupDescriptionCommand로 ProductGroupDescription을 생성한다")
        void create_ValidCommand_ReturnsDescription() {
            // given
            RegisterProductGroupDescriptionCommand command =
                    new RegisterProductGroupDescriptionCommand(1L, "<p>상세설명</p>");
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            ProductGroupDescription result = sut.create(command);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("HTML에 img 태그가 포함되면 이미지를 추출하여 Description에 포함한다")
        void create_HtmlWithImages_ExtractsImages() {
            // given
            String htmlWithImages =
                    "<img src=\"https://example.com/img1.jpg\"><p>설명</p>"
                            + "<img src=\"https://example.com/img2.jpg\">";
            RegisterProductGroupDescriptionCommand command =
                    new RegisterProductGroupDescriptionCommand(1L, htmlWithImages);
            given(timeProvider.now()).willReturn(CommonVoFixtures.now());

            // when
            ProductGroupDescription result = sut.create(command);

            // then
            assertThat(result.images()).hasSize(2);
            assertThat(result.isAllImagesUploaded()).isFalse();
        }

        @Test
        @DisplayName("HTML에 img 태그가 없으면 빈 이미지 목록으로 생성한다")
        void create_HtmlWithoutImages_EmptyImages() {
            // given
            RegisterProductGroupDescriptionCommand command =
                    new RegisterProductGroupDescriptionCommand(1L, "<p>텍스트만</p>");
            given(timeProvider.now()).willReturn(CommonVoFixtures.now());

            // when
            ProductGroupDescription result = sut.create(command);

            // then
            assertThat(result.images()).isEmpty();
            assertThat(result.isAllImagesUploaded()).isTrue();
        }
    }

    @Nested
    @DisplayName("createUpdateData() - 수정 UpdateData 생성")
    class CreateUpdateDataTest {

        @Test
        @DisplayName("UpdateProductGroupDescriptionCommand로 DescriptionUpdateData를 생성한다")
        void createUpdateData_ValidCommand_ReturnsUpdateData() {
            // given
            UpdateProductGroupDescriptionCommand command =
                    new UpdateProductGroupDescriptionCommand(1L, "<p>수정된 상세설명</p>");
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            DescriptionUpdateData result = sut.createUpdateData(command);

            // then
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("createDescriptionImageOutboxes() - 이미지 아웃박스 생성")
    class CreateDescriptionImageOutboxesTest {

        @Test
        @DisplayName("이미지 ID와 DescriptionImage 목록으로 ImageUploadOutbox 목록을 생성한다")
        void createDescriptionImageOutboxes_ValidInput_ReturnsOutboxes() {
            // given
            List<Long> imageIds = List.of(10L, 11L);
            List<DescriptionImage> images =
                    List.of(
                            ProductGroupFixtures.defaultDescriptionImage(),
                            ProductGroupFixtures.uploadedDescriptionImage());
            Instant now = CommonVoFixtures.now();

            // when
            List<ImageUploadOutbox> result =
                    sut.createDescriptionImageOutboxes(imageIds, images, now);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("이미지가 없으면 빈 아웃박스 목록을 반환한다")
        void createDescriptionImageOutboxes_EmptyImages_ReturnsEmptyOutboxes() {
            // given
            List<Long> imageIds = List.of();
            List<DescriptionImage> images = List.of();
            Instant now = CommonVoFixtures.now();

            // when
            List<ImageUploadOutbox> result =
                    sut.createDescriptionImageOutboxes(imageIds, images, now);

            // then
            assertThat(result).isEmpty();
        }
    }
}
