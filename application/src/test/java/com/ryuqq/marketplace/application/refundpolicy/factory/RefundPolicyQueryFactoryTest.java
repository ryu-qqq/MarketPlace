package com.ryuqq.marketplace.application.refundpolicy.factory;

import static org.assertj.core.api.Assertions.assertThat;

import com.ryuqq.marketplace.application.common.factory.CommonVoFactory;
import com.ryuqq.marketplace.application.refundpolicy.RefundPolicyQueryFixtures;
import com.ryuqq.marketplace.application.refundpolicy.dto.query.RefundPolicySearchParams;
import com.ryuqq.marketplace.domain.refundpolicy.query.RefundPolicySearchCriteria;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("unit")
@DisplayName("RefundPolicyQueryFactory 단위 테스트")
class RefundPolicyQueryFactoryTest {

    private final RefundPolicyQueryFactory sut =
            new RefundPolicyQueryFactory(new CommonVoFactory());

    @Nested
    @DisplayName("createCriteria() - SearchParams → SearchCriteria 변환")
    class CreateCriteriaTest {

        @Test
        @DisplayName("RefundPolicySearchParams로부터 RefundPolicySearchCriteria를 생성한다")
        void createCriteria_CreatesCriteria() {
            // given
            Long sellerId = 1L;
            RefundPolicySearchParams params = RefundPolicyQueryFixtures.searchParams(sellerId);

            // when
            RefundPolicySearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result).isNotNull();
            assertThat(result.sellerId().value()).isEqualTo(sellerId);
            assertThat(result.queryContext()).isNotNull();
        }

        @Test
        @DisplayName("페이징 정보가 포함된 Criteria를 생성한다")
        void createCriteria_WithPaging_CreatesCriteriaWithPaging() {
            // given
            Long sellerId = 1L;
            int page = 2;
            int size = 10;
            RefundPolicySearchParams params =
                    RefundPolicyQueryFixtures.searchParams(sellerId, page, size);

            // when
            RefundPolicySearchCriteria result = sut.createCriteria(params);

            // then
            assertThat(result.queryContext().page()).isEqualTo(page);
            assertThat(result.queryContext().size()).isEqualTo(size);
        }
    }
}
