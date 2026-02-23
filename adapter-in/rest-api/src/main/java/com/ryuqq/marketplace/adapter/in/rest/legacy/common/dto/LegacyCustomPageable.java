package com.ryuqq.marketplace.adapter.in.rest.legacy.common.dto;

import java.util.Iterator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

/** 세토프 CustomPageable 호환 페이징 응답 DTO. */
public class LegacyCustomPageable<T> implements Page<T> {

    private final List<T> content;
    private final Pageable pageable;
    private final long totalElements;
    private final Long lastDomainId;

    public LegacyCustomPageable(
            List<T> content, Pageable pageable, long totalElements, Long lastDomainId) {
        this.content = content == null ? List.of() : List.copyOf(content);
        this.pageable = pageable;
        this.totalElements = totalElements;
        this.lastDomainId = lastDomainId;
    }

    public Long getLastDomainId() {
        return lastDomainId;
    }

    @Override
    public int getTotalPages() {
        return (int) Math.ceil((double) totalElements / pageable.getPageSize());
    }

    @Override
    public long getTotalElements() {
        return totalElements;
    }

    @Override
    public <U> Page<U> map(Function<? super T, ? extends U> converter) {
        List<U> convertedContent = content.stream().map(converter).collect(Collectors.toList());
        return new LegacyCustomPageable<>(convertedContent, pageable, totalElements, lastDomainId);
    }

    @Override
    public int getNumber() {
        return pageable.getPageNumber();
    }

    @Override
    public int getSize() {
        return pageable.getPageSize();
    }

    @Override
    public int getNumberOfElements() {
        return content.size();
    }

    @Override
    public List<T> getContent() {
        return List.copyOf(content);
    }

    @Override
    public boolean hasContent() {
        return !content.isEmpty();
    }

    @Override
    public Sort getSort() {
        return pageable.getSort();
    }

    @Override
    public Pageable getPageable() {
        return pageable;
    }

    @Override
    public boolean isFirst() {
        return !hasPrevious();
    }

    @Override
    public boolean isLast() {
        return !hasNext();
    }

    @Override
    public boolean hasNext() {
        return getNumber() + 1 < getTotalPages();
    }

    @Override
    public boolean hasPrevious() {
        return getNumber() > 0;
    }

    @Override
    public Pageable nextPageable() {
        return hasNext() ? pageable.next() : Pageable.unpaged();
    }

    @Override
    public Pageable previousPageable() {
        return hasPrevious() ? pageable.previousOrFirst() : Pageable.unpaged();
    }

    @Override
    public Iterator<T> iterator() {
        return content.iterator();
    }
}
