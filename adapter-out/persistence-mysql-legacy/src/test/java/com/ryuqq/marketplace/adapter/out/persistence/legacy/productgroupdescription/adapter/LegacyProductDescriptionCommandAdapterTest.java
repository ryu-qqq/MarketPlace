package com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.adapter;

import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.entity.LegacyProductGroupDetailDescriptionEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.product.mapper.LegacyProductCommandEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.repository.LegacyProductGroupDetailDescriptionJpaRepository;
import com.ryuqq.marketplace.domain.legacy.productdescription.aggregate.LegacyProductGroupDescription;
import com.ryuqq.marketplace.domain.legacy.productgroup.id.LegacyProductGroupId;
import com.ryuqq.marketplace.domain.legacy.productdescription.vo.LegacyProductDescription;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * LegacyProductDescriptionCommandAdapterTest - 레거시 상품 상세설명 Command Adapter 단위 테스트.
 *
 * <p>PER-ADP-001: CommandAdapter는 JpaRepository만 사용.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("LegacyProductDescriptionCommandAdapter 단위 테스트")
class LegacyProductDescriptionCommandAdapterTest {

    @Mock private LegacyProductGroupDetailDescriptionJpaRepository repository;

    @Mock private LegacyProductCommandEntityMapper mapper;

    @InjectMocks private LegacyProductDescriptionCommandAdapter commandAdapter;

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("상품 상세설명을 저장합니다")
        void persist_WithValidDescription_SavesSuccessfully() {
            // given
            LegacyProductGroupId productGroupId = LegacyProductGroupId.of(1L);
            LegacyProductDescription description = new LegacyProductDescription("<p>상세설명</p>");
            LegacyProductGroupDetailDescriptionEntity entity =
                    LegacyProductGroupDetailDescriptionEntity.create(1L, "<p>상세설명</p>");

            given(mapper.toEntity(productGroupId, description)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            commandAdapter.persist(productGroupId, description);

            // then
            then(mapper).should().toEntity(productGroupId, description);
            then(repository).should().save(entity);
        }
    }

    @Nested
    @DisplayName("persistDescription 메서드 테스트")
    class PersistDescriptionTest {

        @Test
        @DisplayName("LegacyProductGroupDescription 전체를 저장합니다")
        void persistDescription_WithValidGroupDescription_SavesSuccessfully() {
            // given
            LegacyProductGroupDescription description =
                    LegacyProductGroupDescription.forNew(1L, "<p>컨텐츠</p>");
            LegacyProductGroupDetailDescriptionEntity entity =
                    LegacyProductGroupDetailDescriptionEntity.createFull(
                            1L, "<p>컨텐츠</p>", null, "PENDING");

            given(mapper.toDescriptionEntity(description)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            commandAdapter.persistDescription(description);

            // then
            then(mapper).should().toDescriptionEntity(description);
            then(repository).should().save(entity);
        }
    }
}
