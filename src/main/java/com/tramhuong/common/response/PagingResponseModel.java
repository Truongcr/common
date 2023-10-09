package com.tramhuong.common.response;

import lombok.*;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author DungTP
 */

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PagingResponseModel<T> {
    private long totalElements;
    private int pageSize;
    private int pageNumber;
    private List<T> rows;

    public PagingResponseModel(Page<T> page) {
        if (page == null) {
            page = new PageImpl<>(Collections.emptyList());
        }
        this.totalElements = page.getTotalElements();
        this.pageNumber = page.getNumber();
        this.pageSize = page.getSize();
        this.rows = page.getContent();
    }

    public <U> PagingResponseModel<U> map(Function<? super T, ? extends U> function) {
        PagingResponseModel<U> newPage = new PagingResponseModel<>();
        newPage.totalElements = totalElements;
        newPage.pageSize = pageSize;
        newPage.pageNumber = pageNumber;
        newPage.rows = rows.stream().map(function).collect(Collectors.toList());
        return newPage;
    }

    public <U> PagingResponseModel<U> customContent(List<U> newRows){
        return PagingResponseModel.<U>builder()
                .totalElements(CollectionUtils.isEmpty(newRows) ? 0L : (this.totalElements - (this.rows.size() - newRows.size())))
                .pageSize(this.pageSize)
                .pageNumber(this.pageNumber)
                .rows(newRows)
                .build();
    }
}
