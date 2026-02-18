package com.ryuqq.marketplace.adapter.out.persistence.productnotice.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.ryuqq.marketplace.adapter.out.persistence.productnotice.ProductNoticeJpaEntityFixtures;
import com.ryuqq.marketplace.adapter.out.persistence.productnotice.entity.ProductNoticeEntryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productnotice.entity.ProductNoticeJpaEntity;
import com.ryuqq.marketplace.domain.productnotice.ProductNoticeFixtures;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNoticeEntry;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * ProductNoticeJpaEntityMapperTest - 상품 고시정보 Entity-Domain 매퍼 단위 테스트.
 *
 * <p>PER-MAP-002: toEntity(Domain) + toDomain(Entity) 메서드 제공.
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@DisplayName("ProductNoticeJpaEntityMapper 단위 테스트")
class ProductNoticeJpaEntityMapperTest {

    private ProductNoticeJpaEntityMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ProductNoticeJpaEntityMapper();
    }

    // ========================================================================
    // 1. toEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntity 메서드 테스트")
    class ToEntityTest {

        @Test
        @DisplayName("새 ProductNotice를 Entity로 변환합니다")
        void toEntity_WithNewProductNotice_ConvertsCorrectly() {
            // given
            ProductNotice domain = ProductNoticeFixtures.newProductNotice();

            // when
            ProductNoticeJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getProductGroupId()).isEqualTo(domain.productGroupIdValue());
            assertThat(entity.getNoticeCategoryId()).isEqualTo(domain.noticeCategoryIdValue());
        }

        @Test
        @DisplayName("기존 ProductNotice를 Entity로 변환 시 ID가 설정됩니다")
        void toEntity_WithExistingProductNotice_SetsId() {
            // given
            ProductNotice domain = ProductNoticeFixtures.existingProductNotice(1L);

            // when
            ProductNoticeJpaEntity entity = mapper.toEntity(domain);

            // then
            assertThat(entity.getId()).isEqualTo(1L);
        }
    }

    // ========================================================================
    // 2. toEntryEntity 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntryEntity 메서드 테스트")
    class ToEntryEntityTest {

        @Test
        @DisplayName("ProductNoticeEntry를 EntryEntity로 변환합니다")
        void toEntryEntity_WithValidEntry_ConvertsCorrectly() {
            // given
            ProductNoticeEntry entry = ProductNoticeFixtures.defaultEntry();

            // when
            ProductNoticeEntryJpaEntity entity = mapper.toEntryEntity(entry);

            // then
            assertThat(entity.getNoticeFieldId()).isEqualTo(entry.noticeFieldIdValue());
            assertThat(entity.getFieldValue()).isEqualTo(entry.fieldValueValue());
        }

        @Test
        @DisplayName("특정 값을 가진 Entry를 Entity로 변환합니다")
        void toEntryEntity_WithSpecificEntry_ConvertsFieldValue() {
            // given
            ProductNoticeEntry entry = ProductNoticeFixtures.entry(200L, "대한민국");

            // when
            ProductNoticeEntryJpaEntity entity = mapper.toEntryEntity(entry);

            // then
            assertThat(entity.getNoticeFieldId()).isEqualTo(200L);
            assertThat(entity.getFieldValue()).isEqualTo("대한민국");
        }
    }

    // ========================================================================
    // 3. toDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toDomain 메서드 테스트")
    class ToDomainTest {

        @Test
        @DisplayName("Entity와 Entry 목록으로 ProductNotice Domain을 생성합니다")
        void toDomain_WithValidEntities_ConvertsCorrectly() {
            // given
            ProductNoticeJpaEntity entity = ProductNoticeJpaEntityFixtures.activeEntity(1L);
            List<ProductNoticeEntryJpaEntity> entries =
                    ProductNoticeJpaEntityFixtures.savedEntryEntities(1L);

            // when
            ProductNotice domain = mapper.toDomain(entity, entries);

            // then
            assertThat(domain.idValue()).isEqualTo(entity.getId());
            assertThat(domain.productGroupIdValue()).isEqualTo(entity.getProductGroupId());
            assertThat(domain.noticeCategoryIdValue()).isEqualTo(entity.getNoticeCategoryId());
            assertThat(domain.entries()).hasSize(entries.size());
        }

        @Test
        @DisplayName("Entry가 없는 경우 빈 목록으로 Domain을 생성합니다")
        void toDomain_WithEmptyEntries_ConvertsWithEmptyList() {
            // given
            ProductNoticeJpaEntity entity = ProductNoticeJpaEntityFixtures.activeEntity(1L);
            List<ProductNoticeEntryJpaEntity> entries =
                    ProductNoticeJpaEntityFixtures.emptyEntries();

            // when
            ProductNotice domain = mapper.toDomain(entity, entries);

            // then
            assertThat(domain.entries()).isEmpty();
        }

        @Test
        @DisplayName("ID가 null인 Entity는 예외를 발생시킵니다")
        void toDomain_WithNullId_ThrowsException() {
            // given
            ProductNoticeJpaEntity entity = ProductNoticeJpaEntityFixtures.newEntity();
            List<ProductNoticeEntryJpaEntity> entries =
                    ProductNoticeJpaEntityFixtures.emptyEntries();

            // when, then
            assertThatThrownBy(() -> mapper.toDomain(entity, entries))
                    .isInstanceOf(IllegalStateException.class);
        }
    }

    // ========================================================================
    // 4. toEntryDomain 테스트
    // ========================================================================

    @Nested
    @DisplayName("toEntryDomain 메서드 테스트")
    class ToEntryDomainTest {

        @Test
        @DisplayName("EntryEntity를 ProductNoticeEntry Domain으로 변환합니다")
        void toEntryDomain_WithValidEntity_ConvertsCorrectly() {
            // given
            ProductNoticeEntryJpaEntity entity =
                    ProductNoticeJpaEntityFixtures.savedEntryEntity(1L, 10L, 100L, "제조국");

            // when
            ProductNoticeEntry domain = mapper.toEntryDomain(entity);

            // then
            assertThat(domain.idValue()).isEqualTo(1L);
            assertThat(domain.noticeFieldIdValue()).isEqualTo(100L);
            assertThat(domain.fieldValueValue()).isEqualTo("제조국");
        }
    }
}
