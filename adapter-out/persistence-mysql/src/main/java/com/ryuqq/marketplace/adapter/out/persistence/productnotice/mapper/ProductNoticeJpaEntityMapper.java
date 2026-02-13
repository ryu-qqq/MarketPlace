package com.ryuqq.marketplace.adapter.out.persistence.productnotice.mapper;

import com.ryuqq.marketplace.adapter.out.persistence.productnotice.entity.ProductNoticeEntryJpaEntity;
import com.ryuqq.marketplace.adapter.out.persistence.productnotice.entity.ProductNoticeJpaEntity;
import com.ryuqq.marketplace.domain.notice.id.NoticeCategoryId;
import com.ryuqq.marketplace.domain.notice.id.NoticeFieldId;
import com.ryuqq.marketplace.domain.productgroup.id.ProductGroupId;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNotice;
import com.ryuqq.marketplace.domain.productnotice.aggregate.ProductNoticeEntry;
import com.ryuqq.marketplace.domain.productnotice.id.ProductNoticeEntryId;
import com.ryuqq.marketplace.domain.productnotice.id.ProductNoticeId;
import com.ryuqq.marketplace.domain.productnotice.vo.NoticeFieldValue;
import java.util.List;
import org.springframework.stereotype.Component;

/**
 * ProductNotice JPA Entity Mapper.
 *
 * <p>PER-MAP-001: @Component 기반 순수 데이터 변환, 도메인 reconstitute() 사용.
 */
@Component
public class ProductNoticeJpaEntityMapper {

    /**
     * 도메인 -> JPA 엔티티 변환.
     *
     * @param domain ProductNotice 도메인 객체
     * @return ProductNoticeJpaEntity
     */
    public ProductNoticeJpaEntity toEntity(ProductNotice domain) {
        return ProductNoticeJpaEntity.create(
                domain.idValue(),
                domain.productGroupIdValue(),
                domain.noticeCategoryIdValue(),
                domain.createdAt(),
                domain.updatedAt());
    }

    /**
     * 도메인 항목 -> JPA 엔티티 변환.
     *
     * @param entry ProductNoticeEntry 도메인 객체
     * @param productNoticeId 부모 고시정보 ID
     * @return ProductNoticeEntryJpaEntity
     */
    public ProductNoticeEntryJpaEntity toEntryEntity(
            ProductNoticeEntry entry, Long productNoticeId) {
        return ProductNoticeEntryJpaEntity.create(
                entry.idValue(),
                productNoticeId,
                entry.noticeFieldIdValue(),
                entry.fieldValueValue());
    }

    /**
     * JPA 엔티티 -> 도메인 변환.
     *
     * @param entity ProductNoticeJpaEntity
     * @param entries 고시정보 항목 엔티티 목록
     * @return ProductNotice 도메인 객체
     */
    public ProductNotice toDomain(
            ProductNoticeJpaEntity entity, List<ProductNoticeEntryJpaEntity> entries) {
        if (entity.getId() == null) {
            throw new IllegalStateException("영속화된 엔티티의 ID는 null일 수 없습니다");
        }

        List<ProductNoticeEntry> domainEntries = entries.stream().map(this::toEntryDomain).toList();

        return ProductNotice.reconstitute(
                ProductNoticeId.of(entity.getId()),
                ProductGroupId.of(entity.getProductGroupId()),
                NoticeCategoryId.of(entity.getNoticeCategoryId()),
                domainEntries,
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    private ProductNoticeEntry toEntryDomain(ProductNoticeEntryJpaEntity entry) {
        return ProductNoticeEntry.reconstitute(
                ProductNoticeEntryId.of(entry.getId()),
                NoticeFieldId.of(entry.getNoticeFieldId()),
                NoticeFieldValue.of(entry.getFieldValue()));
    }
}
