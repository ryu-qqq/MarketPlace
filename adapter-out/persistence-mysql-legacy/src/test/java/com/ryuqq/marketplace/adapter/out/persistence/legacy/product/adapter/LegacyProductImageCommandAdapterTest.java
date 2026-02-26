package com.ryuqq.marketplace.adapter.out.persistence.legacy.product.adapter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyProductGroupImageEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.mapper.LegacyProductCommandEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.repository.LegacyProductGroupImageJpaRepository;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import com.ryuqq.marketplace.domain.legacy.productimage.aggregate.LegacyProductImage;
import com.ryuqq.marketplace.domain.legacy.productimage.vo.ProductGroupImageType;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * LegacyProductImageCommandAdapterTest - 레거시 상품 이미지 Command Adapter 단위 테스트.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyProductImageCommandAdapter 단위 테스트")
class LegacyProductImageCommandAdapterTest {

    @Mock private LegacyProductGroupImageJpaRepository repository;

    @Mock private LegacyProductCommandEntityMapper mapper;

    @InjectMocks private LegacyProductImageCommandAdapter commandAdapter;

    private LegacyProductImage buildImage() {
        return LegacyProductImage.forNew(
                LegacyProductGroupId.of(1L),
                ProductGroupImageType.MAIN,
                "https://cdn.example.com/image.jpg",
                "https://origin.example.com/image.jpg",
                1);
    }

    @Nested
    @DisplayName("persistAll 메서드 테스트")
    class PersistAllTest {

        @Test
        @DisplayName("여러 상품 이미지를 일괄 저장합니다")
        void persistAll_WithMultipleImages_SavesAll() {
            // given
            LegacyProductImage image1 = buildImage();
            LegacyProductImage image2 =
                    LegacyProductImage.forNew(
                            LegacyProductGroupId.of(1L),
                            ProductGroupImageType.DETAIL,
                            "https://cdn.example.com/detail.jpg",
                            "https://origin.example.com/detail.jpg",
                            2);
            List<LegacyProductImage> images = List.of(image1, image2);

            LegacyProductGroupImageEntity entity1 =
                    LegacyProductGroupImageEntity.create(
                            null,
                            1L,
                            "MAIN",
                            "https://cdn.example.com/image.jpg",
                            "https://origin.example.com/image.jpg",
                            1L,
                            "N");
            LegacyProductGroupImageEntity entity2 =
                    LegacyProductGroupImageEntity.create(
                            null,
                            1L,
                            "DETAIL",
                            "https://cdn.example.com/detail.jpg",
                            "https://origin.example.com/detail.jpg",
                            2L,
                            "N");

            given(mapper.toEntity(image1)).willReturn(entity1);
            given(mapper.toEntity(image2)).willReturn(entity2);
            given(repository.saveAll(anyList())).willReturn(List.of(entity1, entity2));

            // when
            commandAdapter.persistAll(images);

            // then
            then(repository).should().saveAll(anyList());
        }

        @Test
        @DisplayName("빈 목록 저장 시 saveAll이 호출됩니다")
        void persistAll_WithEmptyList_CallsSaveAll() {
            // given
            given(repository.saveAll(any())).willReturn(List.of());

            // when
            commandAdapter.persistAll(List.of());

            // then
            then(repository).should().saveAll(any());
        }
    }
}
