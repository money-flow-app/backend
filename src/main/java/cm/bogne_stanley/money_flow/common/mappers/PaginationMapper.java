package cm.bogne_stanley.money_flow.common.mappers;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import cm.bogne_stanley.money_flow.common.wrappers.PaginatedData;

@Component
public class PaginationMapper {
    public <T> PaginatedData<T> toPaginatedData(Page<T> page) {
        return new PaginatedData<>(
            page.getContent(),
            new PaginatedData.Metadata(
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber(),
                page.getSize(),
                page.hasNext(),
                page.hasPrevious()
            )
        );
    }
}
