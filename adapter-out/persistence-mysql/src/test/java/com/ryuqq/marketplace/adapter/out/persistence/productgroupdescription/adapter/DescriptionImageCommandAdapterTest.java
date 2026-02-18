package com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.ProductGroupDescriptionJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.entity.DescriptionImageJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.mapper.ProductGroupDescriptionJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.productgroupdescription.repository.DescriptionImageJpaRepository;
import com.ryuqq.marketplace.domain.productgroup.ProductGroupFixtures;
import com.ryuqq.marketplace.domain.productgroup.aggregate.DescriptionImage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * DescriptionImageCommandAdapterTest - 상세설명 이미지 Command Adapter 단위 테스트.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("DescriptionImageCommandAdapter 단위 테스트")
class DescriptionImageCommandAdapterTest {

    @Mock private DescriptionImageJpaRepository repository;

    @Mock private ProductGroupDescriptionJpaEntityMapper mapper;

    @InjectMocks private DescriptionImageCommandAdapter commandAdapter;

    // ========================================================================
    // 1. persist 테스트
    // ========================================================================

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("DescriptionImage를 Entity로 변환 후 저장하고 ID를 반환합니다")
        void persist_WithValidImage_SavesAndReturnsId() {
            // given
            DescriptionImage domain = ProductGroupFixtures.defaultDescriptionImage();
            DescriptionImageJpaEntity entityToSave =
                    ProductGroupDescriptionJpaEntityFixtures.pendingImageEntity(1L);
            DescriptionImageJpaEntity savedEntity =
                    ProductGroupDescriptionJpaEntityFixtures.imageEntity(100L, 1L);

            given(mapper.toImageEntity(domain)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isEqualTo(100L);
            then(mapper).should().toImageEntity(domain);
            then(repository).should().save(entityToSave);
        }

        @Test
        @DisplayName("업로드 완료된 DescriptionImage를 저장합니다")
        void persist_WithUploadedImage_Saves() {
            // given
            DescriptionImage domain = ProductGroupFixtures.uploadedDescriptionImage();
            DescriptionImageJpaEntity entityToSave =
                    ProductGroupDescriptionJpaEntityFixtures.uploadedImageEntity(1L);
            DescriptionImageJpaEntity savedEntity =
                    ProductGroupDescriptionJpaEntityFixtures.imageEntity(1L, 1L);

            given(mapper.toImageEntity(domain)).willReturn(entityToSave);
            given(repository.save(entityToSave)).willReturn(savedEntity);

            // when
            Long savedId = commandAdapter.persist(domain);

            // then
            assertThat(savedId).isNotNull();
        }
    }
}
