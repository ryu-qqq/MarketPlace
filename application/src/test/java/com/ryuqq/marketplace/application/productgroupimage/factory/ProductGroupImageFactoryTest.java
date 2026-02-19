package com.ryuqq.marketplace.application.productgroupimage.factory;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.ryuqq.marketplace.application.common.time.TimeProvider;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.RegisterProductGroupImagesCommand;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.UpdateProductGroupImagesCommand;
import com.ryuqq.marketplace.domain.common.CommonVoFixtures;
import com.ryuqq.marketplace.domain.imageupload.aggregate.ImageUploadOutbox;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;
import com.ryuqq.marketplace.domain.productgroupimage.vo.ProductGroupImageUpdateData;
import com.ryuqq.marketplace.domain.productgroupimage.vo.ProductGroupImages;
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
@DisplayName("ProductGroupImageFactory 단위 테스트")
class ProductGroupImageFactoryTest {

    @InjectMocks private ProductGroupImageFactory sut;

    @Mock private TimeProvider timeProvider;

    @Nested
    @DisplayName("createFromImageRegistration() - 이미지 등록 Command로 ProductGroupImages 생성")
    class CreateFromImageRegistrationTest {

        @Test
        @DisplayName("이미지 등록 Command로 ProductGroupImages를 생성한다")
        void createFromImageRegistration_ValidCommand_ReturnsImages() {
            // given
            ProductGroupId productGroupId = ProductGroupFixtures.defaultProductGroupId();
            List<RegisterProductGroupImagesCommand.ImageCommand> imageCommands =
                    List.of(
                            new RegisterProductGroupImagesCommand.ImageCommand(
                                    "THUMBNAIL", "https://example.com/thumb.jpg", 0),
                            new RegisterProductGroupImagesCommand.ImageCommand(
                                    "DETAIL", "https://example.com/detail.jpg", 1));

            // when
            ProductGroupImages result =
                    sut.createFromImageRegistration(productGroupId, imageCommands);

            // then
            assertThat(result).isNotNull();
            assertThat(result.toList()).hasSize(2);
        }
    }

    @Nested
    @DisplayName("createUpdateData() - 이미지 수정 UpdateData 생성")
    class CreateUpdateDataTest {

        @Test
        @DisplayName("이미지 수정 Command로 ProductGroupImageUpdateData를 생성한다")
        void createUpdateData_ValidCommand_ReturnsUpdateData() {
            // given
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            List<UpdateProductGroupImagesCommand.ImageCommand> imageCommands =
                    List.of(
                            new UpdateProductGroupImagesCommand.ImageCommand(
                                    "THUMBNAIL", "https://example.com/thumb.jpg", 0));
            UpdateProductGroupImagesCommand command =
                    new UpdateProductGroupImagesCommand(1L, imageCommands);

            // when
            ProductGroupImageUpdateData result = sut.createUpdateData(command);

            // then
            assertThat(result).isNotNull();
        }
    }

    @Nested
    @DisplayName("createProductGroupImageOutboxes() - 이미지 업로드 아웃박스 생성")
    class CreateProductGroupImageOutboxesTest {

        @Test
        @DisplayName("이미지 ID와 이미지 목록으로 ImageUploadOutbox 목록을 생성한다")
        void createProductGroupImageOutboxes_ValidInput_ReturnsOutboxes() {
            // given
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            List<Long> imageIds = List.of(10L, 11L);
            List<ProductGroupImage> images =
                    List.of(
                            ProductGroupFixtures.thumbnailImage(),
                            ProductGroupFixtures.detailImage(1));

            // when
            List<ImageUploadOutbox> result = sut.createProductGroupImageOutboxes(imageIds, images);

            // then
            assertThat(result).hasSize(2);
        }

        @Test
        @DisplayName("이미지가 없으면 빈 아웃박스 목록을 반환한다")
        void createProductGroupImageOutboxes_EmptyImages_ReturnsEmptyOutboxes() {
            // given
            Instant now = CommonVoFixtures.now();
            given(timeProvider.now()).willReturn(now);

            // when
            List<ImageUploadOutbox> result =
                    sut.createProductGroupImageOutboxes(List.of(), List.of());

            // then
            assertThat(result).isEmpty();
        }
    }
}
