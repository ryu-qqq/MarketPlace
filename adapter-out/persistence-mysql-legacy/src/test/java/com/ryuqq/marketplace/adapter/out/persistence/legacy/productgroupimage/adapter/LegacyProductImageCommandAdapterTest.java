package com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupimage.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupimage.entity.LegacyProductGroupImageEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupimage.mapper.LegacyProductGroupImageEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupimage.repository.LegacyProductGroupImageJpaRepository;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageType;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;
import com.ryuqq.marketplace.domain.productgroupimage.aggregate.ProductGroupImage;
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

    @Mock private LegacyProductGroupImageEntityMapper mapper;

    @InjectMocks private LegacyProductImageCommandAdapter commandAdapter;

    private ProductGroupImage buildImage() {
        return ProductGroupImage.forNew(
                ProductGroupId.of(1L),
                ImageUrl.of("https://origin.example.com/image.jpg"),
                ImageType.THUMBNAIL,
                1);
    }

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("상품 이미지를 단건 저장하고 ID를 반환합니다")
        void persist_WithValidImage_ReturnsId() {
            // given
            ProductGroupImage image = buildImage();
            LegacyProductGroupImageEntity entity =
                    LegacyProductGroupImageEntity.create(
                            null,
                            1L,
                            "MAIN",
                            "https://origin.example.com/image.jpg",
                            "https://origin.example.com/image.jpg",
                            1L,
                            "N");
            LegacyProductGroupImageEntity savedEntity =
                    LegacyProductGroupImageEntity.create(
                            100L,
                            1L,
                            "MAIN",
                            "https://origin.example.com/image.jpg",
                            "https://origin.example.com/image.jpg",
                            1L,
                            "N");

            given(mapper.toEntity(image)).willReturn(entity);
            given(repository.save(entity)).willReturn(savedEntity);

            // when
            Long result = commandAdapter.persist(image);

            // then
            assertThat(result).isEqualTo(100L);
            then(mapper).should().toEntity(image);
            then(repository).should().save(entity);
        }
    }
}
