package com.ryuqq.marketplace.adapter.out.client.setof.strategy;

import com.ryuqq.marketplace.domain.outboundsync.vo.ChangedArea;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

/**
 * 세토프 상품 수정 실행기 제공자.
 *
 * <p>변경 영역(changedAreas) 기반으로 전체 수정 또는 부분 수정 실행기를 선택합니다.
 *
 * <p><strong>전략 선택 규칙:</strong>
 *
 * <ul>
 *   <li>changedAreas가 비어있음 → 전체 수정 (레거시 호환)
 *   <li>changedAreas 크기 ≥ {@link #FULL_UPDATE_THRESHOLD} → 전체 수정 (개별 호출보다 효율적)
 *   <li>그 외 → 부분 수정
 * </ul>
 */
@Component
@ConditionalOnProperty(prefix = "setof-commerce", name = "service-token")
public class SetofProductUpdateExecutorProvider {

    private static final Logger log =
            LoggerFactory.getLogger(SetofProductUpdateExecutorProvider.class);

    /**
     * 변경 영역이 이 값 이상이면 전체 수정을 선택합니다.
     *
     * <p>세토프 부분 수정 API는 최대 5개 호출(basicInfo, products, images, description, notice)을 해야 하므로, 대부분의
     * 영역이 바뀌면 전체 PUT 1회가 더 효율적입니다.
     */
    static final int FULL_UPDATE_THRESHOLD = 4;

    private final SetofFullProductUpdateExecutor fullExecutor;
    private final SetofPartialProductUpdateExecutor partialExecutor;

    public SetofProductUpdateExecutorProvider(
            SetofFullProductUpdateExecutor fullExecutor,
            SetofPartialProductUpdateExecutor partialExecutor) {
        this.fullExecutor = fullExecutor;
        this.partialExecutor = partialExecutor;
    }

    /**
     * 변경 영역에 적합한 실행기를 반환합니다.
     *
     * @param changedAreas 변경된 영역 집합
     * @return 선택된 실행기
     */
    public SetofProductUpdateExecutor resolve(Set<ChangedArea> changedAreas) {
        if (shouldFullUpdate(changedAreas)) {
            log.debug("전체 수정 실행기 선택: changedAreas={}", changedAreas);
            return fullExecutor;
        }
        log.debug("부분 수정 실행기 선택: changedAreas={}", changedAreas);
        return partialExecutor;
    }

    private boolean shouldFullUpdate(Set<ChangedArea> changedAreas) {
        if (changedAreas == null || changedAreas.isEmpty()) {
            return true;
        }
        return changedAreas.size() >= FULL_UPDATE_THRESHOLD;
    }
}
