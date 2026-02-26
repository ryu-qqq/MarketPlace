package com.ryuqq.marketplace.adapter.out.persistence.legacy.product.adapter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.entity.LegacyDescriptionImageEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.mapper.LegacyProductCommandEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.repository.LegacyDescriptionImageJpaRepository;
import com.ryuqq.marketplace.domain.legacy.productgroup.aggregate.LegacyDescriptionImage;
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
 * LegacyDescriptionImageCommandAdapterTest - 레거시 상세설명 이미지 Command Adapter 단위 테스트.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyDescriptionImageCommandAdapter 단위 테스트")
class LegacyDescriptionImageCommandAdapterTest {

    @Mock private LegacyDescriptionImageJpaRepository repository;

    @Mock private LegacyProductCommandEntityMapper mapper;

    @InjectMocks private LegacyDescriptionImageCommandAdapter commandAdapter;

    private LegacyDescriptionImage buildImage() {
        return LegacyDescriptionImage.forNew(1L, "https://origin.example.com/img.jpg", 1);
    }

    private LegacyDescriptionImageEntity buildEntity() {
        return LegacyDescriptionImageEntity.create(
                null, 1L, "https://origin.example.com/img.jpg", null, 1, false, null);
    }

    @Nested
    @DisplayName("persistAll 메서드 테스트")
    class PersistAllTest {

        @Test
        @DisplayName("여러 상세설명 이미지를 일괄 저장합니다")
        void persistAll_WithMultipleImages_SavesAll() {
            // given
            LegacyDescriptionImage image1 = buildImage();
            LegacyDescriptionImage image2 =
                    LegacyDescriptionImage.forNew(1L, "https://origin.example.com/img2.jpg", 2);
            List<LegacyDescriptionImage> images = List.of(image1, image2);

            LegacyDescriptionImageEntity entity1 = buildEntity();
            LegacyDescriptionImageEntity entity2 =
                    LegacyDescriptionImageEntity.create(
                            null, 1L, "https://origin.example.com/img2.jpg", null, 2, false, null);

            given(mapper.toImageEntity(image1)).willReturn(entity1);
            given(mapper.toImageEntity(image2)).willReturn(entity2);
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

    @Nested
    @DisplayName("softDeleteAll 메서드 테스트")
    class SoftDeleteAllTest {

        @Test
        @DisplayName("여러 상세설명 이미지를 소프트 삭제합니다")
        void softDeleteAll_WithMultipleImages_SoftDeletesAll() {
            // given
            LegacyDescriptionImage image = buildImage();
            LegacyDescriptionImageEntity entity = buildEntity();

            given(mapper.toImageEntity(image)).willReturn(entity);
            given(repository.saveAll(anyList())).willReturn(List.of(entity));

            // when
            commandAdapter.softDeleteAll(List.of(image));

            // then
            then(repository).should().saveAll(anyList());
        }

        @Test
        @DisplayName("빈 목록 소프트 삭제 시 saveAll이 호출됩니다")
        void softDeleteAll_WithEmptyList_CallsSaveAll() {
            // given
            given(repository.saveAll(any())).willReturn(List.of());

            // when
            commandAdapter.softDeleteAll(List.of());

            // then
            then(repository).should().saveAll(any());
        }
    }
}
