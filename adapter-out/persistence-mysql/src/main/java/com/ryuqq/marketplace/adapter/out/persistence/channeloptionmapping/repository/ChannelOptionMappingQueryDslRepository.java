package com.ryuqq.marketplace.adapter.out.persistence.channeloptionmapping.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.ryuqq.marketplace.adapter.out.persistence.channeloptionmapping.entity.ChannelOptionMappingJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.channeloptionmapping.entity.QChannelOptionMappingJpaEntity;
import com.ryuqq.marketplace.domain.channeloptionmapping.query.ChannelOptionMappingSearchCriteria;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Repository;

/** ChannelOptionMapping QueryDSL Repository. */
@Repository
public class ChannelOptionMappingQueryDslRepository {

    private static final QChannelOptionMappingJpaEntity channelOptionMapping =
            QChannelOptionMappingJpaEntity.channelOptionMappingJpaEntity;

    private final JPAQueryFactory queryFactory;

    public ChannelOptionMappingQueryDslRepository(JPAQueryFactory queryFactory) {
        this.queryFactory = queryFactory;
    }

    public Optional<ChannelOptionMappingJpaEntity> findById(Long id) {
        ChannelOptionMappingJpaEntity result =
                queryFactory
                        .selectFrom(channelOptionMapping)
                        .where(channelOptionMapping.id.eq(id))
                        .fetchOne();
        return Optional.ofNullable(result);
    }

    public List<ChannelOptionMappingJpaEntity> findByCriteria(
            ChannelOptionMappingSearchCriteria criteria) {
        BooleanBuilder builder = buildCondition(criteria);

        return queryFactory
                .selectFrom(channelOptionMapping)
                .where(builder)
                .orderBy(channelOptionMapping.createdAt.desc())
                .offset(criteria.offset())
                .limit(criteria.size())
                .fetch();
    }

    public long countByCriteria(ChannelOptionMappingSearchCriteria criteria) {
        BooleanBuilder builder = buildCondition(criteria);

        Long count =
                queryFactory
                        .select(channelOptionMapping.count())
                        .from(channelOptionMapping)
                        .where(builder)
                        .fetchOne();
        return count != null ? count : 0L;
    }

    public boolean existsBySalesChannelIdAndCanonicalOptionValueId(
            Long salesChannelId, Long canonicalOptionValueId) {
        Integer result =
                queryFactory
                        .selectOne()
                        .from(channelOptionMapping)
                        .where(
                                channelOptionMapping.salesChannelId.eq(salesChannelId),
                                channelOptionMapping.canonicalOptionValueId.eq(
                                        canonicalOptionValueId))
                        .fetchFirst();
        return result != null;
    }

    private BooleanBuilder buildCondition(ChannelOptionMappingSearchCriteria criteria) {
        BooleanBuilder builder = new BooleanBuilder();

        if (criteria.hasSalesChannelFilter()) {
            builder.and(channelOptionMapping.salesChannelId.eq(criteria.salesChannelId()));
        }

        return builder;
    }
}
