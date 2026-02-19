package com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.adapter;

import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.entity.CanonicalOptionGroupJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.entity.CanonicalOptionValueJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.mapper.CanonicalOptionGroupJpaEntityMapper;
import com.ryuqq.marketplace.adapter.out.persistence.canonicaloption.repository.CanonicalOptionGroupQueryDslRepository;
import com.ryuqq.marketplace.application.canonicaloption.port.out.query.CanonicalOptionGroupQueryPort;
import com.ryuqq.marketplace.domain.canonicaloption.aggregate.CanonicalOptionGroup;
import com.ryuqq.marketplace.domain.canonicaloption.id.CanonicalOptionGroupId;
import com.ryuqq.marketplace.domain.canonicaloption.query.CanonicalOptionGroupSearchCriteria;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

/**
 * CanonicalOptionGroup Query Adapter.
 *
 * <p>PER-ADP-001: QueryAdapter는 QueryDslRepository만 사용.
 *
 * <p>Aggregate Root를 통해 자식(CanonicalOptionValue)까지 완전히 로딩합니다.
 */
@Component
public class CanonicalOptionGroupQueryAdapter implements CanonicalOptionGroupQueryPort {

    private final CanonicalOptionGroupQueryDslRepository queryDslRepository;
    private final CanonicalOptionGroupJpaEntityMapper mapper;

    public CanonicalOptionGroupQueryAdapter(
            CanonicalOptionGroupQueryDslRepository queryDslRepository,
            CanonicalOptionGroupJpaEntityMapper mapper) {
        this.queryDslRepository = queryDslRepository;
        this.mapper = mapper;
    }

    @Override
    public Optional<CanonicalOptionGroup> findById(CanonicalOptionGroupId id) {
        return queryDslRepository
                .findById(id.value())
                .map(
                        entity -> {
                            List<CanonicalOptionValueJpaEntity> values =
                                    queryDslRepository.findValuesByGroupId(entity.getId());
                            return mapper.toDomain(entity, values);
                        });
    }

    @Override
    public List<CanonicalOptionGroup> findByIds(List<CanonicalOptionGroupId> ids) {
        List<Long> rawIds = ids.stream().map(CanonicalOptionGroupId::value).toList();
        List<CanonicalOptionGroupJpaEntity> groups = queryDslRepository.findByIds(rawIds);
        if (groups.isEmpty()) {
            return List.of();
        }

        List<Long> groupIds = groups.stream().map(CanonicalOptionGroupJpaEntity::getId).toList();

        Map<Long, List<CanonicalOptionValueJpaEntity>> valuesMap =
                queryDslRepository.findValuesByGroupIds(groupIds).stream()
                        .collect(
                                Collectors.groupingBy(
                                        CanonicalOptionValueJpaEntity::getCanonicalOptionGroupId));

        return groups.stream()
                .map(
                        group ->
                                mapper.toDomain(
                                        group, valuesMap.getOrDefault(group.getId(), List.of())))
                .toList();
    }

    @Override
    public List<CanonicalOptionGroup> findByCriteria(CanonicalOptionGroupSearchCriteria criteria) {
        List<CanonicalOptionGroupJpaEntity> groups = queryDslRepository.findByCriteria(criteria);
        if (groups.isEmpty()) {
            return List.of();
        }

        List<Long> groupIds = groups.stream().map(CanonicalOptionGroupJpaEntity::getId).toList();

        Map<Long, List<CanonicalOptionValueJpaEntity>> valuesMap =
                queryDslRepository.findValuesByGroupIds(groupIds).stream()
                        .collect(
                                Collectors.groupingBy(
                                        CanonicalOptionValueJpaEntity::getCanonicalOptionGroupId));

        return groups.stream()
                .map(
                        group ->
                                mapper.toDomain(
                                        group, valuesMap.getOrDefault(group.getId(), List.of())))
                .toList();
    }

    @Override
    public long countByCriteria(CanonicalOptionGroupSearchCriteria criteria) {
        return queryDslRepository.countByCriteria(criteria);
    }
}
