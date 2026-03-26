package com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.entity.LegacyDescriptionImageEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.mapper.LegacyProductGroupDescriptionEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.repository.LegacyDescriptionImageJpaRepository;
import com.ryuqq.marketplace.domain.common.vo.DeletionStatus;
import com.ryuqq.marketplace.domain.productgroup.aggregate.DescriptionImage;
import com.ryuqq.marketplace.domain.productgroup.id.DescriptionImageId;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupDescriptionId;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageUrl;
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

    @Mock private LegacyProductGroupDescriptionEntityMapper mapper;

    @InjectMocks private LegacyDescriptionImageCommandAdapter commandAdapter;

    private DescriptionImage buildImage() {
        return DescriptionImage.reconstitute(
                DescriptionImageId.of(1L),
                ProductGroupDescriptionId.of(1L),
                ImageUrl.of("https://origin.example.com/img.jpg"),
                null,
                1,
                DeletionStatus.active());
    }

    private LegacyDescriptionImageEntity buildEntity() {
        return LegacyDescriptionImageEntity.create(
                null, 1L, "https://origin.example.com/img.jpg", null, 1, false, null);
    }

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("상세설명 이미지를 단건 저장하고 ID를 반환합니다")
        void persist_WithValidImage_ReturnsId() {
            // given
            DescriptionImage image = buildImage();
            LegacyDescriptionImageEntity entity = buildEntity();
            LegacyDescriptionImageEntity savedEntity =
                    LegacyDescriptionImageEntity.create(
                            100L, 1L, "https://origin.example.com/img.jpg", null, 1, false, null);

            given(mapper.toImageEntity(image)).willReturn(entity);
            given(repository.save(entity)).willReturn(savedEntity);

            // when
            Long result = commandAdapter.persist(image);

            // then
            assertThat(result).isEqualTo(100L);
            then(mapper).should().toImageEntity(image);
            then(repository).should().save(entity);
        }
    }
}
