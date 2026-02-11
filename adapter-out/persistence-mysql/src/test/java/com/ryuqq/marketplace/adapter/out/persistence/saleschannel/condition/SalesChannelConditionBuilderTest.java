package com.ryuqq.marketplace.adapter.out.persistence.saleschannel.condition;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

import com.querydsl.core.types.dsl.BooleanExpression;
import com.ryuqq.marketplace.domain.saleschannel.query.SalesChannelSearchCriteria;
import com.ryuqq.marketplace.domain.saleschannel.query.SalesChannelSearchField;
import com.ryuqq.marketplace.domain.saleschannel.vo.SalesChannelStatus;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * SalesChannelConditionBuilderTest - 판매 채널 조건 빌더 단위 테스트.
 *
 * <p>PER-CND-002: 각 조건은 BooleanExpression 반환.
 *
 * <p>PER-CND-003: null 입력 시 null 반환 (동적 쿼리 지원).
 *
 * @author ryu-qqq
 * @since 1.1.0
 */
@Tag("unit")
@ExtendWith(MockitoExtension.class)
@DisplayName("SalesChannelConditionBuilder 단위 테스트")
class SalesChannelConditionBuilderTest {

    private SalesChannelConditionBuilder conditionBuilder;

    @Mock private SalesChannelSearchCriteria criteria;

    @BeforeEach
    void setUp() {
        conditionBuilder = new SalesChannelConditionBuilder();
    }

    // ========================================================================
    // 1. idEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("idEq 메서드 테스트")
    class IdEqTest {

        @Test
        @DisplayName("유효한 ID 입력 시 BooleanExpression을 반환합니다")
        void idEq_WithValidId_ReturnsBooleanExpression() {
            // given
            Long id = 1L;

            // when
            BooleanExpression result = conditionBuilder.idEq(id);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null ID 입력 시 null을 반환합니다")
        void idEq_WithNullId_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.idEq(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 2. channelNameEq 테스트
    // ========================================================================

    @Nested
    @DisplayName("channelNameEq 메서드 테스트")
    class ChannelNameEqTest {

        @Test
        @DisplayName("유효한 채널명 입력 시 BooleanExpression을 반환합니다")
        void channelNameEq_WithValidName_ReturnsBooleanExpression() {
            // given
            String channelName = "테스트 채널";

            // when
            BooleanExpression result = conditionBuilder.channelNameEq(channelName);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("null 채널명 입력 시 null을 반환합니다")
        void channelNameEq_WithNullName_ReturnsNull() {
            // when
            BooleanExpression result = conditionBuilder.channelNameEq(null);

            // then
            assertThat(result).isNull();
        }
    }

    // ========================================================================
    // 3. statusIn 테스트
    // ========================================================================

    @Nested
    @DisplayName("statusIn 메서드 테스트")
    class StatusInTest {

        @Test
        @DisplayName("상태 필터가 있으면 BooleanExpression을 반환합니다")
        void statusIn_WithStatusFilter_ReturnsBooleanExpression() {
            // given
            List<SalesChannelStatus> statuses = List.of(SalesChannelStatus.ACTIVE);
            given(criteria.hasStatusFilter()).willReturn(true);
            given(criteria.statuses()).willReturn(statuses);

            // when
            BooleanExpression result = conditionBuilder.statusIn(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("상태 필터가 없으면 null을 반환합니다")
        void statusIn_WithoutStatusFilter_ReturnsNull() {
            // given
            given(criteria.hasStatusFilter()).willReturn(false);

            // when
            BooleanExpression result = conditionBuilder.statusIn(criteria);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("여러 상태 필터가 있으면 BooleanExpression을 반환합니다")
        void statusIn_WithMultipleStatuses_ReturnsBooleanExpression() {
            // given
            List<SalesChannelStatus> statuses =
                    List.of(SalesChannelStatus.ACTIVE, SalesChannelStatus.INACTIVE);
            given(criteria.hasStatusFilter()).willReturn(true);
            given(criteria.statuses()).willReturn(statuses);

            // when
            BooleanExpression result = conditionBuilder.statusIn(criteria);

            // then
            assertThat(result).isNotNull();
        }
    }

    // ========================================================================
    // 4. searchCondition 테스트
    // ========================================================================

    @Nested
    @DisplayName("searchCondition 메서드 테스트")
    class SearchConditionTest {

        @Test
        @DisplayName("검색 조건이 있으면 BooleanExpression을 반환합니다")
        void searchCondition_WithSearchCondition_ReturnsBooleanExpression() {
            // given
            given(criteria.hasSearchCondition()).willReturn(true);
            given(criteria.hasSearchField()).willReturn(true);
            given(criteria.searchField()).willReturn(SalesChannelSearchField.CHANNEL_NAME);
            given(criteria.searchWord()).willReturn("테스트");

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("검색 조건이 없으면 null을 반환합니다")
        void searchCondition_WithoutSearchCondition_ReturnsNull() {
            // given
            given(criteria.hasSearchCondition()).willReturn(false);

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNull();
        }

        @Test
        @DisplayName("검색 필드가 없으면 채널명으로 검색합니다")
        void searchCondition_WithoutSearchField_SearchesByChannelName() {
            // given
            given(criteria.hasSearchCondition()).willReturn(true);
            given(criteria.hasSearchField()).willReturn(false);
            given(criteria.searchWord()).willReturn("테스트");

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }

        @Test
        @DisplayName("CHANNEL_NAME 필드로 검색 시 BooleanExpression을 반환합니다")
        void searchCondition_WithChannelNameField_ReturnsBooleanExpression() {
            // given
            given(criteria.hasSearchCondition()).willReturn(true);
            given(criteria.hasSearchField()).willReturn(true);
            given(criteria.searchField()).willReturn(SalesChannelSearchField.CHANNEL_NAME);
            given(criteria.searchWord()).willReturn("테스트");

            // when
            BooleanExpression result = conditionBuilder.searchCondition(criteria);

            // then
            assertThat(result).isNotNull();
        }
    }
}
