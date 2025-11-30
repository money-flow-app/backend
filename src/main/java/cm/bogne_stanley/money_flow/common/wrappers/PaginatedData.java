package cm.bogne_stanley.money_flow.common.wrappers;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;


public record PaginatedData<T>(
    List<T> content,
    Metadata metadata
) {
    public record Metadata(
        @JsonProperty("total_elements")
        Long totalElements,
        @JsonProperty("total_pages")
        Integer totalPages,
        @JsonProperty("current_page")
        Integer currentPage,
        @JsonProperty("page_size")
        Integer pageSize,
        @JsonProperty("has_next")
        boolean hasNext,
        @JsonProperty("has_previous")
        boolean hasPrevious
    ) { }
}
