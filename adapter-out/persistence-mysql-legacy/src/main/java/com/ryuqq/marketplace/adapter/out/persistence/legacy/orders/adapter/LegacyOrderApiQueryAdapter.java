package com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.dto.LegacyOrderCompositeQueryDto;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.dto.LegacyOrderHistoryQueryDto;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.mapper.LegacyOrderApiMapper;
import com.ryuqq.marketplace.adapter.out.persistence.legacy.orders.repository.LegacyOrderApiQueryDslRepository;
import com.ryuqq.marketplace.application.legacy.order.dto.query.LegacyOrderSearchParams;
import com.ryuqq.marketplace.application.legacy.order.dto.result.LegacyOrderDetailResult;
import com.ryuqq.marketplace.application.legacy.order.dto.result.LegacyOrderHistoryResult;
import com.ryuqq.marketplace.application.legacy.order.port.out.query.LegacyOrderQueryPort;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Component;

/**
 * 레거시 주문 조회 Adapter.
 *
 * <p>{@link LegacyOrderQueryPort} 구현체. Repository에서 flat DTO 조회 + optionValues 별도 조회 후 Mapper로
 * Application DTO로 변환합니다.
 */
@Component
public class LegacyOrderApiQueryAdapter implements LegacyOrderQueryPort {

    private final LegacyOrderApiQueryDslRepository repository;
    private final LegacyOrderApiMapper mapper;

    public LegacyOrderApiQueryAdapter(
            LegacyOrderApiQueryDslRepository repository, LegacyOrderApiMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    public Optional<LegacyOrderDetailResult> fetchOrderDetail(long orderId) {
        Optional<LegacyOrderCompositeQueryDto> dtoOpt = repository.fetchOrderComposite(orderId);
        if (dtoOpt.isEmpty()) {
            return Optional.empty();
        }

        LegacyOrderCompositeQueryDto dto = dtoOpt.get();
        List<String> optionValues = repository.fetchOptionValues(orderId);

        return Optional.of(mapper.toDetailResult(dto, optionValues));
    }

    @Override
    public List<LegacyOrderDetailResult> fetchOrderList(LegacyOrderSearchParams params) {
        List<LegacyOrderCompositeQueryDto> dtos = repository.fetchOrderList(params);

        return dtos.stream()
                .map(
                        dto -> {
                            List<String> optionValues =
                                    repository.fetchOptionValues(dto.legacyOrderId());
                            return mapper.toDetailResult(dto, optionValues);
                        })
                .toList();
    }

    @Override
    public long countOrders(LegacyOrderSearchParams params) {
        return repository.countOrders(params);
    }

    @Override
    public List<LegacyOrderHistoryResult> fetchOrderHistories(List<Long> orderIds) {
        List<LegacyOrderHistoryQueryDto> dtos = repository.fetchOrderHistories(orderIds);
        return dtos.stream().map(mapper::toHistoryResult).toList();
    }
}
