package com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupimage.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupimage.LegacyImageApiFixtures;
import com.ryuqq.marketplace.adapter.in.rest.legacy.productgroupimage.dto.request.LegacyCreateProductImageRequest;
import com.ryuqq.marketplace.application.legacy.productgroupimage.dto.command.LegacyUpdateImagesCommand;
import com.ryuqq.marketplace.application.productgroupimage.dto.command.UpdateProductGroupImagesCommand;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("LegacyImageCommandApiMapper 단위 테스트")
class LegacyImageCommandApiMapperTest {

    private LegacyImageCommandApiMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new LegacyImageCommandApiMapper();
    }

    @Nested
    @DisplayName("toLegacyUpdateImagesCommand - 레거시 이미지 수정 Command 변환")
    class ToLegacyUpdateImagesCommandTest {

        @Test
        @DisplayName("이미지 목록을 LegacyUpdateImagesCommand로 변환한다")
        void toLegacyUpdateImagesCommand_ConvertsRequest_ReturnsCommand() {
            // given
            long productGroupId = LegacyImageApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            List<LegacyCreateProductImageRequest> request = LegacyImageApiFixtures.requestList();

            // when
            LegacyUpdateImagesCommand command =
                    mapper.toLegacyUpdateImagesCommand(productGroupId, request);

            // then
            assertThat(command.productGroupId()).isEqualTo(productGroupId);
            assertThat(command.images()).hasSize(2);
        }

        @Test
        @DisplayName("productGroupId가 Command에 올바르게 설정된다")
        void toLegacyUpdateImagesCommand_SetsProductGroupId_Correctly() {
            // given
            long productGroupId = 555L;
            List<LegacyCreateProductImageRequest> request = LegacyImageApiFixtures.requestList();

            // when
            LegacyUpdateImagesCommand command =
                    mapper.toLegacyUpdateImagesCommand(productGroupId, request);

            // then
            assertThat(command.productGroupId()).isEqualTo(555L);
        }

        @Test
        @DisplayName("이미지 엔트리의 타입이 올바르게 변환된다")
        void toLegacyUpdateImagesCommand_ConvertsImageType_Correctly() {
            // given
            long productGroupId = LegacyImageApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            List<LegacyCreateProductImageRequest> request = LegacyImageApiFixtures.requestList();

            // when
            LegacyUpdateImagesCommand command =
                    mapper.toLegacyUpdateImagesCommand(productGroupId, request);

            // then
            assertThat(command.images().get(0).imageType())
                    .isEqualTo(LegacyImageApiFixtures.DEFAULT_IMAGE_TYPE_THUMBNAIL);
            assertThat(command.images().get(1).imageType())
                    .isEqualTo(LegacyImageApiFixtures.DEFAULT_IMAGE_TYPE_DETAIL);
        }

        @Test
        @DisplayName("이미지 URL이 올바르게 변환된다")
        void toLegacyUpdateImagesCommand_ConvertsImageUrls_Correctly() {
            // given
            long productGroupId = LegacyImageApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            List<LegacyCreateProductImageRequest> request =
                    LegacyImageApiFixtures.singleRequestList();

            // when
            LegacyUpdateImagesCommand command =
                    mapper.toLegacyUpdateImagesCommand(productGroupId, request);

            // then
            LegacyUpdateImagesCommand.ImageEntry entry = command.images().get(0);
            assertThat(entry.imageUrl())
                    .isEqualTo(LegacyImageApiFixtures.DEFAULT_PRODUCT_IMAGE_URL);
            assertThat(entry.originUrl()).isEqualTo(LegacyImageApiFixtures.DEFAULT_ORIGIN_URL);
        }
    }

    @Nested
    @DisplayName("toImagesCommand - 내부 이미지 수정 Command 변환")
    class ToImagesCommandTest {

        @Test
        @DisplayName("이미지 목록을 UpdateProductGroupImagesCommand로 변환한다")
        void toImagesCommand_ConvertsRequest_ReturnsCommand() {
            // given
            long productGroupId = LegacyImageApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            List<LegacyCreateProductImageRequest> request = LegacyImageApiFixtures.requestList();

            // when
            UpdateProductGroupImagesCommand command =
                    mapper.toImagesCommand(productGroupId, request);

            // then
            assertThat(command.productGroupId()).isEqualTo(productGroupId);
            assertThat(command.images()).hasSize(2);
        }

        @Test
        @DisplayName("이미지 순서가 1부터 인덱스 기반으로 설정된다")
        void toImagesCommand_SetsSortOrderFromIndex_Correctly() {
            // given
            long productGroupId = LegacyImageApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            List<LegacyCreateProductImageRequest> request = LegacyImageApiFixtures.requestList();

            // when
            UpdateProductGroupImagesCommand command =
                    mapper.toImagesCommand(productGroupId, request);

            // then
            assertThat(command.images().get(0).sortOrder()).isEqualTo(1);
            assertThat(command.images().get(1).sortOrder()).isEqualTo(2);
        }

        @Test
        @DisplayName("이미지 타입과 원본 URL이 올바르게 변환된다")
        void toImagesCommand_ConvertsTypeAndOriginUrl_Correctly() {
            // given
            long productGroupId = LegacyImageApiFixtures.DEFAULT_PRODUCT_GROUP_ID;
            List<LegacyCreateProductImageRequest> request =
                    LegacyImageApiFixtures.singleRequestList();

            // when
            UpdateProductGroupImagesCommand command =
                    mapper.toImagesCommand(productGroupId, request);

            // then
            UpdateProductGroupImagesCommand.ImageCommand imageCommand = command.images().get(0);
            assertThat(imageCommand.imageType())
                    .isEqualTo(LegacyImageApiFixtures.DEFAULT_IMAGE_TYPE_THUMBNAIL);
            assertThat(imageCommand.originUrl())
                    .isEqualTo(LegacyImageApiFixtures.DEFAULT_ORIGIN_URL);
        }
    }
}
