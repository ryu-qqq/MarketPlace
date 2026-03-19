package com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.dto.LegacyOrderCompositeQueryDto;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.mapper.LegacyOrderCompositeMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.repository.LegacyOrderCompositeQueryDslRepository;
import com.ryuqq.marketplace.application.legacyconversion.dto.result.LegacyOrderCompositeResult;
import com.ryuqq.marketplace.application.legacyconversion.port.out.query.LegacyOrderCompositeQueryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * 레거시 주문 복합 조회 Adapter.
 *
 * <p>{@link LegacyOrderCompositeQueryPort} 구현체. Repository에서 flat DTO 조회 + optionValues 별도 조회 후
 * Mapper로 Application DTO로 변환합니다.
 *
 * @author ryu-qqq
 * @since 1.0.0
 */
@Component
public class LegacyOrderCompositeQueryAdapter implements LegacyOrderCompositeQueryPort {

    private final LegacyOrderCompositeQueryDslRepository repository;
    private final LegacyOrderCompositeMapper mapper;

    public LegacyOrderCompositeQueryAdapter(
            LegacyOrderCompositeQueryDslRepository repository, LegacyOrderCompositeMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<LegacyOrderCompositeResult> fetchOrderComposite(long orderId) {
        Optional<LegacyOrderCompositeQueryDto> dtoOpt = repository.fetchOrderComposite(orderId);
        if (dtoOpt.isEmpty()) {
            return Optional.empty();
        }

        LegacyOrderCompositeQueryDto dto = dtoOpt.get();
        List<String> optionValues = repository.fetchOptionValues(orderId);

        return Optional.of(mapper.toResult(dto, optionValues));
    }
}
