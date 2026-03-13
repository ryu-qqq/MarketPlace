package com.ryuqq.marketplace.adapter.out.persistence.outboundproductimage.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.adapter.out.persistence.outboundproductimage.OutboundProductImageJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.outboundproductimage.entity.OutboundProductImageJpaEntity;
import com.ryuqq.marketplace.domain.common.vo.DeletionStatus;
import com.ryuqq.marketplace.domain.outboundproductimage.OutboundProductImageFixtures;
import com.ryuqq.marketplace.domain.outboundproductimage.aggregate.OutboundProductImage;
import com.ryuqq.marketplace.domain.productgroup.vo.ImageType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * OutboundProductImageJpaEntityMapperTest - Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * <p>PER-MAP-003: 순수 변환 로직만.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@DisplayName("OutboundProductImageJpaEntityMapper 단위 테스트")
class OutboundProductImageJpaEntityMapperTest {

    private OutboundProductImageJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new OutboundProductImageJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("활성 THUMBNAIL Domain을 Entity로 변환합니다 - 모든 필드가 매핑됩니다")
        void toEntity_WithActiveThumbnailDomain_ConvertsAllFields() {
            // given
            OutboundProductImage domain = OutboundProductImageFixtures.activeThumbnailImage();

            // when
            OutboundProductImageJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(domain.idValue());
            assertThat(entity.getOutboundProductId()).isEqualTo(domain.outboundProductIdValue());
            assertThat(entity.getProductGroupImageId()).isEqualTo(domain.productGroupImageIdValue());
            assertThat(entity.getOriginUrl()).isEqualTo(domain.originUrl());
            assertThat(entity.getExternalUrl()).isEqualTo(domain.externalUrl());
            assertThat(entity.getImageType()).isEqualTo(domain.imageType().name());
            assertThat(entity.getSortOrder()).isEqualTo(domain.sortOrder());
            assertThat(entity.isDeleted()).isEqualTo(domain.deletionStatus().deleted());
            assertThat(entity.getDeletedAt()).isEqualTo(domain.deletionStatus().deletedAt());
        }

        @Test
        @DisplayName("DETAIL 타입 Domain을 Entity로 변환합니다 - imageType이 DETAIL로 저장됩니다")
        void toEntity_WithDetailTypeDomain_ConvertsImageTypeCorrectly() {
            // given
            OutboundProductImage domain = OutboundProductImageFixtures.activeDetailImage(2L, 1);

            // when
            OutboundProductImageJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getImageType()).isEqualTo(ImageType.DETAIL.name());
        }

        @Test
        @DisplayName("삭제된 Domain을 Entity로 변환합니다 - deleted=true, deletedAt이 설정됩니다")
        void toEntity_WithDeletedDomain_ConvertsDeletedStatus() {
            // given
            OutboundProductImage domain = OutboundProductImageFixtures.deletedThumbnailImage();

            // when
            OutboundProductImageJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.isDeleted()).isTrue();
            assertThat(entity.getDeletedAt()).isNotNull();
            assertThat(entity.getDeletedAt()).isEqualTo(domain.deletionStatus().deletedAt());
        }

        @Test
        @DisplayName("ID가 null인 신규 Domain을 Entity로 변환합니다 - Entity ID도 null입니다")
        void toEntity_WithNewDomain_EntityIdIsNull() {
            // given
            OutboundProductImage domain = OutboundProductImageFixtures.newThumbnailImage();

            // when
            OutboundProductImageJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isNull();
        }

        @Test
        @DisplayName("externalUrl이 null인 Domain을 Entity로 변환합니다 - externalUrl이 null로 저장됩니다")
        void toEntity_WithNullExternalUrl_EntityExternalUrlIsNull() {
            // given
            OutboundProductImage domain = OutboundProductImageFixtures.newThumbnailImage();

            // when
            OutboundProductImageJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getExternalUrl()).isNull();
        }
    }

    // ========================================================================
    // 2. toDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDomain 메서드 테스트")
    class ToDomainTest {

        @Test
        @DisplayName("활성 THUMBNAIL Entity를 Domain으로 변환합니다 - 모든 필드가 매핑됩니다")
        void toDomain_WithActiveThumbnailEntity_ConvertsAllFields() {
            // given
            OutboundProductImageJpaEntity entity =
                    OutboundProductImageJpaEntityFixtures.entityWithId(1L);

            // when
            OutboundProductImage domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.outboundProductIdValue()).isEqualTo(entity.getOutboundProductId());
            assertThat(domain.productGroupImageIdValue()).isEqualTo(entity.getProductGroupImageId());
            assertThat(domain.originUrl()).isEqualTo(entity.getOriginUrl());
            assertThat(domain.externalUrl()).isEqualTo(entity.getExternalUrl());
            assertThat(domain.imageType().name()).isEqualTo(entity.getImageType());
            assertThat(domain.sortOrder()).isEqualTo(entity.getSortOrder());
            assertThat(domain.deletionStatus().deleted()).isEqualTo(entity.isDeleted());
            assertThat(domain.deletionStatus().deletedAt()).isEqualTo(entity.getDeletedAt());
        }

        @Test
        @DisplayName("deleted=false Entity를 Domain으로 변환합니다 - DeletionStatus.active()가 됩니다")
        void toDomain_WithNotDeletedEntity_ReturnsDeletionStatusActive() {
            // given
            OutboundProductImageJpaEntity entity =
                    OutboundProductImageJpaEntityFixtures.newThumbnailEntity();

            // when
            OutboundProductImage domain = mapper.toDomain(entity);

            // then
            assertThat(domain.deletionStatus()).isEqualTo(DeletionStatus.active());
            assertThat(domain.deletionStatus().isDeleted()).isFalse();
            assertThat(domain.deletionStatus().deletedAt()).isNull();
        }

        @Test
        @DisplayName("deleted=true Entity를 Domain으로 변환합니다 - DeletionStatus가 삭제 상태입니다")
        void toDomain_WithDeletedEntity_ReturnsDeletionStatusDeleted() {
            // given
            OutboundProductImageJpaEntity entity =
                    OutboundProductImageJpaEntityFixtures.deletedThumbnailEntity();

            // when
            OutboundProductImage domain = mapper.toDomain(entity);

            // then
            assertThat(domain.deletionStatus().isDeleted()).isTrue();
            assertThat(domain.deletionStatus().deletedAt()).isNotNull();
            assertThat(domain.deletionStatus().deletedAt()).isEqualTo(entity.getDeletedAt());
        }

        @Test
        @DisplayName("ID가 있는 Entity를 Domain으로 변환합니다 - ID가 보존됩니다")
        void toDomain_WithExistingIdEntity_PreservesId() {
            // given
            OutboundProductImageJpaEntity entity =
                    OutboundProductImageJpaEntityFixtures.entityWithId(42L);

            // when
            OutboundProductImage domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(42L);
            assertThat(domain.id().isNew()).isFalse();
        }

        @Test
        @DisplayName("ID가 null인 Entity를 Domain으로 변환합니다 - 신규 ID가 할당됩니다")
        void toDomain_WithNullIdEntity_AssignsNewId() {
            // given
            OutboundProductImageJpaEntity entity =
                    OutboundProductImageJpaEntityFixtures.newThumbnailEntity();

            // when
            OutboundProductImage domain = mapper.toDomain(entity);

            // then
            assertThat(domain.idValue()).isNull();
            assertThat(domain.id().isNew()).isTrue();
        }

        @Test
        @DisplayName("THUMBNAIL 타입 Entity를 Domain으로 변환합니다 - ImageType.THUMBNAIL이 됩니다")
        void toDomain_WithThumbnailTypeEntity_ConvertsImageTypeCorrectly() {
            // given
            OutboundProductImageJpaEntity entity =
                    OutboundProductImageJpaEntityFixtures.newThumbnailEntity();

            // when
            OutboundProductImage domain = mapper.toDomain(entity);

            // then
            assertThat(domain.imageType()).isEqualTo(ImageType.THUMBNAIL);
            assertThat(domain.isThumbnail()).isTrue();
        }

        @Test
        @DisplayName("DETAIL 타입 Entity를 Domain으로 변환합니다 - ImageType.DETAIL이 됩니다")
        void toDomain_WithDetailTypeEntity_ConvertsImageTypeCorrectly() {
            // given
            OutboundProductImageJpaEntity entity =
                    OutboundProductImageJpaEntityFixtures.newDetailEntity();

            // when
            OutboundProductImage domain = mapper.toDomain(entity);

            // then
            assertThat(domain.imageType()).isEqualTo(ImageType.DETAIL);
            assertThat(domain.isThumbnail()).isFalse();
        }
    }

    // ========================================================================
    // 3. 양방향 변환 테스트
    // ========================================================================

    @Nested
    @DisplayName("양방향 변환 테스트")
    class BidirectionalConversionTest {

        @Test
        @DisplayName("Entity -> Domain -> Entity 변환 시 핵심 데이터가 보존됩니다")
        void roundTrip_EntityToDomainToEntity_PreservesData() {
            // given
            OutboundProductImageJpaEntity original =
                    OutboundProductImageJpaEntityFixtures.entityWithId(10L);

            // when
            OutboundProductImage domain = mapper.toDomain(original);
            OutboundProductImageJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.getId()).isEqualTo(original.getId());
            assertThat(converted.getOutboundProductId()).isEqualTo(original.getOutboundProductId());
            assertThat(converted.getProductGroupImageId())
                    .isEqualTo(original.getProductGroupImageId());
            assertThat(converted.getOriginUrl()).isEqualTo(original.getOriginUrl());
            assertThat(converted.getExternalUrl()).isEqualTo(original.getExternalUrl());
            assertThat(converted.getImageType()).isEqualTo(original.getImageType());
            assertThat(converted.getSortOrder()).isEqualTo(original.getSortOrder());
            assertThat(converted.isDeleted()).isEqualTo(original.isDeleted());
            assertThat(converted.getDeletedAt()).isEqualTo(original.getDeletedAt());
        }

        @Test
        @DisplayName("삭제된 Entity 양방향 변환 시 DeletionStatus가 보존됩니다")
        void roundTrip_DeletedEntity_DeletionStatusPreserved() {
            // given
            OutboundProductImageJpaEntity original =
                    OutboundProductImageJpaEntityFixtures.deletedThumbnailEntity();

            // when
            OutboundProductImage domain = mapper.toDomain(original);
            OutboundProductImageJpaEntity converted = mapper.toEntity(domain);

            // then
            assertThat(converted.isDeleted()).isTrue();
            assertThat(converted.getDeletedAt()).isEqualTo(original.getDeletedAt());
        }
    }
}
