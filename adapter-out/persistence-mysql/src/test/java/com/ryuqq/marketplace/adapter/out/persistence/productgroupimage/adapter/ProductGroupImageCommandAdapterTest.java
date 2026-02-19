package com.ryuqq.marketplace.adapter.out.persistence.productgroupimage.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.productgroup.mapper.ProductGroupJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupimage.ProductGroupImageJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupimage.entity.ProductGroupImageJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupimage.repository.ProductGroupImageJpaRepository;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
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
 * ProductGroupImageCommandAdapterTest - 상품 그룹 이미지 Command Adapter 단위 테스트.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("ProductGroupImageCommandAdapter 단위 테스트")
class ProductGroupImageCommandAdapterTest {

    @Mock private ProductGroupImageJpaRepository jpaRepository;

    @Mock private ProductGroupJpaEntityMapper mapper;

    @InjectMocks private ProductGroupImageCommandAdapter commandAdapter;

    // ========================================================================
    // 1. persist 테스트
    // ========================================================================

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("ProductGroupImage를 Entity로 변환 후 저장하고 ID를 반환합니다")
        void persist_WithValidImage_SavesAndReturnsId() {
            // given
            ProductGroupImage domain = ProductGroupFixtures.thumbnailImage();
            ProductGroupImageJpaEntity entityToSave =
                    ProductGroupImageJpaEntityFixtures.newEntity();
            ProductGroupImageJpaEntity savedEntity =
                    ProductGroupImageJpaEntityFixtures.thumbnailEntity(100L, 1L);

            given(mapper.toImageEntity(domain)).willReturn(entityToSave);
            given(jpaRepository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isEqualTo(100L);
            then(mapper).should().toImageEntity(domain);
            then(jpaRepository).should().save(entityToSave);
        }

        @Test
        @DisplayName("썸네일 이미지를 저장합니다")
        void persist_WithThumbnailImage_Saves() {
            // given
            ProductGroupImage domain = ProductGroupFixtures.thumbnailImage();
            ProductGroupImageJpaEntity entity = ProductGroupImageJpaEntityFixtures.newEntity();
            ProductGroupImageJpaEntity saved =
                    ProductGroupImageJpaEntityFixtures.thumbnailEntity(1L, 1L);

            given(mapper.toImageEntity(domain)).willReturn(entity);
            given(jpaRepository.save(entity)).willReturn(saved);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isNotNull();
        }

        @Test
        @DisplayName("업로드 완료된 이미지를 저장합니다")
        void persist_WithUploadedImage_Saves() {
            // given
            ProductGroupImage domain = ProductGroupFixtures.uploadedImage();
            ProductGroupImageJpaEntity entity = ProductGroupImageJpaEntityFixtures.newEntity();
            ProductGroupImageJpaEntity saved =
                    ProductGroupImageJpaEntityFixtures.thumbnailEntity(2L, 1L);

            given(mapper.toImageEntity(domain)).willReturn(entity);
            given(jpaRepository.save(entity)).willReturn(saved);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isNotNull();
        }
    }
}
