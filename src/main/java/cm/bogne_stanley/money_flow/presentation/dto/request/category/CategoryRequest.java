package cm.bogne_stanley.money_flow.presentation.dto.request.category;

import cm.bogne_stanley.money_flow.data.entity.Category;
import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
    @NotBlank(message = "Name is required")
    String name,
    @NotBlank(message = "Description is required")
    String description,
    @NotBlank(message = "Color is required")
    String color
) {
    public Category toCategory() {
        return Category.builder()
            .name(this.name)
            .description(this.description)
            .color(this.color)
            .build();
    }
}
