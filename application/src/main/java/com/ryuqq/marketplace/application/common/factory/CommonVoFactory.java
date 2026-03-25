package com.ryuqq.marketplace.application.common.factory;

import com.ryuqq.marketplace.domain.common.vo.DateRange;
import com.ryuqq.marketplace.domain.common.vo.PageRequest;
import com.ryuqq.marketplace.domain.common.vo.QueryContext;
import com.ryuqq.marketplace.domain.common.vo.SortDirection;
import com.ryuqq.marketplace.domain.common.vo.SortKey;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

/**
 * Common VO Factory
 *
 * <p>Domain Layer의 공통 VO 생성을 담당합니다.
 *
 * <p><strong>설계 원칙:</strong>
 *
 * <ul>
 *   <li>단순 변환만 수행 - 비즈니스 로직/검증 로직 금지
 *   <li>null 체크 금지 - VO 내부 검증 또는 API Mapper에서 처리
 *   <li>기본값 처리 금지 - API Mapper에서 처리
 *   <li>파라미터를 그대로 VO.of()에 전달
 * </ul>
 *
 * <p><strong>사용 예시:</strong>
 *
 * <pre>{@code
 * @Component
 * public class OrderQueryFactory {
 *     private final CommonVoFactory commonVoFactory;
 *
 *     public OrderSearchCriteria create(SearchOrdersQuery query) {
 *         DateRange dateRange = commonVoFactory.createDateRange(
 *             query.startDate(), query.endDate()
 *         );
 *         // ...
 *     }
 * }
 * }</pre>
 *
 * @author development-team
 * @since 1.0.0
 */
@Component
public class CommonVoFactory {

    /**
     * DateRange 생성
     *
     * <p>null 체크 금지 - 둘 다 null이면 null DateRange 생성됨
     *
     * @param startDate 시작일
     * @param endDate 종료일
     * @return DateRange
     */
    public DateRange createDateRange(LocalDate startDate, LocalDate endDate) {
        return DateRange.of(startDate, endDate);
    }

    /**
     * PageRequest 생성
     *
     * @param page 페이지 번호 (0부터 시작)
     * @param size 페이지 크기
     * @return PageRequest
     */
    public PageRequest createPageRequest(Integer page, Integer size) {
        return PageRequest.of(page, size);
    }

    /**
     * SortDirection 파싱
     *
     * @param direction 정렬 방향 문자열 ("ASC", "DESC")
     * @return SortDirection
     */
    public SortDirection parseSortDirection(String direction) {
        return SortDirection.fromString(direction);
    }

    /**
     * QueryContext 생성
     *
     * @param sortKey 정렬 키
     * @param sortDirection 정렬 방향
     * @param pageRequest 페이징 정보
     * @param <K> 정렬 키 타입
     * @return QueryContext
     */
    public <K extends SortKey> QueryContext<K> createQueryContext(
            K sortKey, SortDirection sortDirection, PageRequest pageRequest) {
        return QueryContext.of(sortKey, sortDirection, pageRequest, false);
    }

    /**
     * QueryContext 생성 (includeDeleted 포함)
     *
     * @param sortKey 정렬 키
     * @param sortDirection 정렬 방향
     * @param pageRequest 페이징 정보
     * @param includeDeleted 삭제된 항목 포함 여부
     * @param <K> 정렬 키 타입
     * @return QueryContext
     */
    public <K extends SortKey> QueryContext<K> createQueryContext(
            K sortKey,
            SortDirection sortDirection,
            PageRequest pageRequest,
            boolean includeDeleted) {
        return QueryContext.of(sortKey, sortDirection, pageRequest, includeDeleted);
    }
}
