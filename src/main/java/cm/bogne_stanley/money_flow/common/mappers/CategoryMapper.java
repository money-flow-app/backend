package cm.bogne_stanley.money_flow.common.mappers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import cm.bogne_stanley.money_flow.data.entity.Category;
import cm.bogne_stanley.money_flow.presentation.dto.response.category.CategoryResponse;

@Component
public class CategoryMapper {
    @Value("${app.url}")
    private String appUrl;
    
    public CategoryResponse toResponse(Category category) {
        String icon = category.getIcon() != null ? (category.getIcon().startsWith("http") ? category.getIcon() : appUrl + "/" + category.getIcon()) : null;
        return new CategoryResponse(
            category.getId(),
            category.getName(),
            category.getDescription(),
            icon
        );
    }
}
