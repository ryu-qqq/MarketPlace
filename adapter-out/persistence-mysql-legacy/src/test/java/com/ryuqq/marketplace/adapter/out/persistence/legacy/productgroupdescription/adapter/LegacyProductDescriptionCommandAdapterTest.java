package com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.adapter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.entity.LegacyProductGroupDetailDescriptionEntity;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.mapper.LegacyProductGroupDescriptionEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.productgroupdescription.repository.LegacyProductGroupDetailDescriptionJpaRepository;
import com.ryuqq.marketplace.domain.productgroup.aggregate.ProductGroupDescription;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productgroup.vo.DescriptionHtml;
import java.time.Instant;
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

    @Mock private LegacyProductGroupDescriptionEntityMapper mapper;

    @InjectMocks private LegacyProductDescriptionCommandAdapter commandAdapter;

    @Nested
    @DisplayName("persist 메서드 테스트")
    class PersistTest {

        @Test
        @DisplayName("상품 상세설명을 저장하고 productGroupId를 반환합니다")
        void persist_WithValidDescription_ReturnsProductGroupId() {
            // given
            ProductGroupDescription description =
                    ProductGroupDescription.forNew(
                            ProductGroupId.of(1L),
                            DescriptionHtml.of("<p>상세설명</p>"),
                            Instant.now());
            LegacyProductGroupDetailDescriptionEntity entity =
                    LegacyProductGroupDetailDescriptionEntity.createFull(
                            1L, "<p>상세설명</p>", null, "PENDING");

            given(mapper.toEntity(description)).willReturn(entity);
            given(repository.save(entity)).willReturn(entity);

            // when
            Long result = commandAdapter.persist(description);

            // then
            assertThat(result).isEqualTo(1L);
            then(mapper).should().toEntity(description);
            then(repository).should().save(entity);
        }
    }
}
