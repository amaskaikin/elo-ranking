package com.tretton37.ranking.elo.adapter.web.model;

import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.Page;

import java.util.Collection;

@Data
public class PageResponse<T> {
    private final Collection<T> content;
    private final EloPageable pageable;

    public PageResponse(Page<T> page) {
        this.content = page.getContent();
        this.pageable = EloPageable.builder()
                .pageNumber(page.getNumber())
                .pageSize(page.getSize())
                .numberOfElements(page.getNumberOfElements())
                .totalElements(page.getTotalElements())
                .totalPages(page.getTotalPages())
                .last(page.isLast())
                .build();
    }

    @Data
    @Builder
    private static class EloPageable {
        private int pageNumber;
        private int pageSize;
        private int numberOfElements;
        private long totalElements;
        private int totalPages;
        private boolean last;

    }
}
